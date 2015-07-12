package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.ui.widget.VerticalWeatherBar;
import com.shawnaten.tools.Charts;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;

import java.text.DateFormat;
import java.util.Date;

public class Next24HoursTab extends Tab {
    private View nextHourAndStatsSection;
    private View nextHourSection;
    private View statsSection;
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

        nextHourAndStatsSection = view.findViewById(R.id.next_hour_and_stats_section);
        nextHourSection = nextHourAndStatsSection.findViewById(R.id.next_hour_section);
        statsSection = nextHourAndStatsSection.findViewById(R.id.stats_section);
        next24HoursSection = view.findViewById(R.id.next_24_hours_section);
        verticalWeatherBar = (VerticalWeatherBar) next24HoursSection
                .findViewById(R.id.vertical_weather_bar);

        ViewGroup.LayoutParams layoutParams = nextHourAndStatsSection.getLayoutParams();
        layoutParams.height = screenHeight - screenWidth;
        nextHourAndStatsSection.setLayoutParams(layoutParams);

        nextHourAndStatsSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statsSection.getVisibility() == View.INVISIBLE) {
                    statsSection.setVisibility(View.VISIBLE);
                    nextHourSection.setVisibility(View.INVISIBLE);
                } else {
                    statsSection.setVisibility(View.INVISIBLE);
                    nextHourSection.setVisibility(View.VISIBLE);
                }
            }
        });

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
    public void onScrollChanged(int deltaX, int deltaY) {
        super.onScrollChanged(deltaX, deltaY);

        int scrollAmount = scroll.getScrollY();

        if (scrollAmount > screenHeight - screenWidth) {
            if (!fab.isMenuButtonHidden())
                fab.hideMenuButton(true);
        } else {
            if (fab.isMenuButtonHidden())
                fab.showMenuButton(true);
        }
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (isVisible() && Forecast.Response.class.isInstance(data)) {
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
            TextView sunset = (TextView) root.findViewById(R.id.sunset);

            if (forecast.getMinutely() != null) {
                statsSection.setVisibility(View.INVISIBLE);
                nextHourSection.setVisibility(View.VISIBLE);
                nextHourAndStatsSection.setClickable(true);
                nextHourSummary.setText(forecast.getMinutely().getSummary());
                Charts.setPrecipitationGraph(getActivity(), chart,
                        forecast.getMinutely().getData(), forecast.getTimezone());
            } else {
                statsSection.setVisibility(View.VISIBLE);
                nextHourSection.setVisibility(View.INVISIBLE);
                nextHourAndStatsSection.setClickable(false);
            }

            if ((int) currently.getNearestStormDistance() > 0) {
                nearestStorm.setVisibility(View.VISIBLE);
                nearestStorm.setText(String.format(
                        "(%s: %d %s %s)",
                        getString(R.string.nearest_storm),
                        (int) currently.getNearestStormDistance(),
                        getString(LocalizationSettings.getDistanceUnit()),
                        getString(ForecastTools.getWindString(currently
                                .getNearestStormBearing()))
                ));
            } else
                nearestStorm.setVisibility(View.GONE);

            next24HourSummary.setText(hourly.getSummary());

            verticalWeatherBar.setData(forecast);

            Date nowTime, sunTime;
            Forecast.DataPoint today, tomorrow;

            today = forecast.getDaily().getData()[0];
            tomorrow = forecast.getDaily().getData()[1];

            nowTime = forecast.getCurrently().getTime();

            int sunString;

            if (nowTime.before(today.getSunriseTime())) {
                sunTime = today.getSunriseTime();
                sunString = R.string.sunrise;
            } else if (nowTime.before(today.getSunsetTime())) {
                sunTime = today.getSunsetTime();
                sunString = R.string.sunset;
            } else {
                sunTime = tomorrow.getSunriseTime();
                sunString = R.string.sunrise;
            }

            long difference = sunTime.getTime() - nowTime.getTime();
            long hours = difference / 3600000;
            difference -= hours * 3600000;
            long minutes = difference / 60000;

            if (hours == 0 && minutes == 0) {
                if (sunString == R.string.sunrise) {
                    sunTime = today.getSunsetTime();
                    sunString = R.string.sunset;
                } else {
                    sunTime = tomorrow.getSunriseTime();
                    sunString = R.string.sunrise;
                }
            }

            difference = sunTime.getTime() - nowTime.getTime();
            hours = difference / 3600000;
            difference -= hours * 3600000;
            minutes = difference / 60000;

            String text = String.format("%s %s", getString(sunString), getString(R.string.in));

            if (hours > 0) {
                text += String.format(" %d %s", hours, hours > 1 ? getString(R.string.hours_short)
                        : getString(R.string.hour_short));
            }

            if (minutes > 0) {
                text += String.format(" %d %s", minutes, minutes > 1 ? getString(R.string.minutes_short)
                        : getString(R.string.minute_short));
            }

            DateFormat dateFormat = ForecastTools.getTimeForm(forecast.getTimezone());

            text += String.format(" (%s)", dateFormat.format(sunTime.getTime()));

            sunset.setText(text);
        }
    }
}
