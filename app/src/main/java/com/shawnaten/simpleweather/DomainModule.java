package com.shawnaten.simpleweather;

/**
 * Sparsecast for Android.
 * <p/>
 * File created by Shawn Aten on 2015/01/06.
 */
import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class DomainModule {

    @Provides @Singleton public AnalyticsManager provideAnalyticsManager(Application app){
        return new AnalyticsManager(app);
    }

}
