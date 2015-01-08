package com.shawnaten.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.util.DisplayMetrics;

import com.shawnaten.network.models.Forecast;

/**
 * Created by shawnaten on 7/14/14.
 */
public class WeatherBarShape extends Shape {
    private float density;
    private final int TICK_SIZE_DP = 8, UNIT_SKIP = 2, TICK_COUNT = 11;

    private int width, height,
                unitStart, unitCount, unitSize, barSize, tickSize, tickSpacing, tickColor,
                leftOffset, rightOffset;

    private float[] ticks;
    private WeatherBlock[] blocks;

    public WeatherBarShape(Context context, Forecast.DataPoint[] data, int start, int count, int width, int height) {
        unitStart = start;
        unitCount = count;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;

        setDimensions(width, height);
        setData(0x8a000000, ForecastTools.parseWeatherPoints(context, data, start, count));
    }

    private void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;

        unitSize = width / unitCount;
        leftOffset = (width - (unitSize * unitCount)) / 2;
        rightOffset = leftOffset + unitSize;
        tickSize = (int) (TICK_SIZE_DP * density);
        barSize = height - tickSize;

        tickSpacing = unitSize * UNIT_SKIP;

        ticks = new float[TICK_COUNT * 4];

        int yStop = barSize + tickSize;
        int j = 0;
        for (int i = 0; i < TICK_COUNT; i++) {
            int x = ((i + 1) * tickSpacing) + leftOffset;
            ticks[j++] = x;
            ticks[j++] = barSize;
            ticks[j++] = x;
            ticks[j++] = yStop;
        }

    }

    private void setData(int tickColor, WeatherBlock[] blocks) {
        this.tickColor = tickColor;
        this.blocks = blocks;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(tickColor);

        canvas.drawLines(ticks, paint);

        for(WeatherBlock block : blocks) {
            paint.setColor(block.getColor());
            canvas.drawRect((block.getStart() * unitSize) + leftOffset, 0, (block.getEnd() * unitSize) + rightOffset, barSize, paint);
        }

    }

    protected void onResize (float w, float h) {
        int mWidth = (int) w, mHeight = (int) h;

        if (width != mWidth || height != mHeight) {
            setDimensions(mWidth, mHeight);
        }
    }

    public float getDensity() {
        return density;
    }

    public int getUnitStart() {
        return unitStart;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public int getUnitSkip() {
        return UNIT_SKIP;
    }

    public int getBarSize() {
        return barSize;
    }

    public int getTickCount() {
        return TICK_COUNT;
    }

    public int getTickSize() {
        return tickSize;
    }

    public int getTickSpacing() {
        return tickSpacing;
    }

    public int getTickColor() {
        return tickColor;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public float[] getTicks() {
        return ticks;
    }

    public WeatherBlock[] getBlocks() {
        return blocks;
    }
}
