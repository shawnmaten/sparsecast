package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.shawnaten.simpleweather.App;

import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends Fragment {
    protected CompositeSubscription subs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subs = new CompositeSubscription();
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
