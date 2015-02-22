package com.shawnaten.simpleweather;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class App extends Application {

    private ObjectGraph objectGraph;
    @Inject AnalyticsManager analyticsManager;
    //@Inject @Named("KeysModule") Observable<Keys> keysModule;

    @Override public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
        analyticsManager.registerAppEnter();
        //keysModule.subscribe(keys -> Log.d("KeysModule", keys.getForecastAPIKey()));

    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }
}
