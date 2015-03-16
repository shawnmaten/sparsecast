package com.shawnaten.tools;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Shawn Aten on 8/25/14.
 */
public class Tasks {

    public static class getDefaultUnitsTask extends AsyncTask<Void, Void, Address> {
        private SharedPreferences preferences;
        private String key;
        private Geocoder geocoder;
        private Location location;
        private String defaultName;

        public getDefaultUnitsTask(SharedPreferences preferences, String key,
                                   Geocoder geocoder, Location location, String defaultName) {
            this.preferences = preferences;
            this.key = key;
            this.geocoder = geocoder;
            this.location = location;
            this.defaultName = defaultName;
        }

        @Override
        protected Address doInBackground(Void... params) {
            Address address = new Address(Locale.getDefault());
            try {
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute (Address result) {
            String unitCode = result.getCountryCode() != null ? result.getCountryCode().toLowerCase() : "si";
            ForecastTools.configUnits(unitCode, preferences, key);
            //Network.getInstance().setLastLocationName(result.getLocality() != null ? result.getLocality() : defaultName);
            //Network.getInstance().getForecast(location.getLatitude(), location.getLongitude());
        }

    }

    public static class getLocationNameTask extends AsyncTask<Void, Void, String> {
        private Geocoder geocoder;
        private Location location;
        private String defaultName;

        public getLocationNameTask(Geocoder geocoder, Location location, String defaultName) {
            this.geocoder = geocoder;
            this.location = location;
            this.defaultName = defaultName;
        }

        @Override
        protected String doInBackground(Void... params) {
            Address address = new Address(Locale.getDefault());
            try {
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return address.getLocality() != null ? address.getLocality() : defaultName;
        }

        @Override
        protected void onPostExecute (String result) {
            //Network.getInstance().setLastLocationName(result);
            //Network.getInstance().getForecast(location.getLatitude(), location.getLongitude());
        }

    }

    /*
    public static class getRadarFilesTask extends AsyncTask<Void, Void, LinkedHashMap<String, Date>> {
        private RadarImageListener listener;

        public getRadarFilesTask(RadarImageListener listener) {
            this.listener = listener;
        }

        @Override
        protected LinkedHashMap<String, Date> doInBackground(Void... unused) {

            LinkedHashMap<String, Date> radarFilenames = new LinkedHashMap<>(7);
            Element fileName, fileTime;
            SimpleDateFormat parser = new SimpleDateFormat();
            Date date;

            parser.applyPattern("dd-MMM-yyyy HH:mm");
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                Document radarIndex = Jsoup.connect("http://radar.weather.gov/ridge/Conus/RadarImg/").get();
                Elements conusLinks = radarIndex.select("tr:contains(conus)");
                List<Element> sublist = conusLinks.subList(conusLinks.size() - 7, conusLinks.size());
                for (Element element : sublist) {
                    fileName = element.select("[href]").first();
                    fileTime = element.select("td[align]").first();
                    date = parser.parse(fileTime.text());

                    radarFilenames.put("http://radar.weather.gov/ridge/Conus/RadarImg/" + fileName.text(), date);
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return radarFilenames;

        }

        @Override
        protected void onPostExecute(LinkedHashMap<String, Date> result) {
            listener.onReceiveImageInfo(result);
        }

    }

    public interface RadarImageListener {
        public void onReceiveImageInfo(LinkedHashMap<String, Date> info);
    }
    */

}
