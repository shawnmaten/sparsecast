package com.shawnaten.simpleweather.backend.module;

import com.shawnaten.simpleweather.backend.model.Slack;
import com.shawnaten.simpleweather.lib.model.Forecast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module
public class SlackModule {
    private static final String ENDPOINT = "https://hooks.slack.com/services";

    @Provides
    @Singleton
    public Slack.Service providesSlackService() {
        return new RestAdapter.Builder()
                .setEndpoint(Forecast.ENDPOINT)
                .build()
                .create(Slack.Service.class);
    }

}
