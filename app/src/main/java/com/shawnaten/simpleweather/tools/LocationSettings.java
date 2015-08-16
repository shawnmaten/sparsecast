package com.shawnaten.simpleweather.tools;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;

public class LocationSettings {
    public enum Mode {SAVED, CURRENT}

    private static Mode mode = Mode.CURRENT;

    private static Location currentLocation;

    private static String name;
    private static double lat;
    private static double lng;
    private static String placeId;
    private static boolean isFavorite;

    public static void setPlace(Place place, CharSequence attributions) {
        mode = Mode.SAVED;
        name = place.getName().toString();
        lat = place.getLatLng().latitude;
        lng = place.getLatLng().longitude;
        placeId = place.getId();
        LocationSettings.isFavorite = false;
        if (attributions != null)
            Attributions.setCurrentPlace(attributions.toString());
        else
            Attributions.setCurrentPlace(null);
    }

    public static void setPlace(SavedPlace savedPlace) {
        mode = Mode.SAVED;
        name = savedPlace.getName();
        lat = savedPlace.getLat();
        lng = savedPlace.getLng();
        placeId = savedPlace.getPlaceId();
        LocationSettings.isFavorite = true;
        Attributions.setCurrentPlace(savedPlace.getAttributions());
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        LocationSettings.mode = mode;
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static void setCurrentLocation(Location currentLocation) {
        LocationSettings.currentLocation = currentLocation;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        LocationSettings.name = name;
    }

    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        LocationSettings.lat = lat;
    }

    public static double getLng() {
        return lng;
    }

    public static void setLng(double lng) {
        LocationSettings.lng = lng;
    }

    public static String getPlaceId() {
        return placeId;
    }

    public static void setPlaceId(String placeId) {
        LocationSettings.placeId = placeId;
    }

    public static boolean isFavorite() {
        return isFavorite;
    }

    public static void setIsFavorite(boolean isFavorite) {
        LocationSettings.isFavorite = isFavorite;
    }
}
