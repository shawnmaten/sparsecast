package com.shawnaten.simpleweather.backend.model;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SlackService {
    @POST("/{key}")
    Response sendMessage(@Path("key") String key, @Field("text") String text);
}
