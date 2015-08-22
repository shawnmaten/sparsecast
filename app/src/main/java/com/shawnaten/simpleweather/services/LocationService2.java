package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.locationAPI.LocationAPI;
import com.shawnaten.simpleweather.component.DaggerLocationServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

public class LocationService2 extends IntentService {
    private static final String LAT = "lat";
    private static final String LNG = "lng";

    public static final String ACTION_START = "com.shawnaten.simpleweather.START";
    public static final String ACTION_STOP = "com.shawnaten.simpleweather.STOP";
    public static final String ACTION_PROCESS = "com.shawnaten.simpleweather.PROCESS";

    private static final long INTERVAL_MINUTES = 30;
    private static final long INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(INTERVAL_MINUTES);
    private static final long INTERVAL_SECONDS = TimeUnit.MINUTES.toSeconds(INTERVAL_MINUTES);

    private static final double MIN_RADIUS_METERS = 2000;

    @Inject GoogleApiClient googleApiClient;
    @Inject SharedPreferences prefs;
    @Inject LocationAPI api;
    @Inject @Named("gcmToken") String gcmToken;

    public LocationService2() {
        super("ActivityDetectionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerLocationServiceComponent
                .builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build()
                .inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;

        Log.e(this.getClass().getSimpleName(), intent.getAction());

        googleApiClient.blockingConnect();

        switch (intent.getAction()) {
            case ACTION_START:
                prefs.edit().remove(LAT).remove(LNG).apply();

                String notifyKey = getString(R.string.pref_location_notify_key);

                if (!prefs.getBoolean(notifyKey, false))
                    return;

                LocationRequest request = new LocationRequest();
                request.setInterval(INTERVAL_MILLIS);
                request.setFastestInterval(INTERVAL_MILLIS);
                request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                locationProviderApi.requestLocationUpdates(
                        googleApiClient,
                        request,
                        getPendingIntent()
                );
                break;
            case ACTION_STOP:
                locationProviderApi.removeLocationUpdates(
                        googleApiClient,
                        getPendingIntent()
                );
                break;
            case ACTION_PROCESS:
                if (LocationResult.hasResult(intent))
                    process(LocationResult.extractResult(intent));
                break;
        }
    }

    private void process(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();

        if (location == null)
            return;

        if (prefs.contains(LAT)) {
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng oldLatLng = new LatLng(prefs.getFloat(LAT, 0), prefs.getFloat(LNG, 0));

            double radius = INTERVAL_SECONDS * location.getSpeed();
            radius = Math.max(radius, MIN_RADIUS_METERS);
            double distance = SphericalUtil.computeDistanceBetween(newLatLng, oldLatLng);

            if (distance < radius)
                return;
        }

        try {
            api.report(gcmToken, location.getLatitude(), location.getLongitude()).execute();
            prefs.edit()
                    .putFloat(LAT, (float) location.getLatitude())
                    .putFloat(LNG, (float) location.getLongitude())
                    .apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationService2.class);
        intent.setAction(ACTION_PROCESS);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context , LocationService2.class);
        intent.setAction(LocationService2.ACTION_START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context , LocationService2.class);
        intent.setAction(LocationService2.ACTION_STOP);
        context.startService(intent);
    }
}
