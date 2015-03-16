package com.shawnaten.simpleweather.model;

import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

@Singleton
public class ForecastServiceWrapper {

    Observable<Keys> keysObservable;
    Forecast.Service forecastService;

    @Inject
    public ForecastServiceWrapper(
            Observable<Keys> keysObservable,
            Forecast.Service forecastService) {

        this.keysObservable = keysObservable;
        this.forecastService = forecastService;

    }

    public Observable<Forecast.Response> getForecast() {

        return Observable.create(new Observable.OnSubscribe<Forecast.Response>() {
            @Override
            public void call(Subscriber<? super Forecast.Response> subscriber) {
                keysObservable.subscribe(
                        keys -> forecastService.getForecast(
                                keys.getForecastAPIKey(),
                                37.819120,
                                -122.478498,
                                "en",
                                "us"
                        ).subscribe(subscriber::onNext)
                );
            }
        });
    }
}
