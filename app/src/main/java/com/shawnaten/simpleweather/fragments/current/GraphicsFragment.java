package com.shawnaten.simpleweather.fragments.current;

import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.ui.BaseFragment;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.WeatherBarShape;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by shawnaten on 7/20/14.
 */
public class GraphicsFragment extends BaseFragment {
    @Inject
    public GraphicsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Observable<Forecast.Response> forecast = getApp().getNetworkComponent().forecast();
        final View root = inflater.inflate(R.layout.tab_current_graphics, container, false);

        subs.add(forecast.subscribe(new Action1<Forecast.Response>() {
            @Override
            public void call(Forecast.Response response) {
                WeatherBarShape weatherBarShape;

                Forecast.DataPoint[] hourly = response.getHourly().getData();
                RelativeLayout weatherBarTexts = (RelativeLayout)
                        root.findViewById(R.id.weather_bar_texts);
                ImageView weatherBarImage = (ImageView) root.findViewById(R.id.weather_bar_image);
                ShapeDrawable drawable;

                weatherBarShape = new WeatherBarShape(GraphicsFragment.this.getActivity().getApplicationContext(), hourly, 0,
                        24, GraphicsFragment.this.getResources().getDimensionPixelSize(R.dimen.weather_bar_width),
                        GraphicsFragment.this.getResources().getDimensionPixelSize(R.dimen.weather_bar_height));
                drawable = new ShapeDrawable(weatherBarShape);
                weatherBarImage.setBackgroundDrawable(drawable);
                ForecastTools.createWeatherBarTextViews(GraphicsFragment.this.getActivity().getLayoutInflater(),
                        weatherBarShape, weatherBarTexts);
                ForecastTools.setWeatherBarText(weatherBarShape, hourly, response.getTimezone(),
                        weatherBarTexts);
            }
        }));

        return root;
    }
}
