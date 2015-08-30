package com.shawnaten.simpleweather.tools;

import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;

public class LocationSettings {
    public enum Mode {SAVED, CURRENT}

    private static Mode mode = Mode.CURRENT;
    private static boolean isLocationEnabled;

    private static SavedPlace savedPlace;
    private static boolean isFavorite;

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        LocationSettings.mode = mode;
    }

    public static boolean isLocationEnabled() {
        return isLocationEnabled;
    }

    public static void setIsLocationEnabled(boolean isLocationEnabled) {
        LocationSettings.isLocationEnabled = isLocationEnabled;
    }

    public static void setPlace(SavedPlace savedPlace, boolean isFavorite) {
        mode = Mode.SAVED;

        LocationSettings.savedPlace = savedPlace;
        LocationSettings.isFavorite = isFavorite;

        Attributions.setCurrentPlace(savedPlace.getAttributions());
    }

    public static String getName() {
        return savedPlace.getName();
    }

    public static double getLat() {
        return savedPlace.getLat();
    }

    public static double getLng() {
        return savedPlace.getLng();
    }

    public static boolean isFavorite() {
        return isFavorite;
    }

    public static void setIsFavorite(boolean isFavorite) {
        LocationSettings.isFavorite = isFavorite;
    }

    public static SavedPlace getSavedPlace() {
        return savedPlace;
    }

}
