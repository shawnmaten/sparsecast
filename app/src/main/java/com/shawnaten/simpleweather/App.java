package com.shawnaten.simpleweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.component.ServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.services.GCMRegistrarService;
import com.shawnaten.simpleweather.services.GeofenceService;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;

public class App extends MultiDexApplication {
    public MainComponent mainComponent;
    private ServiceComponent serviceComponent;
    public GoogleAnalytics analytics;
    public Tracker tracker;

    public static String lastTracked = "";

    public static final String KIIP_FAVORITE = "favorite";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mainComponent = DaggerMainComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        serviceComponent = DaggerServiceComponent
                .builder().contextModule(new ContextModule(this))
                .build();

        LocalizationSettings.configure(this);
        LocationSettings.configure();

        analytics = GoogleAnalytics.getInstance(this);

        tracker = analytics.newTracker(R.xml.analytics_global_config);
        tracker.enableAdvertisingIdCollection(true);

        if (!preferences.contains(getString(R.string.pref_gcm_token))) {
            Intent intent = new Intent(this, GCMRegistrarService.class);
            startService(intent);
        }

        if (preferences.getBoolean(getString(R.string.pref_location_notify_key), false)) {
            Intent intent = new Intent(this, GeofenceService.class);
            startService(intent);
        }

        /*
        Kiip kiip = Kiip.init(this, "49b1e29db28b0fa7dcd5fdda21c5cad8",
                "34c3d6ffe0030d104c944dbfabfef073");
        Kiip.setInstance(kiip);
        */
    }

    public ServiceComponent getServiceComponent() {
        return serviceComponent;
    }
}
