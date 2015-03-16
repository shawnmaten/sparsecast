package com.shawnaten.simpleweather.model;

import android.os.Bundle;

import com.shawnaten.simpleweather.R;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    ForecastServiceWrapper forecastServiceWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        getApp().getNetworkComponent().injectMainActivity(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
    }

}