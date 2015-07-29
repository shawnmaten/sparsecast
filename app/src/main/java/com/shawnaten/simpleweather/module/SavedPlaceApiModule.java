package com.shawnaten.simpleweather.module;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SavedPlaceApiModule {

    @Provides
    @Singleton
    public SavedPlaceApi providesSavedPlacesApi(App app, GoogleAccountCredential credential) {
        SavedPlaceApi.Builder build;
        build = new SavedPlaceApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        if (app.getResources().getBoolean(R.bool.localhost))
            build.setRootUrl(app.getString(R.string.root_url));
        build.setApplicationName(app.getString(R.string.app_name));

        return build.build();
    }
}
