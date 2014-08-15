package com.shawnaten.simpleweather.week;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.WeatherBarShape;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/4/14.
 */
public class WeekFragmentAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Forecast.DataPoint[] hourly, daily;

    private Calendar cal;
    private DateFormat timeForm = DateFormat.getTimeInstance(DateFormat.SHORT);
    private TimeZone timeZone;

    private int size;
    private final int childCount = 2, childTypeCount = 2, groupTypeCount = 1;

    private WeatherBarShape[] weatherBars;

    public WeekFragmentAdapter(Context context, Forecast.Response forecast) {
        int tickColor, timeOffset;

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hourly = forecast.getHourly().getData();
        daily = forecast.getDaily().getData();
        size = daily.length - 1;

        this.timeZone = forecast.getTimezone();
        cal = Calendar.getInstance(timeZone);
        timeForm.setTimeZone(timeZone);

        cal.setTime(forecast.getCurrently().getTime());
        timeOffset = 24 - cal.get(Calendar.HOUR_OF_DAY);

        tickColor = context.getResources().getColor(android.R.color.tertiary_text_light);

        weatherBars = new WeatherBarShape[size];
        for (int i = 0; i < size; i++) {
            int start;

            if (i == 0)
                start = 0;
            else
                start = 24 * (i - 1) + timeOffset;

            weatherBars[i] = new WeatherBarShape(context, hourly, start, 24, 768, 64);
        }
    }

    @Override
    public int getGroupCount() {
        return this.size;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return daily[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch (childPosition) {
            case 0:
                return daily[groupPosition];
            case 1:
                return hourly;
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition + (groupPosition * childCount) + 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getGroupId(groupPosition) + childPosition + 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Forecast.DataPoint day = (Forecast.DataPoint) getGroup(groupPosition);
        cal.setTime(day.getTime());
        if (convertView == null)
            convertView = inflater.inflate(R.layout.tab_week_group, parent, false);

        ForecastTools.setText((ViewGroup) convertView, asList(R.id.day, R.id.summary),
                asList(
                        cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                        day.getSummary()
                )
        );

        convertView.setId((int) getGroupId(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int childID = (int) getChildId(groupPosition, childPosition);

        switch (childPosition) {
            case 0:
                Forecast.DataPoint day = (Forecast.DataPoint) getChild(groupPosition, childPosition);
                if (convertView == null)
                    convertView = inflater.inflate(R.layout.tab_week_child_info, parent, false);

                List<Integer> groupSizes = asList(1, 1, 1);
                List<String> unitSeps = asList("", "", ""), groupEnds = asList("\n", "\n", "\n");

                if (convertView.getTag().equals("tab_week_child_port")) {
                    groupSizes = asList(2, 2, 2);
                    unitSeps = asList("\t\t", "\t\t", "\t\t");
                    groupEnds = asList("", "", "");
                }

                String precipType = day.getPrecipType();
                if (precipType == null)
                    precipType = context.getString(R.string.precipitation);
                ForecastTools.setSpannableText((ViewGroup) convertView, asList(R.id.sun, R.id.temp, R.id.precip), asList(2, 3, 2), groupSizes,
                        asList(": ", ": ", ": "), asList("", " ", ""), unitSeps, groupEnds,
                        asList(
                                asList(context.getString(R.string.sunrise), timeForm.format(day.getSunriseTime()),
                                        context.getString(R.string.sunset), timeForm.format(day.getSunsetTime())),

                                asList(context.getString(R.string.high), ForecastTools.tempForm.format(day.getTemperatureMax()),
                                        timeForm.format(day.getTemperatureMaxTime()),
                                        context.getString(R.string.low), ForecastTools.tempForm.format(day.getTemperatureMin()),
                                        timeForm.format(day.getTemperatureMinTime())),

                                asList(ForecastTools.capitalize(precipType), ForecastTools.percForm.format(day.getPrecipProbability()),
                                        context.getString(R.string.intensity), context.getString(ForecastTools.getIntensity(day.getPrecipIntensity())))
                        )
                );
                break;
            case 1:
                Boolean makeNew = false;
                FrameLayout weatherBarContainer;
                RelativeLayout weatherBarTexts;
                ImageView weatherBarImage;
                ShapeDrawable drawable;

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.tab_week_child_weather_bar, parent, false);
                    weatherBarContainer = (FrameLayout) convertView.findViewById(R.id.weather_bar_container);
                    weatherBarTexts = (RelativeLayout) weatherBarContainer.findViewById(R.id.weather_bar_texts);
                    weatherBarImage = (ImageView) weatherBarContainer.findViewById(R.id.weather_bar_image);
                    makeNew = true;
                } else {
                    int prevID = convertView.getId();
                    weatherBarContainer = (FrameLayout) convertView.findViewById(prevID * 10);
                    weatherBarTexts = (RelativeLayout) weatherBarContainer.findViewById(prevID * 100);
                    weatherBarImage = (ImageView) weatherBarContainer.findViewById(prevID * 1000);
                }

                convertView.setScrollX(0);

                weatherBarContainer.setId(childID * 10);
                weatherBarTexts.setId(childID * 100);
                weatherBarImage.setId(childID * 1000);

                if (makeNew)
                    ForecastTools.createWeatherBarTextViews(inflater, weatherBars[groupPosition], weatherBarTexts);

                ForecastTools.setWeatherBarText(weatherBars[groupPosition], hourly, timeZone, weatherBarTexts);
                drawable = new ShapeDrawable(weatherBars[groupPosition]);
                weatherBarImage.setBackgroundDrawable(drawable);
        }

        convertView.setId(childID);
        return convertView;
    }

    @Override
    public int getChildTypeCount () {
        return childTypeCount;
    }

    @Override
    public int getChildType (int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getGroupType (int groupPosition) {
        return 0;
    }

    @Override
    public int getGroupTypeCount () {
        return groupTypeCount;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
