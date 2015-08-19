package com.shawnaten.simpleweather.backend.module;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.backend.Messaging;
import com.shawnaten.simpleweather.backend.model.GCMToken;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class LocationTask implements DeferredTask {

    public static final String QUEUE = "location-queue";

    private GCMToken gcmToken;

    public LocationTask(GCMToken gcmToken) {
        this.gcmToken = gcmToken;
    }

    @Override
    public void run() {

        Message message = new Message.Builder()
                .addData(MessagingCodes.MESSAGE_TYPE, MessagingCodes.LOCATION_REQUEST)
                .build();

        Messaging.sendMessage(gcmToken.getGcmToken(), message);

    }

    public GCMToken getGcmToken() {
        return gcmToken;
    }

    public static void delete(final GCMToken gcmToken) {
        if (gcmToken.getLocationTask() == null)
            return;

        final Queue queue = QueueFactory.getQueue(QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                queue.deleteTask(gcmToken.getLocationTask());
                gcmToken.setLocationTask(null);
                ofy().save().entity(gcmToken).now();
            }
        });
    }

    public static void enqueue(final LocationTask task, final long eta) {

        delete(task.getGcmToken());

        final Queue queue = QueueFactory.getQueue(LocationTask.QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                TaskHandle handler;
                handler = queue.add(TaskOptions.Builder.withPayload(task).etaMillis(eta));
                task.getGcmToken().setLocationTask(handler.getName());
                ofy().save().entity(task.getGcmToken()).now();
            }
        });
    }
}
