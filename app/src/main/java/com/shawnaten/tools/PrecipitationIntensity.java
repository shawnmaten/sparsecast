package com.shawnaten.tools;

public class PrecipitationIntensity {
    public static final int VERY_LIGHT = 0;
    public static final int LIGHT = 1;
    public static final int MODERATE = 2;
    public static final int HEAVY = 3;

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
}
