package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.shawnaten.simpleweather.backend.model.GCMToken;

import java.io.IOException;
import java.util.logging.Logger;

import static com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED;
import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class Messaging {

    private static final Logger log = Logger.getLogger(Messaging.class.getName());

    public static Result sendMessage(String gcmToken, Message msg) {

        Sender sender = new Sender(System.getProperty("gcm.api.key"));
        Result result = null;

        try {
            result = sender.send(msg, gcmToken, 5);

            if (result.getMessageId() == null) {
                switch(result.getErrorCodeName()) {
                    case ERROR_NOT_REGISTERED:
                        GCMToken entity = ofy()
                                .load()
                                .type(GCMToken.class)
                                .filter("gcmToken", gcmToken)
                                .first()
                                .now();
                        ofy().delete().entity(entity).now();
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
