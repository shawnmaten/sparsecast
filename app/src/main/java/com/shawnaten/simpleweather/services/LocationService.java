package com.shawnaten.simpleweather.services;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;

import com.shawnaten.simpleweather.App;

public class LocationService extends Service {

    private LocationHandler locationHandler;
    private Message msg;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        locationHandler = new LocationHandler(thread.getLooper(), (App) getApplication());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (msg == null) {
            msg = locationHandler.obtainMessage();
            msg.arg1 = startId;
            locationHandler.sendMessage(msg);
        }

        return START_STICKY;
    }

}
