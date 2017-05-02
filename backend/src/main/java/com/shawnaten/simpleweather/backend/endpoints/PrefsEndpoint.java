package com.shawnaten.simpleweather.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.UserIdFix;
import com.shawnaten.simpleweather.backend.model.ClientIDs;
import com.shawnaten.simpleweather.backend.model.Prefs;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "prefsAPI",
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
public class PrefsEndpoint {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PrefsEndpoint.class.getName());

    @ApiMethod(
            name = "insert",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void insert(
            User user, @Named("unitCode") String unitCode
    ) throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        Prefs prefs = ofy()
                .load()
                .type(Prefs.class)
                .filter("userId", UserIdFix.getUserId(user))
                .first()
                .now();

        if (prefs == null) {
            prefs = new Prefs();
            prefs.setUserId(UserIdFix.getUserId(user));
        }

        prefs.setUnitCode(unitCode);

        ofy().save().entity(prefs).now();
    }

    @ApiMethod(
            name = "get",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Prefs get(User user) throws OAuthRequestException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        return ofy()
                .load()
                .type(Prefs.class)
                .filter("userId", UserIdFix.getUserId(user))
                .first()
                .now();
    }
}