package com.shawnaten.simpleweather.tools;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public class Places {
    public static class AutocompleteResponse {
        private String status;
        private Prediction[] predictions;

        public AutocompleteResponse() {

        }

        public String getStatus() {
            return status;
        }

        public Prediction[] getPredictions() {
            return predictions;
        }
    }

    public static class Prediction {
        private String description, place_id;

        public Prediction() {

        }

        public String getDescription() {
            return description;
        }

        public String getPlace_id() {
            return place_id;
        }

    }

    public static class DetailsResponse {
        private String status;
        private Result result;

        public DetailsResponse() {

        }

        public String getStatus() {
            return status;
        }

        public Result getResult() {
            return result;
        }
    }

    public static class Result {
        private String name;
        private Geometry geometry;

        public Result() {

        }

        public String getName() {
            return name;
        }

        public Geometry getGeometry() {
            return geometry;
        }
    }

    public static class Geometry{
        private Location location;

        public Geometry() {

        }

        public Location getLocation() {
            return location;
        }
    }

    public static class Location {
        private double lat, lng;

        public Location() {

        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public interface AutoCompleteService {
        @GET("/json")
        AutocompleteResponse getAutocomplete(@Query("key") String key, @Query("input") String query,
            @Query("language") String langCode);

        @GET("/json")
        Observable<AutocompleteResponse> getAutocomplete(@Query("key") String key, @Query("input") String query,
            @Query("language") String langCode, Callback<AutocompleteResponse> cb);
    }

    public interface DetailsService {
        @GET("/json")
        Observable<DetailsResponse> getDetails(@Query("key") String key, @Query("placeid") String placeid,
            @Query("language") String langCode, Callback<DetailsResponse> cb);
    }

}
