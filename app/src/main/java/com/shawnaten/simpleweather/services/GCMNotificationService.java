package com.shawnaten.simpleweather.services;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import java.util.concurrent.atomic.AtomicInteger;

public class GCMNotificationService extends GcmListenerService {
    private final static AtomicInteger c = new AtomicInteger(0);

    private NotificationManager notifyManager;
    private Uri soundUri;

    @Override
    public void onCreate() {
        super.onCreate();

        notifyManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {

        switch (data.getString(MessagingCodes.MESSAGE_TYPE, "")) {
            case MessagingCodes.MESSAGE_TYPE_HOUR:
                switch (data.getString(MessagingCodes.HOUR_TYPE, "")) {
                    case MessagingCodes.HOUR_TYPE_CURRENT:
                        sendNotification(data.getString(MessagingCodes.HOUR_CONTENT));
                        break;
                    case MessagingCodes.HOUR_TYPE_SAVED:
                        break;
                }
                break;
            case MessagingCodes.LOCATION_REQUEST:
                Log.e(this.getClass().getSimpleName(), MessagingCodes.LOCATION_REQUEST);
                LocationService2.start(this);
                break;
        }
    }

    private void sendNotification(String content){

        if (content == null)
            return;

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
