package com.shawnaten.simpleweather.fragments.current;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.ui.BaseFragment;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.LocalizationSettings;

import java.text.DateFormat;
import java.text.DecimalFormat;

import javax.inject.Inject;

import rx.Observable;

import static java.util.Arrays.asList;

/**
 * Created by shawnaten on 7/20/14.
 */
public class DetailsFragment extends BaseFragment {
    private static final SparseArray<String> permIcons = new SparseArray<>();
    static {
        permIcons.put(R.id.sunrise_icon, "sunrise");
        permIcons.put(R.id.sunset_icon,"sunset");
        permIcons.put(R.id.visibility_icon, "fog");
        permIcons.put(R.id.wind_bearing_icon, "wind");
    }

    @Inject
    public DetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Observable<Forecast.Response> forecast = getApp().getNetworkComponent().forecast();
        View root = inflater.inflate(R.layout.tab_current_details, container, false);

        subs.add(forecast.subscribe(response -> {
            Forecast.DataPoint currently, hour, today;
            DateFormat timeForm = ForecastTools.getTimeForm(response.getTimezone());
            DecimalFormat percForm = ForecastTools.getPercForm();
            DecimalFormat intForm = ForecastTools.getIntForm();
            WebView webView;

            currently = response.getCurrently();
            hour = response.getHourly().getData()[0];
            today = response.getDaily().getData()[0];

            ForecastTools.setText((ViewGroup) root, asList(R.id.sunrise_text, R.id.sunset_text,
                            R.id.precip_hour_text, R.id.precip_day_text,
                            R.id.wind_speed, R.id.visibility_text),
                    asList(
                            timeForm.format(today.getSunriseTime()),
                            timeForm.format(today.getSunsetTime()),
                            String.format("%s %s", percForm.format(hour.getPrecipProbability()),
                                    getString(R.string.now)),
                            String.format("%s %s", percForm.format(today.getPrecipProbability()),
                                    getString(R.string.day)),
                            String.format("%s %s %s", intForm.format(currently.getWindSpeed()),
                                    getString(LocalizationSettings.getSpeedUnit()),
                                    getString(ForecastTools.getWindString(
                                            currently.getWindBearing()))),
                            String.format("%s %s", intForm.format(currently.getVisibility()),
                                    getString(LocalizationSettings.getDistanceUnit()))
                    ));

            /*
            if (savedInstanceState == null) {
                for (int i = 0; i < permIcons.size(); i++) {
                    webView = (WebView) root.findViewById(permIcons.keyAt(i));
                    webView.loadUrl("file:///android_asset/" + permIcons.valueAt(i) + ".html");
                    webView.setBackgroundColor(Color.TRANSPARENT);
                }

                webView = (WebView) root.findViewById(R.placeId.precip_hour_icon);
                webView.loadUrl("file:///android_asset/" + hour.getIcon() + ".html");
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView = (WebView) root.findViewById(R.placeId.precip_day_icon);
                webView.loadUrl("file:///android_asset/" + today.getIcon() + ".html");
                webView.setBackgroundColor(Color.TRANSPARENT);
            }
            */
        }));

        return root;
    }
}
