package com.shawnaten.simpleweather;

/**
 * Sparsecast for Android.
 * <p/>
 * File created by Shawn Aten on 2015/01/06.
 */
import android.app.Application;
import android.widget.Toast;

public class AnalyticsManager {

    private Application app;

    public AnalyticsManager(Application app) {
        this.app = app;
    }

    public void registerAppEnter() {
        Toast.makeText(app, "App enter", Toast.LENGTH_LONG).show();
    }
}
