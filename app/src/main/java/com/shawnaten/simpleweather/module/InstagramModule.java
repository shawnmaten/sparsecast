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

    @Singleton
    @Provides
    public Instagram.Service providesInstagramService(OkClient okClient) {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(okClient)
                .build().create(Instagram.Service.class);
    }

}
