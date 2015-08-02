package com.shawnaten.simpleweather.module;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.keysEndpoint.KeysEndpoint;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@Module
public class KeysModule {
    @Provides
    @Singleton
    public KeysEndpoint providesKeysApi(App app, GoogleAccountCredential credential) {
        KeysEndpoint.Builder build;

        build = new KeysEndpoint.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        if (app.getResources().getBoolean(R.bool.localhost))
            build.setRootUrl(app.getString(R.string.root_url));
        build.setApplicationName(app.getString(R.string.app_name));

        return build.build();
    }

    @Provides
    @Singleton
    public Observable<Keys> providesKeysModule(final KeysEndpoint endpoint) {
        return Observable.create(new Observable.OnSubscribe<Keys>() {
            private Keys keys;

            @Override
            public void call(Subscriber<? super Keys> subscriber) {
                if (keys == null) {
                    try {
                        keys = endpoint.getKeys().execute();
                        subscriber.onNext(keys);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    subscriber.onNext(keys);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
