package com.shawnaten.simpleweather.module;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
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
                client.blockingConnect();
                subscriber.onNext(LocationServices.FusedLocationApi
                        .getLastLocation(client));
            }
        }).subscribeOn(Schedulers.io());
    }
}
