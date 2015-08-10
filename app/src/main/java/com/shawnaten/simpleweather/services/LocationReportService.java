package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.locationReportAPI.LocationReportAPI;
import com.shawnaten.simpleweather.backend.locationReportAPI.model.LocationRecord;
import com.shawnaten.simpleweather.backend.locationReportAPI.model.Response;
import com.shawnaten.simpleweather.lib.model.ResponseCodes;

import java.io.IOException;

import javax.inject.Inject;

public class LocationReportService extends IntentService {

    private static final String TAG = "LocationReportService";

    @Inject
    LocationReportAPI locationReportAPI;

    @Inject
    GoogleApiClient googleApiClient;

    public LocationReportService() {
        super("LocationUpdater");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((App) getApplication()).getServiceComponent().injectLocationUpdater(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        googleApiClient.blockingConnect();

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        LocationRecord record = new LocationRecord();
        record.setGcmToken(preferences.getString(getString(R.string.pref_gcm_token), null));
        record.setLat(location.getLatitude());
        record.setLng(location.getLongitude());

        try {
            Response response = locationReportAPI.report(record).execute();
            if (response.getMessage() == ResponseCodes.NOT_REGISTERED) {
                Intent registrarIntent = new Intent(this, GCMRegistrarService.class);
                startService(registrarIntent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
