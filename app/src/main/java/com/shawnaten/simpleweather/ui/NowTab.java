package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Charts;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastIconSelector;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;
import com.shawnaten.tools.StatsGrid;

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
    public void onNewData(Object data) {
        super.onNewData(data);

        if (isVisible() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;
            View root = getView();
            TextView supportingView = (TextView) root.findViewById(R.id.supporting);
            ImageView iconView = (ImageView) root.findViewById(R.id.icon);
            LineChart precipitationChart = (LineChart) root.findViewById(R.id.precipitation_chart);

            if (forecast.getMinutely() != null) {
                supportingView.setText(forecast.getMinutely().getSummary());
                iconView.setImageResource(ForecastIconSelector.getImageId(forecast.getMinutely()
                        .getIcon()));

                precipitationChart.setDescription(null);
                if (!Charts.setPrecipitationGraph(getActivity(), precipitationChart,
                        forecast.getMinutely().getData(), forecast.getTimezone()))
                    root.findViewById(R.id.precipitation_card).setVisibility(View.GONE);
            }
            else {
                supportingView.setText(forecast.getHourly().getData()[0].getSummary());
                iconView.setImageResource(ForecastIconSelector.getImageId(forecast.getHourly()
                        .getData()[0].getIcon()));
                root.findViewById(R.id.precipitation_card).setVisibility(View.GONE);
            }

            ArrayList<Integer> labels = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            ArrayList<String> units = new ArrayList<>();

            Forecast.DataPoint currently = forecast.getCurrently();

            labels.add(R.string.nearest_storm);
            values.add(ForecastTools.getIntForm().format(currently.getNearestStormDistance()));
            units.add(getString(LocalizationSettings.getDistanceUnit()) + " " +
                    getString(ForecastTools.getWindString(currently.getNearestStormBearing())));

            labels.add(R.string.dew_point);
            values.add(ForecastTools.getTempForm().format(currently.getDewPoint()));
            units.add(null);

            labels.add(R.string.wind);
            values.add(ForecastTools.getIntForm().format(currently.getWindSpeed()));
            units.add(getString(LocalizationSettings.getSpeedUnit()) + " " +
                    getString(ForecastTools.getWindString(currently.getWindBearing())));

            labels.add(R.string.cloud_cover);
            values.add(ForecastTools.getPercForm().format(currently.getCloudCover()));
            units.add(null);

            labels.add(R.string.humidity);
            values.add(ForecastTools.getPercForm().format(currently.getHumidity()));
            units.add(null);

            labels.add(R.string.pressure);
            values.add(ForecastTools.getIntForm().format(currently.getPressure()));
            units.add(getString(LocalizationSettings.getPressureUnit()));

            labels.add(R.string.visibility);
            values.add(ForecastTools.getIntForm().format(currently.getVisibility()));
            units.add(getString(LocalizationSettings.getDistanceUnit()));

            labels.add(R.string.ozone);
            values.add(ForecastTools.getIntForm().format(currently.getOzone()));
            units.add(getString(R.string.dobson_units));

            LinearLayout statsGrid = (LinearLayout) root.findViewById(R.id.stats_grid);

            StatsGrid.configureGrid(getActivity(), labels, values, units, statsGrid);
        }
    }
}
