package com.shawnaten.simpleweather;

import android.widget.Toast;

public class AnalyticsManager {

    private App app;

    public AnalyticsManager(App app) {
        this.app = app;
    }

    public void registerAppEnter() {
        Toast.makeText(app, "App enter", Toast.LENGTH_LONG).show();
    }
}
