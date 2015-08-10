package com.shawnaten.simpleweather.component;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.backend.registrationApi.RegistrationApi;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.module.ForecastModule;
import com.shawnaten.simpleweather.module.GeocodingModule;
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
import com.shawnaten.simpleweather.module.RegistrationApiModule;
import com.shawnaten.simpleweather.module.SavedPlaceApiModule;
import com.shawnaten.simpleweather.tools.Forecast;
import com.shawnaten.simpleweather.tools.Instagram;
import com.shawnaten.simpleweather.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;

@Singleton
@Component(
        modules = {
                ContextModule.class,
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
                InstagramModule.class,
                GeocodingModule.class,
                RegistrationApiModule.class
        }
)
public interface MainComponent {
    Observable<Forecast.Response> forecast();
    MainActivity injectMainActivity(MainActivity activity);
    GoogleApiClient googleApiClient();
    SavedPlaceApi savedPlaceApi();
    Instagram.Service instagramService();
    Observable<Keys> keys();
    GoogleAccountCredential credential();
    RegistrationApi registrationApi();
}
