package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/20/14.
 */
public class SummaryFragment extends Fragment implements FragmentListener {

    public SummaryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_current_summary, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        if (MainActivity.hasForecast() && MainActivity.getForecast().isUnread(getTag())) {
            Forecast.Response forecast = MainActivity.getForecast();

            String hourSummary;
            if (forecast.getMinutely() == null)
                hourSummary = forecast.getHourly().getData()[0].getSummary();
            else
                hourSummary = forecast.getMinutely().getSummary();

            ForecastTools.setText((ViewGroup) getView(), asList(R.id.next_hour, R.id.next_24_hours),
                    asList(hourSummary, forecast.getHourly().getSummary()));

            forecast.setRead(getTag());
        }
    }

    @Override
    public void onButtonClick(int id) {

    }
}
