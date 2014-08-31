package com.shawnaten.simpleweather.map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shawnaten.networking.Forecast;
import com.shawnaten.networking.Tasks;
import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.tools.FragmentListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends SupportMapFragment implements FragmentListener, Tasks.RadarImageListener, Target {
    private static final float DEFAULT_ZOOM = 7;
    private static final String SUPPORT_MAP_BUNDLE = "MapOptions", CONFIGURATION_CHANGE = "configurationChange";

    private LinkedHashMap<String, Date> radarFiles;
    private GroundOverlay radarOverlay;

    private Marker marker;

    public MapFragment() {
        GoogleMapOptions options = new GoogleMapOptions();
        Bundle args = new Bundle();
        options.compassEnabled(false).rotateGesturesEnabled(false).tiltGesturesEnabled(false).zoomControlsEnabled(false)
                .mapType(GoogleMap.MAP_TYPE_TERRAIN).zoomGesturesEnabled(false);
        args.putParcelable(SUPPORT_MAP_BUNDLE, options);
        setArguments(args);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateView();

    }

    @Override
    public void onNewData() {

        if (isVisible())
            updateView();
    }

    private void updateView() {
        MainActivity activity = (MainActivity) getActivity();

        new Tasks.getRadarFilesTask(getActivity(), this).execute();

        GoogleMap map = getMap();
        if (map != null && activity.hasForecast()) {
            Forecast.Response forecast = activity.getForecast();
            LatLng position = new LatLng(forecast.getLatitude(), forecast.getLongitude());
            if (marker == null) {
                marker = map.addMarker(new MarkerOptions().position(position));
            } else {
                marker.setPosition(position);
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
        }
    }

    private void setRadarData(Bitmap bitmap) {
        GoogleMap map = getMap();

        if (map != null) {

            if (radarOverlay == null) {
                GroundOverlayOptions conus = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .positionFromBounds(new LatLngBounds(
                                new LatLng(21.652538062803444, -127.620375523875420),
                                new LatLng(50.406626367301044, -66.51793787681802)));
                radarOverlay = map.addGroundOverlay(conus);
            }
            else {
                radarOverlay.setImage(BitmapDescriptorFactory.fromBitmap(bitmap));
            }
        }
    }

    @Override
    public void onReceiveImageInfo(LinkedHashMap<String, Date> info) {
        this.radarFiles = info;

        String fileNames[] = new String[radarFiles.size()];
        radarFiles.keySet().toArray(fileNames);
        Picasso.with(getActivity()).load(fileNames[fileNames.length - 1]).into(this);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setRadarData(bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
