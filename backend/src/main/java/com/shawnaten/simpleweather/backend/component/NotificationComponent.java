package com.shawnaten.simpleweather.backend.component;

import com.shawnaten.simpleweather.backend.model.Slack;
import com.shawnaten.simpleweather.backend.module.ForecastModule;
import com.shawnaten.simpleweather.backend.module.SlackModule;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.lib.module.GsonConverterModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                GsonConverterModule.class,
                ForecastModule.class,
                SlackModule.class
        }
)
public interface NotificationComponent {
    Forecast.Service forecastService();
    Slack.Service slackService();
}
