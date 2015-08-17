package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.module.AnalyticsModule;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.ForecastModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.GsonConverterModule;
import com.shawnaten.simpleweather.module.ImagesApiModule;
import com.shawnaten.simpleweather.module.InstagramModule;
import com.shawnaten.simpleweather.module.LocationReportApiModule;
import com.shawnaten.simpleweather.module.OkClientModule;
import com.shawnaten.simpleweather.module.PlacesAutocompleteModule;
import com.shawnaten.simpleweather.module.PlacesDetailsModule;
import com.shawnaten.simpleweather.module.PreferencesModule;
import com.shawnaten.simpleweather.module.ReactiveLocationProviderModule;
import com.shawnaten.simpleweather.module.SavedPlaceApiModule;
import com.shawnaten.simpleweather.ui.BaseFragment;
import com.shawnaten.simpleweather.ui.MainActivity;
import com.shawnaten.simpleweather.ui.SearchActivity;
import com.shawnaten.simpleweather.ui.SearchTab;
import com.shawnaten.simpleweather.ui.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ContextModule.class,
                ForecastModule.class,
                GsonConverterModule.class,
                OkClientModule.class,
                PlacesAutocompleteModule.class,
                PlacesDetailsModule.class,
                GoogleApiClientModule.class,
                GoogleAccountCredentialModule.class,
                ImagesApiModule.class,
                SavedPlaceApiModule.class,
                InstagramModule.class,
                ReactiveLocationProviderModule.class,
                AnalyticsModule.class,
                LocationReportApiModule.class,
                PreferencesModule.class
        }
)
public interface MainComponent {
    BaseFragment inject(BaseFragment fragment);
    MainActivity inject(MainActivity activity);
    SearchActivity inject(SearchActivity activity);
    SettingsFragment inject(SettingsFragment settingsFragment);
    SearchTab inject(SearchTab tab);
    App inject(App app);
}
