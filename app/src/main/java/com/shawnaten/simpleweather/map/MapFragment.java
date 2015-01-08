package com.shawnaten.simpleweather.map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.radar.RadarProvider;
import com.shawnaten.tools.radar.RemoveBackground;
import com.shawnaten.tools.radar.Tasks;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends SupportMapFragment implements FragmentListener, Tasks.RadarImageListener {
    private static final float DEFAULT_ZOOM = 7;
    private static final String SUPPORT_MAP_BUNDLE = "MapOptions", CONFIGURATION_CHANGE = "configurationChange";
    private static final Transformation transform = new RemoveBackground();

    private LinkedHashMap<String, Date> radarFiles;
    private GroundOverlay radarOverlay;
    private Handler handler = new Handler();
    private String[] fileNames;
    private int i, max;

    private Marker marker;
    private TileOverlay overlay;

    public MapFragment() {
        GoogleMapOptions options = new GoogleMapOptions();
        Bundle args = new Bundle();
        options.compassEnabled(false).rotateGesturesEnabled(false).tiltGesturesEnabled(false).zoomControlsEnabled(false)
                .mapType(GoogleMap.MAP_TYPE_TERRAIN);
        args.putParcelable(SUPPORT_MAP_BUNDLE, options);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //new Tasks.getRadarFilesTask(this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateView();
        //updateRadarData();

    }

    @Override
    public void onNewData() {

        if (isVisible())
            updateView();
    }

    private void updateView() {
        MainActivity activity = (MainActivity) getActivity();

        GoogleMap map = getMap();
        /*
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
        */
    }

    @Override
    public void onReceiveImageInfo(LinkedHashMap<String, Date> info) {
        this.radarFiles = info;
        max = radarFiles.size();
        fileNames = new String[max];
        radarFiles.keySet().toArray(fileNames);
        Activity activity = getActivity();
        if (activity != null)
            Picasso.with(activity).load(fileNames[fileNames.length-1]).transform(new RemoveBackground()).fetch();
        if (isVisible())
            updateRadarData();
    }

    private void updateRadarData() {
        GoogleMap map = getMap();

        if (map != null && fileNames != null)
            if (overlay == null)
                overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(new RadarProvider(getActivity(), fileNames,
                        getActivity().getResources().getDisplayMetrics().density)));
            else
                overlay.clearTileCache();
    }

}
