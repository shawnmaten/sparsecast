package com.shawnaten.simpleweather.module;

import com.google.android.gms.location.LocationRequest;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationRequestModule {

    @Provides
    public LocationRequest providesLocationRequest() {
        LocationRequest request = new LocationRequest();

        request.setInterval(30 * 60000);
        request.setFastestInterval(10 * 60000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return request;
    }
}
