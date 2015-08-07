package com.shawnaten.simpleweather.tools;

import android.content.res.Resources;
import android.graphics.Color;

import com.shawnaten.simpleweather.R;

public class Precipitation {
    public static final int VERY_LIGHT = 0;
    public static final int LIGHT = 1;
    public static final int MODERATE = 2;
    public static final int HEAVY = 3;
    public static final int CLEAR = 0;
    public static final int SCATTERED = 1;
    public static final int BROKEN = 2;
    public static final int OVERCAST = 3;

    public static int getIntensityCode(double intensity) {
        if (intensity >= LocalizationSettings.getPrecipitationMed()) {
            double distToHeavy = LocalizationSettings.getPrecipitationHeavy() - intensity;
            double distToMod = intensity - LocalizationSettings.getPrecipitationMed();

            if (distToHeavy < distToMod)
                return HEAVY;
            else
                return MODERATE;
        } else if (intensity >= LocalizationSettings.getPrecipitationLight()) {
            double distToMod = LocalizationSettings.getPrecipitationMed() - intensity;
            double distToLight = intensity - LocalizationSettings.getPrecipitationLight();

            if (distToMod < distToLight)
                return MODERATE;
            else
                return LIGHT;
        } else {
            double distToLight = LocalizationSettings.getPrecipitationLight() - intensity;

            if (distToLight < intensity)
                return LIGHT;
            else
                return VERY_LIGHT;
        }
    }

    public static int getCloudCode(double coverage) {
        if (coverage >= .75) {
            double distToOvercast = 1 - coverage;
            double distToMod = coverage - .75;

            if (distToOvercast < distToMod)
                return OVERCAST;
            else
                return BROKEN;
        } else if (coverage >= .4) {
            double distToBroken = .75 - coverage;
            double distToScattered = coverage - .4;

            if (distToBroken < distToScattered)
                return BROKEN;
            else
                return SCATTERED;
        } else {
            double distToScattered = .4 - coverage;

            if (distToScattered < coverage)
                return SCATTERED;
            else
                return CLEAR;
        }
    }

    public static Response evaluate(Resources res, Forecast.DataPoint dataPoint) {
        Response response = new Response();

        if (dataPoint.getPrecipProbability() >= .3) {
            switch (dataPoint.getPrecipType()) {
                case "rain":
                    switch (getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case HEAVY:
                            response.color = res.getColor(R.color.heavy_rain);
                            response.summary = res.getString(R.string.heavy_rain);
                            break;
                        case MODERATE:
                            response.color = res.getColor(R.color.moderate_rain);
                            response.summary = res.getString(R.string.moderate_rain);
                            break;
                        case LIGHT:
                            response.color = res.getColor(R.color.light_rain);
                            response.summary = res.getString(R.string.light_rain);
                            break;
                        default:
                            response.color = res.getColor(R.color.drizzle);
                            response.summary = res.getString(R.string.drizzle);
                    }
                    break;
                case "snow":
                    switch (getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case HEAVY:
                            response.color = res.getColor(R.color.heavy_snow);
                            response.summary = res.getString(R.string.heavy_snow);
                            break;
                        case MODERATE:
                            response.color = res.getColor(R.color.moderate_snow);
                            response.summary = res.getString(R.string.moderate_snow);
                            break;
                        default:
                            response.color = res.getColor(R.color.light_snow);
                            response.summary = res.getString(R.string.light_snow);
                    }
                    break;
                case "sleet":
                    switch (getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case HEAVY:
                            response.color = res.getColor(R.color.heavy_sleet);
                            response.summary = res.getString(R.string.heavy_sleet);
                        case MODERATE:
                            response.color = res.getColor(R.color.moderate_sleet);
                            response.summary = res.getString(R.string.moderate_sleet);
                        default:
                            response.color = res.getColor(R.color.light_sleet);
                            response.summary = res.getString(R.string.light_sleet);
                    }
                    break;
                case "hail":
                    switch (getIntensityCode(dataPoint.getPrecipIntensity())) {
                        case HEAVY:
                            response.color = res.getColor(R.color.hail);
                            response.summary = res.getString(R.string.hail);
                        case MODERATE:
                            response.color = res.getColor(R.color.hail);
                            response.summary = res.getString(R.string.hail);
                        default:
                            response.color = res.getColor(R.color.hail);
                            response.summary = res.getString(R.string.hail);
                    }
                    break;
                default:
                    response.color = res.getColor(R.color.clear);
                    response.summary = res.getString(R.string.clear);
            }
        } else {
            switch (getCloudCode(dataPoint.getCloudCover())) {
                case OVERCAST:
                    response.color = res.getColor(R.color.overcast);
                    response.summary = res.getString(R.string.overcast);
                    break;
                case BROKEN:
                    response.color = res.getColor(R.color.mostly_cloudy);
                    response.summary = res.getString(R.string.mostly_cloudy);
                    break;
                case SCATTERED:
                    response.color = res.getColor(R.color.partly_cloudy);
                    response.summary = res.getString(R.string.partly_cloudy);
                    break;
                default:
                    response.color = res.getColor(R.color.clear);
                    response.summary = res.getString(R.string.clear);
            }
        }
        return response;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public static class Response {
        public int color;
        public String summary;
    }
}


