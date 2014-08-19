package com.shawnaten.networking;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
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
    private Keys keys;
    private Context context;

    private Network(Context context, Keys keys) {
        OkHttpClient okHttpClient = new OkHttpClient();
        File networkCacheFile = new File(context.getCacheDir(), "networkCache");
        Cache networkCache = null;

        this.context = context;
        this.keys = keys;

        try {
            networkCache = new Cache(networkCacheFile, 1 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        okHttpClient.setCache(networkCache);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeZone.class, new Deserializers.TimeZoneDeserializer())
                .registerTypeAdapter(Date.class, new Deserializers.DateDeserializer())
                .registerTypeAdapter(Uri.class, new Deserializers.UriDeserializer())
                .create();

        RestAdapter forecastAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.forecast.io/forecast")
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .build();
        forecastService = forecastAdapter.create(Forecast.Service.class);

        RestAdapter placesAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/maps/api/place/autocomplete")
                .setClient(new OkClient(okHttpClient))
                .build();
        placesService = placesAdapter.create(Places.Service.class);

        RestAdapter detailsAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/maps/api/place/details")
                .setClient(new OkClient(okHttpClient))
                .build();
        detailsService = detailsAdapter.create(Places.Service.class);

    }

    public static void setup(Context context, Keys keys) {
        instance = new Network(context, keys);
    }

    public boolean isSetup() {
        return keys != null;
    }

    public static synchronized Network getInstance() {
        return instance;
    }

    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public void getForecast(double lat, double lng, String langCode, Callback<Forecast.Response> cb) {
        forecastService.getForecast(keys.getForecastAPIKey(), lat, lng, langCode, cb);
    }

    public void getAutocomplete(String query, String langCode, Callback<Places.AutocompleteResponse> cb) {
        placesService.getAutocomplete(keys.getGoogleAPIKey(), query, langCode, cb);
    }

    public void getDetails(String placeId, String langCode, Callback<Places.DetailsResponse> cb) {
        detailsService.getDetails(keys.getGoogleAPIKey(), placeId, langCode, cb);
    }

    /*
    private class getForecastTask extends AsyncTask<Void, Void, Void> {
        double lat, lng;
        String langCode;
        Callback<Forecast.Response> cb;

        public getForecastTask(double lat, double lng, String langCode, Callback<Forecast.Response> cb) {
            this.lat = lat;
            this.lng = lng;
            this.langCode = langCode;
            this.cb = cb;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (keys == null) {
                try {
                    keysTask.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            forecastService.getForecast(keys.getForecastAPIKey(), lat, lng, langCode, cb);
            return null;
        }
    }

    private class getAutocompleteTask extends AsyncTask<Void, Void, Void> {
        String query, langCode;
        Callback<Places.AutocompleteResponse> cb;

        public getAutocompleteTask(String query, String langCode, Callback<Places.AutocompleteResponse> cb) {
            this.query = query;
            this.langCode = langCode;
            this.cb = cb;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (keys == null) {
                try {
                    keysTask.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            placesService.getAutocomplete(keys.getGoogleAPIKey(), query, langCode, cb);
            return null;
        }
    }

    private class getDetailsTask extends AsyncTask<Void, Void, Void> {
        String placeId, langCode;
        Callback<Places.DetailsResponse> cb;

        public getDetailsTask(String placeId, String langCode, Callback<Places.DetailsResponse> cb) {
            this.placeId = placeId;
            this.langCode = langCode;
            this.cb = cb;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (keys == null) {
                try {
                    keysTask.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            detailsService.getDetails(keys.getGoogleAPIKey(), placeId, langCode, cb);
            return null;
        }
    }
    */

}
