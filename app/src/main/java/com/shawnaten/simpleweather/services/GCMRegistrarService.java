package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import java.io.IOException;

import javax.inject.Inject;

public class GCMRegistrarService extends IntentService {
    @Inject SharedPreferences prefs;
    @Inject GcmAPI gcmAPI;

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
        String gcmKey = getString(R.string.pref_gcm_token);
        String notifyKey = getString(R.string.pref_location_notify_key);
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getString(R.string.gcm_sender_id);
        String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;

        try {
            String token = instanceID.getToken(senderId, scope, null);
            gcmAPI.insert(token).execute();
            prefs.edit().putString(gcmKey, token).apply();
            if (prefs.getBoolean(notifyKey, false))
                startService(new Intent(this, LocationService.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
