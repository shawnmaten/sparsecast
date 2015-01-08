package com.shawnaten.simpleweather;

import com.shawnaten.network.modules.NetworkModule;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
            App.class
    },
    includes = {
            DomainModule.class,
            NetworkModule.class
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
