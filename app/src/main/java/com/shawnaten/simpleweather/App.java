package com.shawnaten.simpleweather;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.component.ServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.services.GCMRegistrarService;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;

public class App extends MultiDexApplication {

    public MainComponent mainComponent;
    private ServiceComponent serviceComponent;

    public GoogleAnalytics analytics;
    public Tracker tracker;

    public static String lastTracked = "";

    @Override
    public void onCreate() {
        super.onCreate();

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

        Intent gcmRegistrarServiceIntent = new Intent(this, GCMRegistrarService.class);
        startService(gcmRegistrarServiceIntent);

    }

    public ServiceComponent getServiceComponent() {
        return serviceComponent;
    }
}
