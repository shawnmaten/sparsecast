package com.shawnaten.simpleweather.module;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.registrationApi.RegistrationApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RegistrationApiModule {

    @Provides
    @Singleton
    public RegistrationApi GCMRegistrationAPI(App app, GoogleAccountCredential credential) {
        RegistrationApi.Builder build;
        build = new RegistrationApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        if (app.getResources().getBoolean(R.bool.localhost))
            build.setRootUrl(app.getString(R.string.root_url));
        build.setApplicationName(app.getString(R.string.app_name));

        return build.build();
    }
}
