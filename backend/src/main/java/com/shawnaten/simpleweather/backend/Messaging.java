package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.logging.Logger;

import static com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED;
import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class Messaging {
    public static final int SUCCESS = 0;
    public static final int FAIL_RETRY = 1;
    public static final int FAIL_NO_RETRY = 2;

    private static final Logger log = Logger.getLogger(Messaging.class.getName());

    public static int sendMessage(GCMDeviceRecord deviceRecord, Message msg) {

        try {
            Sender sender = new Sender(System.getProperty("gcm.api.key"));
            Result result = sender.send(msg, deviceRecord.getGcmToken(), 5);

            if (result.getMessageId() == null) {
                switch (result.getErrorCodeName()) {
                    case ERROR_NOT_REGISTERED:
                        ofy().delete().entity(deviceRecord).now();
                        return FAIL_NO_RETRY;
                    default:
                        log.warning(result.getErrorCodeName());
                        return FAIL_RETRY;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return FAIL_RETRY;
        }

        return SUCCESS;
    }
}
