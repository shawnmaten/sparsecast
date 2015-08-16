package com.shawnaten.simpleweather.module;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    Tracker providesTracker(Context context)  {
        Tracker tracker =  GoogleAnalytics
                .getInstance(context)
                .newTracker(R.xml.analytics_global_config);

        tracker.enableAdvertisingIdCollection(true);
        return tracker;
    }

}
