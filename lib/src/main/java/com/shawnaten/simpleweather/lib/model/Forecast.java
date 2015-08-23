package com.shawnaten.simpleweather.lib.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

@SuppressWarnings("unused")
public class Forecast {
    public static final String ENDPOINT = "https://api.forecast.io/forecast";

    public static final String CLEAR_DAY = "clear-day";
    public static final String CLEAR_NIGHT = "clear-night";
    public static final String RAIN = "rain";
	public static final String SNOW = "snow";
	public static final String SLEET = "sleet";
	public static final String WIND = "wind";
	public static final String FOG = "fog";
	public static final String CLOUDY = "cloudy";
	public static final String PARTLY_CLOUDY_DAY = "partly-cloudy-day";
	public static final String PARTLY_CLOUDY_NIGHT = "partly-cloudy-night";

    public static final String HAIL = "hail";
    public static final String THUNDERSTORM = "thunderstorm";
    public static final String TORNADO = "tornado";

    public interface Service {
        @GET("/{key}/{lat},{lng}?extend=hourly")
        Observable<Response> getForecast(
                @Path("key") String key,
                @Path("lat") double lat,
                @Path("lng") double lng,
                @Query("lang") String lang,
                @Query("units") String units
        );

        @GET("/{key}/{lat},{lng}?exclude=currently,flags")
        Response notifyVersion(
                @Path("key") String key,
                @Path("lat") double lat,
                @Path("lng") double lng,
                @Query("lang") String lang,
                @Query("units") String units
        );

        @GET("/{key}/{lat},{lng}?exclude=currently,hourly,daily,flags")
        Observable<Response> notifyCheckVersion(
                @Path("key") String key,
                @Path("lat") double lat,
                @Path("lng") double lng,
                @Query("lang") String lang,
                @Query("units") String units
        );
    }

    public static class Response {

        private String name;
        private double latitude;
        private double longitude;
        private TimeZone timezone;
        private double offset;
        private DataPoint currently;
        private DataBlock minutely;
        private DataBlock hourly;
        private DataBlock daily;
        private Alert[] alerts;
        private Flags flags;

        public Response() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public Flags getFlags() {
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
        private Date sunriseTime;
        private Date sunsetTime;
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
        private double temperatureMin;
        private double temperatureMax;
        private Date temperatureMinTime;
        private Date temperatureMaxTime;
        private double apparentTemperature;
        private double apparentTemperatureMin;
        private double apparentTemperatureMax;
        private Date apparentTemperatureMinTime;
        private Date apparentTemperatureMaxTime;
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

        public static class DailyMinComparator implements Comparator<DataPoint> {

            @Override
            public int compare(DataPoint dataPoint, DataPoint t1) {
                if (dataPoint.getTemperatureMin() < t1.getTemperatureMin())
                    return -1;
                else if (dataPoint.getTemperatureMin() == t1.getTemperatureMin())
                    return 0;
                else
                    return 1;
            }
        }

        public static class DailyMaxComparator implements Comparator<DataPoint> {

            @Override
            public int compare(DataPoint dataPoint, DataPoint t1) {
                if (dataPoint.getTemperatureMax() < t1.getTemperatureMax())
                    return -1;
                else if (dataPoint.getTemperatureMax() == t1.getTemperatureMax())
                    return 0;
                else
                    return 1;
            }
        }

    }

    public static class Alert {

        private String title;
        private Date expires;
        private String description;
        private String uri;

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

        public String getUri() {
            return uri;
        }

    }

    public static class Flags {

        @SerializedName("darksky-unavailable") private boolean darkskyUnavailable;
        @SerializedName("darksky-stations") private String[] darkskyStations;
        @SerializedName("datapoint-stations") private String[] datapointStations;
        @SerializedName("isd-stations") private String[] isdStations;
        @SerializedName("lamp-stations") private String[] lampStations;
        @SerializedName("metar-stations") private String[] metarStations;
        @SerializedName("metno-license") private boolean metnoLicense;
        private String[] sources;
        private String units;

        public Flags() {

        }

        public Boolean getDarkskyUnavailable() {
            return darkskyUnavailable;
        }

        public String[] getDarkskyStations() {
            return darkskyStations;
        }

        public String[] getDatapointStations() {
            return datapointStations;
        }

        public String[] getIsdStations() {
            return isdStations;
        }

        public String[] getLampStations() {
            return lampStations;
        }

        public String[] getMetarStations() {
            return metarStations;
        }

        public Boolean getMetnoLicense() {
            return metnoLicense;
        }

        public String[] getSources() {
            return sources;
        }

        public String getUnits() {
            return units;
        }

        public int describeContents() {
            return 0;
        }

    }
}
