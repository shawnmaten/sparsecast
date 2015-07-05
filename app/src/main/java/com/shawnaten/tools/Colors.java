package com.shawnaten.tools;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

import com.shawnaten.simpleweather.R;

public class Colors {
    public static int getColor(Resources res, Forecast.DataPoint dataPoint) {
        String summary = dataPoint.getSummary().toLowerCase();
        int color;

        if (summary.contains("drizzle"))
            color = res.getColor(R.color.drizzle);
        else if (summary.contains("rain")) {
            if (summary.contains("light"))
                color = res.getColor(R.color.light_rain);
            else if (summary.contains(("heavy")))
                color = res.getColor(R.color.heavy_rain);
            else
                color = res.getColor(R.color.moderate_rain);
        } else if (summary.contains("sleet")) {
            if (summary.contains("light"))
                color = res.getColor(R.color.light_sleet);
            else if (summary.contains(("heavy")))
                color = res.getColor(R.color.heavy_sleet);
            else
                color = res.getColor(R.color.moderate_sleet);
        } else if (summary.contains("flurries"))
            color = res.getColor(R.color.flurries);
        else if (summary.contains("snow")) {
            if (summary.contains("light"))
                color = res.getColor(R.color.light_snow);
            else if (summary.contains(("heavy")))
                color = res.getColor(R.color.heavy_snow);
            else
                color = res.getColor(R.color.moderate_snow);
        } else if (summary.contains("cloudy")) {
            if (summary.contains("partly"))
                color = res.getColor(R.color.partly_cloudy);
            else
                color = res.getColor(R.color.mostly_cloudy);
        } else if (summary.contains("overcast"))
            color = res.getColor(R.color.overcast);
        else
            color = res.getColor(R.color.clear);

        // the following conditions can coexist and are blended in
        if (summary.contains("foggy")) {
            color = blendColors(color, res.getColor(R.color.fog), .7f);
        }

        if (summary.contains("breezy")) {
            color = blendColors(color, res.getColor(R.color.light_wind), .7f);
        } else if (summary.contains("windy")) {
            if (summary.contains("dangerously"))
                color = blendColors(color, res.getColor(R.color.heavy_wind), .7f);
            else
                color = blendColors(color, res.getColor(R.color.moderate_wind), .7f);
        }

        return color;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }
}


