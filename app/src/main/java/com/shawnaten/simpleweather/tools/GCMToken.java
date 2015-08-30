package com.shawnaten.simpleweather.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GCMToken {
    public static Observable<Void> configure(final Context context, final GcmAPI gcmAPI) {
        final String gcmTokenKey = context.getString(R.string.pref_gcm_token);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final boolean localeChange = LocaleSettings.configure(context);

        final String oldToken = prefs.getString(gcmTokenKey, null);

        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (oldToken != null && !localeChange) {
                    subscriber.onCompleted();
                } else if (oldToken != null) {
                    try {
                        gcmAPI.updatePrefs(
                                LocaleSettings.getLangCode(),
                                oldToken,
                                LocaleSettings.getUnitCode()
                        ).execute();
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                } else {
                    InstanceID instanceID = InstanceID.getInstance(context);
                    String senderId = context.getString(R.string.gcm_sender_id);
                    String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                    try {
                        String newToken = instanceID.getToken(senderId, scope, null);
                        gcmAPI.create(
                                LocaleSettings.getLangCode(),
                                newToken,
                                LocaleSettings.getUnitCode()
                        ).execute();
                        prefs.edit().putString(gcmTokenKey, newToken).apply();
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
