package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.tools.LocalizationSettings;

import java.io.IOException;

import javax.inject.Inject;

public class GCMRegistrarService extends IntentService {
    public static final String KEY = "gcmToken";

    @Inject SharedPreferences prefs;
    @Inject GcmAPI gcmAPI;
    @Inject GoogleApiClient googleApiClient;

    public GCMRegistrarService() {
        super("GCMRegistrationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerServiceComponent
                .builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build()
                .inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String notifyKey = getString(R.string.pref_location_notify_key);

        String oldToken = prefs.getString(GCMRegistrarService.KEY, null);

        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getString(R.string.gcm_sender_id);
        String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;

        stopService(new Intent(this, LocationService.class));
        prefs.edit().remove(GCMRegistrarService.KEY).apply();

        try {
            String newToken = instanceID.getToken(senderId, scope, null);
            if (oldToken != null)
                gcmAPI.update(oldToken, newToken, LocalizationSettings.getLangCode()).execute();
            else
                gcmAPI.insert(newToken, LocalizationSettings.getLangCode()).execute();
            prefs.edit().putString(GCMRegistrarService.KEY, newToken).apply();
            if (prefs.getBoolean(notifyKey, false))
                LocationService2.start(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
