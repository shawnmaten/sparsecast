package com.shawnaten.simpleweather.services;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

public class LocationHandler extends Handler implements LocationListener {
    public static final double REPORT_DISTANCE = 1000;

    @Inject GoogleApiClient googleApiClient;
    @Inject LocationAPI locationAPI;
    @Inject @Named("gcmToken") String gcmToken;

    private Location lastReport;

    public LocationHandler(Looper looper, Context context) {
        super(looper);

        DaggerServiceComponent.builder()
                .contextModule(new ContextModule(context))
                .build()
                .inject(this);
    }

    @Override
    public void handleMessage(Message msg) {

        googleApiClient.blockingConnect();

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        LocationRequest request = new LocationRequest();
        request.setInterval(TimeUnit.HOURS.toMillis(1));
        request.setFastestInterval(TimeUnit.HOURS.toMillis(1));
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastReport != null) {
            LatLng oldLatLng = new LatLng(lastReport.getLatitude(), lastReport.getLongitude());

            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            double distance = SphericalUtil.computeDistanceBetween(oldLatLng, newLatLng);

            if (distance < LocationHandler.REPORT_DISTANCE)
                return;
        }

        try {
            locationAPI
                    .report(gcmToken, location.getLatitude(), location.getLongitude())
                    .execute();
            lastReport = location;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
