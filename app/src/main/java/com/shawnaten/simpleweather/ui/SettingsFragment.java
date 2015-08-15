package com.shawnaten.simpleweather.ui;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.locationReportAPI.LocationReportAPI;
import com.shawnaten.simpleweather.services.GCMRegistrarService;
import com.shawnaten.simpleweather.services.LocationService;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;

import java.util.ArrayList;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    protected String analyticsTrackName = "SettingsFragment";

    @Inject
    LocationReportAPI locationReportAPI;
    @Inject
    GoogleApiClient googleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SettingsActivity) getBaseActivity())
                .getSettingsComponent()
                .injectSettingsFragment(this);

        Account accounts[];
        ArrayList<CharSequence> accountNamesList = new ArrayList<>();
        CharSequence accountNames[];
        ListPreference accountPref, unitsPref;

        addPreferencesFromResource(R.xml.preferences);

        accountPref = (ListPreference) findPreference(getString(R.string.pref_account_key));
        accounts = getApp().mainComponent.credential().getAllAccounts();

        for (Account account : accounts) accountNamesList.add(account.name);

        accountNames = new CharSequence[accountNamesList.size()];
        accountNamesList.toArray(accountNames);

        accountPref.setEntries(accountNames);
        accountPref.setEntryValues(accountNames);

        if (!accountNamesList.contains(accountPref.getValue()))
            accountPref.setValueIndex(0);
        accountPref.setSummary(accountPref.getValue());

        unitsPref = (ListPreference) findPreference(getString(R.string.pref_units_key));
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
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch(key) {
            case "prefAccountName":
                findPreference(key).setSummary(prefs.getString(key, ""));
                getApp().mainComponent.credential()
                        .setSelectedAccountName(prefs.getString(key, ""));
                LocationSettings.setMode(LocationSettings.Mode.CURRENT);
                break;
            case "prefUnits":
                ListPreference unitsPref = (ListPreference) findPreference(key);
                unitsPref.setSummary(unitsPref.getEntry());
                LocalizationSettings.configure(getApp());
                break;
            case "prefLocationNotify":

                if (prefs.getBoolean(key, false)) {
                    Intent intent = new Intent(getBaseActivity(), GCMRegistrarService.class);
                    getBaseActivity().startService(intent);
                } else {
                    Intent intent = new Intent(getBaseActivity(), LocationService.class);
                    getBaseActivity().stopService(intent);
                }

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
