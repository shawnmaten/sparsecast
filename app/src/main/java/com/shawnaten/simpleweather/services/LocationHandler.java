package com.shawnaten.simpleweather.services;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.backend.messagingApi.MessagingApi;
import com.shawnaten.simpleweather.component.DaggerNotificationComponent;
import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.simpleweather.tools.GCMSettings;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

public class LocationHandler extends Handler implements LocationListener {
    private static final String LAST_LATITUDE_KEY = "lastLatitude";
    private static final String LAST_LONGITUDE_KEY = "lastLongitude";
    private static final String LAST_UPDATE_KEY = "lastUpdate";

    private AppModule appModule;

    private Location lastLocation;
    private Date lastUpdate;

    @Inject GoogleApiClient googleApiClient;
    @Inject LocationRequest locationRequest;
    @Inject MessagingApi messagingApi;

    public LocationHandler(Looper looper, App app) {
        super(looper);

        appModule = new AppModule(app);

        DaggerNotificationComponent.builder()
                .appModule(appModule)
                .build()
                .injectNotificationThread(this);
    }

    @Override
    public void handleMessage(Message msg) {

        try {
            Log.e("LocationService", GCMSettings.configure(appModule.provideApp()));
            googleApiClient.blockingConnect();

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        double maxDistance = 1000;
        double maxSpeed = 1.667;
        double maxTimeDelta = 10 * 60000;

        double distance = 0;
        Date currentTime = new Date();
        long timeDelta = 0;

        if (lastLocation != null) {
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            distance = SphericalUtil.computeDistanceBetween(lastLatLng, newLatLng);
            timeDelta = currentTime.getTime() - lastUpdate.getTime();
        }

        if (lastLocation == null)
            updateLocation(currentTime, location);
        else if (location.getSpeed() >= maxSpeed && timeDelta >= maxTimeDelta)
            updateLocation(currentTime, location);
        else if (distance >= maxDistance)
            updateLocation(currentTime, location);
    }

    private void updateLocation(Date currentTime, Location location) {
        lastUpdate = currentTime;
        lastLocation = location;

        try {
            messagingApi.sendMessage(GCMSettings.regId, "Updating Location").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
