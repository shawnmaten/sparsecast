package com.shawnaten.simpleweather.tools;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by shawnaten on 7/17/14.
 */
public class Forecast {

    public interface Service {
        @GET("/{key}/{lat},{lng}?extend=hourly")
        Response getForecast(
                @Path("key") String key,
                @Path("lat") double lat,
                @Path("lng") double lng,
                @Query("lang") String lang,
                @Query("units") String units
        );
    }

    public static class Response implements Parcelable {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };
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

        protected Response(Parcel in) {
            name = in.readString();
            latitude = in.readDouble();
            longitude = in.readDouble();
            timezone = (TimeZone) in.readValue(TimeZone.class.getClassLoader());
            offset = in.readDouble();
            currently = (DataPoint) in.readValue(DataPoint.class.getClassLoader());
            minutely = (DataBlock) in.readValue(DataBlock.class.getClassLoader());
            hourly = (DataBlock) in.readValue(DataBlock.class.getClassLoader());
            daily = (DataBlock) in.readValue(DataBlock.class.getClassLoader());
            int alertSize = in.readInt();
            if (alertSize > 0) {
                alerts = new Alert[alertSize];
                in.readTypedArray(alerts, Alert.CREATOR);
            }
            flags = (Flags) in.readValue(Flags.class.getClassLoader());
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            dest.writeValue(timezone);
            dest.writeDouble(offset);
            dest.writeValue(currently);
            dest.writeValue(minutely);
            dest.writeValue(hourly);
            dest.writeValue(daily);
            if (alerts != null) {
                dest.writeInt(alerts.length);
                dest.writeTypedArray(alerts, 0);
            } else {
                dest.writeInt(0);
            }
            dest.writeValue(this.flags);
        }

    }

    public static class DataBlock implements Parcelable {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<DataBlock> CREATOR = new Parcelable.Creator<DataBlock>() {
            @Override
            public DataBlock createFromParcel(Parcel in) {
                return new DataBlock(in);
            }

            @Override
            public DataBlock[] newArray(int size) {
                return new DataBlock[size];
            }
        };
        private String summary;
        private String icon;
        private DataPoint[] data;

        @SuppressWarnings("unused")
        public DataBlock() {

        }

        protected DataBlock(Parcel in) {
            summary = in.readString();
            icon = in.readString();
            int dataSize = in.readInt();
            if (dataSize > 0) {
                data = new DataPoint[dataSize];
                in.readTypedArray(data, DataPoint.CREATOR);
            }
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(summary);
            dest.writeString(icon);
            if (data != null) {
                dest.writeInt(data.length);
                dest.writeTypedArray(data, 0);
            } else  {
                dest.writeInt(0);
            }
        }

    }

    public static class DataPoint implements Parcelable {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<DataPoint> CREATOR = new Parcelable.Creator<DataPoint>() {
            @Override
            public DataPoint createFromParcel(Parcel in) {
                return new DataPoint(in);
            }

            @Override
            public DataPoint[] newArray(int size) {
                return new DataPoint[size];
            }
        };
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
        protected DataPoint(Parcel in) {
            long tmpTime = in.readLong();
            time = tmpTime != -1 ? new Date(tmpTime) : null;
            summary = in.readString();
            icon = in.readString();
            long tmpSunriseTime = in.readLong();
            sunriseTime = tmpSunriseTime != -1 ? new Date(tmpSunriseTime) : null;
            long tmpSunsetTime = in.readLong();
            sunsetTime = tmpSunsetTime != -1 ? new Date(tmpSunsetTime) : null;
            moonPhase = in.readDouble();
            nearestStormDistance = in.readDouble();
            nearestStormBearing = in.readDouble();
            precipIntensity = in.readDouble();
            precipIntensityMax = in.readDouble();
            long tmpPrecipIntensityMaxTime = in.readLong();
            precipIntensityMaxTime = tmpPrecipIntensityMaxTime != -1 ? new Date(tmpPrecipIntensityMaxTime) : null;
            precipProbability = in.readDouble();
            precipType = in.readString();
            precipAccumulation = in.readDouble();
            temperature = in.readDouble();
            temperatureMin = in.readDouble();
            temperatureMax = in.readDouble();
            long tmpTemperatureMinTime = in.readLong();
            temperatureMinTime = tmpTemperatureMinTime != -1 ? new Date(tmpTemperatureMinTime) : null;
            long tmpTemperatureMaxTime = in.readLong();
            temperatureMaxTime = tmpTemperatureMaxTime != -1 ? new Date(tmpTemperatureMaxTime) : null;
            apparentTemperature = in.readDouble();
            apparentTemperatureMin = in.readDouble();
            apparentTemperatureMax = in.readDouble();
            long tmpApparentTemperatureMinTime = in.readLong();
            apparentTemperatureMinTime = tmpApparentTemperatureMinTime != -1 ? new Date(tmpApparentTemperatureMinTime) : null;
            long tmpApparentTemperatureMaxTime = in.readLong();
            apparentTemperatureMaxTime = tmpApparentTemperatureMaxTime != -1 ? new Date(tmpApparentTemperatureMaxTime) : null;
            dewPoint = in.readDouble();
            windSpeed = in.readDouble();
            windBearing = in.readDouble();
            cloudCover = in.readDouble();
            humidity = in.readDouble();
            pressure = in.readDouble();
            visibility = in.readDouble();
            ozone = in.readDouble();
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(time != null ? time.getTime() : -1L);
            dest.writeString(summary);
            dest.writeString(icon);
            dest.writeLong(sunriseTime != null ? sunriseTime.getTime() : -1L);
            dest.writeLong(sunsetTime != null ? sunsetTime.getTime() : -1L);
            dest.writeDouble(moonPhase);
            dest.writeDouble(nearestStormDistance);
            dest.writeDouble(nearestStormBearing);
            dest.writeDouble(precipIntensity);
            dest.writeDouble(precipIntensityMax);
            dest.writeLong(precipIntensityMaxTime != null ? precipIntensityMaxTime.getTime() : -1L);
            dest.writeDouble(precipProbability);
            dest.writeString(precipType);
            dest.writeDouble(precipAccumulation);
            dest.writeDouble(temperature);
            dest.writeDouble(temperatureMin);
            dest.writeDouble(temperatureMax);
            dest.writeLong(temperatureMinTime != null ? temperatureMinTime.getTime() : -1L);
            dest.writeLong(temperatureMaxTime != null ? temperatureMaxTime.getTime() : -1L);
            dest.writeDouble(apparentTemperature);
            dest.writeDouble(apparentTemperatureMin);
            dest.writeDouble(apparentTemperatureMax);
            dest.writeLong(apparentTemperatureMinTime != null ? apparentTemperatureMinTime.getTime() : -1L);
            dest.writeLong(apparentTemperatureMaxTime != null ? apparentTemperatureMaxTime.getTime() : -1L);
            dest.writeDouble(dewPoint);
            dest.writeDouble(windSpeed);
            dest.writeDouble(windBearing);
            dest.writeDouble(cloudCover);
            dest.writeDouble(humidity);
            dest.writeDouble(pressure);
            dest.writeDouble(visibility);
            dest.writeDouble(ozone);
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

    public static class Alert implements Parcelable {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Alert> CREATOR = new Parcelable.Creator<Alert>() {
            @Override
            public Alert createFromParcel(Parcel in) {
                return new Alert(in);
            }

            @Override
            public Alert[] newArray(int size) {
                return new Alert[size];
            }
        };
        private String title;
        private Date expires;
        private String description;
        private Uri uri;

        public Alert() {

        }

        protected Alert(Parcel in) {
            title = in.readString();
            long tmpExpires = in.readLong();
            expires = tmpExpires != -1 ? new Date(tmpExpires) : null;
            description = in.readString();
            uri = (Uri) in.readValue(Uri.class.getClassLoader());
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

        public Uri getUri() {
            return uri;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeLong(expires != null ? expires.getTime() : -1L);
            dest.writeString(description);
            dest.writeValue(uri);
        }

    }

    public static class Flags implements Parcelable {
        public static final Parcelable.Creator<Flags> CREATOR
                = new Parcelable.Creator<Flags>() {
            public Flags createFromParcel(Parcel in) {
                return new Flags(in);
            }

            public Flags[] newArray(int size) {
                return new Flags[size];
            }
        };
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

        private Flags(Parcel in) {
            int tmpSize;

            darkskyUnavailable = in.readByte() != 0;

            tmpSize = in.readInt();
            if (tmpSize > 0) {
                darkskyStations = new String[tmpSize];
                in.readStringArray(darkskyStations);
            }

            tmpSize = in.readInt();
            if (tmpSize > 0) {
                datapointStations = new String[tmpSize];
                in.readStringArray(datapointStations);
            }

            tmpSize = in.readInt();
            if (tmpSize > 0) {
                isdStations = new String[tmpSize];
                in.readStringArray(isdStations);
            }

            tmpSize = in.readInt();
            if (tmpSize > 0) {
                lampStations = new String[tmpSize];
                in.readStringArray(lampStations);
            }

            tmpSize = in.readInt();
            if (tmpSize > 0) {
                metarStations = new String[tmpSize];
                in.readStringArray(metarStations);
            }

            metnoLicense = in.readByte() != 0;

            tmpSize = in.readInt();
            if (tmpSize > 0) {
                sources = new String[tmpSize];
                in.readStringArray(sources);
            }

            units = in.readString();

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

            if (darkskyStations != null) {
                out.writeInt(darkskyStations.length);
                out.writeStringArray(darkskyStations);
            } else {
                out.writeInt(0);
            }

            if (datapointStations != null) {
                out.writeInt(datapointStations.length);
                out.writeStringArray(datapointStations);
            } else {
                out.writeInt(0);
            }

            if (isdStations != null) {
                out.writeInt(isdStations.length);
                out.writeStringArray(isdStations);
            } else {
                out.writeInt(0);
            }

            if (lampStations != null) {
                out.writeInt(lampStations.length);
                out.writeStringArray(lampStations);
            } else {
                out.writeInt(0);
            }

            if (metarStations != null) {
                out.writeInt(metarStations.length);
                out.writeStringArray(metarStations);
            } else {
                out.writeInt(0);
            }

            out.writeByte((byte) (metnoLicense ? 1 : 0));

            if (sources != null) {
                out.writeInt(sources.length);
                out.writeStringArray(sources);
            } else {
                out.writeInt(0);
            }

            out.writeString(units);
        }

    }
}
