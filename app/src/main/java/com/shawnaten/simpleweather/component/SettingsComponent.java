package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.LocationReportApiModule;
import com.shawnaten.simpleweather.ui.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ContextModule.class,
                GoogleAccountCredentialModule.class,
                LocationReportApiModule.class,
        }
)
public interface SettingsComponent {
    SettingsFragment injectSettingsFragment(SettingsFragment settingsFragment);
}
