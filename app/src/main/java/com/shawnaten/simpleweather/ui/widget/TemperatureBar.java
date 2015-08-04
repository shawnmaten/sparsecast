package com.shawnaten.simpleweather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.ForecastTools;

import java.text.DecimalFormat;

public class TemperatureBar extends View {
    private Paint paint;

    private Paint textPaint;
    private float paddingStart, paddingEnd;
    private float textHeight;
    private Rect bounds;

    private double totalMin, totalMax;
    private double totalRange;
    private double min, max;

    private float halfMargin;
    private float cornerRadius;

    private DecimalFormat tempFormat;

    public TemperatureBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.text_secondary));
        textPaint = new TextView(context).getPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.subhead));
        textPaint.setColor(getResources().getColor(R.color.text_primary));
        tempFormat = ForecastTools.getTempForm();
        bounds = new Rect();
        halfMargin = getResources().getDimension(R.dimen.half_margin);
        cornerRadius = getResources().getDimension(R.dimen.rounded_rectangle_corner_radius);
    }

    public void setData(double min, double max, double totalMin, double totalMax) {
        this.min = min;
        this.max = max;
        this.totalMin = totalMin;
        this.totalMax = totalMax;
        totalRange = totalMax - totalMin;

        String tempString = tempFormat.format(totalMin);
        textPaint.getTextBounds(tempString, 0, tempString.length(), bounds);
        paddingStart = bounds.width() + halfMargin * 2;
        textHeight = bounds.height();
        tempString = tempFormat.format(totalMax);
        textPaint.getTextBounds(tempString, 0, tempString.length(), bounds);
        paddingEnd = bounds.width() + halfMargin * 2;
        textHeight = Math.max(textHeight, bounds.height());

        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), (int) (textHeight + halfMargin * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        float maxBarWidth = width - paddingStart - paddingEnd;
        float degreeWidth = (float) (maxBarWidth / totalRange);

        float barStart = (float) (paddingStart + (min - totalMin) * degreeWidth);
        float barEnd = (float) (width - paddingEnd - (totalMax - max) * degreeWidth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(barStart, halfMargin, barEnd, height - halfMargin, cornerRadius,
                    cornerRadius, paint);
        } else {
            canvas.drawRect(barStart, halfMargin, barEnd, height - halfMargin, paint);
        }


        String tempString = tempFormat.format(min);
        textPaint.getTextBounds(tempString, 0, tempString.length(), bounds);
        float textStart = barStart - halfMargin - bounds.width();
        float textBottom = height - (height - bounds.height()) / 2;
        canvas.drawText(tempString, textStart, textBottom, textPaint);

        String timeString = tempFormat.format(max);
        textPaint.getTextBounds(timeString, 0, timeString.length(), bounds);
        textStart = barEnd + halfMargin;
        textBottom = height - (height - bounds.height()) / 2;
        canvas.drawText(timeString, textStart, textBottom, textPaint);
    }
}
