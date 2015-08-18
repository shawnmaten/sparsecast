package com.shawnaten.simpleweather.backend.model;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public class Slack {

    public interface Service {
        @POST("/T097JC770/B097JGDGW/KjifjGgWGOcCoWnEA5hCazEm")
        Response sendMessage(@Body Message message);
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
