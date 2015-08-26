package com.shawnaten.simpleweather.backend.endpoints;

import com.google.android.gcm.server.Message;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.Messaging;
import com.shawnaten.simpleweather.backend.model.Constants;
import com.shawnaten.simpleweather.backend.model.GCMRecord;
import com.shawnaten.simpleweather.backend.tasks.ForecastTask;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "locationAPI",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.simpleweather.shawnaten.com",
                ownerName = "backend.simpleweather.shawnaten.com",
                packagePath = ""
        ),
        clientIds = {
                Constants.WEB_LOCAL_ID,
                Constants.WEB_APP_ENGINE_ID,
                Constants.ANDROID_DEBUG_ID,
                Constants.ANDROID_RELEASE_ID
        },
        audiences = {
                Constants.WEB_LOCAL_ID,
                Constants.WEB_APP_ENGINE_ID
        }
)
public class LocationEndpoint {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(LocationEndpoint.class.getName());

    @ApiMethod(
            name = "report",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void report(
            User user,
            @Named("gcmToken") String gcmToken,
            @Named("lat") double lat,
            @Named("lng") double lng
    ) throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord gcmRecord = ofy().load()
                .type(GCMRecord.class)
                .filter("gcmToken", gcmToken)
                .first().now();

        if (gcmRecord == null) {
            log.setLevel(Level.SEVERE);
            log.severe("gcmRecord was null");
            return;
        }

        ForecastTask task = new ForecastTask(gcmRecord, lat, lng);

        ForecastTask.enqueue(task);

    }

    @ApiMethod(
            name = "test",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void test(
            User user,
            @Named("gcmToken") String gcmToken
    ) throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord record;
        record = ofy().load().type(GCMRecord.class).filter("gcmToken", gcmToken).first().now();

        Message msg = new Message.Builder()
                .addData(MessagingCodes.TYPE, MessagingCodes.NOTIFY_ENABLED)
                .build();
        Messaging.sendMessage(record.getGcmToken(), msg);

    }

}
