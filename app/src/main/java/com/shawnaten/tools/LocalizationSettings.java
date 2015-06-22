package com.shawnaten.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shawnaten.simpleweather.R;

public class LocalizationSettings {
    private static String langCode;
    private static String unitCode;
    private static int speedUnit;
    private static int distanceUnit;
    private static int precipitationUnit;
    private static float precipitationMax;
    private static int tempUnit;
    private static int pressureUnit;

    public static void configure(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        unitCode = preferences.getString(context.getString(R.string.units_key),
                context.getResources().getConfiguration().locale.getCountry().toLowerCase());
        langCode = context.getResources().getConfiguration().locale.getLanguage().toLowerCase();

        switch (unitCode != null ? unitCode : "si") {
            case "ca":
                speedUnit = R.string.kilometers_per_hour;
                distanceUnit = R.string.kilometers;
                break;
            case "uk":
            case "gb":
                unitCode = "uk";
                speedUnit = R.string.miles_per_hour;
                distanceUnit = R.string.kilometers;
                break;
            case "us":
                speedUnit = R.string.miles_per_hour;
                distanceUnit = R.string.miles;
                break;
            default:
                unitCode = "si";
                speedUnit = R.string.meters_per_second;
                distanceUnit = R.string.kilometers;
        }

        if (unitCode.equals("us")) {
            precipitationUnit = R.string.inches_per_hour;
            precipitationMax = 0.4f;
            tempUnit = R.string.fahrenheit;
            pressureUnit = R.string.millibars;
        } else {
            precipitationUnit = R.string.millimeters_per_hour;
            precipitationMax = 10.2f;
            tempUnit = R.string.celsius;
            pressureUnit = R.string.hectopascals;
        }

        preferences.edit().putString(context.getString(R.string.units_key), unitCode).apply();
    }

    public static String getLangCode() {
        return langCode;
    }

    public static String getUnitCode() {
        return unitCode;
    }

    public static int getSpeedUnit() {
        return speedUnit;
    }

    public static int getDistanceUnit() {
        return distanceUnit;
    }

    public static int getPrecipitationUnit() {
        return precipitationUnit;
    }

    public static float getPrecipitationMax() {
        return precipitationMax;
    }

    public static int getTempUnit() {
        return tempUnit;
    }

    public static int getPressureUnit() {
        return pressureUnit;
    }
}
