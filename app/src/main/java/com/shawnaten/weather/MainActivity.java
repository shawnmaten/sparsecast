package com.shawnaten.weather;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.shawnaten.networking.Forecast;
import com.shawnaten.networking.Network;
import com.shawnaten.networking.Places;

import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class MainActivity extends Activity implements View.OnFocusChangeListener, Callback {
    private Uri uri;
    private AdView bannerAd;
    private TabDataListener cListen, wListen, mListen;

    public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 0;

    private static MenuItem searchWidget;
    private static String title;
    private static Forecast.Response lastForecastResponse;
    private static double lat, lng;
    private static int navItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fragment cFrag, wFrag, mFrag;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        if (playServicesAvailable()) {

            Bundle bundle = new Bundle();
            bundle.putString("color_bg", "#ff80ab");
            bundle.putString("color_link", "ffffff");
            bundle.putString("color_text", "ffffff");
            bundle.putString("color_url", "ffffff");
            AdMobExtras extras = new AdMobExtras(bundle);
            bannerAd = (AdView) findViewById(R.id.ad);
            bannerAd.loadAd(new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("15205FD74742674BD0B4A04EC2C27A8D")
                    .addNetworkExtras(extras)
                    .build());
        }

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            cFrag = new CurrentFragment();
            ft.add(R.id.main_fragment, cFrag, "current");
            cListen = (TabDataListener) cFrag;

            wFrag = new WeekFragment();
            ft.add(R.id.main_fragment, wFrag, "week");
            ft.detach(wFrag);
            wListen = (TabDataListener) wFrag;

            mFrag = new MapFragment();
            ft.add(R.id.main_fragment, mFrag, "map");
            ft.detach(mFrag);
            mListen = (TabDataListener) mFrag;
            ft.commit();

            title = getString(R.string.app_name);
        } else {
            cFrag = getFragmentManager().findFragmentByTag("current");
            cListen = (TabDataListener) cFrag;
            wFrag = getFragmentManager().findFragmentByTag("week");
            wListen = (TabDataListener) wFrag;
            mFrag = getFragmentManager().findFragmentByTag("map");
            mListen = (TabDataListener) mFrag;

            if(lastForecastResponse != null) {
                if (lastForecastResponse.getExpiration().after(new Date())) {
                    cListen.onRestoreData(lastForecastResponse);
                    wListen.onRestoreData(lastForecastResponse);
                    mListen.onRestoreData(lastForecastResponse);
                } else {
                    Network.getInstance(getApplicationContext()).getForecast(lat, lng, Locale.getDefault().getLanguage(), this);
                    changeVisibility(R.id.main_fragment, false);
                    changeVisibility(R.id.progress_spinner, true);
                }
            }
        }

        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        TabListener currentTab, weekTab, mapTab;
        currentTab = new TabListener(cFrag);
        weekTab = new TabListener(wFrag);
        mapTab = new TabListener(mFrag);

        Tab tab = actionBar.newTab().setText(R.string.tab_current).setTabListener(currentTab);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab_week).setTabListener(weekTab);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab_map).setTabListener(mapTab);
        actionBar.addTab(tab);

        actionBar.setSelectedNavigationItem(navItem);
        actionBar.setTitle(title);

        setIntent(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        View searchText = searchView.findViewById(searchTextId);
        searchText.setOnFocusChangeListener(this);

        searchWidget = menu.findItem(R.id.action_search);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        int submitAreaId = searchView.getContext().getResources().getIdentifier("android:id/submit_area", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        View submitArea = searchView.findViewById(submitAreaId);
        if (searchPlate != null)
            searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_dark);
        if (submitArea != null)
            submitArea.setBackgroundResource(R.drawable.textfield_searchview_right_holo_dark);

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bannerAd != null)
            bannerAd.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

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
            changeVisibility(R.id.progress_spinner, true);
            String query = intent.getStringExtra(SearchManager.QUERY);
            Network.getInstance(getApplicationContext()).getAutocomplete(query, Locale.getDefault().getLanguage(), this);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            changeVisibility(R.id.progress_spinner, true);
            Network.getInstance(getApplicationContext()).getDetails(intent.getDataString(), Locale.getDefault().getLanguage(), this);
        }
    }

    public void alertClick(View view) {
        cListen.onNewData(view);
    }

    public boolean playServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.device_unsupported), Toast.LENGTH_LONG);
                toast.show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void changeVisibility(int viewID, Boolean visible) {
        View view;
        view = findViewById(viewID);
        if (visible) {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (getIntent() == null) {

            if (hasFocus) {
                changeVisibility(R.id.main_fragment, false);
            } else {
                changeVisibility(R.id.main_fragment, true);
            }
        }
    }

    @Override
    public void success(Object response, Response response2) {
        if (Forecast.Response.class.isInstance(response)) {
            Forecast.Response forecast = (Forecast.Response) response;

            for (Header header : response2.getHeaders())
                if (header.getName() != null && header.getName().equals("Expires"))
                    forecast.setExpiration(header.getValue());

            cListen.onNewData(forecast);
            wListen.onNewData(forecast);
            mListen.onNewData(forecast);
            getActionBar().setTitle(title);
            changeVisibility(R.id.progress_spinner, false);
            changeVisibility(R.id.main_fragment, true);
            setIntent(null);

            lastForecastResponse = forecast;
        } else if (Places.AutocompleteResponse.class.isInstance(response)) {
            Places.AutocompleteResponse autocompleteResponse = (Places.AutocompleteResponse) response;

            if (autocompleteResponse.getStatus().equals("OK")) {
                Network.getInstance(getApplicationContext()).getDetails(autocompleteResponse.getPredictions()[0].getPlace_id(),
                        Locale.getDefault().getLanguage(), this);
            } else {
                setIntent(null);
                changeVisibility(R.id.progress_spinner, false);
                changeVisibility(R.id.main_fragment, true);
            }

            if (autocompleteResponse.getStatus().equals("ZERO_RESULTS")) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.no_search_results), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.processing_error), Toast.LENGTH_SHORT);
                toast.show();
                Log.e("Autocomplete Error", autocompleteResponse.getStatus());
            }

        } else if (Places.DetailsResponse.class.isInstance(response)) {
            Places.DetailsResponse details = (Places.DetailsResponse) response;

            if (details.getStatus().equals("OK")) {
                title = details.getResult().getName();

                Network.getInstance(getApplicationContext()).getForecast(details.getResult().getGeometry().getLocation().getLat(),
                        details.getResult().getGeometry().getLocation().getLng(),
                        Locale.getDefault().getLanguage(), this);
            } else {
                setIntent(null);
                changeVisibility(R.id.progress_spinner, false);
                changeVisibility(R.id.main_fragment, true);
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.processing_error), Toast.LENGTH_SHORT);
                toast.show();
                Log.e("Place Details Error", details.getStatus());
            }

        }
    }

    @Override
    public void failure(RetrofitError error) {
        Toast toast = Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

}