package com.shawnaten.tools;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class PlaceLikelihoodService {
    @Inject GoogleApiClient googleApiClient;

    @Inject
    public PlaceLikelihoodService() {

    }

    public Observable<PlaceLikelihoodBuffer> getPlaceLikelihood() {
        return Observable.create(new Observable.OnSubscribe<PlaceLikelihoodBuffer>() {
            @Override
            public void call(Subscriber<? super PlaceLikelihoodBuffer> subscriber) {
                googleApiClient.blockingConnect();
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(googleApiClient, null);
                result.setResultCallback(subscriber::onNext);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
