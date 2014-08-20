package com.shawnaten.simpleweather.map;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.tools.FragmentListener;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends SupportMapFragment implements FragmentListener {
    private static final float DEFAULT_ZOOM = 9, DEFAULT_ZOOM_OUT = 7;
    private static final String SUPPORT_MAP_BUNDLE = "MapOptions", CONFIGURATION_CHANGE = "configurationChange";

    private Marker marker;
    private boolean configChanged = false;

    public MapFragment() {
        GoogleMapOptions options = new GoogleMapOptions();
        Bundle args = new Bundle();
        options.compassEnabled(false).rotateGesturesEnabled(false).tiltGesturesEnabled(false).zoomControlsEnabled(false)
                /*.mapType(GoogleMap.MAP_TYPE_TERRAIN)*/;
        args.putParcelable(SUPPORT_MAP_BUNDLE, options);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            configChanged = savedInstanceState.getBoolean(CONFIGURATION_CHANGE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(CONFIGURATION_CHANGE, getActivity().isChangingConfigurations());
    }

    @Override
    public void onDestroyView () {

        super.onDestroyView();

    }

    @Override
    public void onNewData() {
        updateView();
    }

    private void updateView() {
        MainActivity activity = (MainActivity) getActivity();

        GoogleMap map = getMap();
        if (map != null && activity.hasForecast()) {
            Forecast.Response forecast = activity.getForecast();
            LatLng position = new LatLng(forecast.getLatitude(), forecast.getLongitude());
            if (marker == null) {
                marker = map.addMarker(new MarkerOptions().position(position));
            } else {
                marker.setPosition(position);
            }

            if (!configChanged) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, map.getMaxZoomLevel() - DEFAULT_ZOOM_OUT));
            } else {
                configChanged = false;
            }
        }
    }

}
