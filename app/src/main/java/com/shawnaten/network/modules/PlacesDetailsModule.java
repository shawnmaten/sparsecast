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
public class PlacesDetailsModule {
    private static final String
            ENDPOINT = "https://maps.googleapis.com/maps/api/place/details";

    @Provides
    @Singleton
    public Places.DetailsService providesPlacesAutocompleteService(OkClient client) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .build().create(Places.DetailsService.class);
    }
}
