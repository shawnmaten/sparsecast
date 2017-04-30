package com.shawnaten.simpleweather.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.model.ClientIDs;
import com.shawnaten.simpleweather.backend.model.Keys;
import com.shawnaten.simpleweather.lib.model.APIKeys;

import java.util.logging.Logger;

/** An endpoint class we are exposing */
@Api(
        name = "keysEndpoint",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.simpleweather.shawnaten.com",
        ownerName = "backend.simpleweather.shawnaten.com", packagePath=""),
        clientIds = {
                ClientIDs.WEB_LOCAL_ID,
                ClientIDs.WEB_APP_ENGINE_ID,
                ClientIDs.ANDROID_DEBUG_ID,
                ClientIDs.ANDROID_RELEASE_ID
        },
        audiences = {ClientIDs.WEB_LOCAL_ID, ClientIDs.WEB_APP_ENGINE_ID}
)

public class KeysEndpoint {
    private static final Keys keys = new Keys(
            APIKeys.GOOGLE,
            APIKeys.FORECAST,
            APIKeys.INSTAGRAM);
    private static final Logger LOG = Logger.getLogger(KeysEndpoint.class.getName());

    @ApiMethod(name = "getKeys")
    public Keys getKeys(User user) throws OAuthRequestException {
        if (user != null) {
            return keys;
        }
        throw new OAuthRequestException("unauthorized request");
    }

}