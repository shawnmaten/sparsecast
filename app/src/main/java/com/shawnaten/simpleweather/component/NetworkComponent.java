package com.shawnaten.simpleweather.component;

import com.google.android.gms.common.api.GoogleApiClient;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.simpleweather.module.ForecastModule;
import com.shawnaten.simpleweather.module.GoogleAccountCredentialModule;
import com.shawnaten.simpleweather.module.GoogleApiClientModule;
import com.shawnaten.simpleweather.module.GsonConverterModule;
import com.shawnaten.simpleweather.module.ImagesApiModule;
import com.shawnaten.simpleweather.module.InstagramModule;
import com.shawnaten.simpleweather.module.KeysModule;
import com.shawnaten.simpleweather.module.LocationModule;
import com.shawnaten.simpleweather.module.OkClientModule;
import com.shawnaten.simpleweather.module.PlacesAutocompleteModule;
import com.shawnaten.simpleweather.module.PlacesDetailsModule;
import com.shawnaten.simpleweather.module.SavedPlaceApiModule;
import com.shawnaten.simpleweather.ui.MainActivity;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.Instagram;

import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;

@Singleton
@Component(
        modules = {
                AppModule.class,
                ForecastModule.class,
                GsonConverterModule.class,
                KeysModule.class,
                OkClientModule.class,
                PlacesAutocompleteModule.class,
                PlacesDetailsModule.class,
                LocationModule.class,
                GoogleApiClientModule.class,
                GoogleAccountCredentialModule.class,
                ImagesApiModule.class,
                SavedPlaceApiModule.class,
                InstagramModule.class
        }
)
public interface NetworkComponent {
    Observable<Forecast.Response> forecast();
    App injectApp(App app);
    MainActivity injectMainActivity(MainActivity activity);
    GoogleApiClient googleApiClient();
    SavedPlaceApi savedPlaceApi();
    Instagram.Service instagramService();
    Observable<Keys> keys();
}
