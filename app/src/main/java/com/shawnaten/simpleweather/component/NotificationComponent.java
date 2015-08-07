package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.LocationRequestModule;
import com.shawnaten.simpleweather.module.MessagingApiModule;
import com.shawnaten.simpleweather.services.LocationHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                GoogleApiClientModule.class,
                LocationRequestModule.class,
                MessagingApiModule.class,
                GoogleAccountCredentialModule.class
        }
)
public interface NotificationComponent {
    LocationHandler injectNotificationThread(LocationHandler notificationThread);
}
