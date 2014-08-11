package com.shawnaten.simpleweather;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.shawnaten.networking.Forecast;
import com.shawnaten.networking.Network;
import com.shawnaten.networking.Places;
import com.shawnaten.simpleweather.current.CurrentFragment;
import com.shawnaten.simpleweather.map.MapFragment;
import com.shawnaten.simpleweather.week.WeekFragment;
import com.shawnaten.tools.CustomAlertDialog;
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.TabListener;

import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity implements Callback, CustomAlertDialog.CustomAlertListener {
    private AdView bannerAd;
    private FragmentListener cListen, wListen, mListen;
    private TabListener currentTab, weekTab, mapTab;
    private Boolean isActive = false;
    private GoogleMap map;
    private Marker marker;
    /* to allow user to select account
    private SharedPreferences settings;
    private GoogleAccountCredential credential;
    private String accountName;
    private static final String PREF_ACCOUNT_NAME = "prefAccountName";
    */

    public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 0, REQUEST_CODE_ACCOUNT_PICKER = 1;

    private static MenuItem searchWidget;
    private static String locationName;
    private static Forecast.Response lastForecastResponse;
    private static int navItem;
    private static Modes modes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fragment cFrag, wFrag, mFrag, lFrag, sFrag;
        ActionBar actionBar;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        super.onCreate(savedInstanceState);

        Network.setup(this);
        modes = new Modes();

        setContentView(R.layout.main_activity);

        actionBar = getActionBar();
        assert actionBar != null;

        if (savedInstanceState == null) {
            /* to allow user to select account

            settings = getSharedPreferences("Weather", 0);
            credential = GoogleAccountCredential.usingAudience(getApplicationContext(), "server:client_id:" + Constants.WEB_ID);
            setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

            if (credential.getSelectedAccountName() == null)
                startActivityForResult(credential.newChooseAccountIntent(), REQUEST_CODE_ACCOUNT_PICKER);

            */

            cFrag = new CurrentFragment();
            ft.add(R.id.main_fragment, cFrag, "current");
            ft.detach(cFrag);

            wFrag = new WeekFragment();
            ft.add(R.id.main_fragment, wFrag, "week");
            ft.detach(wFrag);

            mFrag = new MapFragment();
            ft.add(R.id.main_fragment, mFrag, "map");
            ft.detach(mFrag);

            lFrag = new GenericFragment();
            Bundle args = new Bundle();
            args.putInt("layout", R.layout.loading);
            lFrag.setArguments(args);
            ft.add(R.id.main_fragment, lFrag, "loading");
            ft.detach(lFrag);

            sFrag = new GenericFragment();
            args = new Bundle();
            args.putInt("layout", R.layout.searching);
            sFrag.setArguments(args);
            ft.add(R.id.main_fragment, sFrag, "searching");
            ft.detach(sFrag);

            ft.commit();

        } else {

            cFrag = getSupportFragmentManager().findFragmentByTag("current");
            wFrag = getSupportFragmentManager().findFragmentByTag("week");
            mFrag = getSupportFragmentManager().findFragmentByTag("map");

            if (lastForecastResponse == null)
                ft.detach(fm.findFragmentById(R.id.main_fragment)).commit();

        }

        cListen = (FragmentListener) cFrag;
        wListen = (FragmentListener) wFrag;
        mListen = (FragmentListener) mFrag;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(getResources().getDrawable(R.drawable.ic_logo));

        currentTab = new TabListener(cFrag);
        weekTab = new TabListener(wFrag);
        mapTab = new TabListener(mFrag);

        Tab tab = actionBar.newTab().setText(R.string.tab_current).setTag("current").setTabListener(currentTab);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab_week).setTag("week").setTabListener(weekTab);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab_map).setTag("map").setTabListener(mapTab);
        actionBar.addTab(tab);
        setIntent(null);

        if (lastForecastResponse != null) {
            modes.enableTabs(true);
            actionBar.setSelectedNavigationItem(navItem);
            if (lastForecastResponse.getExpiration().before(new Date())) {
                Network.getInstance().getForecast(lastForecastResponse.getLatitude(), lastForecastResponse.getLongitude(),
                        Locale.getDefault().getLanguage(), this);
            }
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

        if (bannerAd != null)
            bannerAd.resume();

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

        navItem = getActionBar().getSelectedNavigationIndex();

        if (bannerAd != null)
            bannerAd.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bannerAd != null)
            bannerAd.destroy();
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

    public void onButtonClick(View view) {
        int id = view.getId();

        switch ((String) view.getTag()) {
            case "current":
                cListen.onButtonClick(id);
                break;
            case "week":
                wListen.onButtonClick(id);
                break;
            case "map":
                mListen.onButtonClick(id);
                break;
        }

    }

    @Override
    public void success(Object response, Response response2) {
        if (Forecast.Response.class.isInstance(response)) {
            Forecast.Response forecast = (Forecast.Response) response;

            for (Header header : response2.getHeaders())
                if (header.getName() != null && header.getName().equals("Expires"))
                    forecast.setExpiration(header.getValue());

            getActionBar().setSelectedNavigationItem(navItem);

            setTitle(locationName);
            lastForecastResponse = forecast;
            modes.setLoading(false);
            setIntent(null);
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

                ft.detach(fm.findFragmentById(R.id.main_fragment));

                if (isLoading) {
                    enableTabs(false);
                    if (isSearching)
                        ft.attach(fm.findFragmentByTag("searching"));
                    else
                        ft.attach(fm.findFragmentByTag("loading"));
                } else {
                    if (isSearching) {
                        enableTabs(false);
                        ft.attach(fm.findFragmentByTag("searching"));
                    } else {
                        if (lastForecastResponse != null) {
                            enableTabs(true);
                            ActionBar ab = getActionBar();
                            ft.attach(fm.findFragmentByTag((String) ab.getTabAt(ab.getSelectedNavigationIndex()).getTag()));
                        }
                    }
                }
                ft.commit();
                fm.executePendingTransactions();
                needsUpdate = false;
            }
        }

        public void enableTabs(Boolean enabled) {
            currentTab.setEnabled(enabled);
            weekTab.setEnabled(enabled);
            mapTab.setEnabled(enabled);
        }

    }

    @Override
    public void onDialogClosed(int code) {
        switch (code) {
            case 0:
                finish();
                break;
        }
    }

    public String getLocationName() {
        return locationName;
    }

    public static boolean hasForecast() {
        if (lastForecastResponse != null)
            return true;
        return false;
    }

    public static Forecast.Response getForecast() {
        return lastForecastResponse;
    }

    /* to allow user to select account

    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        // User is authorized.
                    }
                }
                break;
        }
    }
    */

}