package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.model.BaseActivity;
import com.shawnaten.simpleweather.scopes.Activity;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Activity
@Module
public class ActivityModule {

    private BaseActivity activity;

    @Inject
    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Activity
    @Provides
    public BaseActivity provideActivity(){
        return activity;
    }
}
