package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.App;

import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends Fragment {
    protected CompositeSubscription subs;
    protected boolean analyticsTrack = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subs = new CompositeSubscription();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        String className = this.getClass().getSimpleName();

        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint() && analyticsTrack && !App.lastTracked.equals(className)) {
            App.lastTracked = className;
            Tracker tracker = getApp().tracker;
            tracker.setScreenName(className);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

    }

    @Override
    public void onResume() {
        String className = this.getClass().getSimpleName();

        super.onResume();

        if (getUserVisibleHint() && analyticsTrack && !App.lastTracked.equals(className)) {
            App.lastTracked = className;
            Tracker tracker = getApp().tracker;
            tracker.setScreenName(className);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
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
