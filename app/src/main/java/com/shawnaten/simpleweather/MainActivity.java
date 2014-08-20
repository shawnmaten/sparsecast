package com.shawnaten.simpleweather;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.networking.Forecast;
import com.shawnaten.networking.Network;
import com.shawnaten.networking.Places;
import com.shawnaten.simpleweather.backend.keysEndpoint.KeysEndpoint;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.current.CurrentFragment;
import com.shawnaten.simpleweather.map.MapFragment;
import com.shawnaten.simpleweather.week.WeekFragment;
import com.shawnaten.tools.ActionBarListener;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity implements Callback,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0, REQUEST_CODE_ACCOUNT_PICKER = 1;
    private static final String[] helperFragNames = {"searching", "loading"};
    private static final String NAV_ITEM = "navIndex", FRESH_LAUNCH = "freshLaunch", FORECAST = "forecast",
            PREF_ACCOUNT_NAME = "prefAccountName", ACCOUNT_SELECTION_DIALOG = "accountSelectionDialog",
            IS_CURRENT_LOCATION_ENABLED ="isCurrentLocationEnabled";
    public static final String PREFS = "prefs", GOOGLE_API_KEY = "googleAPIKey", FORECAST_API_KEY = "forecastAPIKey";

    private KeysEndpoint keysService;

    private String[] mainFragments;
    private MenuItem searchWidget;
    private Boolean isLoading = false, isSearching = false, needsUpdate = false;
    private LocationClient locationClient;
    private ActionBarListener actionBarListener;

    private boolean isActive = false;
    private boolean isFreshLaunch = true;

    private Forecast.Response lastForecastResponse;
    private String lastLocationName;
    private boolean isCurrentLocationEnabled = true;
    private Location lastCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playServicesAvailable()) {
            int navItem = 0;

            setContentView(R.layout.main_activity);
            mainFragments = getResources().getStringArray(R.array.main_fragments);
            locationClient = new LocationClient(this, this, this);

            if (Network.getInstance() == null) {
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
                File networkCacheFile = new File(getCacheDir(), "networkCache");

                String googleAPIKey = settings.getString(MainActivity.GOOGLE_API_KEY, null);
                String forecastAPIKey = settings.getString(MainActivity.FORECAST_API_KEY, null);

                Keys keys = new Keys();
                if (googleAPIKey != null && forecastAPIKey != null) {
                    keys.setGoogleAPIKey(googleAPIKey);
                    keys.setForecastAPIKey(forecastAPIKey);
                } else {
                    getKeys();
                }

                Network.setup(networkCacheFile, keys);
            }

            if (savedInstanceState == null) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment cFrag, wFrag, mFrag, lFrag, sFrag;

                cFrag = new CurrentFragment();
                ft.add(R.id.main_fragment, cFrag, mainFragments[0]);
                ft.detach(cFrag);

                wFrag = new WeekFragment();
                ft.add(R.id.main_fragment, wFrag, mainFragments[1]);
                ft.detach(wFrag);

                mFrag = new MapFragment();
                ft.add(R.id.main_fragment, mFrag, mainFragments[2]);
                ft.detach(mFrag);

                lFrag = new GenericFragment();
                Bundle args = new Bundle();
                args.putInt(GenericFragment.LAYOUT, R.layout.loading);
                lFrag.setArguments(args);
                ft.add(R.id.main_fragment, lFrag, helperFragNames[1]);
                ft.detach(lFrag);

                sFrag = new GenericFragment();
                args = new Bundle();
                args.putInt(GenericFragment.LAYOUT, R.layout.searching);
                sFrag.setArguments(args);
                ft.add(R.id.main_fragment, sFrag, helperFragNames[0]);
                ft.detach(sFrag);

                ft.commit();

            } else {
                navItem = savedInstanceState.getInt(NAV_ITEM, 0);
                lastForecastResponse = savedInstanceState.getParcelable(FORECAST);
                isFreshLaunch = savedInstanceState.getBoolean(FRESH_LAUNCH);
                isCurrentLocationEnabled = savedInstanceState.getBoolean(IS_CURRENT_LOCATION_ENABLED);
            }

            actionBarListener = new ActionBarListener(getSupportFragmentManager(), mainFragments);
            ActionBar actionBar = getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(
                    ArrayAdapter.createFromResource(this, R.array.main_fragments, android.R.layout.simple_spinner_dropdown_item),
                    actionBarListener);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setIcon(R.drawable.ic_logo);
            actionBar.setSelectedNavigationItem(navItem);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchWidget = menu.findItem(R.id.action_search);
        searchWidget.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setSearching(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setSearching(false);
                return true;
            }
        });

        /*
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        int submitAreaId = searchView.getContext().getResources().getIdentifier("android:id/submit_area", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        View submitArea = searchView.findViewById(submitAreaId);
        if (searchPlate != null)
            searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_dark);
        if (submitArea != null)
            submitArea.setBackgroundResource(R.drawable.textfield_searchview_right_holo_dark);
        */

        return true;
    }

    @Override
    public void onStart() {

        super.onStart();

        if (isFreshLaunch) {
            if (Network.getInstance().isSetup())
                locationClient.connect();
        } else {
            actionBarListener.setEnabled(true);
        }

    }

    @Override
    protected void onResumeFragments () {
        super.onResumeFragments();

        isActive = true;
        updateFragments();

    }

    @Override
    public void onPause() {
        super.onPause();

        isActive = false;

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(NAV_ITEM, getActionBar().getSelectedNavigationIndex());
        outState.putParcelable(FORECAST, lastForecastResponse);
        isFreshLaunch = !isChangingConfigurations();
        outState.putBoolean(FRESH_LAUNCH, isFreshLaunch);
        if (isFreshLaunch)
            outState.putBoolean(IS_CURRENT_LOCATION_ENABLED, true);
        else
            outState.putBoolean(IS_CURRENT_LOCATION_ENABLED, isCurrentLocationEnabled);

    }

    @Override
    protected void onStop() {
        super.onStop();

        locationClient.disconnect();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_current_location:
                isCurrentLocationEnabled = true;
                if (locationClient.isConnected()) {
                    new getLocationNameTask(new Geocoder(this)).execute();
                }
                else
                    locationClient.connect();
                return true;
            case R.id.action_refresh:
                refreshForecast();
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (searchWidget != null)
            searchWidget.collapseActionView();

        String action = intent.getAction();
        if (Intent.ACTION_SEARCH.equals(action)) {
            setLoading(true);
            String query = intent.getStringExtra(SearchManager.QUERY);
            Network.getInstance().getAutocomplete(query, Locale.getDefault().getLanguage(), this);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            setLoading(true);
            Network.getInstance().getDetails(intent.getDataString(), Locale.getDefault().getLanguage(), this);
        }

    }

    private void refreshForecast() {
        if (isCurrentLocationEnabled) {
            if (locationClient.isConnected()) {
                lastCurrentLocation = locationClient.getLastLocation();
                new getLocationNameTask(new Geocoder(this)).execute();
            }
            else
                locationClient.connect();
        } else {
            if (lastForecastResponse != null) {
                setLoading(true);
                Network.getInstance().getForecast(lastForecastResponse.getLatitude(), lastForecastResponse.getLongitude(),
                        Locale.getDefault().getLanguage(), this);
            }
        }
    }

    @Override
    public void success(Object response, Response response2) {
        if (Forecast.Response.class.isInstance(response)) {

            lastForecastResponse = (Forecast.Response) response;
            lastForecastResponse.setName(lastLocationName);
            setLoading(false);

        } else if (Places.AutocompleteResponse.class.isInstance(response)) {
            Places.AutocompleteResponse autocompleteResponse = (Places.AutocompleteResponse) response;

            if (Network.getInstance().responseOkay(autocompleteResponse.getStatus(), this)) {
                Network.getInstance().getDetails(autocompleteResponse.getPredictions()[0].getPlace_id(),
                        Locale.getDefault().getLanguage(), this);
            } else {
                setLoading(false);
            }

        } else if (Places.DetailsResponse.class.isInstance(response)) {
            Places.DetailsResponse details = (Places.DetailsResponse) response;

            if (Network.getInstance().responseOkay(details.getStatus(), this)) {
                lastLocationName = details.getResult().getName();
                Places.Location location = details.getResult().getGeometry().getLocation();
                isCurrentLocationEnabled = false;
                Network.getInstance().getForecast(location.getLat(), location.getLng(), Locale.getDefault().getLanguage(), this);
            } else  {
                setLoading(false);
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        setLoading(false);
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT);
        toast.show();
        Log.e("Retrofit", error.getMessage());
        if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(MainActivity.GOOGLE_API_KEY, null)
                    .putString(MainActivity.FORECAST_API_KEY, null)
                    .apply();
        }
    }

    private void setLoading(Boolean state) {
        isLoading = state;
        needsUpdate = true;
        updateFragments();
    }

    private void setSearching(Boolean state) {
        isSearching = state;
        needsUpdate = true;
        updateFragments();
    }

    private void updateFragments() {
        if (isActive && needsUpdate) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment toDetach, toAttach = null;

            toDetach = fm.findFragmentById(R.id.main_fragment);

            if (isLoading) {
                actionBarListener.setEnabled(false);
                if (isSearching)
                    toAttach = fm.findFragmentByTag(helperFragNames[0]);
                else
                    toAttach = fm.findFragmentByTag(helperFragNames[1]);
            } else {
                actionBarListener.setEnabled(true);
                if (isSearching) {
                    toAttach = fm.findFragmentByTag(helperFragNames[0]);
                } else {
                    if (lastForecastResponse != null) {
                        toAttach = fm.findFragmentByTag(mainFragments[getActionBar().getSelectedNavigationIndex()]);
                    }
                }
            }

            if (!toDetach.equals(toAttach)) {
                fm.beginTransaction()
                        .detach(toDetach)
                        .attach(toAttach)
                        .commit();
            }

            needsUpdate = false;
        }
    }

    public boolean hasForecast() {
        return lastForecastResponse != null;
    }

    public Forecast.Response getForecast() {
        return lastForecastResponse;
    }

    private boolean playServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            GooglePlayServicesUtil.showErrorNotification(resultCode, this);
            finish();
            return false;
        }
    }

    // location services

    private class getLocationNameTask extends AsyncTask<Void, Void, String> {
        private Geocoder geocoder;

        public getLocationNameTask(Geocoder geocoder) {
            this.geocoder = geocoder;
        }

        @Override
        protected void onPreExecute () {
            lastCurrentLocation = locationClient.getLastLocation();
            setLoading(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            Address address = new Address(Locale.getDefault());
            try {
                address = geocoder.getFromLocation(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude(), 1).get(0);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return address.getLocality() != null ? address.getLocality() : getString(R.string.action_current_location);
        }

        @Override
        protected void onPostExecute (String result) {
            lastLocationName = result;
            getLocalWeather();
        }

    }

    private void getLocalWeather() {
        Network.getInstance().getForecast(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude(),
                Locale.getDefault().getLanguage(), this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        new getLocationNameTask(new Geocoder(this)).execute();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // location services

    public void getKeys() {
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accountName = defaultPrefs.getString(PREF_ACCOUNT_NAME, null);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(getApplicationContext(), "server:client_id:" + getString(R.string.WEB_ID));
        if (accountName == null) {
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_CODE_ACCOUNT_PICKER);
        } else {
            credential.setSelectedAccountName(accountName);

            KeysEndpoint.Builder keysEndpointBuilder =
                    new KeysEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential);
            if (getResources().getBoolean(R.bool.localhost))
                keysEndpointBuilder.setRootUrl(getString(R.string.root_url));
            keysService = keysEndpointBuilder.build();

            new getKeysTask(this, credential).execute();
        }
    }

    private class getKeysTask extends AsyncTask<Void, Void, Keys> {
        private GoogleAccountCredential credential;
        private Context context;

        public getKeysTask(Context context, GoogleAccountCredential credential) {
            this.context = context;
            this.credential = credential;
        }

        @Override
        protected Keys doInBackground(Void... unused) {
            Keys keys = new Keys();
            try {
                Log.d("getKeysTask", "getting keys");
                keys = keysService.getKeys().execute();
                if (keys != null) {
                    Network.getInstance().setKeys(keys);
                    setKeyPrefs(keys);
                }
            } catch (IOException e) {
                if (GoogleAuthException.class.isInstance(e.getCause())) {
                    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_CODE_ACCOUNT_PICKER);
                }
                Log.e("getKeysTask", e.getMessage(), e);
            }
            return keys;
        }

        @Override
        protected void onPostExecute (Keys result) {
            if (locationClient.isConnected())
                new getLocationNameTask(new Geocoder(context)).execute();
            else
                locationClient.connect();
        }

    }

    private void setKeyPrefs(Keys keys) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString(GOOGLE_API_KEY, keys.getGoogleAPIKey())
                .putString(FORECAST_API_KEY, keys.getForecastAPIKey())
                .apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                        defaultPrefs.edit().putString(PREF_ACCOUNT_NAME, accountName).apply();
                        getKeys();
                    }
                } else {
                    getKeys();
                }
            break;
        }
    }

}