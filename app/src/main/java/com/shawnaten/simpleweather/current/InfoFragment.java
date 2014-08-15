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

import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/20/14.
 */
public class InfoFragment extends Fragment implements FragmentListener {
    private ArrayList<Integer> iconIds = new ArrayList<>(), iconValues = new ArrayList<>();

    public InfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_current_info, container, false);
    }

    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            iconIds = savedInstanceState.getIntegerArrayList("iconIds");
            iconValues = savedInstanceState.getIntegerArrayList("iconValues");
        }

        ViewGroup parent = (ViewGroup) getView();

        if (MainActivity.hasForecast() && MainActivity.getForecast().isUnread(getTag())) {
            Forecast.Response forecast = MainActivity.getForecast();
            Forecast.DataPoint currently, hour, today;

            ForecastTools.timeForm.setTimeZone(forecast.getTimezone());

            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            ForecastTools.setText(parent, asList(R.id.sunrise_text, R.id.sunset_text,
                            R.id.precip_hour_text, R.id.precip_day_text,
                            R.id.wind_speed, R.id.visibility_text),
                    asList(

                            ForecastTools.timeForm.format(today.getSunriseTime()), ForecastTools.timeForm.format(today.getSunsetTime()),
                            String.format("%s %s", ForecastTools.percForm.format(hour.getPrecipProbability()), getString(R.string.now)),
                            String.format("%s %s", ForecastTools.percForm.format(today.getPrecipProbability()), getString(R.string.day)),
                            String.format("%s %s", ForecastTools.intForm.format(currently.getWindSpeed()), getString(R.string.wind_unit)),
                            String.format("%s %s", ForecastTools.intForm.format(currently.getVisibility()), getString(R.string.visibility_unit))

                    ));

            iconIds.clear();
            iconValues.clear();

            iconIds.add(R.id.precip_hour_icon);
            iconValues.add(ForecastTools.getWeatherIcon(hour.getPrecipType()));
            iconIds.add(R.id.precip_day_icon);
            iconValues.add(ForecastTools.getWeatherIcon(today.getPrecipType()));
            iconIds.add(R.id.wind_bearing_icon);
            iconValues.add(ForecastTools.getWindString(currently.getWindBearing()));

            forecast.setRead(getTag());
        }

        for (int i = 0; i < iconIds.size(); i++) {
            ((SVGImageView) parent.findViewById(iconIds.get(i))).setImageResource(iconValues.get(i));
        }

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntegerArrayList("iconIds", iconIds);
        outState.putIntegerArrayList("iconValues", iconValues);
    }

    @Override
    public void onButtonClick(int id) {

    }
}
