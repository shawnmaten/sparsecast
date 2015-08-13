package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shawnaten.simpleweather.R;

import java.io.IOException;

public class GCMRegistrarService extends IntentService {

    public GCMRegistrarService() {
        super("GCMRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences preferences;
        String key;

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        key = getString(R.string.pref_gcm_token);

        if (!preferences.contains(key)) {
            try {

                InstanceID instanceID = InstanceID.getInstance(this);
                String senderId = getString(R.string.gcm_sender_id);
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;

                String token = instanceID.getToken(senderId, scope, null);
                preferences.edit().putString(key, token).apply();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (preferences.getBoolean(getString(R.string.pref_location_notify_key), false)) {
            Intent locationServiceIntent = new Intent(this, LocationService.class);
            startService(locationServiceIntent);
        }

    }
}
