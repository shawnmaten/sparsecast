package com.shawnaten.simpleweather.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

@Module
public class ReactiveLocationProviderModule {

    @Provides
    @Singleton
    public ReactiveLocationProvider providesReactiveLocationProvider(Context context) {
        return  new ReactiveLocationProvider(context);
    }
}
