package com.shawnaten.tools;

import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.shawnaten.simpleweather.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class Charts {
    public static boolean setPrecipitationGraph(Context context, LineChart chart,
        Forecast.DataPoint data[], TimeZone timeZone) {

        DecimalFormat percentFormat = new DecimalFormat("###%");
        ArrayList<Entry> probabilities = new ArrayList<>();
        ArrayList<Entry> intensities = new ArrayList<>();
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        for (int i = 0; i < data.length; i++) {
            Forecast.DataPoint dataPoint = data[i];
            Entry probability = new Entry((float) dataPoint.getPrecipProbability(), i);
            Entry intensity = new Entry((float) dataPoint.getPrecipIntensity(), i);
            if (probability.getVal() > 0)
                probabilities.add(probability);
            intensities.add(intensity);
        }

        if (probabilities.size() != 0) {
            LineDataSet probabilitiesSet = new LineDataSet(probabilities,
                    context.getString(R.string.probability));
            LineDataSet intensitiesSet = new LineDataSet(intensities,
                    context.getString(R.string.intensity));

            probabilitiesSet.setDrawCircles(false);
            probabilitiesSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            probabilitiesSet.setColor(context.getResources().getColor(R.color.text_primary)
                    + 0xFF000000);

            intensitiesSet.setDrawCircles(false);
            intensitiesSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            intensitiesSet.setDrawFilled(true);
            intensitiesSet.setColor(context.getResources().getColor(R.color.chart));
            intensitiesSet.setFillColor(context.getResources().getColor(R.color.chart));

            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(probabilitiesSet);
            dataSets.add(intensitiesSet);

            LineData lineData = new LineData(genXValues(data, timeZone), dataSets);
            lineData.setDrawValues(false);

            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setDrawAxisLine(false);

            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawAxisLine(false);
            leftAxis.setAxisMaxValue(1.1f);
            leftAxis.setValueFormatter(percentFormat::format);

            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawAxisLine(false);
            rightAxis.setAxisMaxValue(LocalizationSettings.getPrecipitationMax());

            chart.setDescription(null);
            chart.setData(lineData);
            chart.invalidate();
            chart.setTouchEnabled(false);
            return true;
        }

        return false;
    }

    public static void setTemperatureGraph(Context context, LineChart chart,
        Forecast.DataPoint data[], TimeZone timeZone) {

        DecimalFormat percentFormat = new DecimalFormat("###%");
        ArrayList<Entry> actualTemps = new ArrayList<>();
        ArrayList<Entry> apparentTemps = new ArrayList<>();

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        for (int i = 0; i < data.length; i++) {
            Forecast.DataPoint dataPoint = data[i];
            Entry actual = new Entry((float) dataPoint.getTemperature(), i);
            Entry apparent = new Entry((float) dataPoint.getApparentTemperature(), i);
            actualTemps.add(actual);
            apparentTemps.add(apparent);
        }

        LineDataSet actualSet = new LineDataSet(actualTemps,
                context.getString(R.string.actual));
        LineDataSet apparentSet = new LineDataSet(apparentTemps,
                context.getString(R.string.apparent));

        apparentSet.setDrawCircles(false);
        apparentSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        apparentSet.setColor(context.getResources().getColor(R.color.text_primary));

        actualSet.setDrawCircles(false);
        actualSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        actualSet.setDrawFilled(true);
        actualSet.setColor(context.getResources().getColor(R.color.chart));
        actualSet.setFillColor(context.getResources().getColor(R.color.chart));

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(apparentSet);
        dataSets.add(actualSet);

        LineData lineData = new LineData(genXValues(data, timeZone), dataSets);
        lineData.setDrawValues(false);

        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setAvoidFirstLastClipping(true);

        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        //leftAxis.setAxisMaxValue(1.1f);
        //leftAxis.setValueFormatter(percentFormat::format);

        rightAxis.setEnabled(false);
        //rightAxis.setDrawGridLines(false);
        //rightAxis.setDrawAxisLine(false);
        //rightAxis.setAxisMaxValue(LocalizationSettings.getPrecipitationMax());

        chart.setDescription(null);
        chart.setData(lineData);
        chart.invalidate();
        chart.setTouchEnabled(false);
    }

    public static void setDailyTemperatureGraph(Context context, LineChart chart,
                                           Forecast.DataPoint data[], TimeZone timeZone) {

        ArrayList<Entry> minTemps = new ArrayList<>();
        ArrayList<Entry> maxTemps = new ArrayList<>();

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        for (int i = 0; i < data.length; i++) {
            Forecast.DataPoint dataPoint = data[i];
            minTemps.add(new Entry((float) dataPoint.getTemperatureMin(), i));
            maxTemps.add(new Entry((float) dataPoint.getTemperatureMax(), i));
        }

        LineDataSet minSet = new LineDataSet(minTemps, context.getString(R.string.min));
        LineDataSet maxSet = new LineDataSet(maxTemps, context.getString(R.string.max));

        minSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        minSet.setColor(context.getResources().getColor(R.color.chart_min));
        minSet.setCircleColor(context.getResources().getColor(R.color.chart_min));

        maxSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        maxSet.setColor(context.getResources().getColor(R.color.chart_max));
        maxSet.setCircleColor(context.getResources().getColor(R.color.chart_max));

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(minSet);
        dataSets.add(maxSet);

        LineData lineData = new LineData(genXValues(data, timeZone), dataSets);

        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setAvoidFirstLastClipping(true);

        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        rightAxis.setEnabled(false);

        chart.setDescription(null);
        chart.setData(lineData);
        chart.invalidate();
        chart.setTouchEnabled(false);
    }

    private static ArrayList<String> genXValues(Forecast.DataPoint[] data, TimeZone timeZone) {
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
            DateFormat shortTimeForm = ForecastTools.getTimeForm(timeZone);
            for (Forecast.DataPoint dataPoint : data)
                xValues.add(String.format("%s", shortTimeForm.format(dataPoint.getTime())));
        }

        return xValues;
    }
}
