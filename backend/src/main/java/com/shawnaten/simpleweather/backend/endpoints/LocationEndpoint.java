package com.shawnaten.simpleweather.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.backend.Constants;
import com.shawnaten.simpleweather.backend.Dagger;
import com.shawnaten.simpleweather.backend.ForecastTask;
import com.shawnaten.simpleweather.backend.model.GCMToken;
import com.shawnaten.simpleweather.lib.model.Slack;

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
    ) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        Slack.Message message = new Slack.Message();
        message.setText(LocationEndpoint.class.getSimpleName() + " report");
        Dagger.getNotificationComponent().slackService().sendMessage(message).getUrl();

        GCMToken record;
        record = ofy().load().type(GCMToken.class).filter("gcmToken", gcmToken).first().now();

        ForecastTask task = new ForecastTask(record, lat, lng);

        ForecastTask.enqueue(task);

    }

}
