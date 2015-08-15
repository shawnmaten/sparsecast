package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.lib.model.Forecast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public class ForecastModule {
    private static final String ENDPOINT = "https://api.forecast.io/forecast";

    @Provides
    @Singleton
    public Forecast.Service provideForecastService(OkClient client, GsonConverter converter) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(converter)
                .build().create(Forecast.Service.class);
    }

}
