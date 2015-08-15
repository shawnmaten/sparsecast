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
        String gcmKey;

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gcmKey = getString(R.string.pref_gcm_token);

        if (!preferences.contains(gcmKey)) {
            try {

                InstanceID instanceID = InstanceID.getInstance(this);
                String senderId = getString(R.string.gcm_sender_id);
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;

                String token = instanceID.getToken(senderId, scope, null);
                preferences.edit().putString(gcmKey, token).apply();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String notifyKey = getString(R.string.pref_location_notify_key);

        if (preferences.getBoolean(notifyKey, false)) {
            Intent locationServiceIntent = new Intent(this, LocationService.class);
            startService(locationServiceIntent);
        }

    }
}
