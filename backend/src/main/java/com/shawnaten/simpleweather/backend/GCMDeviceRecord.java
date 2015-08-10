package com.shawnaten.simpleweather.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class GCMDeviceRecord {

    @Id
    Long id;

    @Index
    private String userId;

    @Index
    private String gcmToken;

    private boolean currentLocationNotify = false;

    private String locationTaskName;

    private String forecastTaskName;

    private long delay;

    public GCMDeviceRecord() {

    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public boolean isCurrentLocationNotify() {
        return currentLocationNotify;
    }

    public void setCurrentLocationNotify(boolean currentLocationNotify) {
        this.currentLocationNotify = currentLocationNotify;
    }

    public String getLocationTaskName() {
        return locationTaskName;
    }

    public void setLocationTaskName(String locationTaskName) {
        this.locationTaskName = locationTaskName;
    }

    public String getForecastTaskName() {
        return forecastTaskName;
    }

    public void setForecastTaskName(String forecastTaskName) {
        this.forecastTaskName = forecastTaskName;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GCMDeviceRecord)) {
            return false;
        }

        GCMDeviceRecord lhs = (GCMDeviceRecord) o;

        return userId.equals(lhs.userId) && gcmToken.equals(lhs.gcmToken);
    }

}