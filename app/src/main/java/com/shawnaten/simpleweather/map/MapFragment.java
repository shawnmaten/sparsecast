package com.shawnaten.simpleweather.map;

import android.os.Bundle;
import android.view.View;

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
    private LatLng position;
    private Marker marker;

    private static final float defaultZoom = 9;
    private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions", MAP_POSITION = "MapPosition";

    public MapFragment() {
        GoogleMapOptions options = new GoogleMapOptions();
        Bundle args = new Bundle();
        options.compassEnabled(false).rotateGesturesEnabled(false).tiltGesturesEnabled(false).zoomControlsEnabled(false)
                .mapType(GoogleMap.MAP_TYPE_TERRAIN);
        args.putParcelable(SUPPORT_MAP_BUNDLE_KEY, options);
        setArguments(args);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        updateView();
    }

    /*
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_marker:
                GoogleMap map = getMap();
                if (map != null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoom));
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return  true;
    }
    */

    @Override
    public void onNewData() {
        if (isVisible()) {
            updateView();
        }
    }

    private void updateView() {
        MainActivity activity = (MainActivity) getActivity();

        GoogleMap map = getMap();
        if (map != null && activity.hasForecast()) {
            Forecast.Response forecast = activity.getForecast();
            position = new LatLng(forecast.getLatitude(), forecast.getLongitude());
            if (marker == null) {
                marker = map.addMarker(new MarkerOptions().position(position));
            } else {
                marker.setPosition(position);
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoom));
        }
    }

}
