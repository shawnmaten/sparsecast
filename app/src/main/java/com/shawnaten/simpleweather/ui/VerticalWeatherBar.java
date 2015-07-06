package com.shawnaten.simpleweather.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Precipitation;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class VerticalWeatherBar extends View {
    private Paint paint;
    private float segWidth;
    private Forecast.DataBlock hourly;
    private float margin;
    private TextPaint textPaint;
    private ArrayList<String> summaries;
    private ArrayList<Integer> summaryStarts;
    private Rect bounds;
    private TimeZone timeZone;

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
    }

    public void setData(Forecast.Response forecast) {
        this.hourly = forecast.getHourly();
        timeZone = forecast.getTimezone();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hourly == null)
            return;

        float height = getHeight();
        float width = getWidth();

        float colorHeight = (float) Math.floor(height / 24);
        float colorLeft = width / 2 - segWidth / 2;
        float colorRight = colorLeft + segWidth;
        float barTop = (height - 24 * colorHeight) / 2;
        float colorTop = barTop;
        float colorBottom = colorTop + colorHeight;

        float summaryWidth = (width - colorRight) - margin * 2;
        float summaryLeft = colorRight + margin;

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
        summaries.add(Precipitation.evaluate(getResources(), hourly.getData()[0]).summary);
        summaryStarts.add(0);

        for (int i = 0; i < 24; i++) {
            Forecast.DataPoint dataPoint = hourly.getData()[i];
            Precipitation.Response evaluation = Precipitation.evaluate(getResources(), dataPoint);

            if (!summaries.get(summaries.size() - 1).equals(evaluation.summary)) {
                summaries.add(evaluation.summary);
                summaryStarts.add(i);
            }
            paint.setColor(evaluation.color);
            canvas.drawRect(colorLeft, colorTop, colorRight, colorBottom, paint);
            colorTop += colorHeight;
            colorBottom += colorHeight;
        }

        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < 24; i += skip) {
            Forecast.DataPoint dataPoint = hourly.getData()[i];

            float textTop = barTop + i * colorHeight;

            canvas.drawText(tempFormat.format(dataPoint.getTemperature()), tempRight,
                    textTop + textHeight, textPaint);
            canvas.drawText(timeFormat.format(dataPoint.getTime()), timeRight,
                    textTop + textHeight, textPaint);
        }

        textPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < summaries.size(); i++) {
            float summaryTop = summaryStarts.get(i);
            float summaryBottom;

            if (i != summaries.size() - 1)
                summaryBottom = summaryStarts.get(i + 1);
            else
                summaryBottom = 24;

            if (summaryBottom - summaryTop >= 2) {
                summaryTop = barTop + summaryTop * colorHeight;
                summaryBottom = barTop + summaryBottom * colorHeight;

                float textY = summaryTop + (summaryBottom - summaryTop) / 2 + textHeight / 2;
                CharSequence summary = TextUtils.ellipsize(summaries.get(i), textPaint,
                        summaryWidth, TextUtils.TruncateAt.MIDDLE);
                canvas.drawText(summary, 0, summary.length(), summaryLeft, textY, textPaint);
            }
        }
    }


}
