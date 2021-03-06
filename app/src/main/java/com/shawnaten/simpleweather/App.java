package com.shawnaten.simpleweather;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.bugsnag.android.Bugsnag;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.instabug.library.Instabug;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.backend.prefsAPI.PrefsAPI;
import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import javax.inject.Inject;

public class App extends MultiDexApplication {
    @Inject SharedPreferences prefs;
    @Inject GoogleApiClient googleApiClient;
    @Inject GoogleAccountCredential googleAccountCredential;
    @Inject GcmAPI gcmAPI;
    @Inject PrefsAPI prefsAPI;

    private MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Bugsnag.init(this);

        mainComponent = DaggerMainComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        mainComponent.inject(this);

        //Bugsnag.addToTab("User", "Email", googleAccountCredential.getAllAccounts()[0].name);

//        Instabug.initialize(this, "b35476559976660ba01ceb378d76f6d5");
//        Instabug.getInstance().setShowIntroDialog(false);

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
