package com.shawnaten.network.modules;

import com.shawnaten.simpleweather.App;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.client.OkClient;

@Module (
        complete = false,
        library = true
)
public class OkClientModule {
    private static final String
            OKHTTP_CACHE_FILE = "okhttp_cache_file";
    private static final long
            CACHE_SIZE = 1 * 1024 * 1024;

    @Provides
    @Singleton
    public OkClient providesOkClient(App app) {
        File cacheFile;
        Cache cache;
        OkHttpClient okHttpClient;

        cacheFile = new File(app.getCacheDir(), OKHTTP_CACHE_FILE);
        okHttpClient = new OkHttpClient();
        try {
            cache = new Cache(cacheFile, CACHE_SIZE);
            okHttpClient.setCache(cache);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new OkClient(okHttpClient);

    }
}
