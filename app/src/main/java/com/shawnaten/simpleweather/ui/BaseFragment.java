package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.tools.AnalyticsCodes;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends Fragment {
    protected CompositeSubscription subs;

    @Inject Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApp().getMainComponent().injectBaseFragment(this);

        subs = new CompositeSubscription();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint())
            AnalyticsCodes.sendScreenView(tracker, this.getClass());

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint())
            AnalyticsCodes.sendScreenView(tracker, this.getClass());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        subs.unsubscribe();
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public App getApp() {
        return getBaseActivity().getApp();
    }
}
