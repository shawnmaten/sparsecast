package com.shawnaten.networking;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by shawnaten on 7/17/14.
 */
public class Network {
    private static Network instance;
    private Forecast.Service forecastService;
    private Places.Service placesService, detailsService;

    private Network(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient();

        File networkCacheFile = new File(context.getCacheDir(), "networkCache");

        Cache networkCache = null;
        try {
            networkCache = new Cache(networkCacheFile, 1 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        okHttpClient.setCache(networkCache);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeZone.class, new Deserializers.TimeZoneDeserializer())
                .registerTypeAdapter(Date.class, new Deserializers.DateDeserializer())
                .create();

        RestAdapter forecastAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setEndpoint("https://api.forecast.io/forecast")
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .build();
        forecastService = forecastAdapter.create(Forecast.Service.class);

        RestAdapter placesAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setEndpoint("https://maps.googleapis.com/maps/api/place/autocomplete")
                .setClient(new OkClient(okHttpClient))
                .build();
        placesService = placesAdapter.create(Places.Service.class);

        RestAdapter detailsAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setEndpoint("https://maps.googleapis.com/maps/api/place/details")
                .setClient(new OkClient(okHttpClient))
                .build();
        detailsService = detailsAdapter.create(Places.Service.class);

    }

    public static synchronized Network getInstance(Context context) {
        if (instance == null)
            instance = new Network(context);
        return instance;
    }

    public void getForecast(double lat, double lng, String langCode, Callback<Forecast.Response> cb) {
        forecastService.getForecast(lat, lng, langCode, cb);
    }

    public void getAutocomplete(String query, String langCode, Callback<Places.AutocompleteResponse> cb) {
        placesService.getAutocomplete(query, langCode, cb);
    }

    public void getDetails(String placeid, String langCode, Callback<Places.DetailsResponse> cb) {
        detailsService.getDetails(placeid, langCode, cb);
    }

}
