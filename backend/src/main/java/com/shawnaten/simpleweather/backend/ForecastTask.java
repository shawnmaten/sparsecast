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
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class ForecastTask implements DeferredTask {

    private static final double MINUTELY_THRESHOLD = .5;
    private static final double HOURLY_DAILY_THRESHOLD = .2;

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
        eta = 0;

        Forecast.DataBlock minutely = forecast.getMinutely();
        Forecast.DataBlock hourly = forecast.getHourly();
        Forecast.DataBlock daily = forecast.getDaily();

        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("US/Central"));

        if (minutely == null || hourly == null || daily == null)
            return;

        String text = "***\n" + ForecastTask.class.getSimpleName() + "\n\n";
        text += dateFormat.format(new Date()) + "\n\n";
        boolean notify = false;

        long endOfMinutely = minutely.getData()[60].getTime().getTime();
        long endOfHourly = hourly.getData()[48].getTime().getTime();

        text += "minutely\n\n[";
        for (int i = 0; i < minutely.getData().length; i++) {
            Forecast.DataPoint dataPoint = minutely.getData()[i];
            text += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() >= MINUTELY_THRESHOLD) {
                notify = true;
                eta = endOfMinutely;
            }
        }
        text = text.substring(0, text.length() - 2);
        text += "]\n\n";

        text += "hourly\n\n[";
        for (int i = 1; i < hourly.getData().length; i++) {
            Forecast.DataPoint dataPoint = hourly.getData()[i];
            text += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() >= HOURLY_DAILY_THRESHOLD) {
                eta = dataPoint.getTime().getTime() - TimeUnit.MINUTES.toMillis(30);
                eta = Math.max(eta, endOfMinutely);
            }
        }
        text = text.substring(0, text.length() - 2);
        text += "]\n\n";

        text += "daily\n\n[";
        for (int i = 2; i < daily.getData().length; i++) {
            Forecast.DataPoint dataPoint = daily.getData()[i];
            text += String.format("%.2f, ", dataPoint.getPrecipProbability());
            if (eta == 0 && dataPoint.getPrecipProbability() >= HOURLY_DAILY_THRESHOLD) {
                eta = dataPoint.getTime().getTime() - TimeUnit.MINUTES.toMillis(30);
                eta = Math.max(eta, endOfHourly);
            }
        }
        text = text.substring(0, text.length() - 2);
        text += "]\n\n";

        if (notify)
            text += ":bell: This would have sent a notification. :bell:\n\n";

        text += "Will check again at " + dateFormat.format(new Date(eta)) + "\n***";

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
