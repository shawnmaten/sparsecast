package com.shawnaten.simpleweather.ui;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.locationReportAPI.LocationReportAPI;
import com.shawnaten.simpleweather.backend.locationReportAPI.model.GCMDeviceRecord;
import com.shawnaten.simpleweather.services.GeofenceService;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    protected String analyticsTrackName = "SettingsFragment";

    @Inject
    LocationReportAPI locationReportAPI;

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
    public void onSharedPreferenceChanged(final SharedPreferences prefs, String key) {
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
                final String prefLocationNotifyKey = getString(R.string.pref_location_notify_key);
                final GCMDeviceRecord deviceRecord = new GCMDeviceRecord();

                deviceRecord.setGcmToken(prefs.getString(getString(R.string.pref_gcm_token), null));

                Observable<Void> observable;

                observable = Observable.create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        try {
                            if (prefs.getBoolean(prefLocationNotifyKey, false)) {
                                Intent intent = new Intent(getBaseActivity(),
                                        GeofenceService.class);
                                getBaseActivity().startService(intent);
                            } else {
                                locationReportAPI.disable(deviceRecord).execute();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

                getBaseActivity().subs.add(observable.subscribe());

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
