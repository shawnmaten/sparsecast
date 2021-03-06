package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.tools.ForecastIconSelector;
import com.shawnaten.simpleweather.ui.widget.HorizontalWeatherBar;
import com.shawnaten.simpleweather.ui.widget.TemperatureBar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class Next7DaysTab extends Tab implements View.OnClickListener {
    private LinearLayout daysList;
    private TextView summary;

    public static Next7DaysTab create(String title, int layout) {
        Bundle args = new Bundle();
        Next7DaysTab tab = new Next7DaysTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        summary = (TextView) root.findViewById(R.id.summary);
        daysList = (LinearLayout) root.findViewById(R.id.days_list);

        for (int i = 0; i < 7; i++) {
            View item = inflater.inflate(R.layout.day_list_item, daysList, false);
            item.setOnClickListener(this);
            daysList.addView(item);
        }

        return root;
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (isVisible() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;
            Forecast.DataPoint daily[] = forecast.getDaily().getData();
            Forecast.DataPoint hourly[] = forecast.getHourly().getData();
            SimpleDateFormat dayFormat = new SimpleDateFormat("ccc");
            dayFormat.setTimeZone(forecast.getTimezone());

            Forecast.DataPoint dailyMin[] = Arrays.copyOf(daily, daily.length);
            Arrays.sort(dailyMin, new Forecast.DataPoint.DailyMinComparator());
            Forecast.DataPoint dailyMax[] = Arrays.copyOf(daily, daily.length);
            Arrays.sort(dailyMax, new Forecast.DataPoint.DailyMaxComparator());

            int totalMin = (int) dailyMin[0].getTemperatureMin();
            int totalMax = (int) dailyMax[dailyMax.length - 1].getTemperatureMax();

            summary.setText(forecast.getDaily().getSummary());

            int hoursLeftInDay = 24 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int hourlyOffsets[] = new int[8];
            hourlyOffsets[0] = 0;
            for (int i = 1; i < 8; i++) {
                hourlyOffsets[i] = hoursLeftInDay + (i - 1) * 24;
            }

            for (int i = 0; i < 7; i++) {
                Forecast.DataPoint dataPoint = daily[i];
                View item = daysList.getChildAt(i);
                ImageView icon = (ImageView) item.findViewById(R.id.icon);
                TextView dayOfWeek = (TextView) item.findViewById(R.id.day_of_week);
                TextView summary = (TextView) item.findViewById(R.id.summary);
                TemperatureBar tempBar = (TemperatureBar) item.findViewById(R.id.temperature_bar);
                HorizontalWeatherBar weatherBar = (HorizontalWeatherBar)
                        item.findViewById(R.id.weather_bar);

                icon.setImageResource(ForecastIconSelector.getImageId(dataPoint.getIcon()));
                dayOfWeek.setText(dayFormat.format(dataPoint.getTime()));
                summary.setText(dataPoint.getSummary());
                tempBar.setData((int) dataPoint.getTemperatureMin(), (int) dataPoint.getTemperatureMax(),
                        totalMin, totalMax);
                weatherBar.setData(hourly, hourlyOffsets[i], forecast.getTimezone());
            }
        }
    }

    @Override
    public void onClick(View view) {
        View info = view.findViewById(R.id.info);
        View weatherBar = view.findViewById(R.id.weather_bar);

        if (info.getVisibility() == View.INVISIBLE) {
            weatherBar.setVisibility(View.INVISIBLE);
            info.setVisibility(View.VISIBLE);
        } else {
            weatherBar.setVisibility(View.VISIBLE);
            info.setVisibility(View.INVISIBLE);
        }
    }

}
