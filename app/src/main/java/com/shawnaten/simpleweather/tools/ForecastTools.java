package com.shawnaten.simpleweather.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shawnaten on 7/12/14.
 */
public class ForecastTools {

    private final static double VERY_LIGHT = 0.002, LIGHT = 0.017, MODERATE = 0.1, HEAVY = 0.4,
            SCATTERED = 0.4, BROKEN = 0.75, OVERCAST = 1.0,
            FOG = 0.6214, MIST = 1.2427, HAZE = 3.1069;
    private static HashMap<String, Integer> weatherIcons = new HashMap<>();

    static {
        weatherIcons.put("clear-day", R.raw.clear_day);
        weatherIcons.put("clear-night", R.raw.clear_night);
        weatherIcons.put("rain", R.raw.rain);
        weatherIcons.put("snow", R.raw.snow);
        weatherIcons.put("sleet", R.raw.sleet);
        weatherIcons.put("wind", R.raw.wind);
        weatherIcons.put("fog", R.raw.fog);
        weatherIcons.put("cloudy", R.raw.cloudy);
        weatherIcons.put("partly-cloudy-day", R.raw.partly_cloudy_day);
        weatherIcons.put("partly-cloudy-night", R.raw.partly_cloudy_night);
        weatherIcons.put("hail", R.raw.hail);
        weatherIcons.put("thunderstorm", R.raw.thunderstorm);
        weatherIcons.put("tornado", R.raw.tornado);
    }

    public static DateFormat getTimeForm(TimeZone timeZone) {
        DateFormat timeForm = DateFormat.getTimeInstance(DateFormat.SHORT);
        timeForm.setTimeZone(timeZone);
        return timeForm;
    }

    public static DecimalFormat getTempForm() {
        return new DecimalFormat("###\u00b0");
    }

    public static DecimalFormat getPercForm() {
        return new DecimalFormat("###%");
    }

    public static DecimalFormat getIntForm() {
        return new DecimalFormat("###");
    }

    public static int getWindString(double bearing) {
        int b = (int) (bearing / 22.5);
        if ((b % 2) != 0)
            b += 1;
        if (b == 16)
            b = 0;

        switch (b) {
            case 0:
                return R.string.south;
            case 2:
                return R.string.southwest;
            case 4:
                return R.string.west;
            case 6:
                return R.string.northwest;
            case 8:
                return R.string.north;
            case 10:
                return R.string.northeast;
            case 12:
                return R.string.east;
            case 14:
                return R.string.southeast;
            default:
                return 0;
        }
    }

    public static int getWeatherIcon(String icon) {
        if (icon == null)
            return weatherIcons.get("clear-day");
        if (weatherIcons.containsKey(icon)) {
            return weatherIcons.get(icon);
        }

        return R.raw.fallback;
    }

    public static String capitalize(String string) {
        int start, end;
        String match;
        StringBuilder editable = new StringBuilder(string);
        Pattern p = Pattern.compile("^[a-z]|\\s[a-z]");
        Matcher m = p.matcher(string);

        while (m.find()) {
            match = m.group();
            start = m.start();
            end = start + match.length();

            editable.replace(start, end, match.toUpperCase());
        }

        return editable.toString();
    }

    public static void setSpannableText(ViewGroup parent, List<Integer> childIDs, List<Integer> unitSizes, List<Integer> groupSizes,
                                        List<String> spanEnds, List<String> seps, List<String> unitSeps, List<String> groupEnds,
                                        List<List<String>> allStrings) {
        TextView textView;
        SpannableStringBuilder text;
        StyleSpan styleBold;
        String spanEnd, sep, unitSep, groupEnd, string;
        Iterator<String> viewStrings;
        int iChild, iView, len, unitSize, groupSize;

        iChild = 0;
        for (int ID : childIDs) {
            textView = (TextView) parent.findViewById(ID);

            spanEnd = spanEnds.get(iChild);
            sep = seps.get(iChild);
            unitSep = unitSeps.get(iChild);
            groupEnd = groupEnds.get(iChild);

            unitSize = unitSizes.get(iChild);
            groupSize = groupSizes.get(iChild);

            viewStrings = allStrings.get(iChild).iterator();
            text = new SpannableStringBuilder();

            iView = 1;
            while (viewStrings.hasNext()) {
                string = viewStrings.next();
                switch (iView % unitSize) {
                    case 1:
                        string += spanEnd;
                        styleBold = new StyleSpan(Typeface.BOLD);
                        len = text.length();
                        text.append(string);
                        text.setSpan(styleBold, len, len + string.length(), 0);
                        break;
                    case 0:
                        switch (iView % (groupSize * unitSize)) {
                            case 0:
                                if (viewStrings.hasNext())
                                    string += groupEnd;
                                text.append(string);
                                break;
                            default:
                                string += unitSep;
                                text.append(string);
                        }
                        break;
                    default:
                        string += sep;
                        text.append(string);
                }
                iView++;
            }
            textView.setText(text);
            iChild++;
        }
    }

