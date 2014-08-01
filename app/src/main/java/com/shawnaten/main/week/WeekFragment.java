package com.shawnaten.main.week;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.shawnaten.main.R;
import com.shawnaten.networking.Forecast;
import com.shawnaten.tools.FragmentListener;

/**
 * Created by shawnaten on 7/3/14.
 */
public class WeekFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;
    private static WeekFragmentAdapter adapter;

    public WeekFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExpandableListView view = (ExpandableListView) inflater.inflate(R.layout.tab_week, container, false);
        view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {
            ExpandableListView view = (ExpandableListView) getView().findViewById(R.id.list);
            adapter = new WeekFragmentAdapter(getActivity(), forecast);
            view.setAdapter(adapter);
            forecast = null;
            forecast = null;
        }

    }

    @Override
    public void onNewData(Forecast.Response data) {
        forecast = data;
        if (isVisible())
            this.onResume();
    }

    @Override
    public void onButtonClick(View view) {

    }

}
