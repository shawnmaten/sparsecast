package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Charts;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;

public class Next24HoursTab extends Tab {
    private View nextHourSection;
    private View next24HoursSection;
    private VerticalWeatherBar verticalWeatherBar;

    public static Next24HoursTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        Next24HoursTab tab = new Next24HoursTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextHourSection = view.findViewById(R.id.next_hour_section);
        next24HoursSection = view.findViewById(R.id.next_24_hours_section);
        verticalWeatherBar = (VerticalWeatherBar) next24HoursSection
                .findViewById(R.id.vertical_weather_bar);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        ViewGroup.LayoutParams layoutParams = nextHourSection.getLayoutParams();
        layoutParams.height = screenHeight - screenWidth;
        nextHourSection.setLayoutParams(layoutParams);

        next24HoursSection.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                ViewGroup.LayoutParams layoutParams1 = next24HoursSection.getLayoutParams();
                layoutParams1.height = screenHeight - getMinPhotoHeight();
                next24HoursSection.setLayoutParams(layoutParams1);
            }
        });
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (getUserVisibleHint() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;

            verticalWeatherBar.setData(forecast);

            Forecast.DataPoint currently = forecast.getCurrently();
            Forecast.DataBlock hourly = forecast.getHourly();

            View root = getView();

            LineChart chart = (LineChart) root.findViewById(R.id.precipitation_chart);
            TextView nextHourSummary = (TextView) root.findViewById(R.id.next_hour_summary);
            TextView nearestStorm = (TextView) root.findViewById(R.id.nearest_storm);
            TextView next24HourSummary = (TextView)
                    root.findViewById(R.id.next_24_hours_summary);

            if (forecast.getMinutely() != null) {
                nextHourSection.setVisibility(View.VISIBLE);
                nextHourSummary.setText(forecast.getMinutely().getSummary());
                Charts.setPrecipitationGraph(getActivity(), chart,
                        forecast.getMinutely().getData(), forecast.getTimezone());
            } else
                nextHourSection.setVisibility(View.GONE);

            if ((int) currently.getNearestStormDistance() > 0) {
                nearestStorm.setVisibility(View.VISIBLE);
                nearestStorm.setText(String.format(
                        "(%s: %d %s %s %s)",
                        getString(R.string.nearest_storm),
                        (int) currently.getNearestStormDistance(),
                        getString(LocalizationSettings.getDistanceUnit()),
                        getString(R.string.to_the),
                        getString(ForecastTools.getWindString(currently
                                .getNearestStormBearing()))
                ));
            } else
                nearestStorm.setVisibility(View.GONE);

            next24HourSummary.setText(hourly.getSummary());

            verticalWeatherBar.setData(forecast);
        }
    }
}
