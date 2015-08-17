package com.shawnaten.simpleweather.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.Constants;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

@Api(
        name = "gcmAPI",
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
public class GCM {

    private static final Logger log = Logger.getLogger(GCM.class.getName());

    @ApiMethod(
            name = "insert",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void insert(
            User user, @Named("token") String token
    ) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        log.setLevel(Level.INFO);
        log.info(user.getUserId());

    }

    @ApiMethod(
            name = "update",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void update(
            User user,
            @Named("oldToken") String oldToken,
            @Named("newToken") String newToken
    ) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        log.setLevel(Level.INFO);
        log.info(user.getUserId());

    }

}