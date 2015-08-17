package com.shawnaten.simpleweather.tools;

import com.google.android.gms.location.places.Place;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;

public class LocationSettings {
    public enum Mode {SAVED, CURRENT}

    private static Mode mode = Mode.CURRENT;

    private static SavedPlace savedPlace;
    private static Place place;
    private static boolean isFavorite;

    public static void setPlace(SavedPlace savedPlace, boolean isFavorite) {
        mode = Mode.SAVED;

        LocationSettings.savedPlace = savedPlace;
        LocationSettings.place = null;
        LocationSettings.isFavorite = isFavorite;

        Attributions.setCurrentPlace(savedPlace.getAttributions());
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        LocationSettings.mode = mode;
    }

    public static String getName() {
        if (savedPlace != null)
            return savedPlace.getName();
        return place.getName().toString();
    }

    public static double getLat() {
        if (savedPlace != null)
            return savedPlace.getLat();
        return place.getLatLng().latitude;
    }

    public static double getLng() {
        if (savedPlace != null)
            return savedPlace.getLng();
        return place.getLatLng().longitude;
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
