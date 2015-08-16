package com.shawnaten.simpleweather.module;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.tools.AnalyticsCodes;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public class ForecastModule {
    private static final String ENDPOINT = "https://api.forecast.io/forecast";

    @Provides
    @Singleton
    public Forecast.Service provideForecastService(OkClient client, GsonConverter converter,
                                                   final Tracker tracker)
    {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setProfiler(new Profiler() {
                    @Override
                    public Object beforeCall() {
                        return null;
                    }

                    @Override
                    public void afterCall(RequestInformation requestInfo, long elapsedTime,
                                          int statusCode, Object beforeCallData) {

                        Map<String, String> hit = new HitBuilders.TimingBuilder()
                                .setCategory(AnalyticsCodes.CATEGORY_FORECAST_LOAD)
                                .setValue(elapsedTime)
                                .build();

                        tracker.send(hit);
                    }
                })
                .setClient(client)
                .setConverter(converter)
                .build().create(Forecast.Service.class);
    }

}
