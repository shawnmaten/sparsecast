package com.shawnaten.simpleweather.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shawnaten.networking.Forecast;
import com.shawnaten.tools.FragmentListener;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;
    private MapView mapView;
    private GoogleMap map;
    private Marker marker;

    private static final float defaultZoom = 9;

    public MapFragment() {

    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleMapOptions options = new GoogleMapOptions();
        options
                .mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);
        mapView = new MapView(getActivity(), options);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng position = null;
        if (forecast != null) {
            position = new LatLng(forecast.getLatitude(), forecast.getLongitude());
            markerOptions.visible(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoom));
        } else {
            position = new LatLng(0, 0);
            markerOptions.visible(false);
        }
        markerOptions.position(position);
        marker = map.addMarker(markerOptions);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mapView;
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        ((ViewGroup)getView()).removeAllViews();
    }

    @Override
    public void onResume () {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause () {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory () {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onNewData(Forecast.Response data) {
        forecast = data;
        marker.setPosition(new LatLng(forecast.getLatitude(), forecast.getLongitude()));
        marker.setVisible(true);
        MapsInitializer.initialize(getActivity());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), defaultZoom));
    }

    @Override
    public void onButtonClick(View view) {

    }

}
