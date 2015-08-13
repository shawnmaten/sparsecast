package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import java.util.logging.Logger;

@Api(
        name = "locationReportAPI",
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
public class LocationEndpoint {

    private static final Logger log = Logger.getLogger(LocationEndpoint.class.getName());

    @ApiMethod(
            name = "report",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void report(User user, Location location) throws OAuthRequestException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        ForecastTask task = new ForecastTask(
                MessagingCodes.HOUR_TYPE_CURRENT,
                location.getGcmToken(),
                location.getLat(),
                location.getLng()
        );

        Queue queue = QueueFactory.getQueue(ForecastTask.QUEUE);

        queue.add(TaskOptions.Builder.withPayload(task));

    }

}
