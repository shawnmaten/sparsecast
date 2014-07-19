package com.shawnaten.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.shawnaten.networking.Forecast;
import com.shawnaten.weather.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    public final static double VERY_LIGHT = 0.002, LIGHT = 0.017, MODERATE = 0.1, HEAVY = 0.4,
        SCATTERED = 0.4, BROKEN = 0.75, OVERCAST = 1.0,
        FOG = 0.6214, MIST = 1.2427, HAZE = 3.1069;

    public static final DecimalFormat tempForm = new DecimalFormat("###\u00b0"), percForm = new DecimalFormat("###%"),
            intForm = new DecimalFormat("###");

    public static final DateFormat timeForm = DateFormat.getTimeInstance(DateFormat.SHORT);

    private static final HashMap<String, Integer> icons = new HashMap<>();
    static {
        icons.put("clear-day", R.raw.sun);
        icons.put("clear-night", R.raw.moon);
        icons.put("rain-day", R.raw.cloud_rain_sun);
        icons.put("rain-night", R.raw.cloud_rain_moon);
        icons.put("snow-day", R.raw.cloud_snow_sun_alt);
        icons.put("snow-night", R.raw.cloud_snow_moon_alt);
        icons.put("sleet-day", R.raw.cloud_snow_sun);
        icons.put("sleet-night", R.raw.cloud_snow_moon);
        icons.put("wind-day", R.raw.cloud_wind_sun);
        icons.put("wind-night", R.raw.cloud_wind_moon);
        icons.put("fog-day", R.raw.cloud_fog_sun);
        icons.put("fog-night", R.raw.cloud_fog_moon);
        icons.put("cloudy-day", R.raw.cloud);
        icons.put("cloudy-night", R.raw.cloud);
        icons.put("partly-cloudy-day", R.raw.cloud_sun);
        icons.put("partly-cloudy-night", R.raw.cloud_moon);
        icons.put("hail-day", R.raw.cloud_hail_sun_alt);
        icons.put("hail-night", R.raw.cloud_hail_moon_alt);
        icons.put("thunderstorm-day", R.raw.cloud_lightning_sun);
        icons.put("thunderstorm-night", R.raw.cloud_lightning_moon);
        icons.put("tornado-day", R.raw.tornado);
        icons.put("tornado-night", R.raw.tornado);
        icons.put("default", R.raw.cloud_refresh);
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

    public static int getIconValue (Date currentTime, Date sunrise, Date sunset, String iconName) {
        if (currentTime.before(sunrise) || currentTime.after(sunset)) { // it's night
            if (iconName.endsWith("-night")) {
                // do nothing
            } else {
                iconName = iconName.concat("-night");
            }
        } else { // it's day
            if (iconName.endsWith("-day")) {
                // do nothing
            } else {
                iconName = iconName.concat("-day");
            }
        }

        if (icons.containsKey(iconName))
            return icons.get(iconName);
        return icons.get("default");
    }

    public static int getWindDirection (double bearing) {
        int windDirection;
        int b = (int) (bearing / 22.5);
        if ((b % 2) != 0)
            b += 1;
        if (b == 16)
            b = 0;

        switch (b) {
            case 0:
                windDirection = R.string.south;
                break;
            case 2:
                windDirection = R.string.southwest;
                break;
            case 4:
                windDirection = R.string.west;
                break;
            case 6:
                windDirection = R.string.northwest;
                break;
            case 8:
                windDirection = R.string.north;
                break;
            case 10:
                windDirection = R.string.northeast;
                break;
            case 12:
                windDirection = R.string.east;
                break;
            case 14:
                windDirection = R.string.southeast;
                break;
            default:
                windDirection = R.string.unknown;

        }
        return windDirection;
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
        if (rate < VERY_LIGHT)
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
        SparseArray<String> types = new SparseArray<>();
        types.put(R.string.rain, context.getString(R.string.rain));
        types.put(R.string.snow, context.getString(R.string.snow));
        types.put(R.string.sleet, context.getString(R.string.sleet));
        types.put(R.string.hail, context.getString(R.string.hail));

        final int count = types.size();
        for (int i = 0; i < count; i++) {
            if (name.equalsIgnoreCase(types.valueAt(i)))
                return types.keyAt(i);
        }

        return -1;
    }

    public static void createWeatherBarTextViews(LayoutInflater inflater, WeatherBarShape weatherBar, RelativeLayout layout) {
        int[] dimensions;

        int width, leftOffset, tickCount, to_end_of, parent_start, tickSize;

        RelativeLayout.LayoutParams params;

        // set up
        dimensions = weatherBar.getDimensions();
        width = dimensions[9] * 2;
        leftOffset = dimensions[11];
        tickCount = dimensions[7];
        tickSize = dimensions[8];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            to_end_of = RelativeLayout.END_OF;
            parent_start = RelativeLayout.ALIGN_PARENT_START;
        } else {
            to_end_of = RelativeLayout.RIGHT_OF;
            parent_start = RelativeLayout.ALIGN_PARENT_LEFT;
        }

        params = new RelativeLayout.LayoutParams(0, 0);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
        Space space = (Space) inflater.inflate(R.layout.space, null);
        space.setId(R.id.center);
        layout.addView(space, params);

        params = new RelativeLayout.LayoutParams((width / 2) + leftOffset, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.center);
        params.addRule(parent_start, 1);
        space = (Space) inflater.inflate(R.layout.space, null);
        space.setId(R.id.left_offset);
        layout.addView(space, params);
        // end of set up

        params = new RelativeLayout.LayoutParams(width + leftOffset, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.center);
        params.addRule(parent_start, 1);
        TextView textView = (TextView) inflater.inflate(R.layout.time, null);
        //noinspection ResourceType
        textView.setId(1);
        textView.setPadding(0, tickSize, 0, 0);
        layout.addView(textView, params);

        params = new RelativeLayout.LayoutParams(width + leftOffset, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.center);
        params.addRule(to_end_of, R.id.left_offset);
        textView = (TextView) inflater.inflate(R.layout.time, null);
        //noinspection ResourceType
        textView.setId(2);
        textView.setPadding(0, tickSize, 0, 0);
        layout.addView(textView, params);

        tickCount+=2;
        for(int i = 3; i < tickCount; i++) {
            params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.center);
            params.addRule(to_end_of, i-2);

            textView = (TextView) inflater.inflate(R.layout.time, null);
            textView.setPadding(0, tickSize, 0, 0);
            //noinspection ResourceType
            textView.setId(i);
            layout.addView(textView, params);
        }

    }

    public static void setWeatherBarText(WeatherBarShape weatherBar, Forecast.DataPoint[] data, TimeZone timeZone, RelativeLayout layout) {
        TextView textView;
        SimpleDateFormat timeForm;
        int unitStart, unitCount, unitSkip, tickCount;
        int[] dimensions;

        dimensions = weatherBar.getDimensions();

        unitStart = dimensions[2];
        unitCount = dimensions[3];
        unitSkip = dimensions[5];
        tickCount = dimensions[7];

        timeForm = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);
        timeForm.setTimeZone(timeZone);

        String pattern = timeForm.toPattern();

        if (pattern.contains("a")) {
            switch (unitCount) {
                case 60:
                    pattern = pattern.replaceAll("\\s*a", "");
                    break;
                case 24:
                    pattern = pattern.replaceAll(":mm\\s*", "");
                    break;
            }
        }

        timeForm.applyPattern(pattern);

        for (int i = 0; i < tickCount; i++) {
            //noinspection ResourceType
            textView = (TextView) layout.findViewById(i + 1);
            textView.setText(timeForm.format(data[((i + 1) * unitSkip) + unitStart].getTime()));
        }


    }

    public static WeatherBlock[] parseWeatherPoints(Context context, Forecast.DataPoint[] dataPoints, int start, int size) {
        ArrayList<WeatherBlock> blocks;
        WeatherBlock prevBlock, newBlock;
        int count = 0;

        prevBlock = null;
        blocks = new ArrayList<>();

        final int end = start + size;
        for(int i = start; i < end; i++) {
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

                newBlock = new WeatherBlock(context, WeatherBlock.PRECIPITATION, count, count+1, scattered, intensityString,
                        getPrecipitationID(context, dataPoint.getPrecipType()));

            } else if (visibility < HAZE) {
                newBlock = new WeatherBlock(context, WeatherBlock.FOG, count, count+1, getFogLevel(visibility));
            } else {
                newBlock = new WeatherBlock(context, WeatherBlock.CLOUDS, count, count+1, getCloudCover(dataPoint.getCloudCover()));
            }
            if (newBlock.equals(prevBlock))
                prevBlock.add(newBlock);
            else {
                blocks.add(newBlock);
                prevBlock = newBlock;
            }
            count++;
        }

        Log.e("blocks", Arrays.toString(blocks.toArray()));
        return blocks.toArray(new WeatherBlock[blocks.size()]);
    }

}
