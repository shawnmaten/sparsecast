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
    public Observable<Keys> providesKeysModule(App app) {
        return Observable.create(new Observable.OnSubscribe<Keys>() {
            @Override
            public void call(Subscriber<? super Keys> subscriber) {
                GoogleAccountCredential cred;
                KeysEndpoint.Builder build;
                KeysEndpoint end;

                cred = GoogleAccountCredential.usingAudience(
                        app,
                        "server:client_id:" + app.getString(R.string.WEB_ID));
                if (cred.getAllAccounts() != null)
                    cred.setSelectedAccountName(cred.getAllAccounts()[0].name);
                else
                    subscriber.onError(new Exception());

                build = new KeysEndpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        cred);
                if (app.getResources().getBoolean(R.bool.localhost))
                    build.setRootUrl(app.getString(R.string.root_url));
                build.setApplicationName(app.getString(R.string.app_name));

                end = build.build();

                try {
                    subscriber.onNext(end.getKeys().execute());
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        })
                .cache()
                .subscribeOn(Schedulers.io());
    }
}
