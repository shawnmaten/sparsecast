package com.shawnaten.simpleweather.lib.tools;

import com.shawnaten.simpleweather.lib.model.Forecast;

public class Precip {

    public static boolean is(String icon) {
        switch (icon) {
            case Forecast.RAIN:
            case Forecast.SNOW:
            case Forecast.SLEET:
            case Forecast.HAIL:
            case Forecast.THUNDERSTORM:
                return true;
            default:
                return false;
        }
    }
}
