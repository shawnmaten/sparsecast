package com.shawnaten.simpleweather.current;

// test comment

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caverock.androidsvg.SVGImageView;
import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.SVGManager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import static java.util.Arrays.asList;

/**
 * Created by Shawn Aten on 7/20/14.
 */
public class StatsFragment extends Fragment implements FragmentListener {

    public StatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_current_stats, container, false);
    }

    @Override
    public void onResume() {
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
            ViewGroup parent = (ViewGroup) getView();
            Forecast.Response forecast = activity.getForecast();
            Forecast.DataPoint currently, hour, today;
            String moisturePref, moistureLabel, moistureValue;

            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            DateFormat timeForm = ForecastTools.getTimeForm(forecast.getTimezone());
            SimpleDateFormat shortTimeForm = ForecastTools.getShortTimeForm(forecast.getTimezone(), 24);
            DecimalFormat percForm = ForecastTools.getPercForm();
            DecimalFormat tempForm = ForecastTools.getTempForm();

            moisturePref = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.moisture_key), "humidity");

            switch (moisturePref) {
                case "dew":
                    moistureLabel = getString(R.string.dew_point);
                    moistureValue = tempForm.format(currently.getDewPoint());
                    break;
                default:
                    moistureLabel = getString(R.string.humidity);
                    moistureValue = percForm.format(currently.getHumidity());
            }

            ForecastTools.setText(parent, asList(R.id.title, R.id.temp, R.id.moisture_label, R.id.moisture, R.id.high_temp, R.id.high_temp_time, R.id.low_temp, R.id.low_temp_time, R.id.time, R.id.currently),
                    asList(
                            forecast.getName(),
                            tempForm.format(currently.getTemperature()),
                            moistureLabel, moistureValue,
                            tempForm.format(today.getTemperatureMax()),
                            String.format("%s %s", getString(R.string.high), shortTimeForm.format(today.getTemperatureMaxTime())),
                            tempForm.format(today.getTemperatureMin()),
                            String.format("%s %s", getString(R.string.low), shortTimeForm.format(today.getTemperatureMinTime())),
                            timeForm.format(currently.getTime()),
                            String.format("%s - %s %s", currently.getSummary(), getString(R.string.feels_like), tempForm.format(currently.getApparentTemperature()))
                    )
            );


            ((SVGImageView) parent.findViewById(R.id.weather_icon)).setSVG(SVGManager.getSVG(getActivity(),
                ForecastTools.getWeatherIcon(currently.getIcon())));
        }

    }

}
