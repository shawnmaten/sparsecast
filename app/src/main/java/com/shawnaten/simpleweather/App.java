package com.shawnaten.simpleweather;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.tools.LocalizationSettings;
import com.shawnaten.tools.LocationSettings;

public class App extends MultiDexApplication {
    public MainComponent mainComponent;
    public GoogleAnalytics analytics;
    public Tracker tracker;

    public static String lastTracked = "";

    public static final String KIIP_FAVORITE = "favorite";

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .build();

        LocalizationSettings.configure(this);
        LocationSettings.configure();

        analytics = GoogleAnalytics.getInstance(this);

        tracker = analytics.newTracker(R.xml.analytics_global_config);
        tracker.enableAdvertisingIdCollection(true);

        /*
        Kiip kiip = Kiip.init(this, "49b1e29db28b0fa7dcd5fdda21c5cad8",
                "34c3d6ffe0030d104c944dbfabfef073");
        Kiip.setInstance(kiip);
        */
    }
}
