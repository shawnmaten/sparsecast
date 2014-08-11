package com.shawnaten.simpleweather.map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.FragmentListener;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends SupportMapFragment implements FragmentListener {
    private LatLng position;

    private static final float defaultZoom = 9;

    public MapFragment() {

    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tab_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_marker:
                GoogleMap map = getMap();
                if (map != null)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoom));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return  true;
    }

    @Override
    public void onReceiveData(Forecast.Response data) {
        position = new LatLng(data.getLatitude(), data.getLongitude());
    }

    @Override
    public void onButtonClick(int id) {

    }

}
