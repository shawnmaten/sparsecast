package com.shawnaten.simpleweather.module;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.shawnaten.simpleweather.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GoogleApiClientModule {

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApiClient(App app) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(app)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
        return googleApiClient;
    }
}
