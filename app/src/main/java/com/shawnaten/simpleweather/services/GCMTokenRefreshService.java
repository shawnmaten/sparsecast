package com.shawnaten.simpleweather.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMTokenRefreshService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrarService.class);
        startService(intent);
    }

}
