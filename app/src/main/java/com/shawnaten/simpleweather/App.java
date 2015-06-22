package com.shawnaten.simpleweather;

import android.support.multidex.MultiDexApplication;

import com.shawnaten.simpleweather.component.DaggerNetworkComponent;
import com.shawnaten.simpleweather.component.NetworkComponent;
import com.shawnaten.simpleweather.module.AppModule;
import com.shawnaten.tools.LocalizationSettings;
import com.shawnaten.tools.LocationSettings;

public class App extends MultiDexApplication {
    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        networkComponent = DaggerNetworkComponent.builder()
                .appModule(new AppModule(this))
                .build();
        networkComponent.injectApp(this);

        LocalizationSettings.configure(this);
        LocationSettings.configure();
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
}
