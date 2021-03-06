package com.shawnaten.simpleweather.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;

@Entity
public class GCMRecord implements Serializable {

    @Id private Long id;

    @Index private String userId;
    @Index private String gcmToken;
    private String langCode;
    private String unitCode;
    private String forecastTask;
    private String locationTask;

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

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getForecastTask() {
        return forecastTask;
    }

    public void setForecastTask(String forecastTask) {
        this.forecastTask = forecastTask;
    }

    public String getLocationTask() {
        return locationTask;
    }

    public void setLocationTask(String locationTask) {
        this.locationTask = locationTask;
    }
}
