package com.shawnaten.simpleweather.current;

import android.os.Bundle;
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

import static java.util.Arrays.asList;

/**
 * Created by Shawn Aten on 7/20/14.
 */
public class StatsFragment extends Fragment implements FragmentListener {
    private final String weatherIconValueKey = "weatherIconValue";
    private int weatherIconValue;

    public StatsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            weatherIconValue = savedInstanceState.getInt(weatherIconValueKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.tab_current_stats, container, false);
        view.setSelected(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup parent = (ViewGroup) getView().findViewById(R.id.stats);

        if (MainActivity.hasForecast() && MainActivity.getForecast().isUnread(getTag())) {
            Forecast.Response forecast = MainActivity.getForecast();

            Forecast.DataPoint currently, hour, today;
            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            ForecastTools.timeForm.setTimeZone(forecast.getTimezone());

            weatherIconValue = ForecastTools.getWeatherIcon(currently.getIcon());

            ForecastTools.setText(parent, asList(R.id.title, R.id.temp, R.id.humidity, R.id.high_temp, R.id.high_temp_time, R.id.low_temp, R.id.low_temp_time, R.id.time, R.id.currently),
                    asList(
                            ((MainActivity) getActivity()).getLocationName(),
                            ForecastTools.tempForm.format(currently.getTemperature()),
                            ForecastTools.percForm.format(currently.getHumidity()),
                            ForecastTools.tempForm.format(today.getTemperatureMax()),
                            ForecastTools.timeForm.format(today.getTemperatureMaxTime()),
                            ForecastTools.tempForm.format(today.getTemperatureMin()),
                            ForecastTools.timeForm.format(today.getTemperatureMinTime()),
                            ForecastTools.timeForm.format(currently.getTime()),
                            String.format("%s - %s %s", currently.getSummary(), getString(R.string.feels_like), ForecastTools.tempForm.format(currently.getApparentTemperature()))
                    )
            );

            forecast.setRead(getTag());
        }

        ((SVGImageView) parent.findViewById(R.id.weather_icon)).setImageResource(weatherIconValue);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(weatherIconValueKey, weatherIconValue);
    }

    @Override
    public void onButtonClick(int id) {

    }

}
