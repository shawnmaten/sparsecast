package com.shawnaten.simpleweather;

import android.widget.Toast;

import javax.inject.Inject;

public class AnalyticsManager {

    private App app;

    @Inject
    public AnalyticsManager(App app) {
        this.app = app;
    }

    public void registerAppEnter() {
        Toast.makeText(app, "App enter", Toast.LENGTH_LONG).show();
    }
}
