package com.shawnaten.tools;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import me.kiip.sdk.Kiip;

/**
 * Created by Shawn Aten on 8/22/14.
 */
public class App extends Application {
    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }

    private static final String TRACKING_ID = "UA-54261604-1";

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Kiip kiip = Kiip.init(this, "288859cf6300709e69efadb87b8c86e8", "26103017c52cb131e3569749db04f5cd");
        Kiip.setInstance(kiip);

    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(TRACKING_ID);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
