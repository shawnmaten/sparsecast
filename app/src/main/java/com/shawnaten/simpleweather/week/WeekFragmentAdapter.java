package com.shawnaten.simpleweather.week;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caverock.androidsvg.SVGImageView;
import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.SVGManager;
import com.shawnaten.tools.WeatherBarShape;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/4/14.
 */
public class WeekFragmentAdapter extends BaseExpandableListAdapter {
    private static final int[] iconIds = {R.id.temp_min_icon, R.id.temp_max_icon, R.id.sunrise_icon, R.id.sunset_icon};
    private static final int[] iconValues = {R.raw.temp_0, R.raw.temp_100, R.raw.sunrise, R.raw.sunset};

    private Context context;
    private LayoutInflater inflater;
    private Forecast.DataPoint[] hourly, daily;

    private Calendar cal;
    private DateFormat timeForm = DateFormat.getTimeInstance(DateFormat.SHORT);
    private TimeZone timeZone;

    private int size;
    private final int childCount = 1, childTypeCount = 1, groupTypeCount = 1;

    private WeatherBarShape[] weatherBars;

    public WeekFragmentAdapter(Context context, Forecast.Response forecast) {
        int timeOffset;

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

        weatherBars = new WeatherBarShape[size];
        for (int i = 0; i < size; i++) {
            int start;

            if (i == 0)
                start = 0;
            else
                start = 24 * (i - 1) + timeOffset;

            weatherBars[i] = new WeatherBarShape(context, hourly, start, 24,
                    context.getResources().getDimensionPixelSize(R.dimen.weather_bar_width),
                    context.getResources().getDimensionPixelSize(R.dimen.weather_bar_height));
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
        DecimalFormat percForm = ForecastTools.getPercForm();
        cal.setTime(day.getTime());
        if (convertView == null)
            convertView = inflater.inflate(R.layout.tab_week_group, parent, false);

        ((SVGImageView) convertView.findViewById(R.id.weather_icon))
                .setSVG(SVGManager.getSVG(context, ForecastTools.getWeatherIcon(day.getIcon())));

        ForecastTools.setText((ViewGroup) convertView, asList(R.id.day, R.id.summary),
                asList(
                        cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()),
                        String.format("%s - %s", percForm.format(day.getPrecipProbability()),
                                day.getSummary())
                )
        );

        convertView.setId((int) getGroupId(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Forecast.DataPoint day = (Forecast.DataPoint) getChild(groupPosition, childPosition);
        Boolean makeNewTextViews = false;
        RelativeLayout weatherBarTexts;
        ImageView weatherBarImage;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tab_week_child, parent, false);
            makeNewTextViews = true;
        }

        for (int i = 0; i < iconIds.length; i++) {
            ((SVGImageView) convertView.findViewById(iconIds[i])).setSVG(SVGManager.getSVG(context, iconValues[i]));
        }

        DateFormat timeForm = ForecastTools.getTimeForm(timeZone);
        SimpleDateFormat shortTimeForm = ForecastTools.getShortTimeForm(timeZone, 24);
        DecimalFormat tempForm = ForecastTools.getTempForm();

        ForecastTools.setText((ViewGroup) convertView, asList(R.id.temp_min_text, R.id.temp_max_text, R.id.sunrise_text, R.id.sunset_text),
                asList(
                        String.format("%s %s", tempForm.format(day.getTemperatureMin()),
                                shortTimeForm.format(day.getTemperatureMinTime())),
                        String.format("%s %s", tempForm.format(day.getTemperatureMax()),
                                shortTimeForm.format(day.getTemperatureMaxTime())),
                        timeForm.format(day.getSunriseTime()), timeForm.format(day.getSunsetTime())
                ));

        weatherBarTexts = (RelativeLayout) convertView.findViewById(R.id.weather_bar_texts);
        weatherBarImage = (ImageView) convertView.findViewById(R.id.weather_bar_image);

        if (makeNewTextViews)
            ForecastTools.createWeatherBarTextViews(inflater, weatherBars[groupPosition], weatherBarTexts);

        ForecastTools.setWeatherBarText(weatherBars[groupPosition], hourly, timeZone, weatherBarTexts);
        weatherBarImage.setBackgroundDrawable(new ShapeDrawable(weatherBars[groupPosition]));
        convertView.findViewById(R.id.weather_bar).setScrollX(0);

        convertView.setId((int) getChildId(groupPosition, childPosition));
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
