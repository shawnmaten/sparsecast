package com.shawnaten.networking;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.tools.ForecastTools;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by shawnaten on 7/17/14.
 */
public class Network implements Callback {

    public static final String
            PLACES_STATUS_OK = "OK", PLACES_STATUS_ZERO_RESULTS = "ZERO_RESULTS", PLACES_STATUS_DENIED = "REQUEST_DENIED";

    private static Network instance;

    private NetworkListener listener;

    private String lastLocationName;
    private Forecast.Service forecastService;
    private Places.Service placesService, detailsService;
    private Keys keys;

    private Network(File networkCacheFile, Keys keys) {
        OkHttpClient okHttpClient = new OkHttpClient();
        OkClient okClient;
        Cache networkCache = null;
        this.keys = keys;

        try {
            networkCache = new Cache(networkCacheFile, 1 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        okHttpClient.setCache(networkCache);
        okClient = new OkClient(okHttpClient);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeZone.class, new Deserializers.TimeZoneDeserializer())
                .registerTypeAdapter(Date.class, new Deserializers.DateDeserializer())
                .registerTypeAdapter(Uri.class, new Deserializers.UriDeserializer())
                .create();

        RestAdapter forecastAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.forecast.io/forecast")
                .setClient(okClient)
                .setConverter(new GsonConverter(gson))
                .build();
        forecastService = forecastAdapter.create(Forecast.Service.class);

        RestAdapter placesAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/maps/api/place/autocomplete")
                .setClient(okClient)
                .build();
        placesService = placesAdapter.create(Places.Service.class);

        RestAdapter detailsAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/maps/api/place/details")
                .setClient(okClient)
                .build();
        detailsService = detailsAdapter.create(Places.Service.class);

    }

    public String getLastLocationName() {
        return lastLocationName;
    }

    public void setLastLocationName(String lastLocationName) {
        this.lastLocationName = lastLocationName;
    }

    public static void setup(File networkCacheFile, Keys keys) {
        if (instance == null || keys.getGoogleAPIKey() == null || keys.getForecastAPIKey() == null)
            instance = new Network(networkCacheFile, keys);
    }

    public boolean isSetup() {
        return keys.getGoogleAPIKey() != null && keys.getForecastAPIKey() != null;
    }

    public void setListener(NetworkListener listener) {
        this.listener = listener;
    }

    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public static synchronized Network getInstance() {
        if (instance != null)
            return instance;
        else
            return null;
    }

    public void getForecast(double lat, double lng) {
        forecastService.getForecast(keys.getForecastAPIKey(), lat, lng,
                Locale.getDefault().getLanguage(), ForecastTools.UNIT_CODE, this);
    }

    public Places.AutocompleteResponse getAutocompleteSync(String query) {
        return placesService.getAutocomplete(keys.getGoogleAPIKey(), query, Locale.getDefault().getLanguage());
    }

    public void getAutocompleteAsync(String query) {
        placesService.getAutocomplete(keys.getGoogleAPIKey(), query, Locale.getDefault().getLanguage(), this);
    }

    public void getDetails(String placeId) {
        detailsService.getDetails(keys.getGoogleAPIKey(), placeId, Locale.getDefault().getLanguage(), this);
    }

    private boolean responseOkay(String status) {

        switch (status) {
            case PLACES_STATUS_OK:
                return true;
            case PLACES_STATUS_ZERO_RESULTS:
                listener.onFailure();
                listener.onShowNetworkToast(R.string.no_search_results);
                return false;
            case PLACES_STATUS_DENIED:
                listener.onShowNetworkDialog();
                return false;
            default:
                listener.onFailure();
                listener.onShowNetworkToast(R.string.network_error);
                return false;
        }

    }

    @Override
    public void success(Object response, Response response2) {
        if (Forecast.Response.class.isInstance(response)) {

            Forecast.Response forecast = (Forecast.Response) response;
            forecast.setName(getInstance().getLastLocationName());
            listener.onNewData(forecast);

        } else if (Places.AutocompleteResponse.class.isInstance(response)) {
            Places.AutocompleteResponse autocompleteResponse = (Places.AutocompleteResponse) response;

            if (responseOkay(autocompleteResponse.getStatus())) {
                Network.getInstance().getDetails(autocompleteResponse.getPredictions()[0].getPlace_id());
            }

        } else if (Places.DetailsResponse.class.isInstance(response)) {
            Places.DetailsResponse details = (Places.DetailsResponse) response;

            if (Network.getInstance().responseOkay(details.getStatus())) {
                Network.getInstance().setLastLocationName(details.getResult().getName());
                Places.Location location = details.getResult().getGeometry().getLocation();
                Network.getInstance().getForecast(location.getLat(), location.getLng());
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {

        if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
            listener.onShowNetworkDialog();
        } else {
            listener.onFailure();
            listener.onShowNetworkToast(R.string.network_error);
            Log.e("Retrofit", error.getMessage());
        }

    }

    public interface NetworkListener {
        public void onNewData(Forecast.Response forecast);
        public void onFailure();
        public void onShowNetworkToast(int resId);
        public void onShowNetworkDialog();
    }
}
