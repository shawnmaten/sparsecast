package com.shawnaten.simpleweather.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.UserIdFix;
import com.shawnaten.simpleweather.backend.model.ClientIDs;
import com.shawnaten.simpleweather.backend.model.GCMRecord;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "gcmAPI",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.simpleweather.shawnaten.com",
                ownerName = "backend.simpleweather.shawnaten.com",
                packagePath = ""
        ),
        clientIds = {
                ClientIDs.WEB_LOCAL_ID,
                ClientIDs.WEB_APP_ENGINE_ID,
                ClientIDs.ANDROID_DEBUG_ID,
                ClientIDs.ANDROID_RELEASE_ID
        },
        audiences = {
                ClientIDs.WEB_LOCAL_ID,
                ClientIDs.WEB_APP_ENGINE_ID
        }
)
public class GCMEndpoint {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(GCMEndpoint.class.getName());

    @ApiMethod(
            name = "create",
            path = "create",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void create(
            User user,
            @Named("token") String token,
            @Named("langCode") String langCode,
            @Named("unitCode") String unitCode
    ) throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord record = new GCMRecord();
        record.setUserId(UserIdFix.getUserId(user));
        record.setGcmToken(token);
        record.setLangCode(langCode);
        record.setUnitCode(unitCode);

        ofy().save().entity(record).now();
    }

    @ApiMethod(
            name = "updateToken",
            path = "updateToken",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void updateToken(
            User user,
            @Named("oldToken") String oldToken,
            @Named("newToken") String newToken
    ) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord record = ofy()
                .load()
                .type(GCMRecord.class)
                .filter("gcmToken", oldToken)
                .first()
                .now();

        record.setGcmToken(newToken);
        ofy().save().entity(record).now();
    }

    @ApiMethod(
            name = "updatePrefs",
            path = "updatePrefs",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void updatePrefs(
            User user,
            @Named("token") String token,
            @Named("langCode") String langCode,
            @Named("unitCode") String unitCode
    ) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord record = ofy()
                .load()
                .type(GCMRecord.class)
                .filter("gcmToken", token)
                .first()
                .now();

        record.setLangCode(langCode);
        record.setUnitCode(unitCode);
        ofy().save().entity(record).now();
    }

    // TODO the following are deprecated

    @ApiMethod(
            name = "insert",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void insert(
            User user,
            @Named("token") String token,
            @Named("langCode") String langCode
    ) throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord record = new GCMRecord();
        record.setUserId(UserIdFix.getUserId(user));
        record.setGcmToken(token);
        record.setLangCode(langCode);

        ofy().save().entity(record).now();
    }

    @ApiMethod(
            name = "update",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void update(
            User user,
            @Named("oldToken") String oldToken,
            @Named("newToken") String newToken,
            @Named("langCode") String langCode
    ) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        GCMRecord record = ofy()
                .load()
                .type(GCMRecord.class)
                .filter("gcmToken", oldToken)
                .first()
                .now();

        record.setGcmToken(newToken);
        record.setLangCode(langCode);
        ofy().save().entity(record).now();
    }
}