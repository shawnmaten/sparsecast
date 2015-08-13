package com.shawnaten.simpleweather.services;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.shawnaten.simpleweather.backend.locationReportAPI.LocationReportAPI;
import com.shawnaten.simpleweather.backend.locationReportAPI.model.LocationReport;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

public class LocationHandler extends Handler implements LocationListener {

    @Inject
    GoogleApiClient googleApiClient;
    @Inject
    LocationReportAPI locationReportAPI;
    @Inject @Named("gcmToken")
    String gcmToken;

    public LocationHandler(Looper looper, Context context) {
        super(looper);

        DaggerServiceComponent.builder()
                .contextModule(new ContextModule(context))
                .build()
                .injectLocationHandler(this);
    }

    @Override
    public void handleMessage(Message msg) {

        FusedLocationProviderApi fusedLocationApi = LocationServices.FusedLocationApi;

        LocationRequest request = new LocationRequest();

        request.setInterval(TimeUnit.HOURS.toMillis(1));
        request.setFastestInterval(TimeUnit.HOURS.toMillis(1));
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        googleApiClient.blockingConnect();

        fusedLocationApi.requestLocationUpdates(googleApiClient, request, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationReport locationReport = new LocationReport();

        locationReport.setGcmToken(gcmToken);
        locationReport.setLat(location.getLatitude());
        locationReport.setLng(location.getLongitude());

        try {
            locationReportAPI.report(locationReport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
