package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
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
import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/20/14.
 */
public class DetailsFragment extends Fragment implements FragmentListener {

    private static final SparseArray<Integer> permIcons = new SparseArray<>();
    static {
        permIcons.put(R.id.sunrise_icon, R.raw.sunrise);
        permIcons.put(R.id.sunset_icon, R.raw.sunset);
        permIcons.put(R.id.wind_icon, R.raw.wind);
        permIcons.put(R.id.visibility_icon, R.raw.fog);
    }

    public DetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_current_details, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        updateView();

    }

    @Override
    public void onNewData() {
        if(isVisible()) {
            updateView();
        }
    }

    private void updateView() {
        ViewGroup parent = (ViewGroup) getView();
        MainActivity activity = (MainActivity) getActivity();

        if (activity.hasForecast()) {
            ArrayList<Integer> iconIds = new ArrayList<>(), iconValues = new ArrayList<>();
            Forecast.Response forecast = activity.getForecast();
            Forecast.DataPoint currently, hour, today;

            DateFormat timeForm = ForecastTools.getTimeForm(forecast.getTimezone());
            DecimalFormat percForm = ForecastTools.getPercForm();
            DecimalFormat intForm = ForecastTools.getIntForm();

            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            ForecastTools.setText(parent, asList(R.id.sunrise_text, R.id.sunset_text,
                            R.id.precip_hour_text, R.id.precip_day_text,
                            R.id.wind_speed, R.id.visibility_text),
                    asList(

                            timeForm.format(today.getSunriseTime()), timeForm.format(today.getSunsetTime()),
                            String.format("%s %s", percForm.format(hour.getPrecipProbability()), getString(R.string.now)),
                            String.format("%s %s", percForm.format(today.getPrecipProbability()), getString(R.string.day)),
                            String.format("%s %s ", intForm.format(currently.getWindSpeed()), getString(R.string.wind_unit)),
                            String.format("%s %s", intForm.format(currently.getVisibility()), getString(R.string.visibility_unit))

                    ));

            iconIds.clear();
            iconValues.clear();

            iconIds.add(R.id.precip_hour_icon);
            iconValues.add(ForecastTools.getWeatherIcon(hour.getPrecipType()));
            iconIds.add(R.id.precip_day_icon);
            iconValues.add(ForecastTools.getWeatherIcon(today.getPrecipType()));
            iconIds.add(R.id.wind_bearing_icon);
            iconValues.add(ForecastTools.getWindString(currently.getWindBearing()));

            for (int i = 0; i < permIcons.size(); i++) {
                ((SVGImageView) parent.findViewById(permIcons.keyAt(i))).setSVG(SVGManager.getSVG(getActivity(), permIcons.valueAt(i)));
            }

            for (int i = 0; i < iconIds.size(); i++) {
                ((SVGImageView) parent.findViewById(iconIds.get(i))).setSVG(SVGManager.getSVG(getActivity(), iconValues.get(i)));
            }
        }

    }

}
