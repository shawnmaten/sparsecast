package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.module.AnalyticsModule;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.ForecastModule;
import com.shawnaten.simpleweather.module.GCMApiModule;
import com.shawnaten.simpleweather.module.GCMTokenModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.GsonConverterModule;
import com.shawnaten.simpleweather.module.ImagesApiModule;
import com.shawnaten.simpleweather.module.InstagramModule;
import com.shawnaten.simpleweather.module.LocationApiModule;
import com.shawnaten.simpleweather.module.OkClientModule;
import com.shawnaten.simpleweather.module.PlacesAutocompleteModule;
import com.shawnaten.simpleweather.module.PlacesDetailsModule;
import com.shawnaten.simpleweather.module.PreferencesModule;
import com.shawnaten.simpleweather.module.PrefsApiModule;
import com.shawnaten.simpleweather.module.ReactiveLocationProviderModule;
import com.shawnaten.simpleweather.module.SavedPlaceApiModule;
import com.shawnaten.simpleweather.ui.BaseFragment;
import com.shawnaten.simpleweather.ui.MainActivity;
import com.shawnaten.simpleweather.ui.SearchActivity;
import com.shawnaten.simpleweather.ui.SearchTab;
import com.shawnaten.simpleweather.ui.SettingsFragment;
import com.shawnaten.simpleweather.ui.UploadConfirmFragment;

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
                PreferencesModule.class,
                PrefsApiModule.class,
                GCMApiModule.class,
                LocationApiModule.class,
                GCMTokenModule.class
        }
)
public interface MainComponent {
    BaseFragment inject(BaseFragment fragment);
    MainActivity inject(MainActivity activity);
    SearchActivity inject(SearchActivity activity);
    SettingsFragment inject(SettingsFragment settingsFragment);
    SearchTab inject(SearchTab tab);
    UploadConfirmFragment inject(UploadConfirmFragment fragment);
    App inject(App app);
}
