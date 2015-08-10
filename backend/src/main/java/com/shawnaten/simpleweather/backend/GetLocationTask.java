package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.DeferredTaskContext;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class GetLocationTask implements DeferredTask {

    public static final String LOCATION_QUEUE = "get-location-queue";
    private static final Logger log = Logger.getLogger(GetLocationTask.class.getName());

    private String gcmToken;

    public GetLocationTask(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    @Override
    public void run() {
        final GCMDeviceRecord deviceRecord = GCMRegistrationEndpoint.get(gcmToken);

        Message msg = new Message.Builder().addData(MessagingCodes.TYPE,
                MessagingCodes.TYPE_LOCATION_REQUEST).build();

        switch (Messaging.sendMessage(deviceRecord, msg)) {
            case Messaging.SUCCESS:
                long delay = Math.min(
                        deviceRecord.getDelay() + TimeUnit.MINUTES.toMillis(5),
                        TimeUnit.MINUTES.toMillis(30)
                );

                deviceRecord.setDelay(delay);
                ofy().save().entity(deviceRecord).now();

                enqueue(deviceRecord, System.currentTimeMillis() + delay);
                break;
            case Messaging.FAIL_RETRY:
                DeferredTaskContext.markForRetry();
                break;
        }

    }

    public static void enqueue(final GCMDeviceRecord deviceRecord, final long etaMillis) {

        final GetLocationTask task = new GetLocationTask(deviceRecord.getGcmToken());
        final Queue locationQueue = QueueFactory.getQueue(LOCATION_QUEUE);

        if (deviceRecord.getLocationTaskName() != null) {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    Queue queue = QueueFactory.getQueue(GetLocationTask.LOCATION_QUEUE);
                    queue.deleteTask(deviceRecord.getLocationTaskName());
                    deviceRecord.setLocationTaskName(null);
                    ofy().save().entity(deviceRecord).now();
                }
            });
        }

        if (deviceRecord.getForecastTaskName() != null) {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    Queue queue = QueueFactory.getQueue(CheckForecastTask.PRECIP_NOTIFY_QUEUE);
                    queue.deleteTask(deviceRecord.getForecastTaskName());
                    deviceRecord.setForecastTaskName(null);
                    ofy().save().entity(deviceRecord).now();
                }
            });
        }

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                TaskHandle handle = locationQueue.add(
                        TaskOptions.Builder
                                .withPayload(task)
                                .etaMillis(etaMillis)
                );

                deviceRecord.setLocationTaskName(handle.getName());

                ofy().save().entity(deviceRecord).now();
            }
        });

    }

}
