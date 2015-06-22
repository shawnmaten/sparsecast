package com.shawnaten.simpleweather.module;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ImagesApiModule {
    @Provides
    @Singleton
    public ImagesApi providesImagesApi(App app, GoogleAccountCredential credential) {
        ImagesApi.Builder build;
        build = new ImagesApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        if (app.getResources().getBoolean(R.bool.localhost))
            build.setRootUrl(app.getString(R.string.root_url));
        build.setApplicationName(app.getString(R.string.app_name));

        return build.build();
    }
}
