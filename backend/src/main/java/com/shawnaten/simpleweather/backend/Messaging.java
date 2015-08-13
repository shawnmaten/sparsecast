package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.logging.Logger;

public class Messaging {

    private static final Logger log = Logger.getLogger(Messaging.class.getName());

    public static Result sendMessage(String gcmToken, Message msg) {

        Sender sender = new Sender(System.getProperty("gcm.api.key"));
        Result result = null;

        try {
            result = sender.send(msg, gcmToken, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
