package com.shawnaten.simpleweather.module;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@Module
public class LocationModule {
    @Provides
    public Observable<Location> providesLocation(final GoogleApiClient client) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                FusedLocationProviderApi api = LocationServices.FusedLocationApi;

                client.blockingConnect();

                LocationAvailability availability = api.getLocationAvailability(client);

                if (availability != null && availability.isLocationAvailable())
                    subscriber.onNext(api.getLastLocation(client));
                else
                    subscriber.onError(new Throwable("Location Services Unavailable"));
            }
        }).subscribeOn(Schedulers.io());
    }
}
