package com.shawnaten.simpleweather.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.tools.LocaleSettings;

public class LaunchActivity extends BaseActivity {
    private static final int REQUEST_ACCOUNT = RESULT_FIRST_USER;

    private String prefAccountKey;

    private GoogleAccountCredential cred;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String audience = "server:client_id:" + getString(R.string.WEB_ID);
        cred = GoogleAccountCredential.usingAudience(this, audience);
        prefAccountKey = getString(R.string.pref_account_key);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String prefAccountName = prefs.getString(prefAccountKey, null);
        Account accounts[] = cred.getAllAccounts();

        boolean accountRemoved = true;
        if (prefAccountName != null)
            for (Account account : accounts)
                if (account.name.equals(prefAccountName))
                    accountRemoved = false;

        if (prefAccountName == null || accountRemoved || accounts.length == 0) {
            startActivityForResult(cred.newChooseAccountIntent(), REQUEST_ACCOUNT);
            return;
        }

        LocaleSettings.configure(this);

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ACCOUNT:
                if (data != null && data.getExtras() != null) {
                    String accountName;
                    accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null)
                        setSelectedAccountName(accountName);
                }
                onResume();
                break;
        }
    }

    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefAccountKey, accountName);
        editor.apply();
        cred.setSelectedAccountName(accountName);
    }
}
