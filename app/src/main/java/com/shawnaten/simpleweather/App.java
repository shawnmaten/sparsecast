package com.shawnaten.simpleweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.services.GCMRegistrarService;
import com.shawnaten.simpleweather.tools.LocalizationSettings;

import javax.inject.Inject;

public class App extends MultiDexApplication {
    @Inject SharedPreferences prefs;

    private MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        mainComponent.inject(this);

        LocalizationSettings.configure(this);

        String gcmKey = getString(R.string.pref_gcm_token);
        String notifyKey = getString(R.string.pref_location_notify_key);

        if (!prefs.contains(gcmKey))
            startService(new Intent(this, GCMRegistrarService.class));
        //else if (prefs.getBoolean(notifyKey, false))
        //    startService(new Intent(this, LocationService.class));
    }

    public MainComponent getMainComponent() {
        return mainComponent;
    }
}
