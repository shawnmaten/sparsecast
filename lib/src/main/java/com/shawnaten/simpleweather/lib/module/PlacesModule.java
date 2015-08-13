package com.shawnaten.simpleweather.lib.module;

import com.shawnaten.simpleweather.lib.model.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module
public class PlacesModule {
    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/place";

    @Provides
    @Singleton
    public Places.DetailsService providesPlacesDetailService() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build()
                .create(Places.DetailsService.class);
    }
}
