package com.shawnaten.networking;

import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by shawnaten on 7/17/14.
 */
public class Forecast {

    public static class Response {
        private Date expiration;

        private double latitude, longitude;
        private TimeZone timezone;
        private double offset;

        private DataPoint currently;

        private DataBlock minutely, hourly, daily;

        private Alert[] alerts;

        private Flag flags;

        public Response() {

        }

        public void setExpiration(String expirationString) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            try {
                expiration = simpleDateFormat.parse(expirationString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public Date getExpiration() {
            return expiration;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public TimeZone getTimezone() {
            return timezone;
        }

        public double getOffset() {
            return offset;
        }

        public DataPoint getCurrently() {
            return currently;
        }

        public DataBlock getMinutely() {
            return minutely;
        }

        public DataBlock getHourly() {
            return hourly;
        }

        public DataBlock getDaily() {
            return daily;
        }

        public Alert[] getAlerts() {
            return alerts;
        }

        public Flag getFlags() {
            return flags;
        }
    }

    public static class DataBlock {
        private String summary;
        private String icon;
        private DataPoint[] data;

        public DataBlock() {

        }

        public String getSummary() {
            return summary;
        }

        public String getIcon() {
            return icon;
        }

        public DataPoint[] getData() {
            return data;
        }
    }

    public static class DataPoint {
        private Date time;
        private String summary;
        private String icon;
        private Date sunriseTime, sunsetTime;
        private double moonPhase;
        private double nearestStormDistance;
        private double nearestStormBearing;
        private double precipIntensity;
        private double precipIntensityMax;
        private Date precipIntensityMaxTime;
        private double precipProbability;
        private String precipType;
        private double precipAccumulation;
        private double temperature;
        private double temperatureMin, temperatureMax;
        private Date temperatureMinTime, temperatureMaxTime;
        private double apparentTemperature;
        private double apparentTemperatureMin, apparentTemperatureMax;
        private Date apparentTemperatureMinTime, apparentTemperatureMaxTime;
        private double dewPoint;
        private double windSpeed;
        private double windBearing;
        private double cloudCover;
        private double humidity;
        private double pressure;
        private double visibility;
        private double ozone;

        public DataPoint() {

        }

        public Date getTime() {
            return time;
        }

        public String getSummary() {
            return summary;
        }

        public String getIcon() {
            return icon;
        }

        public Date getSunriseTime() {
            return sunriseTime;
        }

        public Date getSunsetTime() {
            return sunsetTime;
        }

        public double getMoonPhase() {
            return moonPhase;
        }

        public double getNearestStormDistance() {
            return nearestStormDistance;
        }

        public double getNearestStormBearing() {
            return nearestStormBearing;
        }

        public double getPrecipIntensity() {
            return precipIntensity;
        }

        public double getPrecipIntensityMax() {
            return precipIntensityMax;
        }

        public Date getPrecipIntensityMaxTime() {
            return precipIntensityMaxTime;
        }

        public double getPrecipProbability() {
            return precipProbability;
        }

        public String getPrecipType() {
            return precipType;
        }

        public double getPrecipAccumulation() {
            return precipAccumulation;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getTemperatureMin() {
            return temperatureMin;
        }

        public double getTemperatureMax() {
            return temperatureMax;
        }

        public Date getTemperatureMinTime() {
            return temperatureMinTime;
        }

        public Date getTemperatureMaxTime() {
            return temperatureMaxTime;
        }

        public double getApparentTemperature() {
            return apparentTemperature;
        }

        public double getApparentTemperatureMin() {
            return apparentTemperatureMin;
        }

        public double getApparentTemperatureMax() {
            return apparentTemperatureMax;
        }

        public Date getApparentTemperatureMinTime() {
            return apparentTemperatureMinTime;
        }

        public Date getApparentTemperatureMaxTime() {
            return apparentTemperatureMaxTime;
        }

        public double getDewPoint() {
            return dewPoint;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public double getWindBearing() {
            return windBearing;
        }

        public double getCloudCover() {
            return cloudCover;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getPressure() {
            return pressure;
        }

        public double getVisibility() {
            return visibility;
        }

        public double getOzone() {
            return ozone;
        }
    }

    public static class Alert {
        private String title;
        private Date expires;
        private String description;
        private URI uri;

        public Alert() {

        }

        public String getTitle() {
            return title;
        }

        public Date getExpires() {
            return expires;
        }

        public String getDescription() {
            return description;
        }

        public URI getUri() {
            return uri;
        }
    }

    public static class Flag {
        @SerializedName("darksky-unavailable") private Boolean darksky_unavailable;
        @SerializedName("darksky-stations") private String[] darksky_stations;
        @SerializedName("datapoint-stations") private String[] datapoint_stations;
        @SerializedName("isd-stations") private String[] isd_stations;
        @SerializedName("lamp-stations") private String[] lamp_stations;
        @SerializedName("metar-stations") private String[] metar_stations;
        @SerializedName("metno-license") private Boolean metno_license;
        private String[] sources;
        private String units;

        public Flag() {

        }

        public Boolean getDarksky_unavailable() {
            return darksky_unavailable;
        }

        public String[] getDarksky_stations() {
            return darksky_stations;
        }

        public String[] getDatapoint_stations() {
            return datapoint_stations;
        }

        public String[] getIsd_stations() {
            return isd_stations;
        }

        public String[] getLamp_stations() {
            return lamp_stations;
        }

        public String[] getMetar_stations() {
            return metar_stations;
        }

        public Boolean getMetno_license() {
            return metno_license;
        }

        public String[] getSources() {
            return sources;
        }

        public String getUnits() {
            return units;
        }
    }

    public static interface Service {
        @GET("/{key}/{lat},{lng}?extend=hourly")
        void getForecast(@Path("key") String key, @Path("lat") double lat, @Path("lng") double lng, @Query("lang") String lang, Callback<Response> cb);
    }
}
