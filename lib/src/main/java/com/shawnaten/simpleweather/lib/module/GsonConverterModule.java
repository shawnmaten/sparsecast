package com.shawnaten.simpleweather.lib.module;

import com.google.gson.GsonBuilder;
import com.shawnaten.simpleweather.lib.model.DateDeserializer;
import com.shawnaten.simpleweather.lib.model.TimeZoneDeserializer;

import java.util.Date;
import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.converter.GsonConverter;

@Module
public class GsonConverterModule {

    @Provides
    @Singleton
    public GsonConverter providesGson(TimeZoneDeserializer timeDes, DateDeserializer dateDes) {

        return new GsonConverter(
                new GsonBuilder()
                        .registerTypeAdapter(TimeZone.class, timeDes)
                        .registerTypeAdapter(Date.class, dateDes)
                        .create());
    }
}
