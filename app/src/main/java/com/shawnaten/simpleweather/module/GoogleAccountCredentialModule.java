package com.shawnaten.simpleweather.module;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GoogleAccountCredentialModule {

    @Provides
    @Singleton
    public GoogleAccountCredential providesGoogleAccountCredential(App app) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        String accountName;
        GoogleAccountCredential cred;

        cred = GoogleAccountCredential.usingAudience(app,
                "server:client_id:" + app.getString(R.string.WEB_ID));

        accountName = preferences.getString(app.getString(R.string.account_key),
                cred.getAllAccounts()[0].name);

        cred.setSelectedAccountName(accountName);

        return cred;
    }
}
