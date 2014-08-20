package com.shawnaten.tools;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Shawn Aten on 8/20/14.
 */
public class PlayServices {

    public static boolean playServicesAvailable(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            GooglePlayServicesUtil.showErrorNotification(resultCode, context);
            return false;
        }
    }

}
