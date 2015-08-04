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
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.Precipitation;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class VerticalWeatherBar extends View {
    private Paint paint;
    private float segWidth;
    private Forecast.DataPoint data[];
    private float margin;
    private TextPaint textPaint;
    private ArrayList<String> summaries;
    private ArrayList<Integer> summaryStarts;
    private Rect bounds;
    private TimeZone timeZone;
    private float cornerRadius;

    private float oldColorHeight;
    private ArrayList<StaticLayout> staticLayouts;
    private ArrayList<Float> summaryY;

    public VerticalWeatherBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        segWidth = getResources().getDimensionPixelSize(R.dimen.vertical_weather_bar_width);
        summaries = new ArrayList<>();
        summaryStarts = new ArrayList<>();
        margin = getResources().getDimension(R.dimen.standard_margin);
        textPaint = new TextView(context).getPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.body_1));
        textPaint.setColor(getResources().getColor(R.color.text_primary));
        bounds = new Rect();
        cornerRadius = getResources().getDimension(R.dimen.rounded_rectangle_corner_radius);

        staticLayouts = new ArrayList<>();
        summaryY = new ArrayList<>();
    }

    public void setData(Forecast.Response forecast) {
        this.data = forecast.getHourly().getData();
        timeZone = forecast.getTimezone();

        staticLayouts.clear();
        summaryY.clear();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null)
            return;

        float height = getHeight();
        float width = getWidth();

        float colorHeight = (float) Math.floor(height / 24);
        float colorLeft = width / 2 - segWidth / 2;
        float colorRight = colorLeft + segWidth;
        float barTop = (height - 24 * colorHeight) / 2;
        float colorTop = barTop;
        float colorBottom = colorTop + colorHeight;

        float summaryStartX = colorRight + margin;
        float summaryEndX = width - margin;
        float summaryWidth = summaryEndX - summaryStartX;

        DecimalFormat tempFormat = ForecastTools.getTempForm();
        SimpleDateFormat timeFormat = ForecastTools.getShortTimeForm(timeZone, 24);

        textPaint.getTextBounds("M", 0, 1, bounds);
        float tempRight = colorLeft - margin;
        float textHeight = bounds.height();

        textPaint.getTextBounds(tempFormat.format(100), 0, tempFormat.format(100).length(), bounds);
        float timeRight = tempRight - bounds.width() - margin;

        int skip = 2;
        if (textHeight > colorHeight)
            skip += Math.round(textHeight / colorHeight);

        summaries.clear();
        summaryStarts.clear();
        summaries.add(Precipitation.evaluate(getResources(), data[0]).summary);
        summaryStarts.add(0);

        for (int i = 0; i < 24; i++) {
            Forecast.DataPoint dataPoint = data[i];
            Precipitation.Response evaluation = Precipitation.evaluate(getResources(), dataPoint);

            if (!summaries.get(summaries.size() - 1).equals(evaluation.summary)) {
                summaries.add(evaluation.summary);
                summaryStarts.add(i);
            }
        }

        paint.setColor(Precipitation.evaluate(getResources(), data[0]).color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(colorLeft, colorTop, colorRight, colorBottom, cornerRadius,
                    cornerRadius, paint);
        } else {
            canvas.drawRect(colorLeft, colorTop, colorRight, colorBottom, paint);
        }

        colorTop += colorHeight / 2;
        canvas.drawRect(colorLeft, colorTop, colorRight, colorBottom, paint);
        colorTop += colorHeight / 2;
        colorBottom += colorHeight;

        for (int i = 1; i < 23; i++) {
            Forecast.DataPoint dataPoint = data[i];
            Precipitation.Response evaluation = Precipitation.evaluate(getResources(), dataPoint);

            paint.setColor(evaluation.color);
            canvas.drawRect(colorLeft, colorTop, colorRight, colorBottom, paint);
            colorTop += colorHeight;
            colorBottom += colorHeight;
        }

        paint.setColor(Precipitation.evaluate(getResources(), data[23]).color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(colorLeft, colorTop, colorRight, colorBottom, cornerRadius,
                    cornerRadius, paint);
        } else {
            canvas.drawRect(colorLeft, colorTop, colorRight, colorBottom, paint);
        }

        colorBottom -= colorHeight / 2;
        canvas.drawRect(colorLeft, colorTop, colorRight, colorBottom, paint);

        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < 24; i += skip) {
            Forecast.DataPoint dataPoint = data[i];

            float textTop = barTop + (i + 1) * colorHeight + textHeight / 2;

            canvas.drawText(tempFormat.format(dataPoint.getTemperature()), tempRight, textTop,
                    textPaint);
            canvas.drawText(timeFormat.format(dataPoint.getTime()), timeRight, textTop, textPaint);
        }

        textPaint.setTextAlign(Paint.Align.LEFT);
        if (staticLayouts.size() == 0 || colorHeight != oldColorHeight) {
            oldColorHeight = colorHeight;
            staticLayouts.clear();
            summaryY.clear();

            for (int i = 0; i < summaries.size(); i++) {
                StaticLayout staticLayout;
                String summary = summaries.get(i);

                int startIndex = summaryStarts.get(i);
                int endIndex = i + 1 != summaries.size() ? summaryStarts.get(i + 1) : 24;

                float startY = barTop + startIndex * colorHeight + margin / 2;
                float endY = barTop + endIndex * colorHeight - margin / 2;
                float summaryHeight = endY - startY;
                float middleY = startY + summaryHeight / 2;

                staticLayout = new StaticLayout(summary, textPaint, (int) summaryWidth,
                        Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

                float textY = middleY - staticLayout.getHeight() / 2;

                if (staticLayout.getWidth() <= summaryWidth && staticLayout.getHeight()
                        <= summaryHeight && staticLayout.getLineCount()
                        <= summary.split("\\s+").length) {
                    staticLayouts.add(staticLayout);
                    summaryY.add(textY);

                    /*
                    canvas.drawLine(summaryStartX, startY, summaryEndX, startY, paint);
                    canvas.drawLine(summaryStartX, middleY, summaryEndX, middleY, paint);
                    canvas.drawLine(summaryStartX, endY, summaryEndX, endY, paint);
                    */
                }
            }
        }

        for (int i = 0; i < staticLayouts.size(); i++) {
            canvas.save();
            canvas.translate(summaryStartX, summaryY.get(i));
            staticLayouts.get(i).draw(canvas);
            canvas.restore();
        }
    }


}
