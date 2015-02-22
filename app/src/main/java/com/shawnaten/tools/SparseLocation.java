package com.shawnaten.tools;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.shawnaten.simpleweather.R;

// TODO the calls that use the location info shouldn't be called within each other

public class SparseLocation {
    private Context context;
    private StatusAnimations statusAnimations;
    private GoogleApiClient googleApiClient;

    public SparseLocation(Context context, StatusAnimations statusAnimations) {
        this.context = context;
        this.statusAnimations = statusAnimations;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new ConnectionCallbacks())
                .addOnConnectionFailedListener(new OnConnectionFailedListener())
                .addApi(LocationServices.API)
                .build();
    }
    
    public void getLastLocation() {
        if (googleApiClient.isConnected())
            getLastLocationTask();
        else
            googleApiClient.connect();
    }

    public void disconnect() {
        googleApiClient.disconnect();
    }

    private void getLastLocationTask() {
        statusAnimations.changeState(StatusAnimations.LOAD, true);
        Location lastLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);

        if (ForecastTools.UNIT_CODE == null) {
            String units = PreferenceManager.getDefaultSharedPreferences(context).getString(
                    context.getString(R.string.units_key), null);
            if (units == null)
                new Tasks.getDefaultUnitsTask(PreferenceManager
                        .getDefaultSharedPreferences(context),
                        context.getString(R.string.units_key),
                        new Geocoder(context), lastLocation,
                        context.getString(R.string.action_current_location)).execute();
            else {
                ForecastTools.configUnits(units, null, null);
                new Tasks.getLocationNameTask(new Geocoder(context), lastLocation,
                        context.getString(R.string.action_current_location)).execute();
            }
        } else  {
            new Tasks.getLocationNameTask(new Geocoder(context), lastLocation,
                    context.getString(R.string.action_current_location)).execute();
        }
    }

    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle bundle) {
            getLastLocationTask();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    private class OnConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast toast = Toast.makeText(context, connectionResult.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
