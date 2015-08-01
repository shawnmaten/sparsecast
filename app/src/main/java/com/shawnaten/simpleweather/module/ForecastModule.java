package com.shawnaten.simpleweather.module;

import android.location.Location;

import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.LocalizationSettings;
import com.shawnaten.tools.LocationSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Module
public class ForecastModule {
    private static final String ENDPOINT = "https://api.forecast.io/forecast";

    @Provides
    @Singleton
    public Forecast.Service provideForecastService(OkClient client, GsonConverter converter) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(client)
                .setConverter(converter)
                .build().create(Forecast.Service.class);
    }

    @Provides
    @Singleton
    public Observable<Forecast.Response> providesForecast(
            final Observable<Keys> keysObservable,
            final Forecast.Service forecastService,
            final Observable<Location> locationObservable) {

        return Observable.create(new Observable.OnSubscribe<Forecast.Response>() {


            @Override
            public void call(final Subscriber<? super Forecast.Response> subscriber) {
                Action1<Keys> action1 = new Action1<Keys>() {
                    @Override
                    public void call(Keys keys) {
                        subscriber.onNext(forecastService.getForecast(
                                keys.getForecastAPIKey(),
                                LocationSettings.getLatLng().latitude,
                                LocationSettings.getLatLng().longitude,
                                LocalizationSettings.getLangCode(),
                                LocalizationSettings.getUnitCode()
                        ));
                    }
                };

                Func2<Keys, Location, Object> func2 = new Func2<Keys, Location, Object>() {
                    @Override
                    public Object call(Keys keys, Location location) {
                        subscriber.onNext(forecastService.getForecast(
                                keys.getForecastAPIKey(),
                                location.getLatitude(), location.getLongitude(),
                                LocalizationSettings.getLangCode(),
                                LocalizationSettings.getUnitCode()
                        ));
                        return null;
                    }
                };


                if (LocationSettings.getMode() == LocationSettings.Mode.SAVED)
                    keysObservable.subscribe(action1);
                else
                    Observable.zip(keysObservable, locationObservable, func2).subscribe();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
