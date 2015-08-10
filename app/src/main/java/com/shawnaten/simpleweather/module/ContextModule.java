package com.shawnaten.simpleweather.module;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class ContextModule {

    private Context context;

    @Inject
    public ContextModule(Context contex) {
        this.context = contex;
    }

    @Provides
    public Context providesContext(){
        return context;
    }
}
