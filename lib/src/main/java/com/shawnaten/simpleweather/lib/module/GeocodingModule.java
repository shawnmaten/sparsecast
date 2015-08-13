package com.shawnaten.simpleweather.lib.module;

import com.shawnaten.simpleweather.lib.model.Geocoding;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module
public class GeocodingModule {
    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/geocode";

    @Provides
    @Singleton
    public Geocoding.Service providesGeocodingService() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build()
                .create(Geocoding.Service.class);
    }
}
