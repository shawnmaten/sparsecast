package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.util.logging.Logger;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "registrationApi",
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
        audiences = {Constants.WEB_LOCAL_ID, Constants.WEB_APP_ENGINE_ID}

)
public class GCMRegistrationEndpoint {

    private static final Logger log = Logger.getLogger(GCMRegistrationEndpoint.class.getName());

    @ApiMethod(
            name = "register",
            httpMethod = HttpMethod.POST
    )
    public GCMDeviceRecord register(User user, GCMDeviceRecord deviceRecord)
            throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        log.warning("register running");

        String userId = UserIdFix.getUserId(user);
        deviceRecord.setUserId(userId);

        GCMDeviceRecord checkRecord;

        checkRecord = ofy().load()
                .type(GCMDeviceRecord.class)
                .filter("gcmToken", deviceRecord.getGcmToken())
                .first()
                .now();

        if (checkRecord == null) {
            ofy().save().entity(deviceRecord).now();
        } else {
            log.info("Id (" + deviceRecord.getGcmToken() + ") already registered.");
        }

        return deviceRecord;
    }

    @ApiMethod(
            name = "unregister",
            httpMethod = HttpMethod.POST
    )
    public void unregister(User user, GCMDeviceRecord deviceRecord)
            throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMDeviceRecord checkRecord;

        checkRecord = ofy().load()
                .type(GCMDeviceRecord.class)
                .filter("gcmToken", deviceRecord.getGcmToken())
                .first()
                .now();

        if (checkRecord != null) {
            ofy().delete().entity(checkRecord).now();
        } else {
            log.info("Id (" + deviceRecord.getGcmToken() + ") doesn't exist");
        }

    }

    public static GCMDeviceRecord get(String gcmToken) {
        return ofy().load().type(GCMDeviceRecord.class).filter("gcmToken", gcmToken).first().now();
    }

}
