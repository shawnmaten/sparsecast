package com.shawnaten.tools;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import me.kiip.sdk.KiipFragmentCompat;
import me.kiip.sdk.Poptart;

/**
 * Created by Shawn Aten on 8/22/14.
 */
public class BaseActivity extends ActionBarActivity {
    private final static String KIIP_TAG = "kiip_fragment_tag";

    private KiipFragmentCompat mKiipFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        // Create or re-use KiipFragment.
        if (savedInstanceState != null) {
            mKiipFragment = (KiipFragmentCompat) getSupportFragmentManager().findFragmentByTag(KIIP_TAG);
        } else {
            mKiipFragment = new KiipFragmentCompat();
            getSupportFragmentManager().beginTransaction().add(mKiipFragment, KIIP_TAG).commit();
        }
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
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
        */
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
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
        */
    }

    public void onPoptart(Poptart poptart) {
        mKiipFragment.showPoptart(poptart);
    }
}
