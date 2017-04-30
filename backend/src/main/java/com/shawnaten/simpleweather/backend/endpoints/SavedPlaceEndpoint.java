package com.shawnaten.simpleweather.backend.endpoints;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.UserIdFix;
import com.shawnaten.simpleweather.backend.model.ClientIDs;
import com.shawnaten.simpleweather.backend.model.SavedPlace;

import java.util.logging.Logger;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

@Api(
        name = "savedPlaceApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "simpleweather.shawnaten.com",
                ownerName = "shawnaten",
                packagePath = "backend"
        ),
        clientIds = {
                ClientIDs.WEB_LOCAL_ID,
                ClientIDs.WEB_APP_ENGINE_ID,
                ClientIDs.ANDROID_DEBUG_ID,
                ClientIDs.ANDROID_RELEASE_ID
        },
        audiences = {ClientIDs.WEB_LOCAL_ID, ClientIDs.WEB_APP_ENGINE_ID},
        defaultVersion = AnnotationBoolean.TRUE
)
public class SavedPlaceEndpoint {

    private static final Logger logger = Logger.getLogger(SavedPlaceEndpoint.class.getName());

    @ApiMethod(
            name = "insert",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void insert(User user, SavedPlace place) throws OAuthRequestException,
            EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        place.setUserId(UserIdFix.getUserId(user));
        ofy().save().entity(place).now();
    }

    @ApiMethod(
            name = "get",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public SavedPlace.Response get(User user) throws OAuthRequestException,
            EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        SavedPlace.Response response = new SavedPlace.Response();

        response.setData(ofy().load()
                .type(SavedPlace.class)
                .filter("userId", UserIdFix.getUserId(user))
                .order("name")
                .list());

        return response;
    }

    @ApiMethod(
            name = "delete",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void delete(User user, SavedPlace savedPlace) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        ofy().delete().entity(savedPlace).now();

    }

}