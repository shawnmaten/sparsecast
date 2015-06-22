package com.shawnaten.simpleweather.module;

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
        GoogleAccountCredential cred;

        cred = GoogleAccountCredential.usingAudience(
                app,
                "server:client_id:" + app.getString(R.string.WEB_ID));
        if (cred.getAllAccounts() != null)
            cred.setSelectedAccountName(cred.getAllAccounts()[0].name);

        return cred;
    }
}
