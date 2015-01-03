package com.shawnaten.simpleweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.shawnaten.tools.ForecastTools;

/**
 * Created by Shawn Aten on 8/25/14.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Account accounts[];
        ArrayList<CharSequence> accountNamesList = new ArrayList<>();
        CharSequence accountNames[];*/
        ListPreference /*accountPref,*/ unitsPref, moisturePref;
        /*GoogleAccountCredential credential;*/

        addPreferencesFromResource(R.xml.preferences);

        /*accountPref = (ListPreference) findPreference(getString(R.string.account_key));
        credential = GoogleAccountCredential.usingAudience(getApplicationContext(), "server:client_id:" + getString(R.string.WEB_ID));
        accounts = credential.getAllAccounts();

        for (int i = 0; i < accounts.length; i++)
            accountNamesList.add(accounts[i].name);

        accountNames = new CharSequence[accountNamesList.size()];
        accountNamesList.toArray(accountNames);

        accountPref.setEntries(accountNames);
        accountPref.setEntryValues(accountNames);

        if (!accountNamesList.contains(accountPref.getValue()))
            accountPref.setValueIndex(0);
        accountPref.setSummary(accountPref.getValue());*/

        unitsPref = (ListPreference) findPreference(getString(R.string.units_key));
        unitsPref.setSummary(unitsPref.getEntry());

        moisturePref = (ListPreference) findPreference(getString(R.string.moisture_key));
        moisturePref.setSummary(moisturePref.getEntry());
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
            /*case "prefAccountName":
                findPreference(key).setSummary(sharedPreferences.getString(key, ""));
                break;*/
            case "prefUnits":
                ListPreference unitsPref = (ListPreference) findPreference(key);
                ForecastTools.configUnits(unitsPref.getValue(), null, null);
                unitsPref.setSummary(unitsPref.getEntry());
                break;
            case "prefMoistureMetric":
                ListPreference moisturePref = (ListPreference) findPreference(key);
                moisturePref.setSummary(moisturePref.getEntry());
                break;
        }
    }
}
