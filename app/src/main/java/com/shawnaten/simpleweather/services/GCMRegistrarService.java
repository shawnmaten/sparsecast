package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.registrationApi.RegistrationApi;
import com.shawnaten.simpleweather.backend.registrationApi.model.GCMDeviceRecord;

import java.io.IOException;

import javax.inject.Inject;

public class GCMRegistrarService extends IntentService {

    @Inject
    RegistrationApi registrationApi;

    public GCMRegistrarService() {
        super("GCMRegistrationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((App) getApplication()).getServiceComponent().injectGCMRegistrarService(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID;
        SharedPreferences preferences;

        String gcmTokenKey = getString(R.string.pref_gcm_token);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        instanceID = InstanceID.getInstance(this);

        try {
            String oldToken = preferences.getString(gcmTokenKey, null);

            if (oldToken != null) {
                GCMDeviceRecord oldDeviceRecord = new GCMDeviceRecord();

                oldDeviceRecord.setGcmToken(oldToken);
                preferences.edit().remove(gcmTokenKey).apply();

                registrationApi.unregister(oldDeviceRecord).execute();
            }

            String newToken = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            GCMDeviceRecord newDeviceRecord = new GCMDeviceRecord();
            newDeviceRecord.setGcmToken(newToken);

            preferences.edit().putString(gcmTokenKey, newToken).apply();

            registrationApi.register(newDeviceRecord).execute();

            if (preferences.getBoolean(getString(R.string.pref_location_notify_key), false)) {
                Intent serviceIntent = new Intent(this, GeofenceService.class);
                startService(serviceIntent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
