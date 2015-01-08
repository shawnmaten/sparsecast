package com.shawnaten.simpleweather;

import com.shawnaten.network.modules.ForecastModule;
import com.shawnaten.network.modules.GsonConverterModule;
import com.shawnaten.network.modules.OkClientModule;
import com.shawnaten.network.modules.PlacesAutocompleteModule;
import com.shawnaten.network.modules.PlacesDetailsModule;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
            App.class
    },
    includes = {
            DomainModule.class,
            ForecastModule.class,
            GsonConverterModule.class,
            OkClientModule.class,
            PlacesAutocompleteModule.class,
            PlacesDetailsModule.class
    }
)
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides public App provideApp() {
        return app;
    }
}
