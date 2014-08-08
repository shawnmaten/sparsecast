package com.shawnaten.tools;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.shawnaten.simpleweather.MainActivity;
import com.shawnaten.simpleweather.R;

/**
 * Created by Shawn Aten on 8/7/14.
 */
public class PlayServices {
    private static Dialog playServicesError;

    public static boolean playServicesAvailable(FragmentActivity activity) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                if (playServicesError == null || !playServicesError.isShowing()) {
                    playServicesError = GooglePlayServicesUtil.getErrorDialog(status, activity, MainActivity.REQUEST_CODE_RECOVER_PLAY_SERVICES);
                    playServicesError.setCancelable(false);
                    playServicesError.show();
                }
            } else if (activity.getSupportFragmentManager().findFragmentByTag("playServicesError") == null) {
                CustomAlertDialog playServicesError = new CustomAlertDialog();
                playServicesError.setCancelable(false);
                Bundle args = new Bundle();
                args.putString("title", activity.getString(R.string.play_services));
                args.putString("message", activity.getString(R.string.play_services_unsupported));
                args.putInt("code", 0);
                playServicesError.setArguments(args);
                playServicesError.show(activity.getSupportFragmentManager(), "playServicesError");
            }
            return false;
        }
        return true;
    }
}
