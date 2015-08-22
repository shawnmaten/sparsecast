package com.shawnaten.simpleweather.backend.tasks;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.backend.Messaging;
import com.shawnaten.simpleweather.backend.model.GCMRecord;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class LocationTask implements DeferredTask {

    public static final String QUEUE = "location-queue";

    private GCMRecord gcmRecord;

    public LocationTask(GCMRecord gcmRecord) {
        this.gcmRecord = gcmRecord;
    }

    @Override
    public void run() {
        Message message = new Message.Builder()
                .addData(MessagingCodes.TYPE, MessagingCodes.LOCATION_REQUEST)
                .build();

        Messaging.sendMessage(gcmRecord.getGcmToken(), message);
    }

    public GCMRecord getGcmRecord() {
        return gcmRecord;
    }

    public static void delete(final GCMRecord gcmRecord) {
        if (gcmRecord.getLocationTask() == null)
            return;

        final Queue queue = QueueFactory.getQueue(QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                queue.deleteTask(gcmRecord.getLocationTask());
                gcmRecord.setLocationTask(null);
                ofy().save().entity(gcmRecord).now();
            }
        });
    }

    public static void enqueue(final LocationTask task, final long eta) {

        delete(task.getGcmRecord());

        final Queue queue = QueueFactory.getQueue(QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                TaskHandle handler;
                handler = queue.add(TaskOptions.Builder.withPayload(task).etaMillis(eta));
                task.getGcmRecord().setLocationTask(handler.getName());
                ofy().save().entity(task.getGcmRecord()).now();
            }
        });
    }
}
