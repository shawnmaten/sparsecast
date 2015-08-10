package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "savedPlaceApi",
        version = "v1",
        resource = "savedPlace",
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
public class SavedPlaceEndpoint {

    private static final Logger logger = Logger.getLogger(SavedPlaceEndpoint.class.getName());

    @ApiMethod(
            name = "insert",
            path = "savedPlace",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SavedPlace insert(User user, SavedPlace savedPlace) throws OAuthRequestException,
            EntityNotFoundException {
        if (user != null) {
            SavedPlace existing = check(user, savedPlace);
            savedPlace.setUserId(UserIdFix.getUserId(user));
            savedPlace.setTimestamp(new Date());
            if (existing == null) {
                ofy().save().entity(savedPlace).now();
                return ofy().load().entity(savedPlace).now();
            }
            return existing;
        } else {
            throw new OAuthRequestException("unauthorized request");
        }
    }

    @ApiMethod(
            name = "list",
            path = "savedPlace",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<SavedPlace> list(User user) throws OAuthRequestException,
            EntityNotFoundException {
        if (user != null) {
            List<SavedPlace> savedPlaces = ofy().load().type(SavedPlace.class)
                    .filter("userId", UserIdFix.getUserId(user)).list();
            Collections.sort(savedPlaces, new Comparator<SavedPlace>() {
                @Override
                public int compare(SavedPlace o1, SavedPlace o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
            return savedPlaces;
        } else
            throw new OAuthRequestException("unauthorized request");
    }

    @ApiMethod(
            name = "check",
            path = "savedPlaceCheck",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SavedPlace check(User user, SavedPlace savedPlace) throws OAuthRequestException,
            EntityNotFoundException {
        if (user != null) {
            List<SavedPlace> matches = ofy().load().type(SavedPlace.class)
                    .filter("userId", UserIdFix.getUserId(user))
                    .filter("placeId", savedPlace.getPlaceId())
                    .list();
            return matches.size() == 0 ? null : matches.get(0);
        } else
            throw new OAuthRequestException("unauthorized request");
    }

    @ApiMethod(
            name = "delete",
            path = "savedPlaceDelete",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void delete(User user, SavedPlace savedPlace) throws OAuthRequestException,
            EntityNotFoundException {
        if (user != null) {
            ofy().delete().entity(check(user, savedPlace));
        } else
            throw new OAuthRequestException("unauthorized request");
    }
}