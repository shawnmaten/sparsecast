package com.shawnaten.simpleweather.lib.module;

import com.shawnaten.simpleweather.lib.model.Slack;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

@Module
public class SlackModule {
    private static final String ENDPOINT = "https://hooks.slack.com/services";

    private static final Logger log = Logger.getLogger(SlackModule.class.getName());

    @Provides
    @Singleton
    public Slack.Service providesSlackService() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        log.setLevel(Level.WARNING);
                        log.warning(cause.getUrl());
                        return cause;
                    }
                })
                .build()
                .create(Slack.Service.class);
    }

}
