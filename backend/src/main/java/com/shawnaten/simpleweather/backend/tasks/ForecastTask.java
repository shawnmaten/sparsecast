package com.shawnaten.simpleweather.backend.tasks;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.backend.Dagger;
import com.shawnaten.simpleweather.backend.Messaging;
import com.shawnaten.simpleweather.backend.model.GCMToken;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit.RetrofitError;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class ForecastTask implements DeferredTask {

    private static final double THRESHOLD = .5;
    public static final String QUEUE = "forecast-queue";
    private static final Logger log = Logger.getLogger(Messaging.class.getName());

    private GCMToken gcmToken;
    private double lat;
    private double lng;

    public ForecastTask(GCMToken gcmToken, double lat, double lng) {
        this.gcmToken = gcmToken;
        this.lat = lat;
        this.lng = lng;
    }

    public GCMToken getGcmToken() {
        return gcmToken;
    }

    @Override
    public void run() {
        Forecast.Service forecastService = Dagger.getNotificationComponent().forecastService();

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

        Forecast.DataBlock minutely = forecast.getMinutely();
        Forecast.DataBlock hourly = forecast.getHourly();
        Forecast.DataBlock daily = forecast.getDaily();

        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("US/Central"));

        if (minutely == null || hourly == null || daily == null)
            return;

        String message = "***\n" + ForecastTask.class.getSimpleName() + "\n\n";
        message += dateFormat.format(new Date()) + "\n\n";
        boolean notify = false;

        long endOfMinutely = minutely.getData()[60].getTime().getTime();
        long endOfHourly = hourly.getData()[48].getTime().getTime();

        message += "minutely\n\n[";
        for (int i = 0; i < minutely.getData().length; i++) {
            Forecast.DataPoint dataPoint = minutely.getData()[i];
            message += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() > THRESHOLD) {
                notify = true;
                eta = endOfMinutely;
            }
        }
        message = message.substring(0, message.length() - 2);
        message += "]\n\n";

        message += "hourly\n\n[";
        for (int i = 1; i < hourly.getData().length; i++) {
            Forecast.DataPoint dataPoint = hourly.getData()[i];
            message += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() > THRESHOLD) {
                eta = dataPoint.getTime().getTime() - TimeUnit.MINUTES.toMillis(30);
                eta = Math.max(eta, endOfMinutely);
            }
        }
        message = message.substring(0, message.length() - 2);
        message += "]\n\n";

        message += "daily\n\n[";
        for (int i = 2; i < daily.getData().length; i++) {
            Forecast.DataPoint dataPoint = daily.getData()[i];
            message += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() > THRESHOLD) {
                eta = dataPoint.getTime().getTime() - TimeUnit.MINUTES.toMillis(30);
                eta = Math.max(eta, endOfHourly);
            }
        }
        message = message.substring(0, message.length() - 2);
        message += "]\n\n";

        if (notify) {
            message += ":bell: This would have sent a notification. :bell:\n\n";
            message += minutely.getSummary() + "\n\n";
        }

        message += "Will check again at " + dateFormat.format(new Date(eta)) + "\n***";

        LocationTask task = new LocationTask(gcmToken);
        LocationTask.enqueue(task, eta);
    }

    public static void delete(final GCMToken gcmToken) {
        if (gcmToken.getForecastTask() == null)
            return;

        final Queue queue = QueueFactory.getQueue(QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                queue.deleteTask(gcmToken.getForecastTask());
                gcmToken.setForecastTask(null);
                ofy().save().entity(gcmToken).now();
            }
        });
    }

    public static void enqueue(final ForecastTask task) {

        LocationTask.delete(task.getGcmToken());
        delete(task.getGcmToken());

        final Queue queue = QueueFactory.getQueue(LocationTask.QUEUE);

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                TaskHandle handler;
                handler = queue.add(TaskOptions.Builder.withPayload(task));
                task.gcmToken.setForecastTask(handler.getName());
                ofy().save().entity(task.gcmToken).now();
            }
        });
    }

}
