package com.shawnaten.tools;

import android.app.Application;

import me.kiip.sdk.Kiip;

/**
 * Created by Shawn Aten on 8/22/14.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Kiip kiip = Kiip.init(this, "288859cf6300709e69efadb87b8c86e8", "26103017c52cb131e3569749db04f5cd");
        Kiip.setInstance(kiip);

    }
}
