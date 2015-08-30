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
        String accountKey = context.getString(R.string.pref_account_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String webId = context.getString(R.string.WEB_ID);
        String accountName = prefs.getString(accountKey, null);
        GoogleAccountCredential cred;

        cred = GoogleAccountCredential.usingAudience(context, "server:client_id:" + webId);

        if (accountName != null)
            cred.setSelectedAccountName(accountName);

        return cred;
    }
}
