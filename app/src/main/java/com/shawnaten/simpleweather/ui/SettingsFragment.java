package com.shawnaten.simpleweather.ui;

import android.accounts.Account;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.backend.prefsAPI.PrefsAPI;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.services.LocationService2;
import com.shawnaten.simpleweather.tools.AnalyticsCodes;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;

import java.util.ArrayList;

import javax.inject.Inject;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject GoogleApiClient googleApiClient;
    @Inject GoogleAccountCredential credential;
    @Inject Tracker tracker;
    @Inject SharedPreferences preferences;
    @Inject GcmAPI gcmAPI;
    @Inject PrefsAPI prefsAPI;
    @Inject ReactiveLocationProvider locationProvider;
    @Inject Forecast.Service forecastService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApp().getMainComponent().inject(this);

        setHasOptionsMenu(false);

        Account accounts[];
        ArrayList<CharSequence> accountNamesList = new ArrayList<>();
        CharSequence accountNames[];
        ListPreference accountPref, unitsPref;

        addPreferencesFromResource(R.xml.preferences);

        accountPref = (ListPreference) findPreference(getString(R.string.pref_account_key));
        accounts = credential.getAllAccounts();

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

        final String notifyKey = getString(R.string.pref_location_notify_key);
        final SwitchPreference notifyPref = (SwitchPreference) findPreference(notifyKey);

        locationProvider.getLastKnownLocation()
                .flatMap(new Func1<Location, Observable<Forecast.Response>>() {
                    @Override
                    public Observable<Forecast.Response> call(Location location) {
                        if (location == null) {
                            notifyPref.setEnabled(false);
                            notifyPref.setSummary(R.string.pref_location_notify_disabled);
                            preferences.edit().putBoolean(notifyKey, false).apply();
                            return null;
                        } else {
                            return forecastService.notifyCheckVersion(
                                    APIKeys.FORECAST,
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    LocalizationSettings.getLangCode(),
                                    LocalizationSettings.getUnitCode()
                            );
                        }
                    }
                })
                .subscribe(new Subscriber<Forecast.Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Forecast.Response response) {
                        if (response != null && response.getMinutely() == null) {
                            notifyPref.setEnabled(false);
                            notifyPref.setSummary(R.string.pref_location_notify_unavailable);
                            preferences.edit().putBoolean(notifyKey, false).apply();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        preferences.registerOnSharedPreferenceChangeListener(this);

        if (getUserVisibleHint())
            AnalyticsCodes.sendScreenView(tracker, this.getClass());
    }

    @Override
    public void onPause() {
        super.onPause();

        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch(key) {
            case "prefAccountName":
                findPreference(key).setSummary(prefs.getString(key, ""));
                credential.setSelectedAccountName(prefs.getString(key, ""));
                LocationSettings.setMode(LocationSettings.Mode.CURRENT);
                break;
            case "prefUnits":
                ListPreference unitsPref = (ListPreference) findPreference(key);
                unitsPref.setSummary(unitsPref.getEntry());
                LocalizationSettings.configure(getApp(), prefsAPI, gcmAPI);
                break;
            case "prefLocationNotify":
                if (prefs.getBoolean(key, false))
                    LocationService2.start(getBaseActivity());
                else
                    LocationService2.stop(getBaseActivity());
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
