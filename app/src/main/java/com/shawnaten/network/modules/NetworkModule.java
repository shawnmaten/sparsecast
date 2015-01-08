package com.shawnaten.network.modules;

import dagger.Module;

@Module(
    includes = {
            ForecastModule.class,
            GsonConverterModule.class,
            OkClientModule.class,
            PlacesAutocompleteModule.class,
            PlacesDetailsModule.class
    }
)
public class NetworkModule {

}
