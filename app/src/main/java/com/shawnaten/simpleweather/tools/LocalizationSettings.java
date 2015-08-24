package com.shawnaten.simpleweather.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.backend.prefsAPI.PrefsAPI;
import com.shawnaten.simpleweather.services.GCMRegistrarService;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@SuppressWarnings("unused")
public class LocalizationSettings {
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

    public static void configure(
            final Context context,
            final PrefsAPI prefsAPI,
            final GcmAPI gcmAPI
    ) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String unitCodeKey = context.getString(R.string.pref_units_key);

        final String oldUnitCode = prefs.getString(unitCodeKey, null);
        final String oldLangCode = prefs.getString(LANG_KEY, null);

        langCode = context.getResources().getConfiguration().locale.getLanguage().toLowerCase();

        unitCode = oldUnitCode != null && langCode.equals(oldLangCode) ? oldUnitCode :
            context.getResources().getConfiguration().locale.getCountry().toLowerCase();

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

        if (!SUPPORTED_LANGS.contains(langCode))
            langCode = "en";

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    if (!unitCode.equals(oldUnitCode)) {
                        prefsAPI.insert(unitCode).execute();
                        String key = context.getString(R.string.pref_units_key);
                        prefs.edit().putString(key, unitCode).apply();
                    }

                    if (prefs.contains(GCMRegistrarService.KEY) && !langCode.equals(oldLangCode)) {
                        String token = prefs.getString(GCMRegistrarService.KEY, "");
                        gcmAPI.update(token, token, langCode).execute();
                        prefs.edit().putString(LANG_KEY, langCode).apply();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
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
