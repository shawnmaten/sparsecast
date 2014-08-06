package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/20/14.
 */
public class SummariesFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;

    public SummariesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summaries, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {

            String hourSummary;
            if (forecast.getMinutely() == null)
                hourSummary = forecast.getHourly().getData()[0].getSummary();
            else
                hourSummary = forecast.getMinutely().getSummary();

            ForecastTools.setSpannableText((ViewGroup) getView(), asList(R.id.currently, R.id.next_hour, R.id.next_24_hours), asList(2, 2, 2), asList(1, 1, 1),
                    asList("\n", "\n", "\n"), asList("", "", ""), asList("", "", ""), asList("", "", ""),

                    asList(

                            asList(getString(R.string.currently), forecast.getCurrently().getSummary()),

                            asList(getString(R.string.next_hour), hourSummary),

                            asList(getString(R.string.next_24_hours), forecast.getHourly().getSummary())

                    )
            );

            forecast = null;
        }
    }

    @Override
    public void onNewData(Forecast.Response data) {
        forecast = data;
        if (isVisible())
            onResume();
    }

    @Override
    public void onButtonClick(View view) {

    }
}
