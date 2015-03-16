package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.model.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class PlacesAutocompleteModule {
    private static final String
            ENDPOINT = "https://maps.googleapis.com/maps/api/place/autocomplete";

    @Provides
    @Singleton
    public Places.AutoCompleteService providesPlacesAutocompleteService(OkClient client) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .build().create(Places.AutoCompleteService.class);
    }
}
