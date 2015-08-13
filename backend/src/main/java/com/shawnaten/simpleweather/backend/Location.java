package com.shawnaten.simpleweather.backend;

public class Location {

    private String gcmToken;

    private double lat, lng;

    public Location(String gcmToken, double lat, double lng) {
        this.gcmToken = gcmToken;
        this.lat = lat;
        this.lng = lng;
    }

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
