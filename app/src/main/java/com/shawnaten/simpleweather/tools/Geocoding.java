package com.shawnaten.simpleweather.tools;

import com.google.gson.annotations.SerializedName;

import retrofit.http.GET;
import retrofit.http.Query;

public class Geocoding {
    public static class Response {
        private String status;
        private Result results[];

        public Result[] getResults() {
            return results;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class Result {
        private String types[];
        @SerializedName("formatted_address") private String formattedAddress;
        @SerializedName("address_components") private AddressComponents addressComponents[];
        private Geometry geometry;
        @SerializedName("place_id") private String placeId;

        public String[] getTypes() {
            return types;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public AddressComponents[] getAddressComponents() {
            return addressComponents;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public String getPlaceId() {
            return placeId;
        }
    }

    public static class AddressComponents {
        private String types[];
        @SerializedName("long_name") private String longName;
        @SerializedName("short_name") private String shortName;

        public String[] getTypes() {
            return types;
        }

        public String getLongName() {
            return longName;
        }

        public String getShortName() {
            return shortName;
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

    public interface Service {
        @GET("/json")
        Geocoding.Response getAddresses(
                @Query("key") String key,
                @Query("latlng") String latlng
        );
    }
}
