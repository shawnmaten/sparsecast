package com.shawnaten.tools;

import android.util.Log;

import com.shawnaten.simpleweather.R;

/*
precipType: A string representing the type of precipitation occurring at the given time. If defined, this property will have one of the following values: rain, snow, sleet (which applies to each of freezing rain, ice pellets, and “wintery mix”), or hail. (If precipIntensity is zero, then this property will not be defined.)
 */

public class Colors {
    public static int getColor(Forecast.DataPoint dataPoint) {
        String summary = dataPoint.getSummary().toLowerCase();

        if (summary.contains("clear"))
            return R.color.clear;
        else if (summary.contains("drizzle"))
            return R.color.drizzle;
        else if (summary.contains("light rain"))
            return R.color.light_rain;
        else if (summary.contains("rain"))
            return R.color.moderate_rain;
        else if (summary.contains("heavy rain"))
            return R.color.heavy_rain;
        else if (summary.contains("light sleet"))
            return R.color.light_sleet;
        else if (summary.contains("sleet"))
            return R.color.moderate_sleet;
        else if (summary.contains("heavy sleet"))
            return R.color.heavy_sleet;
        else if (summary.contains("flurries"))
            return R.color.flurries;
        else if (summary.contains("light snow"))
            return R.color.light_snow;
        else if (summary.contains("snow"))
            return R.color.moderate_snow;
        else if (summary.contains("heavy snow"))
            return R.color.heavy_snow;
        else if (summary.contains("foggy"))
            return R.color.fog;
        else if (summary.contains("partly cloudy"))
            return R.color.partly_cloudy;
        else if (summary.contains("mostly cloudy"))
            return R.color.mostly_cloudy;
        else if (summary.contains("overcast"))
            return R.color.overcast;
        else {
            Log.e("Colors", "No Match");
            return R.color.clear;
        }

        /*
        if (dataPoint.getPrecipProbability() >= .2) {
            switch (dataPoint.getIcon()) {
                case "rain":
                    switch (PrecipitationIntensity.getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case PrecipitationIntensity.HEAVY:
                            return R.color.heavy_rain;
                        case PrecipitationIntensity.MODERATE:
                            return R.color.moderate_rain;
                        case PrecipitationIntensity.LIGHT:
                            return R.color.light_rain;
                        case PrecipitationIntensity.VERY_LIGHT:
                            return R.color.drizzle;
                    }
                case "snow":
                    switch (PrecipitationIntensity.getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case PrecipitationIntensity.HEAVY:
                            return R.color.heavy_snow;
                        case PrecipitationIntensity.MODERATE:
                            return R.color.moderate_snow;
                        case PrecipitationIntensity.LIGHT:
                        case PrecipitationIntensity.VERY_LIGHT:
                            return R.color.light_snow;
                    }
                case "sleet":
                    switch (PrecipitationIntensity.getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case PrecipitationIntensity.HEAVY:
                            return R.color.heavy_sleet;
                        case PrecipitationIntensity.MODERATE:
                            return R.color.moderate_sleet;
                        case PrecipitationIntensity.LIGHT:
                        case PrecipitationIntensity.VERY_LIGHT:
                            return R.color.light_sleet;
                    }
                case "hail":
                    switch (PrecipitationIntensity.getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case PrecipitationIntensity.HEAVY:
                            return R.color.hail;
                        case PrecipitationIntensity.MODERATE:
                            return R.color.hail;
                        case PrecipitationIntensity.LIGHT:
                        case PrecipitationIntensity.VERY_LIGHT:
                            return R.color.hail;
                    }
                default:
                    return R.color.clear;
            }
        } else {
            switch (CloudCover.getCloudCode(dataPoint.getCloudCover())) {
                case CloudCover.OVERCAST:
                    return R.color.overcast;
                case CloudCover.BROKEN:
                    return R.color.mostly_cloudy;
                case CloudCover.SCATTERED:
                    return R.color.partly_cloudy;
                default:
                    return R.color.clear;
            }
        }
        */
    }
}


