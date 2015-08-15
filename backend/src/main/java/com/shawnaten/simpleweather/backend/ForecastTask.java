package com.shawnaten.simpleweather.backend;

import com.google.android.gcm.server.Message;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.lib.model.MessagingCodes;
import com.shawnaten.simpleweather.lib.tools.Precip;

import java.util.logging.Logger;

public class ForecastTask implements DeferredTask {

    public static final String QUEUE = "forecast-queue";

    private static final Logger log = Logger.getLogger(ForecastTask.class.getName());

    private String type;
    private String id;

    private double lat;
    private double lng;



    public ForecastTask(String type, String id, double lat, double lng) {
        this.type = type;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public void run() {

        Forecast.Response forecast = Dagger.getForecastComponent().forecastService().notifyVersion(
                APIKeys.FORECAST_API_KEY,
                lat,
                lng,
                "en",
                "us"
        );

        Forecast.DataBlock minutely = forecast.getMinutely();

        if (minutely != null && Precip.is(minutely.getIcon())) {
            if (type.equals(MessagingCodes.HOUR_TYPE_CURRENT)) {
                Message msg = new Message.Builder()
                        .addData(MessagingCodes.MESSAGE_TYPE, MessagingCodes.HOUR_TYPE)
                        .addData(MessagingCodes.HOUR_TYPE, type)
                        .addData(MessagingCodes.HOUR_CONTENT, minutely.getSummary())
                        .build();
                Messaging.sendMessage(id, msg);
            }

           // TODO send to group

        }

    }

}
