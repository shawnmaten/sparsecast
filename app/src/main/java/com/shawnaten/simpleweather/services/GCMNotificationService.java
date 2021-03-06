package com.shawnaten.simpleweather.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;
import com.shawnaten.simpleweather.tools.ForecastIconSelector;
import com.shawnaten.simpleweather.ui.MainActivity;

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
        String icon;
        String content;

        switch (data.getString(MessagingCodes.TYPE, "")) {
            case MessagingCodes.PRECIPITATION:
                icon = data.getString(MessagingCodes.ICON);
                content = data.getString(MessagingCodes.CONTENT);

                sendNotification(icon, content);
                break;
            case MessagingCodes.LOCATION_REQUEST:
                LocationService2.start(this);
                break;
            case MessagingCodes.NOTIFY_ENABLED:
                icon = "rain";
                content = getString(R.string.notify_enabled);

                sendNotification(icon, content);
                break;
        }
    }

    private void sendNotification(String icon, String content) {
        if (icon == null || content ==  null)
            return;

        Intent settingsIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(settingsIntent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(ForecastIconSelector.getNotifyIcon(icon))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notifyManager.notify(getID(), builder.build());
    }

    public static int getID() {
        return c.incrementAndGet();
    }
}
