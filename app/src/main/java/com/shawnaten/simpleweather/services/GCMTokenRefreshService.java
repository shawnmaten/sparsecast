package com.shawnaten.simpleweather.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.shawnaten.simpleweather.R;

public class GCMTokenRefreshService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {

        SharedPreferences preferences;
        String key;

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        key = getString(R.string.pref_gcm_token);

        preferences.edit().putStringSet(key, null).apply();

        Intent intent = new Intent(this, GCMRegistrarService.class);
        startService(intent);
    }

}
