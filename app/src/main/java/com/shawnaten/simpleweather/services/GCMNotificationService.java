package com.shawnaten.simpleweather.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.shawnaten.simpleweather.App;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.locationReportAPI.LocationReportAPI;
import com.shawnaten.simpleweather.backend.locationReportAPI.model.GCMDeviceRecord;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

public class GCMNotificationService extends GcmListenerService {

    private final static AtomicInteger c = new AtomicInteger(0);

    private NotificationManager notifyManager;
    private Uri soundUri;
    private SharedPreferences prefs;

    @Inject
    LocationReportAPI locationReportAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        ((App) getApplication()).getServiceComponent().injectGCMNotificationService(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        notifyManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString(MessagingCodes.TYPE);

        boolean prefLocationNotify;
        prefLocationNotify = prefs.getBoolean(getString(R.string.pref_location_notify_key), false);
        String gcmToken = prefs.getString(getString(R.string.pref_gcm_token), null);
        GCMDeviceRecord deviceRecord = new GCMDeviceRecord();
        deviceRecord.setGcmToken(gcmToken);

        if (type != null) {
            switch (type) {
                case MessagingCodes.TYPE_NOTIFY:
                    String category = data.getString(MessagingCodes.NOTIFY_CATEGORY);

                    if (category != null) {
                        switch (category) {
                            case MessagingCodes.NOTIFY_CATEGORY_CURRENT_LOCATION:
                                if (prefLocationNotify) {
                                    String content = data.getString(MessagingCodes.NOTIFY_CONTENT);
                                    if (content != null)
                                        sendNotification(content);
                                } else {
                                    try {
                                        locationReportAPI.disable(deviceRecord).execute();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case MessagingCodes.TYPE_LOCATION_REQUEST:
                    if (prefLocationNotify) {
                        Intent locationUpdaterIntent = new Intent(this, LocationReportService.class);
                        startService(locationUpdaterIntent);
                    } else {
                        try {
                            locationReportAPI.disable(deviceRecord).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private void sendNotification(String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notifyManager.notify(getID(), builder.build());
    }

    public static int getID() {
        return c.incrementAndGet();
    }
}
