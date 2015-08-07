/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "messagingApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.simpleweather.shawnaten.com",
                ownerName = "backend.simpleweather.shawnaten.com",
                packagePath = ""
        ),
        clientIds = {Constants.WEB_LOCAL_ID, Constants.WEB_APP_ENGINE_ID,
                Constants.ANDROID_DEBUG_ID, Constants.ANDROID_RELEASE_ID},
        audiences = {Constants.WEB_LOCAL_ID, Constants.WEB_APP_ENGINE_ID}
)
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());


    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(
            name = "sendMessage"
    )
    public void sendMessage(User user, @Named("regid") String regId,
                            @Named("message") String message)
            throws IOException, OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        } else if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        RegistrationRecord record = new RegistrationRecord();
        record.setRegId(regId);

        Result result = sender.send(msg, record.getRegId(), 5);

        if (result.getMessageId() != null) {
            log.info("Message sent to " + record.getRegId());
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                record.setRegId(canonicalRegId);
                ofy().save().entity(record).now();
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED)) {
                log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                // if the device is no longer registered with Gcm, remove it from the datastore
                ofy().delete().entity(record).now();
            } else {
                log.warning("Error when sending message : " + error);
            }
        }

    }

    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    /*
    public void sendMessages(User user, @Named("message") String message) throws IOException,
            OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(10).list();
        for (RegistrationRecord record : records) {
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                } else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }
    */
}
