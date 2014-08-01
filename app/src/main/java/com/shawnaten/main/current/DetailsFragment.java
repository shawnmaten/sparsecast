package com.shawnaten.main.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnaten.main.R;
import com.shawnaten.networking.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/20/14.
 */
public class DetailsFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;

    public DetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {

            Forecast.DataPoint currently, hour, today;

            ForecastTools.timeForm.setTimeZone(forecast.getTimezone());

            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            ForecastTools.setSpannableText((ViewGroup) getView().findViewById(R.id.details),
                    asList(R.id.details_1, R.id.details_2, R.id.details_3, R.id.details_4), asList(2, 2, 2, 2), asList(1, 1, 1, 1),
                    asList(": ", ": ", ": ", ": "), asList("", "", "", ""), asList("", "", "", ""), asList("\n", "\n", "\n", "\n"),

                    asList(
                            asList(getString(R.string.sunrise), ForecastTools.timeForm.format(today.getSunriseTime()), getString(R.string.sunset),
                                    ForecastTools.timeForm.format(today.getSunsetTime())),

                            asList(
                                    getString(R.string.wind), String.format("%s %s %s", ForecastTools.intForm.format(currently.getWindSpeed()),
                                            getString(R.string.wind_unit), getString(ForecastTools.getWindDirection(currently.getWindBearing()))),
                                    getString(R.string.pressure), String.format("%s %s", ForecastTools.intForm.format(currently.getPressure()),
                                            getString(R.string.pressure_unit))
                            ),

                            asList(
                                    getString(ForecastTools.getPrecipitationID(getActivity(), currently.getPrecipType())),
                                        String.format("%s (%s)", ForecastTools.percForm.format(currently.getPrecipProbability()),
                                        getString(R.string.now).toLowerCase()),
                                    getString(ForecastTools.getPrecipitationID(getActivity(), hour.getPrecipType())),
                                    String.format("%s (%s)", ForecastTools.percForm.format(hour.getPrecipProbability()),
                                            getString(R.string.hour).toLowerCase()),
                                    getString(ForecastTools.getPrecipitationID(getActivity(), today.getPrecipType())),
                                    String.format("%s (%s)", ForecastTools.percForm.format(today.getPrecipProbability()),
                                            getString(R.string.today).toLowerCase())
                            ),

                            asList(
                                    getString(R.string.visibility), String.format("%s %s", ForecastTools.intForm.format(currently.getVisibility()),
                                            getString(R.string.visibility_unit)),
                                    getString(R.string.ozone), String.format("%s %s", ForecastTools.intForm.format(currently.getOzone()), getString(R.string.ozone_unit)),
                                    getString(R.string.dew_point), ForecastTools.tempForm.format(currently.getDewPoint())
                            )
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
