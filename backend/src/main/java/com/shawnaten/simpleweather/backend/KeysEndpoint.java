package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.util.logging.Logger;

/** An endpoint class we are exposing */
@Api(
        name = "keysEndpoint",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.simpleweather.shawnaten.com",
        ownerName = "backend.simpleweather.shawnaten.com", packagePath=""),
        clientIds = {Constants.WEB_ID, Constants.ANDROID_ID},
        audiences = {Constants.ANDROID_AUDIENCE}
)

public class KeysEndpoint {
    private static final Keys keys = new Keys(Constants.PUBLIC_GOOGLE_API_KEY, Constants.PUBLIC_FORECAST_API_KEY);
    private static final Logger LOG = Logger.getLogger(KeysEndpoint.class.getName());

    @ApiMethod(name = "getKeys")
    public Keys getKeys(User user) throws OAuthRequestException {
        if (user != null) {
            return keys;
        }
        throw new OAuthRequestException("unauthorized request");
    }

}