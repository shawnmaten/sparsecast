package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shawnaten.simpleweather.backend.locationAPI.LocationAPI;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

public class ActivityRecognitionService extends IntentService {
    private static final String LAT = "activityRecognitionServiceLat";
    private static final String LNG = "activityRecognitionServiceLng";

    public static final String ACTION_START = "com.shawnaten.simpleweather.START";
    public static final String ACTION_STOP = "com.shawnaten.simpleweather.STOP";
    public static final String ACTION_PROCESS = "com.shawnaten.simpleweather.PROCESS";

    private static final double RADIUS = 1000;

    private static long UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(15);

    @Inject GoogleApiClient googleApiClient;
    @Inject SharedPreferences prefs;
    @Inject LocationAPI api;
    @Inject @Named("gcmToken") String gcmToken;

    public ActivityRecognitionService() {
        super("ActivityDetectionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerServiceComponent
                .builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build()
                .inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        googleApiClient.blockingConnect();

        switch (intent.getAction()) {
            case ACTION_START:
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                        googleApiClient,
                        UPDATE_INTERVAL,
                        getPendingIntent()
                );
                break;
            case ACTION_STOP:
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                        googleApiClient,
                        getPendingIntent()
                );
                break;
            case ACTION_PROCESS:
                process(intent);
                break;
        }
    }

    private void process(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        int type = result.getMostProbableActivity().getType();

        if (type != DetectedActivity.IN_VEHICLE) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (prefs.contains(LAT)) {
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng oldLocation = new LatLng(
                        prefs.getFloat(LAT, 0),
                        prefs.getFloat(LNG, 0)
                );

                double distance = SphericalUtil.computeDistanceBetween(newLocation, oldLocation);

                if (distance < RADIUS)
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
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        intent.setAction(ACTION_PROCESS);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
