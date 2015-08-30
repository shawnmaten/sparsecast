package com.shawnaten.simpleweather.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GCMTokenRefreshService extends InstanceIDListenerService {
    @Inject GcmAPI gcmAPI;
    @Inject SharedPreferences prefs;

    @Override
    public void onTokenRefresh() {
        final Context context = getApplicationContext();
        DaggerServiceComponent.builder()
                .contextModule(new ContextModule(context))
                .build()
                .inject(this);

        final String gcmTokenKey = getString(R.string.pref_gcm_token);
        final String oldToken = prefs.getString(gcmTokenKey, "");
        prefs.edit().remove(gcmTokenKey).apply();

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                InstanceID instanceID = InstanceID.getInstance(context);
                String senderId = getString(R.string.gcm_sender_id);
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;

                try {
                    String newToken = instanceID.getToken(senderId, scope, null);
                    gcmAPI.updateToken(newToken, oldToken);
                    prefs.edit().putString(gcmTokenKey, newToken).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
