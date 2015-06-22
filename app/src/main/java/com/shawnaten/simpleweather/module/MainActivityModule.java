package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.ui.MainActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class MainActivityModule {

    private MainActivity mainActivity;

    @Inject
    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Singleton
    @Provides
    public MainActivity provideMainActivity(){
        return mainActivity;
    }
}
