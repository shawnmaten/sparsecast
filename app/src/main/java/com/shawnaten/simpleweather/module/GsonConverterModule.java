package com.shawnaten.simpleweather.module;

import android.net.Uri;

import com.google.gson.GsonBuilder;
import com.shawnaten.simpleweather.tools.DateDeserializer;
import com.shawnaten.simpleweather.tools.TimeZoneDeserializer;
import com.shawnaten.simpleweather.tools.UriDeserializer;

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
    public GsonConverter providesGson(
            TimeZoneDeserializer timeZoneDeserializer,
            DateDeserializer dateDeserializer,
            UriDeserializer uriDeserializer) {

        return new GsonConverter(
                new GsonBuilder()
                        .registerTypeAdapter(TimeZone.class, timeZoneDeserializer)
                        .registerTypeAdapter(Date.class, dateDeserializer)
                        .registerTypeAdapter(Uri.class, uriDeserializer)
                        .create());
    }
}
