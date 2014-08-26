package com.shawnaten.simpleweather;

import android.accounts.Account;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.tools.ForecastTools;

/**
 * Created by Shawn Aten on 8/25/14.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Account accounts[];
        CharSequence accountNames[];
        ListPreference accountPref, unitsPref;
        GoogleAccountCredential credential;

        addPreferencesFromResource(R.xml.preferences);

        accountPref = (ListPreference) findPreference(getString(R.string.account_key));
        credential = GoogleAccountCredential.usingAudience(getApplicationContext(), "server:client_id:" + getString(R.string.WEB_ID));
        accounts = credential.getAllAccounts();

        accountNames = new CharSequence[accounts.length];
        for (int i = 0; i < accounts.length; i++)
            accountNames[i] = accounts[i].name;

        accountPref.setSummary(accountPref.getValue());
        accountPref.setEntries(accountNames);
        accountPref.setEntryValues(accountNames);

        unitsPref = (ListPreference) findPreference(getString(R.string.units_key));
        unitsPref.setSummary(unitsPref.getEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case "prefAccountName":
                findPreference(key).setSummary(sharedPreferences.getString(key, ""));
                break;
            case "prefUnits":
                ListPreference unitsPref = (ListPreference) findPreference(key);
                ForecastTools.configUnits(unitsPref.getValue());
                unitsPref.setSummary(unitsPref.getEntry());
                break;
        }
    }
}
