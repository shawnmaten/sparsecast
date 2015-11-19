package com.shawnaten.simpleweather;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.backend.prefsAPI.PrefsAPI;
import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import javax.inject.Inject;

public class App extends Application {
    @Inject SharedPreferences prefs;
    @Inject GoogleApiClient googleApiClient;
    @Inject GoogleAccountCredential googleAccountCredential;
    @Inject GcmAPI gcmAPI;
    @Inject PrefsAPI prefsAPI;

    private MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        mainComponent.inject(this);

        /*
        String notifyKey = getString(R.string.pref_location_notify_key);

        LocalizationSettings.configure(this, prefsAPI, gcmAPI);

        if (!prefs.contains(GCMRegistrarService.KEY))
            startService(new Intent(this, GCMRegistrarService.class));
        else if (prefs.getBoolean(notifyKey, false))
            LocationService2.start(this);*/
    }

    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LocalizationSettings.configure(this, prefsAPI, gcmAPI);
    }*/

    public MainComponent getMainComponent() {
        return mainComponent;
    }
}
