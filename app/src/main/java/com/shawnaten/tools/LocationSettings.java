package com.shawnaten.tools;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;

public class LocationSettings {
    public enum Mode {SAVED, CURRENT};

    private static Mode mode = Mode.CURRENT;

    private static boolean locationServicesAvailable = true;

    private static String name;
    private static LatLng latLng;
    private static String placeId;
    private static CharSequence attributions;
    private static SavedPlace savedPlace;

    public static void configure() {
        // check if place services is available and change settings
    }

    public static void setMode(Mode newMode) {
        if (!locationServicesAvailable && mode == Mode.CURRENT)
            mode = Mode.SAVED;
        mode = newMode;
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setPlace(Place place, SavedPlace savedPlace, CharSequence attributions) {
        mode = Mode.SAVED;
        if (place.getPlaceTypes().contains(Place.TYPE_POLITICAL))
            name = place.getAddress().toString();
        else
            name = place.getName().toString();
        latLng = place.getLatLng();
        placeId = place.getId();
        LocationSettings.savedPlace = savedPlace;
        LocationSettings.attributions = attributions;
    }

    public static String getName() {
        return name;
    }

    public static LatLng getLatLng() {
        return latLng;
    }

    public static String getPlaceId() {
        return placeId;
    }

    public static CharSequence getAttributions() {
        return attributions;
    }

    public static SavedPlace getSavedPlace() {
        return savedPlace;
    }

    public static void setSavedPlace(SavedPlace savedPlace) {
        LocationSettings.savedPlace = savedPlace;
    }
}
