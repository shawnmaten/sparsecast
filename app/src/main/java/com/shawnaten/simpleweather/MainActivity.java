package com.shawnaten.simpleweather;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.GeneralAlertDialog;

import java.io.IOException;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity implements Callback, GeneralAlertDialog.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0, REQUEST_CODE_ACCOUNT_PICKER = 1;
    private static final String[] helperFragNames = {"searching", "loading"};
    private static final String NAV_ITEM_KEY = "navIndex", FORECAST_KEY = "forecast",
            PREFS = "prefs", PREF_ACCOUNT_NAME = "prefAccountName", ACCOUNT_SELECTION_DIALOG = "accountSelectionDialog",
            GOOGLE_API_KEY = "googleAPIKey", FORECAST_API_KEY = "forecastAPIKey";

    private KeysEndpoint keysService;

    private String[] mainFragments;
    private int navItem;
    private Modes modes;
    private MenuItem searchWidget;
    private LocationClient locationClient;

    private Forecast.Response lastForecastResponse;

    private String locationName;
    private Boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playServicesAvailable()) {
            ActionBar actionBar = getActionBar();

            SharedPreferences settings = getSharedPreferences(PREFS, MODE_PRIVATE);
            String googleAPIKey = settings.getString(GOOGLE_API_KEY, null);
            String forecastAPIKey = settings.getString(FORECAST_API_KEY, null);
            Keys keys = new Keys();
            if (googleAPIKey != null && forecastAPIKey != null) {
                keys.setGoogleAPIKey(googleAPIKey);
                keys.setForecastAPIKey(forecastAPIKey);
            } else {
                getKeys();
                keys = null;
            }
            Network.setup(this, keys);

            setContentView(R.layout.main_activity);

            mainFragments = getResources().getStringArray(R.array.main_fragments);
            locationClient = new LocationClient(this, this, this);
            modes = new Modes();

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
                navItem = savedInstanceState.getInt(NAV_ITEM_KEY, 0);
                lastForecastResponse = savedInstanceState.getParcelable(FORECAST_KEY);
            }

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(
                    ArrayAdapter.createFromResource(this, R.array.main_fragments, android.R.layout.simple_spinner_dropdown_item),
                    new ActionBarListener(this));
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
                modes.setSearching(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                modes.setSearching(false);
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
    public void onResume() {

        super.onResume();

        if (Network.getInstance().isSetup()) {
            launchTasks();
        }

        /*
        if (PlayServices.playServicesAvailable(this) && bannerAd == null) {

            Bundle bundle = new Bundle();
            bundle.putString("color_bg", "#ff80ab");
            bundle.putString("color_link", "ffffff");
            bundle.putString("color_text", "ffffff");
            bundle.putString("color_url", "ffffff");
            AdMobExtras extras = new AdMobExtras(bundle);
            bannerAd = (AdView) findViewById(R.id.ad);
            bannerAd.loadAd(new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Hash.MD5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)))
                    .addNetworkExtras(extras)
                    .build());
        }
        */

    }

    private void launchTasks() {
        locationClient.connect();
    }

    @Override
    protected void onResumeFragments () {
        super.onResumeFragments();

        isActive = true;
        modes.updateFragments();

    }

    @Override
    public void onPause() {
        super.onPause();

        isActive = false;

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(NAV_ITEM_KEY, navItem);
        outState.putParcelable(FORECAST_KEY, lastForecastResponse);
    }

    @Override
    protected void onStop() {
        super.onStop();

        locationClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
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
            modes.setLoading(true);
            String query = intent.getStringExtra(SearchManager.QUERY);
            Network.getInstance().getAutocomplete(query, Locale.getDefault().getLanguage(), this);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            modes.setLoading(true);
            Network.getInstance().getDetails(intent.getDataString(), Locale.getDefault().getLanguage(), this);
        }

    }

    @Override
    public void success(Object response, Response response2) {
        if (Forecast.Response.class.isInstance(response)) {
            FragmentManager fm = getSupportFragmentManager();

            lastForecastResponse = (Forecast.Response) response;
            lastForecastResponse.setName(locationName);
            modes.setLoading(false);

            for (String name : mainFragments) {
                FragmentListener listener = (FragmentListener) fm.findFragmentByTag(name);
                if (listener != null) {
                    listener.onNewData();
                }
            }

        } else if (Places.AutocompleteResponse.class.isInstance(response)) {
            Places.AutocompleteResponse autocompleteResponse = (Places.AutocompleteResponse) response;
            Toast toast;

            switch (autocompleteResponse.getStatus()) {
                case "OK":
                    Network.getInstance().getDetails(autocompleteResponse.getPredictions()[0].getPlace_id(),
                            Locale.getDefault().getLanguage(), this);
                    break;
                case "ZERO_RESULTS":
                    setIntent(null);
                    modes.setLoading(false);
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.no_search_results), Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                default:
                    setIntent(null);
                    modes.setLoading(false);
                    toast = Toast.makeText(getApplicationContext(), getString(R.string.processing_error), Toast.LENGTH_SHORT);
                    toast.show();
                    Log.e("Autocomplete Error", autocompleteResponse.getStatus());
            }

        } else if (Places.DetailsResponse.class.isInstance(response)) {
            Places.DetailsResponse details = (Places.DetailsResponse) response;

            if (details.getStatus().equals("OK")) {
                locationName = details.getResult().getName();
                Places.Location location = details.getResult().getGeometry().getLocation();
                Network.getInstance().getForecast(location.getLat(), location.getLng(), Locale.getDefault().getLanguage(), this);
            } else {
                setIntent(null);
                modes.setLoading(false);
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.processing_error), Toast.LENGTH_SHORT);
                toast.show();
                Log.e("Place Details Error", details.getStatus());
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.processing_error), Toast.LENGTH_SHORT);
        toast.show();
        Log.e("Retrofit", error.getMessage());
    }

    private class Modes {
        private static final int LOADING = 1, SEARCHING = 2;
        private Boolean isLoading = false, isSearching = false, needsUpdate = false;

        public void setLoading(Boolean state) {
            isLoading = state;
            needsUpdate = true;
            updateFragments();
        }

        public void setSearching(Boolean state) {
            isSearching = state;
            needsUpdate = true;
            updateFragments();
        }

        public void updateFragments() {
            if (isActive && needsUpdate) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment toDetach, toAttach = null;

                toDetach = fm.findFragmentById(R.id.main_fragment);

                if (isLoading) {
                    if (isSearching)
                        toAttach = fm.findFragmentByTag(helperFragNames[0]);
                    else
                        toAttach = fm.findFragmentByTag(helperFragNames[1]);
                } else {
                    if (isSearching) {
                        toAttach = fm.findFragmentByTag(helperFragNames[0]);
                    } else {
                        if (lastForecastResponse != null) {
                            toAttach = fm.findFragmentByTag(mainFragments[getActionBar().getSelectedNavigationIndex()]);
                        }
                    }
                }

                if (toDetach != null)
                    ft.detach(toDetach);
                if (toAttach != null)
                    ft.attach(toAttach);

                ft.commit();
                fm.executePendingTransactions();
                needsUpdate = false;
            }
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

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        modes.setLoading(true);
        Network.getInstance().getForecast(location.getLatitude(), location.getLongitude(), Locale.getDefault().getLanguage(), this);
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

    private void getKeys() {
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accountName = defaultPrefs.getString(PREF_ACCOUNT_NAME, null);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this, "server:client_id:" + getString(R.string.WEB_ID));
        if (accountName == null) {
            if (getSupportFragmentManager().findFragmentByTag(ACCOUNT_SELECTION_DIALOG) == null) {
                GeneralAlertDialog dialog = GeneralAlertDialog.newInstance(
                        ACCOUNT_SELECTION_DIALOG,
                        getString(R.string.account_selection_title),
                        getString(R.string.account_selection_message),
                        null,
                        getString(R.string.account_selection_positive_button)
                );
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), ACCOUNT_SELECTION_DIALOG);
            }

        } else {
            credential.setSelectedAccountName(accountName);

            KeysEndpoint.Builder keysEndpointBuilder =
                    new KeysEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential);
            if (getResources().getBoolean(R.bool.localhost))
                keysEndpointBuilder.setRootUrl(getString(R.string.root_url));
            keysService = keysEndpointBuilder.build();

            new getKeysTask().execute();
        }
    }

    private class getKeysTask extends AsyncTask<Void, Void, Keys> {
        @Override
        protected Keys doInBackground(Void... unused) {
            Keys keys = new Keys();
            try {
                Log.d("getKeysTask", "getting keys");
                keys = keysService.getKeys().execute();
                if (keys != null) {
                    Network.getInstance().setKeys(keys);
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                            .putString(GOOGLE_API_KEY, keys.getGoogleAPIKey())
                            .putString(FORECAST_API_KEY, keys.getForecastAPIKey())
                            .apply();
                }
            } catch (IOException e) {
                Log.e("getKeysTask", e.getMessage(), e);
            }
            return keys;
        }

        @Override
        protected void onPostExecute (Keys result) {
            launchTasks();
        }

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
                }
            break;
        }
    }

    @Override
    public void onDialogClick(String tag, Boolean positive) {
        switch (tag) {
            case ACCOUNT_SELECTION_DIALOG:
                if (positive) {
                    GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this, "server:client_id:" + getString(R.string.WEB_ID));
                    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_CODE_ACCOUNT_PICKER);
                }

        }
    }

}