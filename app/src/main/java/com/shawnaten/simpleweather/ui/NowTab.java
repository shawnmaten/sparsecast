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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

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

        View nextHourSection = view.findViewById(R.id.next_hour_section);
        View next24HoursSection = view.findViewById(R.id.next_24_hours_section);
        View attributions = getBaseActivity().findViewById(R.id.attributions);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        attributions.addOnLayoutChangeListener((view1, i, i1, i2, i3, i4, i5, i6, i7) -> {
            ViewGroup.LayoutParams layoutParams = nextHourSection.getLayoutParams();
            layoutParams.height = screenHeight - attributions.getHeight() - screenWidth;
            nextHourSection.setLayoutParams(layoutParams);

            /*
            layoutParams = next24HoursSection.getLayoutParams();
            layoutParams.height = screenHeight - attributions.getHeight() - screenWidth / 2;
            next24HoursSection.setLayoutParams(layoutParams);
            */
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
            LinearLayout next24HoursSection = (LinearLayout)
                    root.findViewById(R.id.next_24_hours_section);

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

            SimpleDateFormat timeForm = ForecastTools.getShortTimeForm(forecast.getTimezone(), 24);
            LayoutInflater inflater = LayoutInflater.from(next24HoursSection.getContext());
            DecimalFormat tempForm = ForecastTools.getTempForm();

            if (next24HoursSection.getChildCount() == 3) {
                for (int i = 0; i < 24; i++) {
                    next24HoursSection.addView(inflater.inflate(R.layout.vertical_bar_item,
                            next24HoursSection, false));
                }
            }
            for (int i = 0; i < 24; i++) {
                Forecast.DataPoint dataPoint = hourly.getData()[i];
                View item = next24HoursSection.getChildAt(3 + i);
                TextView timeView = (TextView) item.findViewById(R.id.time);
                TextView dataView = (TextView) item.findViewById(R.id.data);
                TextView summaryView = (TextView) item.findViewById(R.id.summary);

                timeView.setText(timeForm.format(dataPoint.getTime()));
                dataView.setText(tempForm.format(dataPoint.getTemperature()));
                summaryView.setText(dataPoint.getSummary());
            }
        }
    }
}
