package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.tools.Geocoding;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class GeocodingModule {
    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/geocode";

    @Provides
    @Singleton
    public Geocoding.Service providesGeocodingService(OkClient client) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .build().create(Geocoding.Service.class);
    }
}
