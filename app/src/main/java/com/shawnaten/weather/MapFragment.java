package com.shawnaten.weather;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shawnaten.networking.Forecast;

/**
 * Created by shawnaten on 7/12/14.
 */
public class MapFragment extends Fragment implements TabDataListener {
    private Forecast.Response forecast;
    private WeatherBarShape weatherBar;
    private RelativeLayout weatherBarTexts;
    private ImageView weatherBarImage;

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("weatherBar"))
                weatherBar = savedInstanceState.getParcelable("weatherBar");
        }
    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {
            if (forecast.getHourly() != null) {
                Forecast.DataPoint hourly[] = forecast.getHourly().getData();
                weatherBar = new WeatherBarShape(getActivity(), hourly, 0, 24, 768, 64);
                weatherBar.setData(getActivity().getResources().getColor(android.R.color.tertiary_text_light), Tabs.parseWeatherPoints(getActivity(), hourly, 0, 24));
                Tabs.createWeatherBarTextViews((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), weatherBar, weatherBarTexts);
                Tabs.setWeatherBarText(weatherBar, hourly, forecast.getTimezone(), weatherBarTexts);
                ShapeDrawable drawable = new ShapeDrawable(weatherBar);
                weatherBarImage.setBackgroundDrawable(drawable);
                forecast = null;
            }
        }

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (weatherBar != null)
            outState.putParcelable("weatherBar", weatherBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_map, container, false);

        weatherBarTexts = (RelativeLayout) view.findViewById(R.id.scroll_container)
                .findViewById(R.id.weather_bar_container).findViewById(R.id.weather_bar_texts);
        weatherBarImage = (ImageView) view.findViewById(R.id.scroll_container)
                .findViewById(R.id.weather_bar_container).findViewById(R.id.weather_bar);

        if (weatherBar != null) {
            Tabs.createWeatherBarTextViews(inflater, weatherBar, weatherBarTexts);
            ShapeDrawable drawable = new ShapeDrawable(weatherBar);
            weatherBarImage.setBackgroundDrawable(drawable);
        }

        return view;
    }

    @Override
    public void onNewData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            this.forecast = (Forecast.Response) data;
            if (isVisible())
                onResume();
        }
    }

    @Override
    public void onRestoreData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            if(weatherBar == null) {
                this.forecast = (Forecast.Response) data;
                if (isVisible())
                    onResume();
            }

        }
    }
}
