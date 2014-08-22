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
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
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
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.GeneralAlertDialog;
import com.shawnaten.tools.PlayServices;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity implements Callback, GeneralAlertDialog.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0, REQUEST_CODE_ACCOUNT_PICKER = 1;
    private static final SparseArray<String> helperFragNames = new SparseArray<>();
    static {
        helperFragNames.put(R.layout.searching, "searching");
        helperFragNames.put(R.layout.loading, "loading");
    }
    private static final String NAV_ITEM = "navIndex", FRESH_LAUNCH = "freshLaunch", FORECAST = "forecast",
            PREF_ACCOUNT_NAME = "prefAccountName", IS_CURRENT_LOCATION_ENABLED ="isCurrentLocationEnabled",
            IS_SEARCHING = "isSearching", IS_LOADING = "isLoading",
            SEARCH_NEEDS_UPDATE = "searchNeedsUpdate", LOADING_NEEDS_UPDATE = "loadingNeedsUpdate",
            LAST_CURRENT_LOCATION = "lastCurrentLocation";
    public static final String PREFS = "prefs", GOOGLE_API_KEY = "googleAPIKey", FORECAST_API_KEY = "forecastAPIKey";

    private KeysEndpoint keysService;

    private String[] mainFragments;
    private MenuItem searchWidget;
    private LocationClient locationClient;
    private ActionBarListener actionBarListener;
    private boolean isActive;

    private int navItem;
    private boolean isFreshLaunch = true;
    private boolean isSearching, isLoading;
    private Boolean searchNeedsUpdate, loadingNeedsUpdate;
    private Forecast.Response lastForecastResponse;
    private boolean isCurrentLocationEnabled = true;
    private Location lastCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PlayServices.playServicesAvailable(this)) {
            FragmentManager fm = getSupportFragmentManager();

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

                Network.setup(this, networkCacheFile, keys);
            } else {
                Network.getInstance().setActivityCallback(this);
            }

            if (savedInstanceState == null) {
                FragmentTransaction ft = fm.beginTransaction();
                Fragment cFrag, wFrag, mFrag, sFrag, lFrag;
                Bundle args;

                cFrag = new CurrentFragment();
                ft.add(R.id.main_fragment, cFrag, mainFragments[0]);

                wFrag = new WeekFragment();
                ft.add(R.id.main_fragment, wFrag, mainFragments[1]);
                ft.detach(wFrag);

                mFrag = new MapFragment();
                ft.add(R.id.main_fragment, mFrag, mainFragments[2]);
                ft.detach(mFrag);

                sFrag = new GenericFragment();
                args = new Bundle();
                args.putInt(GenericFragment.LAYOUT, R.layout.searching);
                sFrag.setArguments(args);
                ft.add(R.id.main_fragment, sFrag, helperFragNames.get(R.layout.searching));
                ft.detach(sFrag);

                lFrag = new GenericFragment();
                args = new Bundle();
                args.putInt(GenericFragment.LAYOUT, R.layout.loading);
                lFrag.setArguments(args);
                ft.add(R.id.main_fragment, lFrag, helperFragNames.get(R.layout.loading));

                ft.commit();

            } else {

                isFreshLaunch = savedInstanceState.getBoolean(FRESH_LAUNCH);
                isSearching = savedInstanceState.getBoolean(IS_SEARCHING);
                isLoading = savedInstanceState.getBoolean(IS_LOADING);

                if (savedInstanceState.containsKey(SEARCH_NEEDS_UPDATE))
                    searchNeedsUpdate = savedInstanceState.getBoolean(SEARCH_NEEDS_UPDATE);
                if (savedInstanceState.containsKey(LOADING_NEEDS_UPDATE))
                    loadingNeedsUpdate = savedInstanceState.getBoolean(LOADING_NEEDS_UPDATE);

                navItem = savedInstanceState.getInt(NAV_ITEM, 0);
                lastForecastResponse = savedInstanceState.getParcelable(FORECAST);
                isCurrentLocationEnabled = savedInstanceState.getBoolean(IS_CURRENT_LOCATION_ENABLED);
                lastCurrentLocation = savedInstanceState.getParcelable(LAST_CURRENT_LOCATION);

            }

            ActionBar actionBar = getActionBar();
            actionBar.setIcon(R.drawable.ic_logo);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBarListener = new ActionBarListener(getSupportFragmentManager(), mainFragments);
            actionBar.setListNavigationCallbacks(
                    ArrayAdapter.createFromResource(this, R.array.main_fragments, android.R.layout.simple_spinner_dropdown_item),
                    actionBarListener);

            if (!isFreshLaunch && !isSearching && !isLoading) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                actionBar.setSelectedNavigationItem(navItem);
            }

        } else {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

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
    protected void onStart() {
        super.onStart();

        if (Network.getInstance().isSetup()) {
            if (isFreshLaunch) {
                locationClient.connect();
            }
        }

    }

    @Override
    protected void onResumeFragments () {
        super.onResumeFragments();

        isActive = true;

        if (searchNeedsUpdate != null) {
            setSearching(searchNeedsUpdate);
            searchNeedsUpdate = null;
        }

        if (loadingNeedsUpdate != null) {
            setLoading(loadingNeedsUpdate);
            loadingNeedsUpdate = null;
        }

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        isActive = false;

        isFreshLaunch = !isChangingConfigurations();
        outState.putBoolean(FRESH_LAUNCH, isFreshLaunch);
        outState.putBoolean(IS_SEARCHING, isSearching);
        outState.putBoolean(IS_LOADING, isLoading);

        if (searchNeedsUpdate != null)
            outState.putBoolean(SEARCH_NEEDS_UPDATE, searchNeedsUpdate);
        if (loadingNeedsUpdate != null)
            outState.putBoolean(LOADING_NEEDS_UPDATE, loadingNeedsUpdate);

        ActionBar ab = getActionBar();
        if (ab.getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST)
            outState.putInt(NAV_ITEM, ab.getSelectedNavigationIndex());
        else
            outState.putInt(NAV_ITEM, navItem);

        outState.putParcelable(FORECAST, lastForecastResponse);
        if (isFreshLaunch)
            outState.putBoolean(IS_CURRENT_LOCATION_ENABLED, true);
        else
            outState.putBoolean(IS_CURRENT_LOCATION_ENABLED, isCurrentLocationEnabled);
        outState.putParcelable(LAST_CURRENT_LOCATION, lastCurrentLocation);

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
        String action = intent.getAction();

        if (Intent.ACTION_SEARCH.equals(action)) {
            searchWidget.collapseActionView();
            setLoading(true);
            isCurrentLocationEnabled = false;
            String query = intent.getStringExtra(SearchManager.QUERY);
            Network.getInstance().getAutocomplete(query, Locale.getDefault().getLanguage(), null);

        } else if (Intent.ACTION_VIEW.equals(action)) {
            searchWidget.collapseActionView();
            setLoading(true);
            isCurrentLocationEnabled = false;
            Network.getInstance().getDetails(intent.getDataString(), Locale.getDefault().getLanguage(), null);
        }

    }

    private void refreshForecast() {
        if (isCurrentLocationEnabled) {
            if (locationClient.isConnected()) {
                new getLocationNameTask(new Geocoder(this)).execute();
            }
            else {
                locationClient.connect();
            }
        } else if (lastForecastResponse != null) {
            setLoading(true);
            Network.getInstance().getForecast(lastForecastResponse.getLatitude(), lastForecastResponse.getLongitude(),
                    Locale.getDefault().getLanguage(), null);
        }
    }

    @Override
    public void success(Object response, Response response2) {
        if (Forecast.Response.class.isInstance(response)) {

            FragmentManager fm = getSupportFragmentManager();

            lastForecastResponse = (Forecast.Response) response;
            lastForecastResponse.setName(Network.getInstance().getLastLocationName());
            setLoading(false);

            for (Fragment fragment : fm.getFragments()) {
                if (fragment != null)
                    ((FragmentListener) fragment).onNewData();
            }

        } else if (Places.AutocompleteResponse.class.isInstance(response)) {
            Places.AutocompleteResponse autocompleteResponse = (Places.AutocompleteResponse) response;

            if (Network.getInstance().responseOkay(autocompleteResponse.getStatus(), this)) {
                Network.getInstance().getDetails(autocompleteResponse.getPredictions()[0].getPlace_id(),
                        Locale.getDefault().getLanguage(), null);
            } else {
                setLoading(false);
            }

        } else if (Places.DetailsResponse.class.isInstance(response)) {
            Places.DetailsResponse details = (Places.DetailsResponse) response;

            if (Network.getInstance().responseOkay(details.getStatus(), this)) {
                Network.getInstance().setLastLocationName(details.getResult().getName());
                Places.Location location = details.getResult().getGeometry().getLocation();
                Network.getInstance().getForecast(location.getLat(), location.getLng(), Locale.getDefault().getLanguage(), null);
            } else {
                setLoading(false);
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {

        setLoading(false);

        if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(MainActivity.GOOGLE_API_KEY, null)
                    .putString(MainActivity.FORECAST_API_KEY, null)
                    .apply();
            GeneralAlertDialog.newInstance(
                    Network.NETWORK_ERROR_DIALOG,
                    getString(R.string.network_error_title),
                    getString(R.string.network_error_message),
                    getString(R.string.network_error_negative),
                    getString(R.string.network_error_positive)
            ).show(getSupportFragmentManager(), Network.NETWORK_ERROR_DIALOG);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT);
            toast.show();
            Log.e("Retrofit", error.getMessage());
        }

    }

    private void setSearching(boolean state) {
        setViewState(R.layout.searching, state);
    }

    public void setLoading(boolean state) {
        isLoading = state;
        setViewState(R.layout.loading, state);
    }

    private void setViewState(int id, boolean state) {

        if (isActive) {
            ActionBar ab = getActionBar();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment toDetach, toAttach;

            if (state) {
                navItem = ab.getSelectedNavigationIndex();
                ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                toAttach = fm.findFragmentByTag(helperFragNames.get(id));
                if (toAttach.isDetached()) {
                    ft.attach(toAttach).commit();
                }
            } else {
                toDetach = fm.findFragmentByTag(helperFragNames.get(id));
                ft.detach(toDetach);

                ft.commit();

                ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                ab.setSelectedNavigationItem(navItem);

            }
        } else {
            switch (id) {
                case R.layout.searching:
                    searchNeedsUpdate = state;
                    break;
                case R.layout.loading:
                    loadingNeedsUpdate = state;
                    break;

            }
        }

    }

    public boolean hasForecast() {
        return lastForecastResponse != null;
    }

    public Forecast.Response getForecast() {
        return lastForecastResponse;
    }

    // location services

    private class getLocationNameTask extends AsyncTask<Void, Void, String> {
        private Geocoder geocoder;

        public getLocationNameTask(Geocoder geocoder) {
            this.geocoder = geocoder;
        }

        @Override
        protected void onPreExecute () {
            setLoading(true);
            lastCurrentLocation = locationClient.getLastLocation();
            isCurrentLocationEnabled = true;
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
            Network.getInstance().setLastLocationName(result);
            getLocalWeather();
        }

    }

    private void getLocalWeather() {
        Network.getInstance().getForecast(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude(),
                Locale.getDefault().getLanguage(), null);
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

    @Override
    protected void onDestroy () {
        super.onDestroy();

        Network.getInstance().setActivityCallback(null);
    }

    @Override
    public void onDialogClick(String tag, Boolean positive) {
        switch (tag) {
            case Network.NETWORK_ERROR_DIALOG:
                if (positive) {
                    setLoading(true);
                    getKeys();
                }
                break;
        }
    }

}