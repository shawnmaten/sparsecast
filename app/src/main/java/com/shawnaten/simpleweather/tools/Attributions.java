package com.shawnaten.simpleweather.tools;

import java.util.ArrayList;

public class Attributions {
    private static String currentPlace;
    private static ArrayList<String> savedPlaces = new ArrayList<>();
    private static String instagramUser;
    private static String instagramUrl;

    public static String getCurrentPlace() {
        return currentPlace;
    }

    public static void setCurrentPlace(String currentPlace) {
        Attributions.currentPlace = currentPlace;
    }

    public static ArrayList<String> getSavedPlaces() {
        return savedPlaces;
    }

    public static void setSavedPlaces(ArrayList<String> savedPlaces) {
        Attributions.savedPlaces = savedPlaces;
    }

    public static String getInstagramUser() {
        return instagramUser;
    }

    public static void setInstagramUser(String instagramUser) {
        Attributions.instagramUser = instagramUser;
    }

    public static String getInstagramUrl() {
        return instagramUrl;
    }

    public static void setInstagramUrl(String instagramUrl) {
        Attributions.instagramUrl = instagramUrl;
    }
}
