package com.shawnaten.simpleweather.model;

import android.app.Activity;

import com.shawnaten.simpleweather.App;

public class BaseActivity extends Activity {

    protected App getApp() {
        return (App) getApplication();
    }

}