    public static void setText(ViewGroup parent, List<Integer> childIDs, List<String> strings) {
        TextView textView;
        int i = 0;
        for (int ID : childIDs) {
            textView = (TextView) parent.findViewById(ID);
            textView.setText(strings.get(i++));
        }
    }

    public static int getIntensity(double rate) {
        if (rate == 0)
            return R.string.none;
        if (rate < LIGHT)
            return R.string.very_light;
        else if (rate < MODERATE)
            return R.string.light;
        else if (rate < HEAVY)
            return R.string.moderate;
        return R.string.heavy;
    }

    public static int getCloudCover(double cloudCover) {
        if (cloudCover < SCATTERED)
            return R.string.clear;
        else if (cloudCover < BROKEN)
            return R.string.partly_cloudy;
        else if (cloudCover < OVERCAST)
            return R.string.cloudy;
        return R.string.overcast;
    }

    public static int getFogLevel(double visibility) {
        if (visibility < FOG)
            return R.string.fog;
        else if (visibility < MIST)
            return R.string.mist;
        return R.string.haze;
    }

    public static int getPrecipitationID(Context context, String name) {
        if (name != null) {
            SparseArray<String> types = new SparseArray<>();
            types.put(R.string.moderate_rain, context.getString(R.string.moderate_rain));
            types.put(R.string.moderate_snow, context.getString(R.string.moderate_snow));
            types.put(R.string.moderate_sleet, context.getString(R.string.moderate_sleet));
            types.put(R.string.hail, context.getString(R.string.hail));

            final int count = types.size();
            for (int i = 0; i < count; i++) {
                if (name.equalsIgnoreCase(types.valueAt(i)))
                    return types.keyAt(i);
            }
        }

        return R.string.precipitation;
    }

    public static void createWeatherBarTextViews(LayoutInflater inflater, WeatherBarShape weatherBar, RelativeLayout layout) {
        int width, leftOffset, tickCount, to_end_of, parent_start;
        RelativeLayout.LayoutParams params1, params2;
        ArrayList<RelativeLayout.LayoutParams> paramsList = new ArrayList<>();

        // set up
        layout.removeAllViews();
        width = weatherBar.getTickSpacing() * 2;
        leftOffset = weatherBar.getLeftOffset();
        tickCount = weatherBar.getTickCount();

        params1 = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2 = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            to_end_of = RelativeLayout.END_OF;
            parent_start = RelativeLayout.ALIGN_PARENT_START;
            params1.setMarginStart(leftOffset);
            params2.setMarginStart(leftOffset + (width / 2));
        } else {
            to_end_of = RelativeLayout.RIGHT_OF;
            parent_start = RelativeLayout.ALIGN_PARENT_LEFT;
            params1.setMargins(leftOffset, 0, 0, 0);
            params2.setMargins(leftOffset + (width / 2), 0, 0, 0);
        }

        params1.addRule(parent_start, 1);
        params2.addRule(parent_start, 1);
        paramsList.add(params1);
        paramsList.add(params2);

        for (int i = 1; i <= tickCount - 2; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(to_end_of, i);
            paramsList.add(params);
        }

