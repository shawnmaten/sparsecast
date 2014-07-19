package com.shawnaten.weather;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;
import com.shawnaten.networking.Forecast;

import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.WeatherAlertDialog;

import static com.shawnaten.tools.ForecastTools.capitalize;
import static java.util.Arrays.asList;

public class CurrentFragment extends Fragment implements FragmentListener {
    private Forecast.Response forecast;
    private static Boolean newData = false;
    private RelativeLayout parent;
    private LinearLayout alertView;
    private ImageView imageView;
	
	public CurrentFragment() {
		
	}
	
	@Override
	public void onResume () {
		super.onResume();

        if (newData) {
            int windDirection;

            ForecastTools.timeForm.setTimeZone(forecast.getTimezone());

            Forecast.DataPoint currently;
            Forecast.DataBlock minutely, hourly, daily;

            currently = forecast.getCurrently();
            minutely = forecast.getMinutely();
            hourly = forecast.getHourly();
            daily = forecast.getDaily();

            windDirection = ForecastTools.getWindDirection(currently.getWindBearing());

            ForecastTools.setText(parent, asList(R.id.temp, R.id.humidity, R.id.high_temp, R.id.high_temp_time, R.id.low_temp, R.id.low_temp_time, R.id.feels_like),
                    asList(
                            ForecastTools.tempForm.format(currently.getTemperature()),
                            ForecastTools.percForm.format(currently.getHumidity()),
                            ForecastTools.tempForm.format(daily.getData()[0].getTemperatureMax()),
                            ForecastTools.timeForm.format(daily.getData()[0].getTemperatureMaxTime()),
                            ForecastTools.tempForm.format(daily.getData()[0].getTemperatureMin()),
                            ForecastTools.timeForm.format(daily.getData()[0].getTemperatureMinTime()),
                            String.format("%s %s", getString(R.string.feels_like), ForecastTools.tempForm.format(currently.getApparentTemperature()))
                    )
            );

            String precipType = hourly.getData()[0].getPrecipType();
            if (precipType == null)
                precipType = getString(R.string.precipitation);

            ForecastTools.setSpannableText(parent, asList(R.id.summary, R.id.sun, R.id.details_1, R.id.details_2), asList(2, 2, 2, 2), asList(1, 2, 1, 1),
                    asList("\n", ": ", ": ", ": "), asList("", "", "", ""), asList("", "\t\t", "", ""), asList("", "", "\n", "\n"),

                    asList(
                            asList(getString(R.string.currently), currently.getSummary()),

                            asList(getString(R.string.sunrise), ForecastTools.timeForm.format(daily.getData()[0].getSunriseTime()), getString(R.string.sunset),
                                    ForecastTools.timeForm.format(daily.getData()[0].getSunsetTime())),

                            asList(
                                    getString(R.string.sunrise), ForecastTools.timeForm.format(daily.getData()[0].getSunriseTime()),
                                    getString(R.string.sunset), ForecastTools.timeForm.format(daily.getData()[0].getSunsetTime()),
                                    capitalize(precipType), String.format("%s (%s)", ForecastTools.percForm.format(hourly.getData()[0].getPrecipProbability()),
                                            getString(R.string.hour)),
                                    getString(R.string.dew_point), ForecastTools.tempForm.format(currently.getDewPoint())
                            ),

                            asList(
                                    getString(R.string.wind), String.format("%s %s %s", ForecastTools.intForm.format(currently.getWindSpeed()),
                                            getString(R.string.wind_unit), getString(windDirection)),
                                    getString(R.string.pressure), String.format("%s %s", ForecastTools.intForm.format(currently.getPressure()), getString(R.string.pressure_unit)),
                                    getString(R.string.visibility), String.format("%s %s", ForecastTools.intForm.format(currently.getVisibility()),
                                            getString(R.string.visibility_unit)),
                                    getString(R.string.ozone), String.format("%s %s", ForecastTools.intForm.format(currently.getOzone()), getString(R.string.ozone_unit))
                            )
                    )
            );

            String hourSummary;
            if (minutely == null) {
                hourSummary = hourly.getData()[0].getSummary();
                Log.e("minutely", "was null");
            }
            else
                hourSummary = minutely.getSummary();

            ForecastTools.setSpannableText((ViewGroup) parent.findViewById(R.id.summaries), asList(R.id.next_hour, R.id.next_24_hours), asList(2, 2), asList(1, 1),
                    asList("\n", "\n"), asList("", ""), asList("", ""), asList("", ""),

                    asList(

                            asList(getString(R.string.next_hour), hourSummary),

                            asList(getString(R.string.next_24_hours), hourly.getSummary())

                    )
            );
            createAlertViews(alertView);
            setAlertText(alertView);
            setIcon(imageView);
            newData = false;
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = (RelativeLayout) inflater.inflate(R.layout.tab_current, container, false);
        alertView = (LinearLayout) parent.findViewById(R.id.alert_view).findViewById(R.id.alert_view_content);
        imageView = (ImageView) parent.findViewById(R.id.weather_icon);
        if (forecast != null) {
            createAlertViews(alertView);
            setIcon(imageView);
        }
        return parent;
    }

	@Override
	public void onNewData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            forecast = (Forecast.Response) data;
            newData = true;
            if (isVisible())
                onResume();
        } else if (View.class.isInstance(data)) {
            int id = ((View) data).getId();

            if (id != R.id.time) {
                ((View) data).performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                WeatherAlertDialog temp = new WeatherAlertDialog();
                Bundle args = new Bundle();

                args.putString("title", forecast.getAlerts()[id-1].getTitle());
                args.putString("message", forecast.getAlerts()[id-1].getDescription());
                temp.setArguments(args);
                temp.show(getFragmentManager(), "tab.current.alert");
            }
        }

	}

    @Override
    public void onRestoreData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            forecast = (Forecast.Response) data;
            if (isVisible())
                onResume();
        }
    }

    private void setIcon(ImageView imageView) {
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        SVG svg = new SVGBuilder()
                .readFromResource(getResources(), ForecastTools.getIconValue(forecast.getCurrently().getTime(), forecast.getDaily().getData()[0].getSunriseTime(),
                        forecast.getDaily().getData()[0].getSunsetTime(), forecast.getCurrently().getIcon()))
                .build();

        Drawable drawable = svg.getDrawable();
        imageView.setImageDrawable(drawable);
    }

    private void createAlertViews(LinearLayout layout) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout.removeAllViews();

        TextView textView = (TextView) inflater.inflate(R.layout.alert_view_item, null);
        textView.setId(R.id.time);
        layout.addView(textView);

        int i = 1;
        if (forecast.getAlerts() != null) {
            for (Forecast.Alert alert : forecast.getAlerts()) {
                textView = (TextView) inflater.inflate(R.layout.alert_view_item, null);
                textView.setId(i++);
                layout.addView(textView);
            }
        } else
            textView.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
    }

    private void setAlertText(LinearLayout layout) {
        TextView textView = (TextView) layout.findViewById(R.id.time);
        textView.setText(String.format("%s %s", getString(R.string.conditions), ForecastTools.timeForm.format(forecast.getCurrently().getTime())));

        int i = 1;
        if (forecast.getAlerts() != null) {
            for (Forecast.Alert alert : forecast.getAlerts()) {
                textView = (TextView) layout.findViewById(i++);
                textView.setText(alert.getTitle());
            }
        }
    }

}
