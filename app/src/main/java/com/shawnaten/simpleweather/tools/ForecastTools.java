package com.shawnaten.simpleweather.tools;

import com.shawnaten.simpleweather.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ForecastTools {

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

}
