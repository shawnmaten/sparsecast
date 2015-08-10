package com.shawnaten.simpleweather.backend.module;

import com.shawnaten.simpleweather.lib.model.Forecast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module
public class ForecastModule {

    @Provides
    @Singleton
    public Forecast.Service providesForecastService(GsonConverter converter) {
        return new RestAdapter.Builder()
                .setEndpoint(Forecast.ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(converter)
                .build().create(Forecast.Service.class);
    }

}
