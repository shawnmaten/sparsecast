package com.shawnaten.tools.modules;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.shawnaten.tools.models.ConnectionCallbacks;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Module (
        complete = false,
        library = true
)
public class LocationModule {

    @Provides
    @Singleton
    public ConnectableObservable<Location> providesLocationModule(Activity activity) {
        ConnectionCallbacks connectionCallbacks;
        GoogleApiClient googleApiClient;
        ConnectableObservable<Location> locationObservable;

        connectionCallbacks = new ConnectionCallbacks();

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(null)
                .addApi(LocationServices.API)
                .build();

        locationObservable = Observable.just(googleApiClient)
                .map(LocationServices.FusedLocationApi::getLastLocation)
                .publish();

        connectionCallbacks.setLocationObservable(locationObservable);

        return locationObservable;
    }

}
