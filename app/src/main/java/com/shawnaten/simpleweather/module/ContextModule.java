package com.shawnaten.simpleweather.module;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private Context context;

    @Inject
    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context providesContext(){
        return context;
    }
}
