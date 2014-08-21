package com.shawnaten.simpleweather.week;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.FragmentListener;

/**
 * Created by shawnaten on 7/3/14.
 */
public class WeekFragment extends Fragment implements FragmentListener {

    public WeekFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExpandableListView view = (ExpandableListView) inflater.inflate(R.layout.tab_week_main, container, false);
        return view;
    }

    @Override
    public void onResume () {
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

        if (activity.hasForecast()) {
            ExpandableListView view = (ExpandableListView) getView().findViewById(R.id.tab_week);
            WeekFragmentAdapter adapter = new WeekFragmentAdapter(getActivity(), activity.getForecast());
            view.setAdapter(adapter);
        }
    }

}
