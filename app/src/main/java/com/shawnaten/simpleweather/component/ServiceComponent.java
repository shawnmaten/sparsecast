package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.GCMApiModule;
import com.shawnaten.simpleweather.module.GCMTokenModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.LocationApiModule;
import com.shawnaten.simpleweather.module.PreferencesModule;
import com.shawnaten.simpleweather.module.ReactiveLocationProviderModule;
import com.shawnaten.simpleweather.services.GCMNotificationService;
import com.shawnaten.simpleweather.services.GCMRegistrarService;
import com.shawnaten.simpleweather.services.GCMTokenRefreshService;
import com.shawnaten.simpleweather.services.LocationHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ContextModule.class,
                GoogleApiClientModule.class,
                GoogleAccountCredentialModule.class,
                LocationApiModule.class,
                PreferencesModule.class,
                GCMTokenModule.class,
                GCMApiModule.class,
                ReactiveLocationProviderModule.class
        }
)
public interface ServiceComponent {
    GCMNotificationService inject(GCMNotificationService notificationService);
    GCMRegistrarService inject(GCMRegistrarService service);
    GCMTokenRefreshService inject(GCMTokenRefreshService service);
    LocationHandler inject(LocationHandler locationHandler);
}
