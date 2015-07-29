package com.shawnaten.simpleweather;

import android.support.multidex.MultiDexApplication;

import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.tools.LocalizationSettings;
import com.shawnaten.tools.LocationSettings;

public class App extends MultiDexApplication {
    public MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .build();

        LocalizationSettings.configure(this);
        LocationSettings.configure();
    }
}
