package com.shawnaten.simpleweather.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.shawnaten.simpleweather.R;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("unused")
public class LocaleSettings {
    private static final ArrayList<String> SUPPORTED_LANGS = new ArrayList<>();
    static {
        SUPPORTED_LANGS.add("ar");
        SUPPORTED_LANGS.add("bs");
        SUPPORTED_LANGS.add("de");
        SUPPORTED_LANGS.add("en");
        SUPPORTED_LANGS.add("es");
        SUPPORTED_LANGS.add("fr");
        SUPPORTED_LANGS.add("it");
        SUPPORTED_LANGS.add("nl");
        SUPPORTED_LANGS.add("pl");
        SUPPORTED_LANGS.add("pt");
        SUPPORTED_LANGS.add("ru");
        SUPPORTED_LANGS.add("sk");
        SUPPORTED_LANGS.add("sv");
        SUPPORTED_LANGS.add("tet");
        SUPPORTED_LANGS.add("tr");
        SUPPORTED_LANGS.add("uk");
        SUPPORTED_LANGS.add("zh");
    }

    private static final String LANG_KEY = "lang";

    private static String langCode;
    private static String unitCode;
    private static int speedUnit;
    private static int distanceUnit;
    private static int precipitationUnit;
    private static int precipitationUnitTime;
    private static double precipitationLight;
    private static double precipitationMed;
    private static double precipitationHeavy;
    private static int pressureUnit;

    public static boolean configure(final Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String unitCodeKey = context.getString(R.string.pref_units_key);

        // Need these to see if something has changed
        final String oldUnitCode = prefs.getString(unitCodeKey, null);
        final String oldLangCode = prefs.getString(LANG_KEY, null);

        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = configuration.locale;

        // Should always try to use current system language
        langCode = locale.getLanguage().toLowerCase();
        // Unless it's null or the language has changed, we want to use the setting
        unitCode = oldUnitCode;

        // If the user has changed their language, we may want to adjust the units they see
        // Example: User changes from US English to GB English, they probably want different units
        if (!langCode.equals(oldLangCode))
            unitCode = null;

        // Want to save this here because we might change it to the default
        // If this language isn't supported by the API, we default to English
        // But we don't want to detect a fake change each time
        prefs.edit().putString(LANG_KEY, langCode).apply();
        if (!SUPPORTED_LANGS.contains(langCode))
            langCode = "en";

        // Pick a default set of units based on device's set locale
        // Example: us for United States, gb for Great Britain
        if (unitCode == null)
            unitCode = locale.getCountry().toLowerCase();

        switch (unitCode) {
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

        prefs.edit().putString(unitCodeKey, unitCode).apply();

        if (unitCode.equals("us")) {
            precipitationUnit = R.string.inches;
            precipitationUnitTime = R.string.inches_per_hour;
            precipitationLight = 0.017;
            precipitationMed = 0.1;
            precipitationHeavy = 0.4;
            pressureUnit = R.string.millibars;
        } else {
            precipitationUnit = R.string.millimeters;
            precipitationUnitTime = R.string.millimeters_per_hour;
            precipitationLight = 0.432;
            precipitationMed = 2.54;
            precipitationHeavy = 10.2;
            pressureUnit = R.string.hectopascals;
        }

        return !(langCode.equals(oldLangCode) && unitCode.equals(oldUnitCode));
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

    public static int getPrecipitationUnitTime() {
        return precipitationUnitTime;
    }

    public static double getPrecipitationLight() {
        return precipitationLight;
    }

    public static double getPrecipitationMed() {
        return precipitationMed;
    }

    public static double getPrecipitationHeavy() {
        return precipitationHeavy;
    }

    public static int getPressureUnit() {
        return pressureUnit;
    }
}
