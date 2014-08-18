package com.shawnaten.networking;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.keysEndpoint.KeysEndpoint;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

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
    private KeysEndpoint keysService;
    private getKeysTask keysTask;
    private static FragmentActivity activity;

    private Network() {
        OkHttpClient okHttpClient = new OkHttpClient();

        File networkCacheFile = new File(activity.getCacheDir(), "networkCache");

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

        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(activity, "server:client_id:" + activity.getString(R.string.WEB_ID));
        credential.setSelectedAccountName(credential.getAllAccounts()[0].name);

        KeysEndpoint.Builder keysEndpointBuilder =
                new KeysEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential);
        if (activity.getResources().getBoolean(R.bool.localhost))
            keysEndpointBuilder.setRootUrl(activity.getString(R.string.root_url));
        keysService = keysEndpointBuilder.build();

        keysTask = (getKeysTask) new getKeysTask().execute();

    }

    public static void setup(FragmentActivity newActivity) {
        activity = newActivity;
        instance = new Network();
    }

    public static synchronized Network getInstance() {
        return instance;
    }

    public void getForecast(double lat, double lng, String langCode, Callback<Forecast.Response> cb) {
        new getForecastTask(lat, lng, langCode, cb).execute();
    }

    public void getAutocomplete(String query, String langCode, Callback<Places.AutocompleteResponse> cb) {
        new getAutocompleteTask(query, langCode, cb).execute();
    }

    public void getDetails(String placeId, String langCode, Callback<Places.DetailsResponse> cb) {
        new getDetailsTask(placeId, langCode, cb).execute();
    }

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

    private class getKeysTask extends AsyncTask<Void, Void, Keys> {
        @Override
        protected Keys doInBackground(Void... unused) {
            try {
                keys = keysService.getKeys().execute();
            } catch (IOException e) {
                Log.e("getKeysTask", e.getMessage(), e);
            }
            return keys;
        }

    }

}
