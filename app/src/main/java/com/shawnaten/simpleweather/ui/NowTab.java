package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Charts;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;

import java.util.ArrayList;

public class NowTab extends Tab {
    public static NowTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        NowTab tab = new NowTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View chartHourSummary = view.findViewById(R.id.next_hour_section);
        View attributions = getBaseActivity().findViewById(R.id.attributions);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        attributions.addOnLayoutChangeListener((view1, i, i1, i2, i3, i4, i5, i6, i7) -> {
            ViewGroup.LayoutParams layoutParams = chartHourSummary.getLayoutParams();
            layoutParams.height = screenHeight - attributions.getHeight() - screenWidth;
            chartHourSummary.setLayoutParams(layoutParams);
        });

    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (isVisible() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;
            Forecast.DataPoint currently = forecast.getCurrently();
            Forecast.DataBlock hourly = forecast.getHourly();

            View root = getView();

            LineChart chart = (LineChart) root.findViewById(R.id.precipitation_chart);
            TextView nextHourSummary = (TextView) root.findViewById(R.id.next_hour_summary);
            TextView nearestStorm = (TextView) root.findViewById(R.id.nearest_storm);
            TextView next24HourSummary = (TextView) root.findViewById(R.id.next_24_hours_summary);

            if (forecast.getMinutely() != null) {
                nextHourSummary.setText(forecast.getMinutely().getSummary());
                chart.setDescription(null);
                Charts.setPrecipitationGraph(getActivity(), chart,
                        forecast.getMinutely().getData(), forecast.getTimezone());
            }
            else
                nextHourSummary.setText(forecast.getHourly().getData()[0].getSummary());

            if ((int) currently.getNearestStormDistance() > 0) {
                nearestStorm.setVisibility(View.VISIBLE);
                nearestStorm.setText(String.format(
                        "(%s: %d %s %s %s)",
                        getString(R.string.nearest_storm),
                        (int) currently.getNearestStormDistance(),
                        getString(LocalizationSettings.getDistanceUnit()),
                        getString(R.string.to_the),
                        getString(ForecastTools.getWindString(currently.getNearestStormBearing()))
                ));
            } else
                nearestStorm.setVisibility(View.GONE);

            next24HourSummary.setText(hourly.getSummary());

            ArrayList<String> labels = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            ArrayList<String> units = new ArrayList<>();

            labels.add(getString(R.string.dew_point));
            values.add(ForecastTools.getTempForm().format(currently.getDewPoint()));
            units.add(null);

            labels.add(getString(R.string.wind));
            values.add(ForecastTools.getIntForm().format(currently.getWindSpeed()));
            units.add(getString(LocalizationSettings.getSpeedUnit()) + " " +
                    getString(ForecastTools.getWindString(currently.getWindBearing())));

            labels.add(getString(R.string.humidity));
            values.add(ForecastTools.getPercForm().format(currently.getHumidity()));
            units.add(null);

            labels.add(getString(R.string.pressure));
            values.add(ForecastTools.getIntForm().format(currently.getPressure()));
            units.add(getString(LocalizationSettings.getPressureUnit()));

            labels.add(getString(R.string.visibility));
            values.add(ForecastTools.getIntForm().format(currently.getVisibility()));
            units.add(getString(LocalizationSettings.getDistanceUnit()));

            labels.add(getString(R.string.ozone));
            values.add(ForecastTools.getIntForm().format(currently.getOzone()));
            units.add(getString(R.string.dobson_units));

            //LinearLayout statsGrid = (LinearLayout) root.findViewById(R.id.stats_grid);

            //StatsGrid.configureGrid(getActivity(), labels, values, units, statsGrid);

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
