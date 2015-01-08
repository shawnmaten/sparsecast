package com.shawnaten.network.modules;

import dagger.Module;

@Module(
        complete = false,
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
