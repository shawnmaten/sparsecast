package com.shawnaten.simpleweather;

import android.support.multidex.MultiDexApplication;

import com.shawnaten.simpleweather.component.DaggerMainComponent;
import com.shawnaten.simpleweather.component.DaggerServiceComponent;
import com.shawnaten.simpleweather.component.MainComponent;
import com.shawnaten.simpleweather.component.ServiceComponent;
import com.shawnaten.simpleweather.module.ContextModule;
import com.shawnaten.simpleweather.tools.LocalizationSettings;

public class App extends MultiDexApplication {

    private MainComponent mainComponent;
    private ServiceComponent serviceComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        serviceComponent = DaggerServiceComponent
                .builder().contextModule(new ContextModule(this))
                .build();

        LocalizationSettings.configure(this);

        /*
        Intent gcmRegistrarServiceIntent = new Intent(this, GCMRegistrarService.class);
        startService(gcmRegistrarServiceIntent);*/

    }

    public ServiceComponent getServiceComponent() {
        return serviceComponent;
    }

    public MainComponent getMainComponent() {
        return mainComponent;
    }
}
