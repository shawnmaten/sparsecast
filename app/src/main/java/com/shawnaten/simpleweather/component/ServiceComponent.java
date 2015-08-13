package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.GCMTokenModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.LocationReportApiModule;
import com.shawnaten.simpleweather.module.PreferencesModule;
import com.shawnaten.simpleweather.services.GCMNotificationService;
import com.shawnaten.simpleweather.services.LocationHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ContextModule.class,
                GoogleApiClientModule.class,
                GoogleAccountCredentialModule.class,
                LocationReportApiModule.class,
                PreferencesModule.class,
                GCMTokenModule.class
        }
)
public interface ServiceComponent {
    GCMNotificationService injectGCMNotificationService(GCMNotificationService notificationService);
    LocationHandler injectLocationHandler(LocationHandler locationHandler);
}
