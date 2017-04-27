package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.instabug.wrapper.support.activity.InstabugAppCompatActivity;
import com.shawnaten.simpleweather.App;

import java.util.ArrayList;
import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends InstabugAppCompatActivity {
    protected CompositeSubscription subs;
    protected ArrayList<FragmentDataListener> dataListeners = new ArrayList<>();
    protected HashMap<String, Object> dataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subs = new CompositeSubscription();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        subs.unsubscribe();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (FragmentDataListener.class.isInstance(fragment))
            dataListeners.add((FragmentDataListener) fragment);
    }

    public App getApp() {
        return (App) getApplication();
    }

    protected void sendDataToFragments(String key, Object data) {
        dataMap.put(key, data);
        for (FragmentDataListener dataListener : dataListeners)
            dataListener.onNewData(data);
    }

    protected void sendDataToFragments(Object data) {
        for (FragmentDataListener dataListener : dataListeners)
            dataListener.onNewData(data);
    }

    protected void checkForData(FragmentDataListener dataListener) {
        for (Object item : dataMap.values())
            dataListener.onNewData(item);
    }

    public interface FragmentDataListener {
        void onNewData(Object data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
