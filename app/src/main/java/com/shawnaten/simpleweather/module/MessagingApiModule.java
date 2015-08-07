package com.shawnaten.simpleweather.module;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.messagingApi.MessagingApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MessagingApiModule {

    @Provides
    @Singleton
    public MessagingApi providesMessagingAPI(App app, GoogleAccountCredential credential) {
        MessagingApi.Builder build;
        build = new MessagingApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        if (app.getResources().getBoolean(R.bool.localhost))
            build.setRootUrl(app.getString(R.string.root_url));
        build.setApplicationName(app.getString(R.string.app_name));

        return build.build();
    }
}
