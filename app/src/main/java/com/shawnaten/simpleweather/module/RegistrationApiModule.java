package com.shawnaten.simpleweather.module;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.registrationApi.RegistrationApi;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RegistrationApiModule {

    @Provides
    @Singleton
    public RegistrationApi GCMRegistrationAPI(Context context, GoogleAccountCredential credential) {
        RegistrationApi.Builder build;
        build = new RegistrationApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        if (context.getResources().getBoolean(R.bool.localhost)) {
            build.setRootUrl(context.getString(R.string.root_url));
            build.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                        throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
        }
        build.setApplicationName(context.getString(R.string.app_name));

        return build.build();
    }
}
