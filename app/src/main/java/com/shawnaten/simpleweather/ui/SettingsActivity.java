package com.shawnaten.simpleweather.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shawnaten.simpleweather.R;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FragmentManager fm = getFragmentManager();
        Toolbar toolbar;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        fm.beginTransaction().add(R.id.content, new SettingsFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }


}
