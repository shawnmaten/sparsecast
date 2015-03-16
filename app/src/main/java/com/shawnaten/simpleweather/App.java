package com.shawnaten.simpleweather;

import android.app.Application;

import com.shawnaten.simpleweather.component.Dagger_NetworkComponent;
import com.shawnaten.simpleweather.component.NetworkComponent;
import com.shawnaten.simpleweather.module.AppModule;

public class App extends Application {

    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        networkComponent = Dagger_NetworkComponent.builder()
                .appModule(new AppModule(this))
                .build();
        networkComponent.injectApp(this);

    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }

}
