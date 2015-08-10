package com.shawnaten.simpleweather.backend;

import com.shawnaten.simpleweather.backend.component.DaggerNotifyComponent;
import com.shawnaten.simpleweather.backend.component.NotifyComponent;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Dagger implements ServletContextListener {
    private static NotifyComponent notifyComp;

    private static final Logger log = Logger.getLogger(Dagger.class.getName());

    public void contextInitialized(ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user
        // request if no warmup request was invoked.

        notifyComp = DaggerNotifyComponent.create();
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }

    public static NotifyComponent getNotifyComp() {
        return notifyComp;
    }
}
