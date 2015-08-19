package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.GCMTokenModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.LocationApiModule;
import com.shawnaten.simpleweather.module.PreferencesModule;
import com.shawnaten.simpleweather.services.LocationService2;

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
                GCMTokenModule.class
        }
)
public interface LocationServiceComponent {
    LocationService2 inject(LocationService2 service);
}
