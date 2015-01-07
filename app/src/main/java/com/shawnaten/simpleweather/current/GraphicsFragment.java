package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.FragmentListener;

/**
 * Created by shawnaten on 7/20/14.
 */
public class GraphicsFragment extends Fragment implements FragmentListener {

    public GraphicsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_current_graphics, container, false);
    }

    @Override
    public void onResume () {
        super.onResume();

        updateView();
    }

    @Override
    public void onNewData() {
        if (isVisible())
            updateView();
    }

    private void updateView() {
        MainActivity activity = (MainActivity) getActivity();

        /*
        if (activity.hasForecast()) {
            View parent = getView();
            Forecast.Response forecast = activity.getForecast();
            WeatherBarShape weatherBarShape;

            Forecast.DataPoint[] hourly = forecast.getHourly().getData();
            RelativeLayout weatherBarTexts = (RelativeLayout) parent.findViewById(R.id.weather_bar_texts);
            ImageView weatherBarImage = (ImageView) parent.findViewById(R.id.weather_bar_image);
            ShapeDrawable drawable;

            weatherBarShape = new WeatherBarShape(getActivity().getApplicationContext(), hourly, 0, 24,
                    getResources().getDimensionPixelSize(R.dimen.weather_bar_width),
                    getResources().getDimensionPixelSize(R.dimen.weather_bar_height));
            drawable = new ShapeDrawable(weatherBarShape);
            weatherBarImage.setBackgroundDrawable(drawable);
            ForecastTools.createWeatherBarTextViews((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), weatherBarShape, weatherBarTexts);
            ForecastTools.setWeatherBarText(weatherBarShape, hourly, forecast.getTimezone(), weatherBarTexts);
        }
        */
    }

}
