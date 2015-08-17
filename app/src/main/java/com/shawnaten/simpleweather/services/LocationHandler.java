package com.shawnaten.simpleweather.services;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shawnaten.simpleweather.backend.locationAPI.LocationAPI;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

public class LocationHandler extends Handler {
    public static final double REPORT_DISTANCE = 1000;

    @Inject LocationAPI locationAPI;
    @Inject ReactiveLocationProvider locationProvider;
    @Inject @Named("gcmToken") String gcmToken;

    private Subscription locationUpdates;

    public LocationHandler(Looper looper, Context context) {
        super(looper);

        DaggerServiceComponent.builder()
                .contextModule(new ContextModule(context))
                .build()
                .inject(this);
    }

    @Override
    public void handleMessage(Message msg) {

        if (locationUpdates != null)
            locationUpdates.unsubscribe();

        LocationRequest request = new LocationRequest();
        request.setInterval(TimeUnit.HOURS.toMillis(1));
        request.setFastestInterval(TimeUnit.HOURS.toMillis(1));
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationUpdates = locationProvider
                .getUpdatedLocation(request)
                .subscribe(new LocationAction(locationAPI, gcmToken));
    }

    public static class LocationAction implements Action1<Location> {
        private Location lastReport;
        private LocationAPI locationAPI;
        private String gcmToken;

        public LocationAction(LocationAPI locationAPI, String gcmToken) {
            this.locationAPI = locationAPI;
            this.gcmToken = gcmToken;
        }

        @Override
        public void call(Location location) {
            if (lastReport != null) {
                LatLng oldLatLng = new LatLng(lastReport.getLatitude(), lastReport.getLongitude());

                LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                double distance = SphericalUtil.computeDistanceBetween(oldLatLng, newLatLng);

                if (distance < REPORT_DISTANCE)
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
}
