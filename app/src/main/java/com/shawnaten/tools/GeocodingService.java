package com.shawnaten.tools;

import android.location.Location;

import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Singleton
public class GeocodingService {
    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/geocode";

    @Inject Observable<Keys> keysObservable;
    @Inject Observable<Location> locationObservable;

    private Geocoding.Service geocodingService;

    @Inject
    public GeocodingService(OkClient okClient) {
        geocodingService = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(okClient)
                .build().create(Geocoding.Service.class);
    }

    public Observable<Geocoding.Response> getAddresses() {
            return Observable.create(new Observable.OnSubscribe<Geocoding.Response>() {
                @Override
                public void call(Subscriber<? super Geocoding.Response> subscriber) {
                    Observable.zip(keysObservable, locationObservable, new Func2<Keys, Location, Geocoding.Response>() {
                        @Override
                        public Geocoding.Response call(Keys keys, Location location) {
                            return geocodingService.getAddresses(
                                    keys.getGoogleAPIKey(),
                                    String.format("%f,%f", location.getLatitude(),
                                            location.getLongitude())
                            );
                        }
                    }).subscribe(new Action1<Geocoding.Response>() {
                        @Override
                        public void call(Geocoding.Response response) {

                        }
                    });
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
