package com.shawnaten.networking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.net.URI;
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

    public static class Flags implements Parcelable {
        @SerializedName("darksky-unavailable") private Boolean darkskyUnavailable;
        @SerializedName("darksky-stations") private String[] darkskyStations;
        @SerializedName("datapoint-stations") private String[] datapointStations;
        @SerializedName("isd-stations") private String[] isdStations;
        @SerializedName("lamp-stations") private String[] lampStations;
        @SerializedName("metar-stations") private String[] metarStations;
        @SerializedName("metno-license") private Boolean metnoLicense;
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

        public void writeToParcel(Parcel out, int flags) {
            out.writeByte((byte) (darkskyUnavailable ? 1 : 0));
            out.writeInt(darkskyStations.length);
            out.writeStringArray(darkskyStations);
            out.writeInt(datapointStations.length);
            out.writeStringArray(datapointStations);
            out.writeInt(isdStations.length);
            out.writeStringArray(isdStations);
            out.writeInt(lampStations.length);
            out.writeStringArray(lampStations);
            out.writeInt(metarStations.length);
            out.writeStringArray(metarStations);
            out.writeByte((byte) (metnoLicense ? 1 : 0));
            out.writeInt(sources.length);
            out.writeStringArray(sources);
            out.writeString(units);
        }

        public static final Parcelable.Creator<Flags> CREATOR
                = new Parcelable.Creator<Flags>() {
            public Flags createFromParcel(Parcel in) {
                return new Flags(in);
            }

            public Flags[] newArray(int size) {
                return new Flags[size];
            }
        };

        private Flags(Parcel in) {
            darkskyUnavailable = in.readByte() != 0;
            darkskyStations = new String[in.readInt()]; in.readStringArray(darkskyStations);
            datapointStations = new String[in.readInt()]; in.readStringArray(datapointStations);
            isdStations = new String[in.readInt()]; in.readStringArray(isdStations);
            lampStations = new String[in.readInt()]; in.readStringArray(lampStations);
            metarStations = new String[in.readInt()]; in.readStringArray(metarStations);
            metnoLicense = in.readByte() != 0;
            sources = new String[in.readInt()]; in.readStringArray(sources);
            units = in.readString();
        }

    }

    public static interface Service {
        @GET("/{key}/{lat},{lng}?extend=hourly")
        void getForecast(@Path("key") String key, @Path("lat") double lat, @Path("lng") double lng, @Query("lang") String lang, Callback<Response> cb);
    }
}
