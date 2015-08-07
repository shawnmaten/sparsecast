/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "registrationApi",
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
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    @ApiMethod(name = "register")
    public void registerDevice(User user, @Named("regId") String regId)
            throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        if (findRecord(regId) != null) {
            log.info("Device " + regId + " already registered, skipping register");
            return;
        }
        RegistrationRecord record = new RegistrationRecord();
        record.setRegId(regId);
        ofy().save().entity(record).now();
    }

    @ApiMethod(name = "unregister")
    public void unregisterDevice(User user, @Named("regId") String regId)
            throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        RegistrationRecord record = findRecord(regId);
        if (record == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
    }

    @ApiMethod(name = "listDevices")
    public CollectionResponse<RegistrationRecord> listDevices(User user, @Named("count") int count)
            throws OAuthRequestException{

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(count).list();
        return CollectionResponse.<RegistrationRecord>builder().setItems(records).build();
    }

    private RegistrationRecord findRecord(String regId) {
        return ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
    }

}
