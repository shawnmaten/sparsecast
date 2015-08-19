package com.shawnaten.simpleweather.backend;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.backend.model.GCMToken;
import com.shawnaten.simpleweather.backend.module.LocationTask;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.lib.model.Slack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class ForecastTask implements DeferredTask {

    public static final String QUEUE = "forecast-queue";

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
        Slack.Service slackService = Dagger.getNotificationComponent().slackService();

        Forecast.Response forecast;
        long eta;
        
        forecast = forecastService.notifyVersion(APIKeys.FORECAST_API_KEY, lat, lng, "en", "us");
        eta = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);

        Forecast.DataBlock minutely = forecast.getMinutely();
        Forecast.DataBlock hourly = forecast.getHourly();
        Forecast.DataBlock daily = forecast.getDaily();

        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        if (minutely == null || hourly == null || daily == null)
            return;

        String text = dateFormat.format(new Date()) + "\n\n";

        text += "minutely\n\n[";
        for (Forecast.DataPoint dataPoint : minutely.getData()) {
            text += String.format("%.2f, ", dataPoint.getPrecipProbability());
        }
        text = text.substring(0, text.length() - 2);
        text += "]";

        text += "hourly\n\n[";
        for (Forecast.DataPoint dataPoint : hourly.getData()) {
            text += String.format("%.2f, ", dataPoint.getPrecipProbability());
        }
        text = text.substring(0, text.length() - 2);
        text += "]";

        text += "daily\n\n[";
        for (Forecast.DataPoint dataPoint : daily.getData()) {
            text += String.format("%.2f, ", dataPoint.getPrecipProbability());
        }
        text = text.substring(0, text.length() - 2);
        text += "]\n\n";

        text += "Sending next on " + dateFormat.format(new Date(eta));

        Slack.Message message = new Slack.Message();
        message.setText(text);
        slackService.sendMessage(message);

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

        delete(task.getGcmToken());
        LocationTask.delete(task.getGcmToken());

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
