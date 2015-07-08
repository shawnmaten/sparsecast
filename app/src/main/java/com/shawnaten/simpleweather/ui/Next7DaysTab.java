package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;

import java.text.SimpleDateFormat;

public class Next7DaysTab extends Tab {
    private LinearLayout daysList;
    private TextView summary;

    public static Next7DaysTab newInstance(String title, int layout) {
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

        for (int i = 0; i < 8; i++)
            daysList.addView(inflater.inflate(R.layout.day_list_item, daysList, false));

        return root;
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (isVisible() && Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;
            Forecast.DataBlock daily = forecast.getDaily();
            SimpleDateFormat dayFormat = new SimpleDateFormat("cccc");
            dayFormat.setTimeZone(forecast.getTimezone());

            summary.setText(daily.getSummary());

            for (int i = 0; i < 8; i++) {
                Forecast.DataPoint dataPoint = daily.getData()[i];
                View item = daysList.getChildAt(i);
                TextView dayOfWeek = (TextView) item.findViewById(R.id.day_of_week);
                TextView summary = (TextView) item.findViewById(R.id.summary);

                dayOfWeek.setText(dayFormat.format(dataPoint.getTime()));
                summary.setText(dataPoint.getSummary());
            }
        }
            /*
            Forecast.Response forecast = (Forecast.Response) data;
            Forecast.DataBlock daily = forecast.getDaily();
            Forecast.DataPoint dailyData[] = Arrays.copyOfRange(daily.getData(), 1, daily.getData().length);
            View root = getView();
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            LinearLayout daysList = (LinearLayout) root.findViewById(R.id.day_list);


            View summaryCard = root.findViewById(R.id.summary_card);
            ((TextView) summaryCard.findViewById(R.id.subhead))
                    .setText(getString(R.string.for_next_7_days));
            ((TextView) summaryCard.findViewById(R.id.next_hour_summary)).setText(daily.getSummary());
            ((ImageView) summaryCard.findViewById(R.id.icon)).setImageResource(ForecastIconSelector
                    .getImageId(daily.getIcon()));

            daysList.removeAllViews();
            for (int i = 1; i < daily.getData().length; i++) {
                View dayCard = layoutInflater.inflate(R.layout.day_card, daysList, false);
                TextView dayText = (TextView) dayCard.findViewById(R.id.title);
                TextView summaryText = (TextView) dayCard.findViewById(R.id.next_hour_summary);
                ImageView icon = (ImageView) dayCard.findViewById(R.id.icon);

                dayText.setText(dayFormat.format(daily.getData()[i].getTime()));
                summaryText.setText(daily.getData()[i].getSummary());
                icon.setImageResource(ForecastIconSelector.getImageId(daily.getData()[i]
                        .getIcon()));

                daysList.addView(dayCard);
            }

            /*
            int cardByCardMargin = getActivity().getResources()
                    .getDimensionPixelSize(R.dimen.card_by_card_margin);
            int cardMargin = getActivity().getResources()
                    .getDimensionPixelSize(R.dimen.card_margin);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    daysList.getChildAt(0).getLayoutParams();
            layoutParams.setMargins(cardByCardMargin, cardByCardMargin, cardByCardMargin,
                    cardMargin);
                    */

        /*
            View precipitationCard = root.findViewById(R.id.precipitation_card);
            View temperatureCard = root.findViewById(R.id.temperature_card);


            ((TextView) precipitationCard.findViewById(R.id.title))
                    .setText(getString(R.string.precipitation));
            ((TextView) precipitationCard.findViewById(R.id.subhead))
                    .setText(getString(R.string.probability_and_intensity));

            Charts.setPrecipitationGraph(
                    getActivity(),
                    (LineChart) precipitationCard.findViewById(R.id.chart),
                    dailyData,
                    forecast.getTimezone());

            ((TextView) temperatureCard.findViewById(R.id.title))
                    .setText(getString(R.string.temperature));
            ((TextView) temperatureCard.findViewById(R.id.subhead))
                    .setText(getString(R.string.actual_and_apparent));

            Charts.setDailyTemperatureGraph(
                    getActivity(),
                    (LineChart) root.findViewById(R.id.temperature_card).findViewById(R.id.chart),
                    dailyData,
                    forecast.getTimezone());
                    */
    }
}
