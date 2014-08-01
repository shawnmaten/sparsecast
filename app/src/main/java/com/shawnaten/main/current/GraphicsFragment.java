package com.shawnaten.main.current;

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
 * Created by shawnaten on 7/20/14.
 */
public class GraphicsFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;
    private static WeatherBarShape weatherBarShape;

    public GraphicsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphics, container, false);
        RelativeLayout weatherBarTexts = (RelativeLayout) view.findViewById(R.id.weather_bar_texts);
        if (weatherBarShape != null) {
            ForecastTools.createWeatherBarTextViews((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), weatherBarShape, weatherBarTexts);
            ShapeDrawable drawable = new ShapeDrawable(weatherBarShape);
            ImageView weatherBarImage = (ImageView) view.findViewById(R.id.weather_bar_image);
            weatherBarImage.setBackgroundDrawable(drawable);
        }
        return view;
    }

    @Override
    public void onResume () {
        super.onResume();

        if (forecast != null) {
            Forecast.DataPoint[] hourly = forecast.getHourly().getData();
            RelativeLayout weatherBarTexts = (RelativeLayout) getView().findViewById(R.id.weather_bar_texts);
            ImageView weatherBarImage = (ImageView) getView().findViewById(R.id.weather_bar_image);
            ShapeDrawable drawable;

            weatherBarShape = new WeatherBarShape(getActivity().getApplicationContext(), hourly, 0, 24, 768, 64);
            drawable = new ShapeDrawable(weatherBarShape);
            weatherBarImage.setBackgroundDrawable(drawable);
            ForecastTools.createWeatherBarTextViews((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), weatherBarShape, weatherBarTexts);
            ForecastTools.setWeatherBarText(weatherBarShape, hourly, forecast.getTimezone(), weatherBarTexts);

            forecast = null;
        }

    }

    @Override
    public void onNewData(Forecast.Response data) {
        forecast = data;
        if (isVisible())
            onResume();
    }

    @Override
    public void onButtonClick(View view) {

    }
}
