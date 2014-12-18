package com.shawnaten.simpleweather;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.shawnaten.networking.Forecast;
import com.shawnaten.networking.Network;
import com.shawnaten.networking.Tasks;
import com.shawnaten.simpleweather.backend.keysEndpoint.KeysEndpoint;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.current.CurrentFragment;
import com.shawnaten.simpleweather.map.MapFragment;
import com.shawnaten.simpleweather.week.WeekFragment;
import com.shawnaten.tools.ActionBarListener;
import com.shawnaten.tools.App;
import com.shawnaten.tools.BaseActivity;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.GeneralAlertDialog;
import com.shawnaten.tools.GenericFragment;
import com.shawnaten.tools.PlayServices;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseActivity implements Network.NetworkListener, GeneralAlertDialog.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0, REQUEST_CODE_ACCOUNT_PICKER = 1;
    private static final SparseArray<String> helperFragNames = new SparseArray<>();
    static {
        helperFragNames.put(R.layout.searching, "searching");
        helperFragNames.put(R.layout.loading, "loading");
    }

    private static final String NAV_ITEM = "navIndex", FRESH_LAUNCH = "freshLaunch", FORECAST = "forecast",
            IS_CURRENT_LOCATION_ENABLED ="isCurrentLocationEnabled", IS_SEARCHING = "isSearching", IS_LOADING = "isLoading",
            LAST_CURRENT_LOCATION = "lastCurrentLocation";

    public static final String PREFS = "prefs", GOOGLE_API_KEY = "googleAPIKey", FORECAST_API_KEY = "forecastAPIKey";

    public static final String NETWORK_ERROR_DIALOG = "networkErrorDialog";

    private KeysEndpoint keysService;

    /*
    private Handler handler = new Handler();
    private Runnable adRunnable;
    */

    private String[] mainFragments;
    private MenuItem searchWidget;
    private LocationClient locationClient;

    private int navItem;
    private boolean isActive, isFreshLaunch = true, isSearching, isLoading = true, isCurrentLocationEnabled = true;
    private Forecast.Response lastForecastResponse;
    private Location lastCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this is to remove old account preference
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        defaultPrefs.edit().remove(getString(R.string.account_key)).apply();

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

                Network.setup(networkCacheFile, keys);
            }
            Network.getInstance().setListener(this);

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

                navItem = savedInstanceState.getInt(NAV_ITEM, 0);
                lastForecastResponse = savedInstanceState.getParcelable(FORECAST);
                isCurrentLocationEnabled = savedInstanceState.getBoolean(IS_CURRENT_LOCATION_ENABLED);
                lastCurrentLocation = savedInstanceState.getParcelable(LAST_CURRENT_LOCATION);

            }

            ActionBar actionBar = getActionBar();
            actionBar.setIcon(R.drawable.ic_logo);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setListNavigationCallbacks(
                    ArrayAdapter.createFromResource(this, R.array.main_fragments, android.R.layout.simple_spinner_dropdown_item),
                    new ActionBarListener(getSupportFragmentManager(), mainFragments));

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
                isFreshLaunch = false;
            }
        }

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        isActive = true;

        if (Network.getInstance().isSetup()) {
            setSearching(isSearching);
            setLoading(isLoading);
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
                   onConnectedTasks();
                }
                else
                    locationClient.connect();
                return true;
            case R.id.action_refresh:
                refreshForecast();
                return true;
            case R.id.action_settings:
                Intent openSettings = new Intent(this, SettingsActivity.class);
                startActivity(openSettings);
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
            Network.getInstance().getAutocompleteAsync(query);

        } else if (Intent.ACTION_VIEW.equals(action)) {
            searchWidget.collapseActionView();
            setLoading(true);
            isCurrentLocationEnabled = false;
            Network.getInstance().getDetails(intent.getDataString());
        }

    }

    private void refreshForecast() {
        if (isCurrentLocationEnabled) {
            if (locationClient.isConnected()) {
                onConnectedTasks();
            }
            else {
                locationClient.connect();
            }
        } else if (lastForecastResponse != null) {
            setLoading(true);
            Network.getInstance().getForecast(lastForecastResponse.getLatitude(), lastForecastResponse.getLongitude());
        }
    }

    private void setSearching(boolean state) {
        isSearching = state;
        setViewState(R.layout.searching, state);
    }

    public void setLoading(boolean state) {
        ActionBar ab = getActionBar();

        isLoading = state;

        if (state) {
            navItem = ab.getSelectedNavigationIndex();
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } else {
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            ab.setSelectedNavigationItem(navItem);
        }
        setViewState(R.layout.loading, state);
    }

    private void setViewState(int id, boolean state) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment toDetach, toAttach;

        if (isActive) {
            if (state) {
                toAttach = fm.findFragmentByTag(helperFragNames.get(id));
                if (toAttach.isDetached()) {
                    ft.attach(toAttach).commit();
                }

            } else {
                toDetach = fm.findFragmentByTag(helperFragNames.get(id));
                ft.detach(toDetach);

                ft.commit();

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

    @Override
    public void onConnected(Bundle bundle) {
        onConnectedTasks();
    }

    private void onConnectedTasks() {
        setLoading(true);
        lastCurrentLocation = locationClient.getLastLocation();
        isCurrentLocationEnabled = true;

        if (ForecastTools.UNIT_CODE == null) {
            String units = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.units_key), null);
            if (units == null)
                new Tasks.getDefaultUnitsTask(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.units_key),
                        new Geocoder(this), lastCurrentLocation, getString(R.string.action_current_location)).execute();
            else {
                ForecastTools.configUnits(units, null, null);
                new Tasks.getLocationNameTask(new Geocoder(this), lastCurrentLocation,
                        getString(R.string.action_current_location)).execute();
            }


        } else  {
            new Tasks.getLocationNameTask(new Geocoder(this), lastCurrentLocation,
                    getString(R.string.action_current_location)).execute();
        }
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

    // end of location services

    /**
     * Gets API keys from GAE. Calls getKeysTask. Changing to no longer prompt user with account
     * selection. It will choose the first account available and validate backend call using that.
     */
    public void getKeys() {
        /*SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accountName = defaultPrefs.getString(getString(R.string.account_key), null);*/
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                getApplicationContext(), "server:client_id:" + getString(R.string.WEB_ID));
        // only using for auth, no need for user selection
        credential.setSelectedAccountName(credential.getAllAccounts()[0].name);

        /*ArrayList<String> allAccounts = new ArrayList<>();
        for (Account account : credential.getAllAccounts())
            allAccounts.add(account.name);*/

        /*if (allAccounts.contains(accountName)) {
            credential.setSelectedAccountName(accountName);*/

        KeysEndpoint.Builder keysEndpointBuilder =
                new KeysEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential);
        if (getResources().getBoolean(R.bool.localhost))
            keysEndpointBuilder.setRootUrl(getString(R.string.root_url));
        keysService = keysEndpointBuilder.build();

        new getKeysTask(this, credential).execute();

        /*}
        else
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_CODE_ACCOUNT_PICKER);*/

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
                onConnectedTasks();
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
        /*switch (requestCode) {
            case REQUEST_CODE_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                        defaultPrefs.edit().putString(getString(R.string.account_key), accountName).apply();
                        getKeys();
                    }
                } else {
                    getKeys();
                }
            break;
        }*/
    }

    @Override
    public void onDialogClick(String tag, Boolean positive) {
        switch (tag) {
            case NETWORK_ERROR_DIALOG:
                if (positive) {
                    setLoading(true);
                    getKeys();
                }
                break;
        }
    }

    @Override
    public void onNewData(Forecast.Response forecast) {
        FragmentManager fm = getSupportFragmentManager();

        Tracker t = ((App) getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.network_category))
                .setAction(getString(R.string.receive_data_action))
                .setLabel(getString(R.string.success_label))
                .build());

        lastForecastResponse = forecast;

        /*
        Kiip.getInstance().saveMoment("weather_check", new Kiip.Callback() {
            @Override
            public void onFinished(Kiip kiip, final Poptart reward) {
                if (adRunnable != null)
                    handler.removeCallbacks(adRunnable);

                adRunnable = new Runnable() {
                    @Override
                    public void run() {
                        onPoptart(reward);
                    }
                };

                handler.postDelayed(adRunnable, 5000);

            }

            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
            }
        });
        */

        for (String tag : mainFragments) {
            FragmentListener listener = (FragmentListener) fm.findFragmentByTag(tag);
            if (listener != null)
                listener.onNewData();
        }

        setLoading(false);
    }

    @Override
    public void onFailure(boolean setLoading) {

        Tracker t = ((App) getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.network_category))
                .setAction(getString(R.string.receive_data_action))
                .setLabel(getString(R.string.failure_label))
                .build());

        setLoading(setLoading);
    }

    @Override
    public void onShowNetworkToast(int resId) {
        Toast toast = Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onShowNetworkDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment dialog = fm.findFragmentByTag(NETWORK_ERROR_DIALOG);

        if (isActive && (dialog == null || dialog.isRemoving())) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(MainActivity.GOOGLE_API_KEY, null)
                    .putString(MainActivity.FORECAST_API_KEY, null)
                    .apply();
            GeneralAlertDialog.newInstance(
                    NETWORK_ERROR_DIALOG,
                    getString(R.string.network_error_title),
                    getString(R.string.network_error_message),
                    getString(R.string.network_error_negative),
                    getString(R.string.network_error_positive)
            ).show(getSupportFragmentManager(), NETWORK_ERROR_DIALOG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Network.getInstance().setListener(null);
    }

}