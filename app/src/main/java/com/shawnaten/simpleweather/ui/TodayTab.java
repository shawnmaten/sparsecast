package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Charts;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TodayTab extends Tab {
    public static TodayTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        TodayTab tab = new TodayTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (isVisible() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;
            View root = getView();
            LineChart precipitationChart = (LineChart) root.findViewById(R.id.precipitation_chart);
            LineChart temperatureChart = (LineChart) root.findViewById(R.id.temperature_chart);
            Forecast.DataPoint today = forecast.getDaily().getData()[0];
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(forecast.getCurrently().getTime());
            Forecast.DataPoint todayHourly[] = Arrays.copyOfRange(forecast.getHourly().getData(), 0,
                    23);

            ((TextView) root.findViewById(R.id.next_hour_summary)).setText(today.getSummary());

            Charts.setPrecipitationGraph(getActivity(), precipitationChart, todayHourly,
                    forecast.getTimezone());

            /*
            Charts.setTemperatureGraph(getActivity(), temperatureChart, todayHourly,
                    forecast.getTimezone());*/

            SimpleDateFormat timeDigits = (SimpleDateFormat)
                    SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
            DateFormat timeAmPm = new SimpleDateFormat("a");
            String timeDigitsFormat = timeDigits.toPattern();
            if (timeDigitsFormat.contains("a"))
                timeDigits = new SimpleDateFormat(timeDigitsFormat.replace("a", ""));

            timeDigits.setTimeZone(forecast.getTimezone());
            timeAmPm.setTimeZone(forecast.getTimezone());

            ArrayList<String> labels = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            ArrayList<String> units = new ArrayList<>();

            labels.add(getString(R.string.sunrise));
            values.add(timeDigits.format(today.getSunriseTime()));
            if (timeDigitsFormat.contains("a"))
                units.add(timeAmPm.format(today.getSunriseTime()));
            else
                units.add(null);

            labels.add(getString(R.string.sunset));
            values.add(timeDigits.format(today.getSunsetTime()));
            if (timeDigitsFormat.contains("a"))
                units.add(timeAmPm.format(today.getSunsetTime()));
            else
                units.add(null);

            labels.add(getString(R.string.moon_phase));
            values.add(ForecastTools.getPercForm().format(today.getMoonPhase()));
            units.add(null);

            if (today.getPrecipAccumulation() != 0) {
                labels.add(getString(R.string.snow_accumulation));
                values.add(Double.toString(today.getPrecipAccumulation()));
                units.add(getString(LocalizationSettings.getPrecipitationUnit()));
            }

            LinearLayout statsList = (LinearLayout) root.findViewById(R.id.stats_list);
            LayoutInflater inflater = LayoutInflater.from(statsList.getContext());

            statsList.removeAllViews();
            for (int i = 0; i < labels.size(); i++) {
                View statItem = inflater.inflate(R.layout.stat_item, statsList, false);
                TextView labelView = (TextView) statItem.findViewById(R.id.label);
                TextView dataView = (TextView) statItem.findViewById(R.id.data);
                labelView.setText(labels.get(i));
                dataView.setText(values.get(i));
                if (units.get(i) != null)
                    dataView.append(" " + units.get(i));
                statsList.addView(statItem);
            }
        }
    }
}
