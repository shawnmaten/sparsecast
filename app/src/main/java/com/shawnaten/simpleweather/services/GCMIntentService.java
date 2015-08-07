package com.shawnaten.simpleweather.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shawnaten.simpleweather.R;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GCMIntentService extends IntentService {

    private NotificationManager notifyManager;
    private Uri soundUri;
    private static final int notfiyId = 0;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notifyManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                showNotification(extras.getString("message"));
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notifyManager.notify(notfiyId, builder.build());
    }
}
