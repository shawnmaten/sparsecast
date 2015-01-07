package com.shawnaten.simpleweather;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
        App.class
    },
    includes = {
        DomainModule.class
    }
)
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides public Application provideApplication() {
        return app;
    }
}
