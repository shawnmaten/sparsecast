package com.shawnaten.simpleweather.backend.model;

public class LocationRecord {

    private String gcmToken;

    private double lat, lng;

    public String getGcmToken() {
        return gcmToken;
    }

    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }

}
