package com.shawnaten.main.current;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;
import com.shawnaten.main.R;
import com.shawnaten.networking.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;

import static java.util.Arrays.asList;

/**
 * Created by Shawn Aten on 7/20/14.
 */
public class MainStatsFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;
    private static Boolean newData = false;

    public MainStatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_stats, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {

            //noinspection ConstantConditions
            RelativeLayout parent = (RelativeLayout) getView().findViewById(R.id.main_stats);
            ImageView weatherIcon = (ImageView) parent.findViewById(R.id.weather_icon);

            setIcon(weatherIcon);

            if (newData) {
                Forecast.DataPoint currently = forecast.getCurrently();
                Forecast.DataBlock daily = forecast.getDaily();

                ForecastTools.setText(parent, asList(R.id.temp, R.id.humidity, R.id.high_temp, R.id.high_temp_time, R.id.low_temp, R.id.low_temp_time, R.id.feels_like),
                        asList(
                                ForecastTools.tempForm.format(currently.getTemperature()),
                                ForecastTools.percForm.format(currently.getHumidity()),
                                ForecastTools.tempForm.format(daily.getData()[0].getTemperatureMax()),
                                ForecastTools.timeForm.format(daily.getData()[0].getTemperatureMaxTime()),
                                ForecastTools.tempForm.format(daily.getData()[0].getTemperatureMin()),
                                ForecastTools.timeForm.format(daily.getData()[0].getTemperatureMinTime()),
                                String.format("%s %s", getString(R.string.feels_like), ForecastTools.tempForm.format(currently.getApparentTemperature()))
                        )
                );

                newData = false;
            }
        }
    }

    @Override
    public void onNewData(Forecast.Response data) {
        forecast = data;
        newData = true;
        if (isVisible())
            onResume();
    }

    @Override
    public void onButtonClick(View view) {

    }

    private void setIcon(ImageView imageView) {
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        SVG svg = new SVGBuilder()
                .readFromResource(getResources(), ForecastTools.getIconValue(forecast.getCurrently().getTime(), forecast.getDaily().getData()[0].getSunriseTime(),
                        forecast.getDaily().getData()[0].getSunsetTime(), forecast.getCurrently().getIcon()))
                .build();

        Drawable drawable = svg.getDrawable();
        imageView.setImageDrawable(drawable);
    }
}
