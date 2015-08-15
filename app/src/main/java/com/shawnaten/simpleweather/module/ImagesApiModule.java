package com.shawnaten.simpleweather.module;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;
import com.shawnaten.simpleweather.backend.imagesApi.model.Image;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.tools.Instagram;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class ImagesApiModule {

    @Provides
    @Singleton
    public ImagesApi providesImagesApi(Context context, GoogleAccountCredential credential) {
        ImagesApi.Builder build;
        build = new ImagesApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential);
        build.setRootUrl(context.getString(R.string.root_url));
        build.setApplicationName(context.getString(R.string.app_name));

        return build.build();
    }

    public static Observable<String> getImage(
            final ImagesApi imagesApi,
            final Instagram.Service service,
            final String category
    ) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Image image = imagesApi.getImage(category).execute();
                    String key = APIKeys.PUBLIC_INSTAGRAM_API_KEY;

                    String url = service.getMedia(key, image.getShortcode())
                            .getData()
                            .getImages()
                            .getStandardResolution()
                            .getUrl();

                    subscriber.onNext(url);
                } catch (IOException e) {
                    subscriber.onCompleted();
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
