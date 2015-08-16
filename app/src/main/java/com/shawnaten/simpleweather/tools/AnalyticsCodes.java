package com.shawnaten.simpleweather.tools;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.ui.BaseFragment;
import com.shawnaten.simpleweather.ui.LoadingFragment;
import com.shawnaten.simpleweather.ui.Next24HoursTab;
import com.shawnaten.simpleweather.ui.Next7DaysTab;
import com.shawnaten.simpleweather.ui.SettingsActivity;

import java.util.HashMap;

public class AnalyticsCodes {
    public static final String UNDEFINED_SCREEN = "Undefined Screen";

    public static final String CATEGORY_FORECAST_LOAD = "Forecast Load";
    public static final String CATEGORY_FULL_LOAD = "Full Load";

    private static final HashMap<Class, String> SCREENS = new HashMap<>();
    static {
        SCREENS.put(BaseFragment.class, "Base Fragment");
        SCREENS.put(Next24HoursTab.class, "Next 24 Hours Tab");
        SCREENS.put(Next7DaysTab.class, "Next 7 Days Tab");
        SCREENS.put(SettingsActivity.class, "Settings Activity");
    }

    private static String getScreenName(Class mClass) {
        if (SCREENS.containsKey(mClass))
                return SCREENS.get(mClass);
        return UNDEFINED_SCREEN;
    }


    private static boolean isTrackingEnabled(Class mClass) {
        return !LoadingFragment.class.isInstance(mClass);
    }

    public static void sendScreenView(Tracker tracker, Class mClass) {
        tracker.setScreenName(getScreenName(mClass));
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
