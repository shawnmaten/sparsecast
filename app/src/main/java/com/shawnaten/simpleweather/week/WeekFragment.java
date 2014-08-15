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
    private static WeekFragmentAdapter adapter;

    public WeekFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExpandableListView view = (ExpandableListView) inflater.inflate(R.layout.tab_week_main, container, false);
        view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume () {
        super.onResume();

        if (MainActivity.hasForecast() && (MainActivity.getForecast().isUnread(getTag()) || adapter == null)) {
            ExpandableListView view = (ExpandableListView) getView().findViewById(R.id.tab_week);
            adapter = new WeekFragmentAdapter(getActivity(), MainActivity.getForecast());
            view.setAdapter(adapter);
            MainActivity.getForecast().setRead(getTag());
        }

    }

    @Override
    public void onButtonClick(int id) {

    }

}
