package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        View nextHourSection = view.findViewById(R.id.next_hour_section);
        View next24HoursSection = view.findViewById(R.id.next_24_hours_section);
        View weatherBarHolder = next24HoursSection.findViewById(R.id.vertical_weather_bar_holder);
        View next24HoursSummary = next24HoursSection.findViewById(R.id.next_24_hours_summary);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        ViewGroup.LayoutParams layoutParams = nextHourSection.getLayoutParams();
        layoutParams.height = screenHeight - screenWidth;
        nextHourSection.setLayoutParams(layoutParams);

        layoutParams = next24HoursSection.getLayoutParams();
        layoutParams.height = screenHeight - screenWidth / 2;
        next24HoursSection.setLayoutParams(layoutParams);

        int segment = getResources().getDimensionPixelSize(R.dimen.vertical_weather_bar_segment);
        next24HoursSummary.addOnLayoutChangeListener((view1, i, i1, i2, i3, i4, i5, i6, i7) -> {
            segmentCount = (int) Math.floor(weatherBarHolder.getHeight() / (double) segment);
            pointsPerSegment = (int) Math.round(24 / (double) segmentCount);
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
            LinearLayout verticalWeatherBar = (LinearLayout)
                    root.findViewById(R.id.vertical_weather_bar);

            if (forecast.getMinutely() != null) {
                nextHourSummary.setText(forecast.getMinutely().getSummary());
                root.findViewById(R.id.space_1).setVisibility(View.VISIBLE);
                chart.setVisibility(View.VISIBLE);
                Charts.setPrecipitationGraph(getActivity(), chart,
                        forecast.getMinutely().getData(), forecast.getTimezone());
            }
            else {
                nextHourSummary.setText(forecast.getHourly().getData()[0].getSummary());
                root.findViewById(R.id.space_1).setVisibility(View.GONE);
                chart.setVisibility(View.GONE);
            }

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
            LayoutInflater inflater = LayoutInflater.from(verticalWeatherBar.getContext());
            DecimalFormat tempForm = ForecastTools.getTempForm();

            if (verticalWeatherBar.getChildCount() != segmentCount) {
                for (int i = 0; i < segmentCount; i++) {
                    RelativeLayout item = (RelativeLayout) inflater.inflate(
                            R.layout.vertical_bar_item, verticalWeatherBar, false);
                    verticalWeatherBar.addView(item);

                    LinearLayout blocksView = (LinearLayout) item.findViewById(R.id.blocks);
                    for (int j = 0; j < pointsPerSegment; j++) {
                        blocksView.addView(inflater.inflate(R.layout.color_segment, blocksView,
                                false));
                    }
                }

            }

            String lastSummary = null;
            for (int i = 0; i < segmentCount; i++) {
                Forecast.DataPoint dataPoint = hourly.getData()[i * pointsPerSegment];

                View item = verticalWeatherBar.getChildAt(i);
                TextView timeView = (TextView) item.findViewById(R.id.time);
                TextView dataView = (TextView) item.findViewById(R.id.data);
                LinearLayout blocksView = (LinearLayout) item.findViewById(R.id.blocks);
                TextView summaryView = (TextView) item.findViewById(R.id.summary);

                timeView.setText(timeForm.format(dataPoint.getTime()));
                dataView.setText(tempForm.format(dataPoint.getTemperature()));
                //ArrayMap<String, Integer> summaries = new ArrayMap<>();
                String summary = null;
                for (int j = 0; j < pointsPerSegment; j++) {
                    Forecast.DataPoint colorDataPoint = hourly.getData()[i + j];
                    summary = colorDataPoint.getSummary();
                    /*
                    if (summaries.containsKey(summary)) {
                        int count = summaries.get(summary);
                        summaries.put(summary, count + 1);
                    } else {
                        summaries.put(summary, 1);
                    }
                    */
                    View colorSegment = blocksView.getChildAt(j);
                    colorSegment.setBackgroundColor(getResources()
                            .getColor(Colors.getColor(colorDataPoint)));
                }
                //List values = new ArrayList<>(summaries.values());
                //Collections.sort(values);
                //String summary = summaries.keyAt(values.size() - 1);

                if (lastSummary == null || !lastSummary.equals(summary)) {
                    lastSummary = summary;
                    summaryView.setText(summary);
                } else
                    summaryView.setText(null);

            }
            ((TextView) verticalWeatherBar.getChildAt(3).findViewById(R.id.time))
                    .setText(R.string.now);
        }
    }
}
