package com.shawnaten.simpleweather.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMTokenRefreshService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        startService(new Intent(this, GCMRegistrarService.class));
    }

}
