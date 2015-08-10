package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.DeferredTaskContext;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.VoidWork;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;
import com.shawnaten.simpleweather.lib.tools.Precip;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class CheckForecastTask implements DeferredTask {

    public static final String PRECIP_NOTIFY_QUEUE = "notify-precip-queue";
    private static final Logger log = Logger.getLogger(CheckForecastTask.class.getName());

    private String gcmToken;

    public CheckForecastTask(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    @Override
    public void run() {
        final GCMDeviceRecord deviceRecord = GCMRegistrationEndpoint.get(gcmToken);
        final LocationRecord locationRecord = LocationEndpoint.get(gcmToken);

        deviceRecord.setDelay(0);
        ofy().save().entity(deviceRecord).now();

        if (!deviceRecord.isCurrentLocationNotify())
            return;

        Forecast.Service forecastService = Dagger.getNotifyComp().forecastService();

        Forecast.Response forecast = forecastService.notifyVersion(
                Constants.PUBLIC_FORECAST_API_KEY,
                locationRecord.getLat(),
                locationRecord.getLng(),
                "en",
                "us"
        );

        Forecast.DataBlock minutely = forecast.getMinutely();
        Forecast.DataBlock hourly = forecast.getHourly();
        Forecast.DataBlock daily = forecast.getDaily();

        boolean notify = false;
        int count = 0;
        String summary = "";
        long eta = 0;

        if (minutely != null && Precip.is(minutely.getIcon())) {

            notify = true;
            log.info("minutely");
            eta = minutely.getData()[minutely.getData().length - 1].getTime().getTime()
                    + TimeUnit.MINUTES.toMillis(1);
            summary = minutely.getSummary();

            for (Forecast.DataPoint minute : minutely.getData()) {
                if (minute.getPrecipProbability() >= .5) {
                    count++;
                }
            }

            if (locationRecord.isLastWasPrecip()) {
                if (count == minutely.getData().length) {
                    notify = false;
                } else {
                    locationRecord.setLastWasPrecip(false);
                    ofy().save().entity(locationRecord).now();
                }
            } else if (count == minutely.getData().length) {
                locationRecord.setLastWasPrecip(true);
                ofy().save().entity(locationRecord).now();
            }

        }

        if (hourly != null && eta == 0) {

            for (Forecast.DataPoint hour : hourly.getData()) {

                if (Precip.is(hour.getIcon())) {

                    log.info("hourly");
                    eta = hour.getTime().getTime();

                    if (minutely == null) {
                        if (!locationRecord.isLastWasPrecip()) {
                            notify = true;
                            summary = hour.getSummary();
                        }

                        locationRecord.setLastWasPrecip(true);
                        ofy().save().entity(locationRecord).now();
                    }

                    break;
                }
            }

            if (eta == 0 && locationRecord.isLastWasPrecip()) {
                locationRecord.setLastWasPrecip(false);
                ofy().save().entity(locationRecord).now();
            }

        }

        if (daily != null && eta == 0) {

            if (locationRecord.isLastWasPrecip()) {
                locationRecord.setLastWasPrecip(false);
                ofy().save().entity(locationRecord).now();
            }

            for (Forecast.DataPoint day : daily.getData()) {

                if (Precip.is(day.getIcon())) {

                    log.info("daily");
                    eta = day.getTime().getTime();

                    if (hourly == null) {
                        notify = true;
                        summary = day.getSummary();
                    }

                    break;
                }
            }
        }

        if (eta == 0) {
            if (daily != null) {
                log.info("max");
                eta = daily.getData()[daily.getData().length - 1].getTime().getTime();
            } else {
                eta = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
                log.info("WRONG");
            }
        }

        if (notify) {
            Message msg = new Message.Builder()
                    .addData(MessagingCodes.TYPE, MessagingCodes.TYPE_NOTIFY)
                    .addData(MessagingCodes.NOTIFY_CONTENT, summary)
                    .build();

            switch (Messaging.sendMessage(deviceRecord, msg)) {
                case Messaging.FAIL_NO_RETRY:
                    return;
                case Messaging.FAIL_RETRY:
                    DeferredTaskContext.markForRetry();
                    return;
            }
        }

        GetLocationTask.enqueue(deviceRecord, eta);

    }

    public static void enqueue(final LocationRecord locationRecord) {

        final CheckForecastTask task = new CheckForecastTask(locationRecord.getGcmToken());
        final Queue queue = QueueFactory.getQueue(PRECIP_NOTIFY_QUEUE);
        final GCMDeviceRecord deviceRecord;
        deviceRecord = GCMRegistrationEndpoint.get(locationRecord.getGcmToken());

        if (deviceRecord.getForecastTaskName() != null) {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    queue.deleteTask(deviceRecord.getForecastTaskName());
                    deviceRecord.setForecastTaskName(null);
                    ofy().save().entity(deviceRecord).now();
                }
            });
        }

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                TaskHandle handle = queue.add(TaskOptions.Builder.withPayload(task));

                deviceRecord.setForecastTaskName(handle.getName());

                ofy().save().entity(deviceRecord).now();
            }
        });

    }

}
