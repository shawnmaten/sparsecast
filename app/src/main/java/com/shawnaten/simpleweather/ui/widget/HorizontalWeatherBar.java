package com.shawnaten.simpleweather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.tools.ForecastTools;
import com.shawnaten.simpleweather.tools.Precipitation;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class HorizontalWeatherBar extends View {
    private Paint paint;
    private TextPaint textPaint;
    private Rect bounds;
    private float timeWidth;
    private float textHeight;

    private SimpleDateFormat timeFormat;
    private DecimalFormat tempFormat;

    private float margin;

    private TimeZone timeZone;
    private Forecast.DataPoint data[];
    private int offset;

    private ArrayList<String> summaries;
    private ArrayList<Integer> summaryStarts;

    private float oldSegWidth;
    private ArrayList<StaticLayout> staticLayouts;
    private ArrayList<Float> summaryX;
    private ArrayList<Float> summaryY;

    private float cornerRadius;

    public HorizontalWeatherBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        margin = getResources().getDimension(R.dimen.standard_margin);
        textPaint = new TextView(context).getPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.caption));
        textPaint.setColor(getResources().getColor(R.color.text_primary));
        bounds = new Rect();
        tempFormat = ForecastTools.getTempForm();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 12);
        DateFormat timeFormat = ForecastTools.getTimeForm(TimeZone.getDefault());
        String timeString = timeFormat.format(calendar.getTime());
        textPaint.getTextBounds(timeString, 0, timeString.length(), bounds);
        timeWidth = bounds.width();
        textHeight = bounds.height();

        summaries = new ArrayList<>();
        summaryStarts = new ArrayList<>();

        staticLayouts = new ArrayList<>();
        summaryX = new ArrayList<>();
        summaryY = new ArrayList<>();

        cornerRadius = getResources().getDimension(R.dimen.rounded_rectangle_corner_radius);
    }

    public void setData(Forecast.DataPoint data[], int offset, TimeZone timeZone) {
        this.data = data;
        this.offset = offset;
        this.timeZone = timeZone;

        timeFormat = ForecastTools.getShortTimeForm(timeZone, 24);

        staticLayouts.clear();
        summaryX.clear();
        summaryY.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        float barWidth = width - margin * 2;
        float segWidth = barWidth / 24;

        float extra = (barWidth - segWidth * 24) / 2;

        int skip = (int) Math.floor((timeWidth + margin) / segWidth);
        while (24 % skip != 0)
            skip++;

        float tempBottom = height - margin / 2;
        float timeBottom = tempBottom - textHeight - margin / 2;

        float barBottom = timeBottom - textHeight - margin / 2;
        float barTop = margin / 2;

        for (int i = skip; i < 24; i += skip) {
            Forecast.DataPoint dataPoint = data[offset + i];

            String tempString = tempFormat.format(dataPoint.getTemperature());
            float tempLeft = margin + extra + segWidth * i;
            textPaint.getTextBounds(tempString, 0, tempString.length(), bounds);
            tempLeft -= bounds.width() / 2;
            canvas.drawText(tempString, tempLeft, tempBottom, textPaint);

            String timeString = timeFormat.format(dataPoint.getTime());
            float timeLeft = margin + extra + segWidth * i;
            textPaint.getTextBounds(timeString, 0, timeString.length(), bounds);
            timeLeft -= bounds.width() / 2;
            canvas.drawText(timeString, timeLeft, timeBottom, textPaint);
        }

        summaries.clear();
        summaryStarts.clear();
        summaries.add(Precipitation.evaluate(getResources(), data[offset]).summary);
        summaryStarts.add(0);

        for (int i = 1; i < 24; i++) {
            Forecast.DataPoint dataPoint = data[offset + i];
            Precipitation.Response evaluation = Precipitation.evaluate(getResources(), dataPoint);

            if (!summaries.get(summaries.size() - 1).equals(evaluation.summary)) {
                summaries.add(evaluation.summary);
                summaryStarts.add(i);
            }
        }

        float colorLeft = margin + extra + segWidth;
        float colorRight = colorLeft + segWidth;

        for (int i = 1; i < 23; i++) {
            Forecast.DataPoint dataPoint = data[offset + i];
            Precipitation.Response evaluation = Precipitation.evaluate(getResources(), dataPoint);

            paint.setColor(evaluation.color);
            canvas.drawRect(colorLeft, barTop, colorRight, barBottom, paint);
            colorLeft += segWidth;
            colorRight += segWidth;
        }

        colorLeft = margin + extra;
        colorRight = colorLeft + segWidth;
        paint.setColor(Precipitation.evaluate(getResources(), data[offset]).color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(colorLeft, barTop, colorRight, barBottom, cornerRadius,
                    cornerRadius, paint);
        } else {
            canvas.drawRect(colorLeft, barTop, colorRight, barBottom, paint);
        }

        colorLeft += segWidth / 2;
        canvas.drawRect(colorLeft, barTop, colorRight, barBottom, paint);

        colorLeft = margin + extra + barWidth - segWidth;
        colorRight = colorLeft + segWidth;
        paint.setColor(Precipitation.evaluate(getResources(), data[offset + 23]).color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(colorLeft, barTop, colorRight, barBottom, cornerRadius,
                    cornerRadius, paint);
        } else {
            canvas.drawRect(colorLeft, barTop, colorRight, barBottom, paint);
        }

        colorRight -= segWidth / 2;
        canvas.drawRect(colorLeft, barTop, colorRight, barBottom, paint);

        if (staticLayouts.size() == 0 || segWidth != oldSegWidth) {
            oldSegWidth = segWidth;
            staticLayouts.clear();
            summaryX.clear();
            summaryY.clear();

            for (int i = 0; i < summaries.size(); i++) {
                StaticLayout staticLayout;

                String summary = summaries.get(i);

                int startIndex = summaryStarts.get(i);
                int endIndex = i + 1 != summaries.size() ? summaryStarts.get(i + 1) : 24;

                float startX = margin + extra + startIndex * segWidth;
                float endX = startX + (endIndex - startIndex) * segWidth;
                float maxWidth = endX - startX - margin / 2;
                float maxHeight = barBottom - barTop - margin / 2;

                if (maxWidth > 0 && maxHeight > 0) {
                    staticLayout = new StaticLayout(summary, textPaint, (int) maxWidth,
                            Layout.Alignment.ALIGN_CENTER, 1, 1, false);

                    float middleX = startX + (endX - startX) / 2;
                    float textX = middleX - staticLayout.getWidth() / 2;
                    float textY = barTop + (barBottom - barTop) / 2 - staticLayout.getHeight() / 2;

                    if (staticLayout.getWidth() <= maxWidth && staticLayout.getHeight()
                            <= maxHeight && staticLayout.getLineCount()
                            <= summary.split("\\s+").length) {
                        staticLayouts.add(staticLayout);
                        summaryX.add(textX);
                        summaryY.add(textY);
                    }
                }
            }
        }

        for (int i = 0; i < staticLayouts.size(); i++) {
            canvas.save();
            canvas.translate(summaryX.get(i), summaryY.get(i));
            staticLayouts.get(i).draw(canvas);
            canvas.restore();
        }
    }
}
