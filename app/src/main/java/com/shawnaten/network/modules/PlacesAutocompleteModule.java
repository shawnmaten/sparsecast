package com.shawnaten.network.modules;

import com.shawnaten.network.models.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module (
        complete = false,
        library = true
)
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
