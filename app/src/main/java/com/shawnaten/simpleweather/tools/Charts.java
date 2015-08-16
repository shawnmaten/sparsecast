package com.shawnaten.simpleweather.tools;

import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.lib.model.Forecast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class Charts {
    public static void setPrecipitationGraph(final Context context, LineChart chart,
        Forecast.DataPoint data[], TimeZone timeZone) {

        ArrayList<Entry> intensities = new ArrayList<>();
        XAxis xAxis = chart.getXAxis();
        YAxis leftAxis = chart.getAxisLeft();
        float textSize = context.getResources().getDimension(R.dimen.body_1)
                / context.getResources().getDisplayMetrics().density;

        for (int i = 0; i < data.length; i++) {
            Forecast.DataPoint dataPoint = data[i];

            Entry intensity = new Entry((float) dataPoint.getPrecipIntensity(), i);
            intensities.add(intensity);
        }

        LineDataSet intensitiesSet = new LineDataSet(intensities, null);

        intensitiesSet.setDrawCircles(false);
        intensitiesSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        intensitiesSet.setDrawFilled(true);
        intensitiesSet.setColor(context.getResources().getColor(R.color.transparent));
        intensitiesSet.setFillColor(context.getResources().getColor(R.color.chart));

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(intensitiesSet);

        LineData lineData = new LineData(genXValues(data, timeZone, context), dataSets);
            lineData.setDrawValues(false);

        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(textSize);

        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinValue(0);
        leftAxis.setAxisMaxValue((float) LocalizationSettings.getPrecipitationHeavy());
        leftAxis.setLabelCount(2);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch (Precipitation.getIntensityCode(value)) {
                    case Precipitation.VERY_LIGHT:
                    case Precipitation.LIGHT:
                        return context.getString(R.string.light);
                    case Precipitation.MODERATE:
                        return context.getString(R.string.moderate);
                    case Precipitation.HEAVY:
                        return context.getString(R.string.heavy);
                    default:
                        return Float.toString(value);
                }
            }
        });
        leftAxis.setTextSize(textSize);

        chart.getAxisRight().setEnabled(false);

        chart.setDescription(null);
        chart.setData(lineData);
        chart.invalidate();
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
    }

    private static ArrayList<String> genXValues(Forecast.DataPoint[] data, TimeZone timeZone,
                                                Context context) {
        ArrayList<String> xValues = new ArrayList<>();

        if (data.length <= 7) {
            SimpleDateFormat dayForm = new SimpleDateFormat("ccc");
            dayForm.setTimeZone(timeZone);
            for (Forecast.DataPoint dataPoint : data)
                xValues.add(String.format("%s", dayForm.format(dataPoint.getTime())));
        } else if (data.length <= 24) {
            SimpleDateFormat shortTimeForm = ForecastTools.getShortTimeForm(timeZone, 24);
            for (Forecast.DataPoint dataPoint : data)
                xValues.add(String.format("%s", shortTimeForm.format(dataPoint.getTime())));
        } else if (data.length >= 60) {
            String minText = context.getString(R.string.minute_short);
            for (int i = 0; i < data.length; i++)
                xValues.add(String.format("%d %s", i, minText));
        }

        return xValues;
    }
}
