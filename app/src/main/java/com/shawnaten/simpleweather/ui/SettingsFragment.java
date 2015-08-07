package com.shawnaten.simpleweather.ui;

import android.accounts.Account;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    protected String analyticsTrackName = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Account accounts[];
        ArrayList<CharSequence> accountNamesList = new ArrayList<>();
        CharSequence accountNames[];
        ListPreference accountPref, unitsPref;

        addPreferencesFromResource(R.xml.preferences);

        accountPref = (ListPreference) findPreference(getString(R.string.account_key));
        accounts = getApp().mainComponent.credential().getAllAccounts();

        for (Account account : accounts) accountNamesList.add(account.name);

        accountNames = new CharSequence[accountNamesList.size()];
        accountNamesList.toArray(accountNames);

        accountPref.setEntries(accountNames);
        accountPref.setEntryValues(accountNames);

        if (!accountNamesList.contains(accountPref.getValue()))
            accountPref.setValueIndex(0);
        accountPref.setSummary(accountPref.getValue());

        unitsPref = (ListPreference) findPreference(getString(R.string.units_key));
        unitsPref.setSummary(unitsPref.getEntry());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (getUserVisibleHint()  && !App.lastTracked.equals(analyticsTrackName)) {
            App.lastTracked = analyticsTrackName;
            Tracker tracker = getApp().tracker;
            tracker.setScreenName(analyticsTrackName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case "prefAccountName":
                findPreference(key).setSummary(sharedPreferences.getString(key, ""));
                getApp().mainComponent.credential()
                        .setSelectedAccountName(sharedPreferences.getString(key, ""));
                LocationSettings.setMode(LocationSettings.Mode.CURRENT);
                break;
            case "prefUnits":
                ListPreference unitsPref = (ListPreference) findPreference(key);
                unitsPref.setSummary(unitsPref.getEntry());
                LocalizationSettings.configure(getApp());
                break;
        }
    }

    private BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    private App getApp() {
        return getBaseActivity().getApp();
    }
}
