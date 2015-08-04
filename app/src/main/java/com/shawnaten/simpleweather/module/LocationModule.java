package com.shawnaten.simpleweather.module;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.shawnaten.tools.LocationSettings;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@Module
public class LocationModule {
    public static final String ERROR = "Location Services Unavailable";

    @Provides
    public Observable<Location> providesLocation(final GoogleApiClient client) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                FusedLocationProviderApi api = LocationServices.FusedLocationApi;

                client.blockingConnect();

                LocationAvailability availability = api.getLocationAvailability(client);

                if (availability != null && availability.isLocationAvailable()) {
                    Location location = api.getLastLocation(client);
                    LocationSettings.currentLocation = location;
                    subscriber.onNext(location);
                } else
                    subscriber.onError(new Throwable(ERROR));
            }
        }).subscribeOn(Schedulers.io());
    }
}
