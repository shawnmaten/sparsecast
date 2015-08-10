package com.shawnaten.simpleweather.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GoogleAccountCredentialModule {

    @Provides
    @Singleton
    public GoogleAccountCredential providesGoogleAccountCredential(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accountName;
        GoogleAccountCredential cred;

        cred = GoogleAccountCredential.usingAudience(context,
                "server:client_id:" + context.getString(R.string.WEB_ID));

        accountName = preferences.getString(context.getString(R.string.pref_account_key),
                cred.getAllAccounts()[0].name);

        cred.setSelectedAccountName(accountName);

        return cred;
    }
}
