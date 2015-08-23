package com.shawnaten.simpleweather.module;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.prefsAPI.PrefsAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PrefsApiModule {

    @Provides
    @Singleton
    public PrefsAPI providesPrefsAPI(Context context, GoogleAccountCredential credential) {
        PrefsAPI.Builder build;
        build = new PrefsAPI.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        build.setRootUrl(context.getString(R.string.root_url));
        build.setApplicationName(context.getString(R.string.app_name));

        return build.build();
    }
}
