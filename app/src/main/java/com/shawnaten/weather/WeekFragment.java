package com.shawnaten.weather;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.shawnaten.networking.Forecast;

/**
 * Created by shawnaten on 7/3/14.
 */
public class WeekFragment extends Fragment implements TabDataListener {
    private Forecast.Response forecast;

    public WeekFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_week, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        if(forecast != null) {
            ExpandableListView view = (ExpandableListView) getView();
            view.setAdapter(new WeekFragmentAdapter(getActivity(), forecast));
        }
    }

    @Override
    public void onNewData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            forecast = (Forecast.Response) data;
            if (isVisible())
                this.onResume();
        }
    }

    @Override
    public void onRestoreData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            forecast = (Forecast.Response) data;
            if (isVisible())
                this.onResume();
        }
    }
}
