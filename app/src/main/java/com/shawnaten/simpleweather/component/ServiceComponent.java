package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.LocationReportApiModule;
import com.shawnaten.simpleweather.module.PreferencesModule;
import com.shawnaten.simpleweather.module.RegistrationApiModule;
import com.shawnaten.simpleweather.services.GCMNotificationService;
import com.shawnaten.simpleweather.services.GCMRegistrarService;
import com.shawnaten.simpleweather.services.GeofenceService;
import com.shawnaten.simpleweather.services.LocationReportService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ContextModule.class,
                GoogleApiClientModule.class,
                GoogleAccountCredentialModule.class,
                LocationReportApiModule.class,
                RegistrationApiModule.class,
                PreferencesModule.class
        }
)
public interface ServiceComponent {
    GeofenceService injectGeofenceControlService(GeofenceService controlService);
    LocationReportService injectLocationUpdater(LocationReportService locationReportService);
    GCMRegistrarService injectGCMRegistrarService(GCMRegistrarService gcmRegistrarService);
    GCMNotificationService injectGCMNotificationService(GCMNotificationService notificationService);
}
