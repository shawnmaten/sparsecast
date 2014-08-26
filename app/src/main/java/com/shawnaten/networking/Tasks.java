package com.shawnaten.networking;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.shawnaten.tools.ForecastTools;

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
            Network.getInstance().setLastLocationName(result.getLocality() != null ? result.getLocality() : defaultName);
            Network.getInstance().getForecast(location.getLatitude(), location.getLongitude());
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
            Network.getInstance().setLastLocationName(result);
            Network.getInstance().getForecast(location.getLatitude(), location.getLongitude());
        }

    }

}
