package com.shawnaten.simpleweather.backend.model;

/**
 * Created by Shawn Aten on 8/3/14.
 */

public class Keys {

    private String googleAPIKey;
    private String forecastAPIKey;
    private String instagramAPIKey;

    public Keys(String googleAPIKey, String forecastAPIKey, String instagramAPIKey) {
        this.googleAPIKey = googleAPIKey;
        this.forecastAPIKey = forecastAPIKey;
        this.instagramAPIKey = instagramAPIKey;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public String getForecastAPIKey() {
        return forecastAPIKey;
    }

    public String getInstagramAPIKey() {
        return instagramAPIKey;
    }
}
