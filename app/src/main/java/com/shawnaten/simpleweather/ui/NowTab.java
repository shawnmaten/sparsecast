package com.shawnaten.simpleweather.ui;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Charts;
import com.shawnaten.tools.Colors;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class NowTab extends Tab {
    private View nextHourSection;
    private LinearLayout weatherBar;

    private int segmentCount;
    private int pointsPerSegment;

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

        nextHourSection = view.findViewById(R.id.next_hour_section);
        View next24HoursSection = view.findViewById(R.id.next_24_hours_section);
        weatherBar = (LinearLayout) next24HoursSection.findViewById(R.id.vertical_weather_bar);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        ViewGroup.LayoutParams layoutParams = nextHourSection.getLayoutParams();
        layoutParams.height = screenHeight - screenWidth;
        nextHourSection.setLayoutParams(layoutParams);

        layoutParams = next24HoursSection.getLayoutParams();
        layoutParams.height = screenHeight -
                (screenWidth / 2 + getResources().getDimensionPixelSize(R.dimen.header_space));
        next24HoursSection.setLayoutParams(layoutParams);
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (getUserVisibleHint() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;
            Forecast.DataPoint currently = forecast.getCurrently();
            Forecast.DataBlock hourly = forecast.getHourly();

            View root = getView();

            LineChart chart = (LineChart) root.findViewById(R.id.precipitation_chart);
            TextView nextHourSummary = (TextView) root.findViewById(R.id.next_hour_summary);
            TextView nearestStorm = (TextView) root.findViewById(R.id.nearest_storm);
            TextView next24HourSummary = (TextView) root.findViewById(R.id.next_24_hours_summary);

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
                        getString(ForecastTools.getWindString(currently.getNearestStormBearing()))
                ));
            } else
                nearestStorm.setVisibility(View.GONE);

            next24HourSummary.setText(hourly.getSummary());

            /*
            weatherBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int segmentHeight = getResources()
                            .getDimensionPixelSize(R.dimen.vertical_weather_bar_segment);
                    Resources res = getResources();

                    LayoutInflater inflater = LayoutInflater.from(weatherBar.getContext());

                    segmentCount = (int) Math.floor(weatherBar.getHeight() / (double) segmentHeight);
                    pointsPerSegment = (int) Math.ceil(24 / (double) segmentCount);

                    segmentCount = (int) Math.ceil(24 / (double) pointsPerSegment);

                    if (weatherBar.getChildCount() != segmentCount) {
                        weatherBar.removeAllViews();
                        for (int i = 0; i < segmentCount; i++) {
                            RelativeLayout item = (RelativeLayout) inflater.inflate(
                                    R.layout.vertical_bar_item, weatherBar, false);
                            weatherBar.addView(item);

                            LinearLayout blocksView = (LinearLayout) item.findViewById(R.id.blocks);
                            for (int j = 0; j < pointsPerSegment; j++) {
                                blocksView.addView(inflater.inflate(R.layout.color_segment,
                                        blocksView, false));
                            }
                        }
                    }

                    SimpleDateFormat timeForm = ForecastTools
                            .getShortTimeForm(forecast.getTimezone(), 24);
                    DecimalFormat tempForm = ForecastTools.getTempForm();

                    String lastSummary = null;
                    for (int i = 0; i < segmentCount; i++) {
                        Forecast.DataPoint dataPoint = hourly.getData()[i * pointsPerSegment];

                        View item = weatherBar.getChildAt(i);
                        TextView timeView = (TextView) item.findViewById(R.id.time);
                        TextView dataView = (TextView) item.findViewById(R.id.data);
                        LinearLayout blocksView = (LinearLayout) item.findViewById(R.id.blocks);
                        TextView summaryView = (TextView) item.findViewById(R.id.summary);

                        timeView.setText(timeForm.format(dataPoint.getTime()));
                        dataView.setText(tempForm.format(dataPoint.getTemperature()));
                        String summary = null;

                        for (int j = 0; j < pointsPerSegment; j++) {
                            Forecast.DataPoint colorDataPoint = hourly.getData()[i + j];
                            summary = colorDataPoint.getSummary();
                            View colorSegment = blocksView.getChildAt(j);
                            colorSegment.setBackgroundColor(getResources()
                                    .getColor(Colors.getColor(colorDataPoint)));
                        }

                        if (lastSummary == null || !lastSummary.equals(summary)) {
                            lastSummary = summary;
                            summaryView.setText(summary);
                        } else
                            summaryView.setText(null);

                    }
                    ((TextView) weatherBar.getChildAt(0).findViewById(R.id.time))
                            .setText(R.string.now);
                    weatherBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            */
        }
    }
}
