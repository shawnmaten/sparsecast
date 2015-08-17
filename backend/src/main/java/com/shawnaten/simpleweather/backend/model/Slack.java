package com.shawnaten.simpleweather.backend.model;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public class Slack {

    public interface Service {
        @POST("/{key}")
        Response sendMessage(@Path("key") String key, @Body Message message);
    }

    public static class Message {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