        for (int i = 1; i <= tickCount; i++) {
            TextView textView = (TextView) inflater.inflate(R.layout.weather_bar_time, layout, false);
            textView.setId(i);
            textView.setText(Long.toString(SystemClock.currentThreadTimeMillis()));
            layout.addView(textView, paramsList.get(i - 1));
        }

    }

    public static void setWeatherBarText(WeatherBarShape weatherBar, Forecast.DataPoint[] data, TimeZone timeZone, RelativeLayout layout) {
        TextView textView;
        SimpleDateFormat timeForm;
        int unitStart, unitCount, unitSkip, tickCount;

        unitStart = weatherBar.getUnitStart();
        unitCount = weatherBar.getUnitCount();
        unitSkip = weatherBar.getUnitSkip();
        tickCount = weatherBar.getTickCount();

        timeForm = getShortTimeForm(timeZone, unitCount);

        for (int i = 0; i < tickCount; i++) {
            Forecast.DataPoint dataPoint = data[((i + 1) * unitSkip) + unitStart];
            textView = (TextView) layout.findViewById(i + 1);
            textView.setText(String.format("%s\n%s", timeForm.format(dataPoint.getTime()),
                    ForecastTools.getTempForm().format(dataPoint.getTemperature())));
        }

    }

    public static WeatherBlock[] parseWeatherPoints(Context context, Forecast.DataPoint[] dataPoints, int start, int size) {
        ArrayList<WeatherBlock> blocks;
        WeatherBlock prevBlock, newBlock;
        int count = 0;

        prevBlock = null;
        blocks = new ArrayList<>();

        final int end = start + size;
        for (int i = start; i < end; i++) {
            double precipProb, visibility, intensity;
            Forecast.DataPoint dataPoint;

            dataPoint = dataPoints[i];

            precipProb = dataPoint.getPrecipProbability();
            visibility = dataPoint.getVisibility();
            intensity = dataPoint.getPrecipIntensity();

            SimpleDateFormat timeForm = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);
            String pattern = timeForm.toPattern();
            pattern = pattern.replaceAll(":mm\\s*", "");
            timeForm.applyPattern(pattern);

            if (dataPoint.getPrecipType() != null && intensity >= VERY_LIGHT) {
                int scattered, intensityString;

                if (precipProb < .6)
                    scattered = 1;
                else
                    scattered = 0;

                intensityString = getIntensity(intensity);
                if (intensityString == R.string.very_light)
                    intensityString = R.string.light;

                newBlock = new WeatherBlock(context, WeatherBlock.PRECIPITATION, count, count + 1, scattered, intensityString,
                        getPrecipitationID(context, dataPoint.getPrecipType()));

            } else if (visibility < HAZE) {
                newBlock = new WeatherBlock(context, WeatherBlock.FOG, count, count + 1, getFogLevel(visibility));
            } else {
                newBlock = new WeatherBlock(context, WeatherBlock.CLOUDS, count, count + 1, getCloudCover(dataPoint.getCloudCover()));
            }
            if (newBlock.equals(prevBlock))
                prevBlock.add(newBlock);
            else {
                blocks.add(newBlock);
                prevBlock = newBlock;
            }
            count++;
        }

        return blocks.toArray(new WeatherBlock[blocks.size()]);
    }

    public static SimpleDateFormat getShortTimeForm(TimeZone timeZone, int unitCount) {
        SimpleDateFormat shortTimeForm = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);

        shortTimeForm.setTimeZone(timeZone);
        String pattern = shortTimeForm.toPattern();

        if (pattern.contains("a")) {
            switch (unitCount) {
                case 60:
                    pattern = pattern.replaceAll("\\s*a", "");
                    break;
                case 24:
                    pattern = pattern.replaceAll(":mm\\s*", " ");
                    break;
            }
        }

        shortTimeForm.applyPattern(pattern);
        return shortTimeForm;
    }

    public static double minTemp(Forecast.DataPoint data[]) {
        double min;

        min = data[0].getTemperature();
        for (Forecast.DataPoint point : data)
            if (point.getTemperature() < min)
                min = point.getTemperature();

        return min;
    }

    public static double maxTemp(Forecast.DataPoint data[]) {
        double max;

        max = data[0].getTemperature();
        for (Forecast.DataPoint point : data)
            if (point.getTemperature() > max)
                max = point.getTemperature();

        return max;
    }

    public static double dailyMinTemp(Forecast.DataPoint data[]) {
        double min;

        min = data[0].getTemperatureMin();
        for (Forecast.DataPoint point : data)
            if (point.getTemperatureMin() < min)
                min = point.getTemperatureMin();

        return min;
    }

    public static double dailyMaxTemp(Forecast.DataPoint data[]) {
        double max;

        max = data[0].getTemperatureMax();
        for (Forecast.DataPoint point : data)
            if (point.getTemperatureMax() > max)
                max = point.getTemperatureMax();

        return max;
    }

}
