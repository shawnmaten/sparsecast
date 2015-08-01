package com.shawnaten.tools;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

public class LocationSettings {
    private static Mode mode = Mode.CURRENT;
    private static boolean locationServicesAvailable = true;
    private static String name;
    private static String address;
    private static LatLng latLng;
    private static String placeId;
    private static String attributions;
    private static boolean isFavorite;

    public static void configure() {
        // check if place services is available and change settings
    }

    public static void setPlace(Place place, boolean isFavorite, CharSequence attributions) {
        mode = Mode.SAVED;
        name = place.getName().toString();
        address = place.getAddress().toString();
        if (address.regionMatches(0, name, 0, name.length())) {
            address = address.replaceFirst(name, "");
            if (address.charAt(0) == ',')
                address = address.substring(1);
            address = address.trim();
        }
        latLng = place.getLatLng();
        placeId = place.getId();
        LocationSettings.isFavorite = isFavorite;
        LocationSettings.attributions = attributions != null ? attributions.toString() : null;
    }

    public static void setIsFavorite(boolean isFavorite) {
        LocationSettings.isFavorite = isFavorite;
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode newMode) {
        if (!locationServicesAvailable && mode == Mode.CURRENT)
            mode = Mode.SAVED;
        mode = newMode;
    }

    public static String getName() {
        return name;
    }

    public static String getAddress() {
        return address;
    }

    public static LatLng getLatLng() {
        return latLng;
    }

    public static String getPlaceId() {
        return placeId;
    }

    public static String getAttributions() {
        return attributions;
    }

    public static boolean isFavorite() {
        return isFavorite;
    }

    public enum Mode {SAVED, CURRENT}
}
