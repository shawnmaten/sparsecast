package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.App;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class AppModule {

    private App app;

    @Inject
    public AppModule(App app) {
        this.app = app;
    }

    @Singleton
    @Provides
    public App provideApp(){
        return app;
    }
}
