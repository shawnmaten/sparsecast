package com.shawnaten.simpleweather.tools;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;

import java.io.IOException;

public class GCMSettings {
    public static final String GCM_ID = "gcmId";
    public static String regId;

    public static String configure(final App app) throws IOException {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);

        if (prefs.contains(GCM_ID)) {
            regId = prefs.getString(GCM_ID, null);
        } else {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
            regId = gcm.register(app.getString(R.string.gcm_sender_id));

            app.mainComponent.registrationApi().register(regId).execute();

            prefs.edit().putString(GCM_ID, regId).apply();
        }

        return "Device registered with GCM id: " + regId;

        /*
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(app, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String s) {
                Toast.makeText(app, s, Toast.LENGTH_SHORT).show();
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
                    regId = gcm.register(app.getString(R.string.gcm_sender_id));

                    app.mainComponent.registrationApi().register(regId).execute();

                    prefs.edit().putString(GCM_ID, regId).apply();
                    subscriber.onNext("Device registered with GCM id: " + regId);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable(e.getMessage()));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(subscriber);
        */
    }
}
