package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.model.Forecast;
import com.shawnaten.simpleweather.model.ForecastServiceWrapper;
import com.shawnaten.simpleweather.model.MainActivity;
import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.simpleweather.module.ForecastModule;
import com.shawnaten.simpleweather.module.GsonConverterModule;
import com.shawnaten.simpleweather.module.KeysModule;
import com.shawnaten.simpleweather.module.OkClientModule;
import com.shawnaten.simpleweather.module.PlacesAutocompleteModule;
import com.shawnaten.simpleweather.module.PlacesDetailsModule;

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
                PlacesDetailsModule.class
        }
)
public interface NetworkComponent {
    Observable<Keys> keysObservable();
    Forecast.Service forecastService();
    ForecastServiceWrapper forecastServiceWrapper();
    App injectApp(App app);
    MainActivity injectMainActivity(MainActivity activity);
}
