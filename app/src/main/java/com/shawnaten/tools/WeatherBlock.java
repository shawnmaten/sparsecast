package com.shawnaten.tools;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.shawnaten.simpleweather.R;

import java.util.Arrays;

/**
 * Created by shawnaten on 7/15/14.
 */
public class WeatherBlock implements Parcelable {
    public static final int PRECIPITATION = 0, FOG = 1, CLOUDS = 2;
    public static final Parcelable.Creator<WeatherBlock> CREATOR
            = new Parcelable.Creator<WeatherBlock>() {
        public WeatherBlock createFromParcel(Parcel in) {
            return new WeatherBlock(in);
        }

        public WeatherBlock[] newArray(int size) {
            return new WeatherBlock[size];
        }
    };
    private String description;
    private int type, start, end, color;
    private int[] data;

    public WeatherBlock(Context context, int type, int start, int end, int... data) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.data = data;
        genColor(context);
        genDescription(context);
    }

    private WeatherBlock(Parcel in) {
        description = in.readString();
        int[] temp = new int[in.readInt()];
        in.readIntArray(temp);
        int i = 0;
        type = temp[i++];
        start = temp[i++];
        end = temp[i++];
        color = temp[i];
        data = new int[in.readInt()];
        in.readIntArray(data);
    }

    private void genColor(Context context) {
        Resources resources = context.getResources();

        switch (type) {
            case PRECIPITATION:
                switch (data[2]) {
                    case R.string.rain:
                        if (data[0] == 1)
                            color = resources.getColor(R.color.drizzle);
                        else
                            switch(data[1]) {
                                case R.string.light:
                                    color = resources.getColor(R.color.light_rain);
                                    break;
                                case R.string.moderate:
                                    color = resources.getColor(R.color.moderate_rain);
                                    break;
                                case R.string.heavy:
                                    color = resources.getColor(R.color.heavy_rain);
                                    break;
                            }
                        break;
                    case R.string.snow:
                        if (data[0] == 1)
                            color = resources.getColor(R.color.flurries);
                        else
                            switch(data[1]) {
                                case R.string.light:
                                    color = resources.getColor(R.color.light_snow);
                                    break;
                                case R.string.moderate:
                                    color = resources.getColor(R.color.moderate_snow);
                                    break;
                                case R.string.heavy:
                                    color = resources.getColor(R.color.heavy_snow);
                                    break;
                            }
                        break;
                    case R.string.sleet:
                        if (data[0] == 1)
                            color = resources.getColor(R.color.scattered_sleet);
                        else
                            switch(data[1]) {
                                case R.string.light:
                                    color = resources.getColor(R.color.light_sleet);
                                    break;
                                case R.string.moderate:
                                    color = resources.getColor(R.color.moderate_sleet);
                                    break;
                                case R.string.heavy:
                                    color = resources.getColor(R.color.heavy_sleet);
                                    break;
                            }
                        break;
                    case R.string.hail:
                        color = resources.getColor(R.color.hail);
                        break;
                }
                break;
            case FOG:
                switch (data[0]) {
                    case R.string.haze:
                        color = resources.getColor(R.color.haze);
                        break;
                    case R.string.mist:
                        color = resources.getColor(R.color.mist);
                        break;
                    case R.string.fog:
                        color = resources.getColor(R.color.fog);
                        break;
                }
                break;
            case CLOUDS:
                switch (data[0]) {
                    case R.string.clear:
                        color = resources.getColor(R.color.clear);
                        break;
                    case R.string.partly_cloudy:
                        color = resources.getColor(R.color.partly_cloudy);
                        break;
                    case R.string.cloudy:
                        color = resources.getColor(R.color.mostly_cloudy);
                        break;
                    case R.string.overcast:
                        color = resources.getColor(R.color.overcast);
                        break;
                }
            break;
        }
    }

    private void genDescription(Context context) {
        switch (type) {
            case PRECIPITATION:
                String modifier;
                if (data[1] == R.string.moderate)
                    modifier = "";
                else
                    modifier = context.getString(data[1]) + " ";

                if (data[0] == 1)
                    description = String.format("%s %s%s", context.getString(R.string.scattered), modifier, context.getString(data[2]));
                else
                    description = String.format("%s%s", modifier, context.getString(data[2]));
                break;
            case FOG:
                    description = context.getString(data[0]);
                break;
            case CLOUDS:
                description = context.getString(data[0]);
                break;
        }
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getColor() {
        return  color;
    }

    public String getDescription() {
        return description;
    }

    public void add(WeatherBlock o) {
        end = o.end;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o) {
            return true;
        }

        if (!(o instanceof WeatherBlock)) {
            return false;
        }

        WeatherBlock lhs = (WeatherBlock) o;

        return type == lhs.type && Arrays.equals(data, lhs.data);
    }

    @Override
    public String toString() {
        return String.format("type: %d %s start: %d end: %d color_holder: %d", type, description, start, end, color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(4);
        dest.writeIntArray(new int[] {type, start, end, color});
        dest.writeInt(data.length);
        dest.writeIntArray(data);
    }
}
