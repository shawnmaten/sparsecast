package com.shawnaten.network.modules;

import com.shawnaten.network.models.Forecast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module (
        complete = false,
        library = true
)
public class ForecastModule {
    private static final String
            ENDPOINT = "https://api.forecast.io/forecast";

    @Provides
    @Singleton
    public Forecast.Service providesForecastService(OkClient client, GsonConverter converter) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .setConverter(converter)
                .build().create(Forecast.Service.class);
    }
}
