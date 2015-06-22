package com.shawnaten.simpleweather.fragments.current;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.ui.BaseFragment;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;

import java.util.Arrays;

import javax.inject.Inject;

import rx.Observable;

public class SummaryFragment extends BaseFragment {
    @Inject
    public SummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Observable<Forecast.Response> forecast = getApp().getNetworkComponent().forecast();
        View root =  inflater.inflate(R.layout.tab_current_summary, container, false);

        subs.add(forecast.subscribe(response -> {

            ForecastTools.setText((ViewGroup) root,
                    Arrays.asList(R.id.next_hour, R.id.next_24_hours),
                    Arrays.asList(
                            response.getMinutely() != null ? response.getMinutely().getSummary() :
                                    response.getHourly().getData()[0].getSummary(),
                            response.getHourly().getSummary()));
        }));

        return root;
    }
}
