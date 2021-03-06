package com.shawnaten.simpleweather.tools;

import com.shawnaten.simpleweather.R;

public class ForecastIconSelector {
    // don't have proper image for hail or tornado
    public static int getImageId(String iconString) {
        switch (iconString) {
            case "clear-day":
                return R.drawable.clear_day;
            case "clear-night":
                return R.drawable.clear_night;
            case "rain":
                return R.drawable.rain;
            case "snow":
                return R.drawable.snow;
            case "sleet":
                return  R.drawable.sleet;
            case "wind":
                return R.drawable.wind;
            case "fog":
                return R.drawable.fog;
            case "cloudy":
                return R.drawable.cloudy;
            case "partly-cloudy-day":
                return R.drawable.partly_cloudy_day;
            case "partly-cloudy-night":
                return R.drawable.partly_cloudy_night;
            case "hail":
                return R.drawable.rain;
            case "thunderstorm":
                return R.drawable.thunderstorm;
            case "tornado":
                return R.drawable.wind;
            default:
                return R.drawable.cloudy;
        }
    }

    public static int getNotifyIcon(String iconString) {
        switch (iconString) {
            case "rain":
                return R.drawable.notify_rain;
            case "snow":
                return R.drawable.notify_snow;
            case "sleet":
                return R.drawable.notify_sleet;
            case "hail":
                return R.drawable.notify_hail;
            case "thunderstorm":
                return R.drawable.notify_thunderstorm;
            case "tornado":
                return R.drawable.notify_tornado;
            default:
                return R.drawable.notify_rain;
        }
    }
}
