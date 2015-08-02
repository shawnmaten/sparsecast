package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.shawnaten.simpleweather.App;

import java.util.ArrayList;
import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {
    protected CompositeSubscription subs;
    protected ArrayList<FragmentDataListener> dataListeners = new ArrayList<>();
    protected HashMap<String, Object> dataMap = new HashMap<>();

    /*
    private final static String KIIP_TAG = "kiip_fragment_tag";
    private KiipFragmentCompat mKiipFragment;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subs = new CompositeSubscription();

        /*
        if (savedInstanceState != null) {
            mKiipFragment = (KiipFragmentCompat) getSupportFragmentManager()
                    .findFragmentByTag(KIIP_TAG);
        } else {
            mKiipFragment = new KiipFragmentCompat();
            getSupportFragmentManager().beginTransaction().add(mKiipFragment, KIIP_TAG).commit();
        }
        */
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        Kiip.getInstance().startSession(new Kiip.Callback() {
            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
            }

            @Override
            public void onFinished(Kiip kiip, Poptart poptart) {
                onPoptart(poptart);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Kiip.getInstance().endSession(new Kiip.Callback() {
            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
            }

            @Override
            public void onFinished(Kiip kiip, Poptart poptart) {
                onPoptart(poptart);
            }
        });
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        subs.unsubscribe();
    }

    public App getApp() {
        return (App) getApplication();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (FragmentDataListener.class.isInstance(fragment))
            dataListeners.add((FragmentDataListener) fragment);
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

    /*
    public void onPoptart(Poptart poptart) {
        mKiipFragment.showPoptart(poptart);
    }
    */
}
