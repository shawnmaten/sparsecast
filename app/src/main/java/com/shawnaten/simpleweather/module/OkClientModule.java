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
    private static final long CACHE_SIZE = 1 * 1024 * 1024;

    @Provides
    @Singleton
    public OkClient providesOkClient(Context context) {
        File cacheFile;
        Cache cache;
        OkHttpClient okHttpClient;

        cacheFile = new File(context.getCacheDir(), OKHTTP_CACHE_FILE);
        okHttpClient = new OkHttpClient();
        cache = new Cache(cacheFile, CACHE_SIZE);
        okHttpClient.setCache(cache);

        return new OkClient(okHttpClient);

    }
}
