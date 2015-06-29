package com.shawnaten.tools;

import com.shawnaten.simpleweather.R;

/*
precipType: A string representing the type of precipitation occurring at the given time. If defined, this property will have one of the following values: rain, snow, sleet (which applies to each of freezing rain, ice pellets, and “wintery mix”), or hail. (If precipIntensity is zero, then this property will not be defined.)
 */

public class Colors {
    public static int getColor(Forecast.DataPoint dataPoint) {
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
    }
}


