package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.lib.model.ResponseCodes;

import java.io.IOException;
import java.util.logging.Logger;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

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
            name = "disable",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void disable(User user, GCMDeviceRecord deviceRecord) throws OAuthRequestException,
            IOException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        if (deviceRecord == null)
            return;

        final GCMDeviceRecord dbDeviceRecord;
        dbDeviceRecord = GCMRegistrationEndpoint.get(deviceRecord.getGcmToken());

        LocationRecord locationRecord = get(deviceRecord.getGcmToken());
        if (locationRecord != null)
            ofy().delete().entity(locationRecord).now();

        if (dbDeviceRecord != null) {
            dbDeviceRecord.setCurrentLocationNotify(false);
            ofy().save().entity(dbDeviceRecord).now();

            if (dbDeviceRecord.getLocationTaskName() != null) {
                ofy().transact(new VoidWork() {
                    @Override
                    public void vrun() {
                        Queue queue = QueueFactory.getQueue(GetLocationTask.LOCATION_QUEUE);
                        queue.deleteTask(dbDeviceRecord.getLocationTaskName());
                        dbDeviceRecord.setLocationTaskName(null);
                        ofy().save().entity(dbDeviceRecord).now();
                    }
                });
            }

            if (dbDeviceRecord.getForecastTaskName() != null) {
                ofy().transact(new VoidWork() {
                    @Override
                    public void vrun() {
                        Queue queue = QueueFactory.getQueue(CheckForecastTask.PRECIP_NOTIFY_QUEUE);
                        queue.deleteTask(dbDeviceRecord.getForecastTaskName());
                        dbDeviceRecord.setForecastTaskName(null);
                        ofy().save().entity(dbDeviceRecord).now();
                    }
                });
            }
        }

    }

    @ApiMethod(
            name = "report",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Response report(User user, LocationRecord locationRecord) throws OAuthRequestException,
            IOException, EntityNotFoundException {

        if (user == null)
            throw new OAuthRequestException("unauthorized request");

        Response response = new Response();

        if (locationRecord == null) {
            response.setMessage(ResponseCodes.NULL_CONTENT);
            return response;
        }

        GCMDeviceRecord deviceRecord = GCMRegistrationEndpoint.get(locationRecord.getGcmToken());

        if (deviceRecord == null) {
            response.setMessage(ResponseCodes.NOT_REGISTERED);
            return response;
        }

        if (!deviceRecord.isCurrentLocationNotify()) {
            deviceRecord.setCurrentLocationNotify(true);
            ofy().save().entity(deviceRecord).now();
        }

        LocationRecord dbLocationRecord = get(locationRecord.getGcmToken());

        if (dbLocationRecord != null) {
            dbLocationRecord.setLat(locationRecord.getLat());
            dbLocationRecord.setLng(locationRecord.getLng());
            locationRecord = dbLocationRecord;
        }

        ofy().save().entity(locationRecord).now();

        CheckForecastTask.enqueue(locationRecord);

        response.setMessage(ResponseCodes.OK);
        return response;
    }

    public static LocationRecord get(String gcmToken) {
        return ofy().load().type(LocationRecord.class).filter("gcmToken", gcmToken).first().now();
    }
}
