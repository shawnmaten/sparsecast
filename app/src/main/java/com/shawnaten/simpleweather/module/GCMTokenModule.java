package com.shawnaten.simpleweather.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.shawnaten.simpleweather.R;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class GCMTokenModule {

    @Provides
    @Named("gcmToken")
    public String providesGCMToken(SharedPreferences preferences, Context context) {
        return preferences.getString(context.getString(R.string.pref_gcm_token), null);
    }

}
