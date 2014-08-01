package com.shawnaten.main.map;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shawnaten.main.R;
import com.shawnaten.networking.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.WeatherBarShape;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;
    private static WeatherBarShape weatherBar;

    private RelativeLayout weatherBarTexts;
    private ImageView weatherBarImage;

    public MapFragment() {

    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {
            if (forecast.getHourly() != null) {
                Forecast.DataPoint hourly[] = forecast.getHourly().getData();
                weatherBar = new WeatherBarShape(getActivity(), hourly, 0, 24, 768, 64);
                ForecastTools.createWeatherBarTextViews((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), weatherBar, weatherBarTexts);
                ForecastTools.setWeatherBarText(weatherBar, hourly, forecast.getTimezone(), weatherBarTexts);
                ShapeDrawable drawable = new ShapeDrawable(weatherBar);
                weatherBarImage.setBackgroundDrawable(drawable);
            }
            forecast = null;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_map, container, false);

        weatherBarTexts = (RelativeLayout) view.findViewById(R.id.scroll_container)
                .findViewById(R.id.weather_bar_container).findViewById(R.id.weather_bar_texts);
        weatherBarImage = (ImageView) view.findViewById(R.id.scroll_container)
                .findViewById(R.id.weather_bar_container).findViewById(R.id.weather_bar);

        if (weatherBar != null) {
            ForecastTools.createWeatherBarTextViews(inflater, weatherBar, weatherBarTexts);
            ShapeDrawable drawable = new ShapeDrawable(weatherBar);
            weatherBarImage.setBackgroundDrawable(drawable);
        }

        return view;
    }

    @Override
    public void onNewData(Forecast.Response data) {
        this.forecast = data;
        if (isVisible())
            onResume();
    }

    @Override
    public void onButtonClick(View view) {

    }

}
