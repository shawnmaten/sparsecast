package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
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
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.info, container, false);

        if (savedInstanceState != null) {
            iconIds = savedInstanceState.getIntegerArrayList("iconIds");
            iconValues = savedInstanceState.getIntegerArrayList("iconValues");
        }

        if (MainActivity.hasForecast() && MainActivity.getForecast().isUnread(getTag())) {

            Log.e("settingData", Long.toString(SystemClock.currentThreadTimeMillis()));

            Forecast.Response forecast = MainActivity.getForecast();
            Forecast.DataPoint currently, hour, today;

            ForecastTools.timeForm.setTimeZone(forecast.getTimezone());

            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            ForecastTools.setText(parent, asList(R.id.sunrise, R.id.sunset, R.id.precip_hour_text, R.id.precip_day_text),
                    asList(

                            ForecastTools.timeForm.format(today.getSunriseTime()), ForecastTools.timeForm.format(today.getSunsetTime()),
                            String.format("%s %s", ForecastTools.percForm.format(hour.getPrecipProbability()), getString(R.string.now)),
                            String.format("%s %s", ForecastTools.percForm.format(today.getPrecipProbability()), getString(R.string.day))

                    ));

            iconIds.add(R.id.precip_hour_icon);
            iconValues.add(ForecastTools.getWeatherIcon(hour.getPrecipType()));
            iconIds.add(R.id.precip_day_icon);
            iconValues.add(ForecastTools.getWeatherIcon(today.getPrecipType()));

            forecast.setRead(getTag());
        }

        for (int i = 0; i < iconIds.size(); i++)
            ((SVGImageView) parent.findViewById(iconIds.get(i))).setImageResource(iconValues.get(i));

        return parent;
    }

    /*
    @Override
    public void onResume () {
        super.onResume();

        if (MainActivity.hasForecast() && MainActivity.getForecast().isUnread(getTag())) {

            Forecast.Response forecast = MainActivity.getForecast();
            Forecast.DataPoint currently, hour, today;
            ViewGroup parent = (ViewGroup) getView();

            ForecastTools.timeForm.setTimeZone(forecast.getTimezone());

            currently = forecast.getCurrently();
            hour = forecast.getHourly().getData()[0];
            today = forecast.getDaily().getData()[0];

            ForecastTools.setText(parent, asList(R.id.sunrise, R.id.sunset, R.id.precip_hour_text, R.id.precip_day_text),
                    asList(

                            ForecastTools.timeForm.format(today.getSunriseTime()), ForecastTools.timeForm.format(today.getSunsetTime()),
                            String.format("%s %s", ForecastTools.percForm.format(hour.getPrecipProbability()), getString(R.string.hour)),
                            String.format("%s %s", ForecastTools.percForm.format(today.getPrecipProbability()), getString(R.string.day))

            ));

            iconIds.add(R.id.precip_hour_icon);
            iconValues.add(ForecastTools.getWeatherIcon(hour.getPrecipType()));


            HashMap<Integer, ArrayList<int[]>> spanIndices = new HashMap<>();
            HashMap<Integer, SpannableStringBuilder> strings = new HashMap<>();
            SpannableStringBuilder tempString;
            ArrayList<int[]> tempSpanIndices;

            // line one
            tempString = new SpannableStringBuilder();
            tempSpanIndices = new ArrayList<>();
            tempSpanIndices.add(new int[]{0, 1});
            tempSpanIndices.add(new int[]{tempString.append(ForecastTools.CLIMACONS_SUNRISE).append(' ')
                    .append(ForecastTools.timeForm.format(today.getSunriseTime())).append(ForecastTools.SPACING).length(), 1});
            tempString.append(ForecastTools.CLIMACONS_SUNSET).append(' ').append(ForecastTools.timeForm.format(today.getSunsetTime()));
            strings.put(R.id.one, tempString);
            spanIndices.put(R.id.one, tempSpanIndices);

            // line two
            tempString = new SpannableStringBuilder();
            tempSpanIndices = new ArrayList<>();
            tempSpanIndices.add(new int[]{0, 1});
            tempString.append(ForecastTools.CLIMACONS_WIND);
            tempString.append(String.format(" %s %s ", ForecastTools.intForm.format(currently.getWindSpeed()), getString(R.string.wind_unit)));
            tempSpanIndices.add(new int[]{tempString.append(getString(ForecastTools.getWindString(currently.getWindBearing())))
                    .append(ForecastTools.SPACING).length(), 1});

            tempString.append(ForecastTools.CLIMACONS_VISIBILITY).append(' ');
            tempString.append(String.format("%s %s%s", ForecastTools.intForm.format(currently.getVisibility()),
                    getString(R.string.visibility_unit), ForecastTools.SPACING));
            strings.put(R.id.three, tempString);
            spanIndices.put(R.id.three, tempSpanIndices);

            // line three
            tempString = new SpannableStringBuilder();
            tempSpanIndices = new ArrayList<>();

            tempSpanIndices.add(new int[]{tempString.append(ForecastTools.percForm.format(hour.getPrecipProbability()))
                    .append(' ').length(), 1});
            tempString.append(ForecastTools.getPrecipCode(hour.getPrecipType())).append(' ');
            tempString.append(getString(R.string.hour)).append(ForecastTools.SPACING);

            tempSpanIndices.add(new int[]{tempString.append(ForecastTools.percForm.format(today.getPrecipProbability()))
                    .append(' ').length(), 1});
            tempString.append(ForecastTools.getPrecipCode(today.getPrecipType())).append(' ');
            tempString.append(getString(R.string.day));
            strings.put(R.id.two, tempString);
            spanIndices.put(R.id.two, tempSpanIndices);

            ClimaconTypefaceSpan.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/climacons.ttf"));
            ForecastTools.setClimaconSpans((ViewGroup) getView(), strings, spanIndices);


            forecast.setRead(getTag());
        }
    }
    */

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
