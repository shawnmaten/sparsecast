package com.shawnaten.simpleweather.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.component.DaggerSettingsComponent;
import com.shawnaten.simpleweather.component.SettingsComponent;
import com.shawnaten.simpleweather.module.ContextModule;

public class SettingsActivity extends BaseActivity {

    private SettingsComponent settingsComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FragmentManager fm = getFragmentManager();
        Toolbar toolbar;

        super.onCreate(savedInstanceState);

        settingsComponent = DaggerSettingsComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        setContentView(R.layout.activity_settings);

        fm.beginTransaction().add(R.id.content, new SettingsFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
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

    public SettingsComponent getSettingsComponent() {
        return settingsComponent;
    }
}
