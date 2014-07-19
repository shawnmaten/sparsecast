package com.shawnaten.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

import com.shawnaten.networking.Forecast;

/**
 * Created by shawnaten on 7/14/14.
 */
public class WeatherBarShape extends Shape implements Parcelable {
    private float density;

    private int width, height,
                unitStart, unitCount, unitSize, unitSkip, barSize, tickCount, tickSize, tickSpacing, tickColor,
                leftOffset, rightOffset;

    private int[] ticks;
    private WeatherBlock[] blocks;

    public WeatherBarShape(Context context, Forecast.DataPoint[] data, int start, int count, int widthDP, int heightDP) {
        unitStart = start;
        unitCount = count;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;

        setDimensions((int) (widthDP * density), (int) (heightDP * density));
    }

    private void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;

        unitSize = width / unitCount;
        leftOffset = (width - (unitSize * unitCount)) / 2;
        rightOffset = leftOffset + unitSize;
        barSize = height / 2;
        tickSize = barSize / 4;

        unitSkip = (int) (64 * density) / unitSize;
        while (unitCount % unitSkip != 0)
            unitSkip++;
        tickCount = unitCount / unitSkip - 1;
        tickSpacing = unitSize * unitSkip;

        ticks = new int[tickCount];

        for (int i = 0; i < tickCount; i++)
            ticks[i] = ((i + 1) * tickSpacing) + leftOffset;

    }

    public void setData(int tickColor, WeatherBlock[] blocks) {
        this.tickColor = tickColor;
        this.blocks = blocks;
    }

    // for parcelable functionality
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(density);
        dest.writeIntArray(new int[]{width, height,
                unitStart, unitCount, unitSize, unitSkip, barSize, tickCount, tickSize, tickSpacing, tickColor,
                leftOffset, rightOffset});
        dest.writeInt(ticks.length);
        dest.writeIntArray(ticks);
        dest.writeParcelableArray(blocks, 0);
    }

    public static final Parcelable.Creator<WeatherBarShape> CREATOR
            = new Parcelable.Creator<WeatherBarShape>() {
        public WeatherBarShape createFromParcel(Parcel in) {
            return new WeatherBarShape(in);
        }

        public WeatherBarShape[] newArray(int size) {
            return new WeatherBarShape[size];
        }
    };

    private WeatherBarShape(Parcel in) {
        density = in.readFloat();
        int[] temp = new int[12];
        in.readIntArray(temp);
        ticks = new int[in.readInt()];
        in.readIntArray(ticks);

        int i = 0;
        width = temp[i++]; height = temp[i++];
        unitStart = temp[i++]; unitCount = temp[i++]; unitSize = temp[i++]; unitSkip = temp[i++]; barSize = temp[i++];
        tickCount = temp[i++]; tickSize = temp[i++]; tickSpacing = temp[i++]; tickColor = temp[i++];
        leftOffset = temp[i++]; rightOffset = temp[i];

        blocks = (WeatherBlock[]) in.readParcelableArray(null);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(tickColor);

        int yStop = barSize + tickSize;
        for (int tick : ticks)
            canvas.drawLine(tick, barSize, tick, yStop, paint);

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

    public int[] getDimensions() {
        return new int[] {width, height, unitStart, unitCount, unitSize, unitSkip, barSize, tickCount, tickSize, tickSpacing, tickColor, leftOffset, rightOffset};
    }

}
