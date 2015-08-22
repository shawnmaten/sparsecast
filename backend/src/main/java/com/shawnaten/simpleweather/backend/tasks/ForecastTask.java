package com.shawnaten.simpleweather.backend.tasks;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.backend.Dagger;
import com.shawnaten.simpleweather.backend.Messaging;
import com.shawnaten.simpleweather.backend.model.GCMRecord;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit.RetrofitError;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class ForecastTask implements DeferredTask {

    public static final String QUEUE = "forecast-queue";
    private static final Logger log = Logger.getLogger(Messaging.class.getName());

    private GCMRecord gcmRecord;
    private double lat;
    private double lng;

    public ForecastTask(GCMRecord gcmRecord, double lat, double lng) {
        this.gcmRecord = gcmRecord;
        this.lat = lat;
        this.lng = lng;
    }

    public GCMRecord getGcmRecord() {
        return gcmRecord;
    }

    @Override
    public void run() {
        Forecast.Service forecastService = Dagger.getNotificationComponent().forecastService();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT
        );
        dateFormat.setTimeZone(TimeZone.getTimeZone("US/Central"));

        Forecast.Response forecast;
        long eta;

        try {
            forecast = forecastService.notifyVersion(APIKeys.FORECAST, lat, lng, "en", "us");
            eta = 0;
        } catch (RetrofitError e) {
            log.setLevel(Level.SEVERE);
            log.severe(e.getKind().name());
            return;
        }

        Forecast.DataBlock minutelyBlock = forecast.getMinutely();
        Forecast.DataBlock hourlyBlock = forecast.getHourly();
        Forecast.DataBlock dailyBlock = forecast.getDaily();

        if (minutelyBlock == null || hourlyBlock == null || dailyBlock == null)
            return;

        Forecast.DataPoint minutelyData[] = minutelyBlock.getData();
        Forecast.DataPoint hourlyData[] = hourlyBlock.getData();
        Forecast.DataPoint dailyData[] = dailyBlock.getData();

        boolean notify = false;

        long endOfMinutely = minutelyData[minutelyData.length - 1].getTime().getTime();
        long endOfHourly = hourlyData[hourlyData.length - 1].getTime().getTime();

        String message = "\nminutely\n[";
        for (Forecast.DataPoint dataPoint : minutelyData) {
            message += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() > 0) {
                notify = true;
                eta = endOfMinutely;
            }
        }
        message = message.substring(0, message.length() - 2);
        message += "]\n\n";

        message += "hourly\n[";
        for (Forecast.DataPoint dataPoint : hourlyData) {
            message += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() > 0)
                eta = Math.max(endOfMinutely, dataPoint.getTime().getTime());
        }
        message = message.substring(0, message.length() - 2);
        message += "]\n\n";

        message += "daily\n[";
        for (Forecast.DataPoint dataPoint : dailyData) {
            message += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() > 0)
                eta = Math.max(endOfHourly, dataPoint.getTime().getTime());
        }
        message = message.substring(0, message.length() - 2);
        message += "]\n\n";

        if (notify) {
            Message msg = new Message.Builder()
                    .addData(MessagingCodes.TYPE, MessagingCodes.PRECIPITATION)
                    .addData(MessagingCodes.ICON, minutelyBlock.getIcon())
                    .addData(MessagingCodes.CONTENT, minutelyBlock.getSummary())
                    .build();
            Messaging.sendMessage(gcmRecord.getGcmToken(), msg);

            message += "This sent a notification.\n";
            message += minutelyBlock.getSummary() + "\n\n";
        }

        message += "Next check at " + dateFormat.format(new Date(eta));

        log.setLevel(Level.INFO);
        log.info(message);

        LocationTask task = new LocationTask(gcmRecord);
        LocationTask.enqueue(task, eta);
    }

    public static void delete(final GCMRecord gcmRecord) {
        if (gcmRecord.getForecastTask() == null)
            return;

        final Queue queue = QueueFactory.getQueue(QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                queue.deleteTask(gcmRecord.getForecastTask());
                gcmRecord.setForecastTask(null);
                ofy().save().entity(gcmRecord).now();
            }
        });
    }

    public static void enqueue(final ForecastTask task) {

        LocationTask.delete(task.getGcmRecord());
        delete(task.getGcmRecord());

        final Queue queue = QueueFactory.getQueue(LocationTask.QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                TaskHandle handler;
                handler = queue.add(TaskOptions.Builder.withPayload(task));
                task.gcmRecord.setForecastTask(handler.getName());
                ofy().save().entity(task.gcmRecord).now();
            }
        });
    }

}
