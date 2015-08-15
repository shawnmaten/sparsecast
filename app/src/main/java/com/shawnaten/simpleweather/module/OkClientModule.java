package com.shawnaten.simpleweather.module;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.client.OkClient;

@Module
public class OkClientModule {
    private static final String OKHTTP_CACHE_FILE = "okhttp_cache_file";
    private static final long CACHE_SIZE = 1024 * 1024;

    @Provides
    @Singleton
    public OkClient providesOkClient(Context context) {
        File cacheFile = new File(context.getCacheDir(), OKHTTP_CACHE_FILE);
        Cache cache = new Cache(cacheFile, CACHE_SIZE);
        OkHttpClient okHttpClient = new OkHttpClient();

        //okHttpClient.setCache(cache);

        return new OkClient(okHttpClient);

    }
}
