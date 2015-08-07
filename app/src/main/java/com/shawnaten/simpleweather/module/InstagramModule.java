package com.shawnaten.simpleweather.module;

import com.shawnaten.simpleweather.tools.Instagram;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class InstagramModule {
    private static final String ENDPOINT = "https://api.instagram.com/v1";

    /*
    @Inject Observable<Keys> keysObservable;
    @Inject Observable<Location> locationObservable;
    */

    @Singleton
    @Provides
    public Instagram.Service providesInstagramService(OkClient okClient) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(okClient)
                .build().create(Instagram.Service.class);
    }

    /*
    public Observable<Instagram.MediaResponse> getTagged(String tag) {
        return Observable.create(new Observable.OnSubscribe<Instagram.MediaResponse>() {
            @Override
            public void call(Subscriber<? super Instagram.MediaResponse> subscriber) {
                subscriber.onNext(instagramService.getTagged(
                        tag, 50, 0, 0, "03bb4eb2867d4ee38dc5ee9395e9833c"
                ));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Instagram.MediaData getTaggedHelper(String tag, long maxTagId) {

        Instagram.MediaResponse mediaResponse = instagramService.getTagged(
                tag,
                50,
                0,
                maxTagId,
                "03bb4eb2867d4ee38dc5ee9395e9833c"
        );
        for (Instagram.MediaData data : mediaResponse.getData()) {
            int index, likes;
            List tags = Arrays.asList(data.getTags());
            if (tags.contains("cloudy")) {
                return data;
            }
        }
        return getTaggedHelper(tag, mediaResponse.getPagination().getNextMaxId());
    }

    public Observable<Instagram.MediaResponse> getPopular(long maxTagId) {
        return instagramService.getPopular(0, maxTagId, "03bb4eb2867d4ee38dc5ee9395e9833c");
    }

    public Observable<Instagram.UserResponse> getUser(long id) {
        return instagramService.getUser(
                id,
                "03bb4eb2867d4ee38dc5ee9395e9833c"
        );
    }

    public Observable<Instagram.MediaResponse> getNearby(Double lat, Double lng, long minTimestamp,
                                                         long maxTimestamp, long distance,
                                                         long count) {
        return instagramService.getNearby(lat, lng, minTimestamp, maxTimestamp, distance, count,
                "03bb4eb2867d4ee38dc5ee9395e9833c");
    }
    */
}
