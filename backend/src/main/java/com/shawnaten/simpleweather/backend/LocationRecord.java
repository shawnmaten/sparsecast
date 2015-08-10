package com.shawnaten.simpleweather.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class LocationRecord {

    @Id
    private Long id;

    @Index
    private String gcmToken;

    private double lat, lng;

    // for edge case where we have long continuous periods of precipitation
    private boolean lastWasPrecip;

    public Long getId() {
        return id;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isLastWasPrecip() {
        return lastWasPrecip;
    }

    public void setLastWasPrecip(boolean lastWasPrecip) {
        this.lastWasPrecip = lastWasPrecip;
    }
}
