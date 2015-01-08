package com.shawnaten.simpleweather;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class DomainModule {

    @Provides @Singleton public AnalyticsManager provideAnalyticsManager(App app){
        return new AnalyticsManager(app);
    }

}
