package com.shawnaten.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.shawnaten.networking.Forecast;
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

    public static final char CLIMACONS_SUNRISE = 'L', CLIMACONS_SUNSET = 'M', CLIMACONS_WIND = 'B', CLIMACONS_VISIBILITY = '<',
            CLIMACONS_DEW = '\'', CLIMACONS_RAIN = '*', CLIMACONS_SNOW = '9', CLIMACONS_SLEET = '6', CLIMACONS_HAIL = '3',
            CLIMACONS_CLEAR_DAY = 'I', CLIMACONS_CLOUDY = '!', CLIMACONS_FALLBACK = 'H';

    public final static double VERY_LIGHT = 0.002, LIGHT = 0.017, MODERATE = 0.1, HEAVY = 0.4,
        SCATTERED = 0.4, BROKEN = 0.75, OVERCAST = 1.0,
        FOG = 0.6214, MIST = 1.2427, HAZE = 3.1069;

    public static final DecimalFormat tempForm = new DecimalFormat("###\u00b0"), percForm = new DecimalFormat("###%"),
            intForm = new DecimalFormat("###");

    public static final DateFormat timeForm = DateFormat.getTimeInstance(DateFormat.SHORT);

    public static final String SPACING = "\t\t";

    public static int getWindString (double bearing) {
        int b = (int) (bearing / 22.5);
        if ((b % 2) != 0)
            b += 1;
        if (b == 16)
            b = 0;

        switch (b) {
            case 0:
                return R.raw.south;
            case 2:
                return R.raw.southwest;
            case 4:
                return R.raw.west;
            case 6:
                return R.raw.northwest;
            case 8:
                return R.raw.north;
            case 10:
                return R.raw.northeast;
            case 12:
                return R.raw.east;
            case 14:
                return R.raw.southeast;
            default:
                return 0;
        }
    }

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

    public static int getWeatherIcon(String icon) {
        if (icon == null)
            return weatherIcons.get("clear-day");
        if (weatherIcons.containsKey(icon)) {
            return weatherIcons.get(icon);
        }

        return R.raw.fallback;
    }

    /*
    private static HashMap<String, Character> precipCodes = new HashMap<>();
    static {
        precipCodes.put("rain", CLIMACONS_RAIN);
        precipCodes.put("snow", CLIMACONS_SNOW);
        precipCodes.put("sleet", CLIMACONS_SLEET);
        precipCodes.put("hail", CLIMACONS_HAIL);
        precipCodes.put("fallback", CLIMACONS_FALLBACK);
    }

    public static char getPrecipCode(String precipType) {
        if (precipType == null)
            return CLIMACONS_CLEAR_DAY;
        if (precipCodes.containsKey(precipType))
            return precipCodes.get(precipType);
        else
            return precipCodes.get("fallback");
    }
    */

    /*
    public static void setClimaconSpans
            (ViewGroup parent, HashMap<Integer, SpannableStringBuilder> strings, HashMap<Integer, ArrayList<int[]>> spanIndices) {

        for (int key : strings.keySet()) {
            TextView textView = (TextView) parent.findViewById(key);
            SpannableStringBuilder tempString = strings.get(key);
            ArrayList<int[]> tempSpanIndices = spanIndices.get(key);
            if (tempSpanIndices != null) {
                for (int[] indices : tempSpanIndices) {
                    ClimaconTypefaceSpan span = new ClimaconTypefaceSpan();
                    tempString.setSpan(span, indices[0], indices[0] + indices[1], 0);
                }
            }
            textView.setText(tempString);
        }
    }
    */

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
            types.put(R.string.rain, context.getString(R.string.rain));
            types.put(R.string.snow, context.getString(R.string.snow));
            types.put(R.string.sleet, context.getString(R.string.sleet));
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
        int width, leftOffset, tickCount, to_end_of, parent_start, tickSize;

        RelativeLayout.LayoutParams params;

        // set up
        layout.removeAllViews();
        width = weatherBar.getTickSpacing() * 2;
        leftOffset = weatherBar.getLeftOffset();
        tickCount = weatherBar.getTickCount();

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
        layout.addView(textView, params);

        params = new RelativeLayout.LayoutParams(width + leftOffset, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.center);
        params.addRule(to_end_of, R.id.left_offset);
        textView = (TextView) inflater.inflate(R.layout.time, null);
        //noinspection ResourceType
        textView.setId(2);
        layout.addView(textView, params);

        tickCount+=2;
        for(int i = 3; i < tickCount; i++) {
            params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.center);
            params.addRule(to_end_of, i-2);

            textView = (TextView) inflater.inflate(R.layout.time, null);
            //noinspection ResourceType
            textView.setId(i);
            layout.addView(textView, params);
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

        return blocks.toArray(new WeatherBlock[blocks.size()]);
    }

}
