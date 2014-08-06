package com.shawnaten.simpleweather.backend;

/**
 * Created by Shawn Aten on 8/3/14.
 */

public class Keys {

    private String googleAPIKey;
    private String forecastAPIKey;

    public Keys(String googleAPIKey, String forecastAPIKey) {
        this.googleAPIKey = googleAPIKey;
        this.forecastAPIKey = forecastAPIKey;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public String getForecastAPIKey() {
        return forecastAPIKey;
    }

}
