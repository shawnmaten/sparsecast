package com.shawnaten.tools.models;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;

import rx.observables.ConnectableObservable;

public class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
    private ConnectableObservable<Location> locationObservable;

    @Override
    public void onConnected(Bundle bundle) {
        locationObservable.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public void setLocationObservable(ConnectableObservable<Location> locationObservable) {
        this.locationObservable = locationObservable;
    }
}
