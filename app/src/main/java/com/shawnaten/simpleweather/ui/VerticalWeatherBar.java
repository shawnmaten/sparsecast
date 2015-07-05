package com.shawnaten.simpleweather.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Colors;
import com.shawnaten.tools.Forecast;

import java.util.TimeZone;

public class VerticalWeatherBar extends View {
    private Paint paint;
    private float segWidth;
    private Forecast.DataBlock hourly;

    public VerticalWeatherBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        segWidth = getResources().getDimensionPixelSize(R.dimen.vertical_weather_bar_width);
    }

    public void setData(Forecast.Response forecast) {
        hourly = forecast.getHourly();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hourly == null)
            return;

        float height = getHeight();
        float width = getWidth();
        float segHeight = (float) Math.floor(height / 24);
        float left = width / 2 - segWidth / 2;
        float right = left + segWidth;
        float top = (height - 24 * segHeight) / 2;
        float bottom = top + segHeight;

        for (Forecast.DataPoint dataPoint : hourly.getData()) {
            paint.setColor(getResources().getColor(Colors.getColor(dataPoint)));
            canvas.drawRect(left, top, right, bottom, paint);
            top += segHeight;
            bottom += segHeight;
        }
    }
}
