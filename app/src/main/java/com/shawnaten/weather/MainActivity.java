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
import com.shawnaten.tools.FragmentListener;
import com.shawnaten.tools.TabListener;

import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class MainActivity extends Activity implements View.OnFocusChangeListener, Callback {
    private Uri uri;
    private AdView bannerAd;
    private FragmentListener cListen, wListen, mListen;

    public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 0;
    private static final int VISIBILITY_SEARCH_STARTED = 0, VISIBILITY_SEARCH_CANCELED = 1, VISIBILITY_REQUEST_STARTED = 2, VISIBILITY_REQUEST_FINISHED = 3,
        VISIBILITY_REQUEST_STARTED_NO_SEARCH = 4;

    private static MenuItem searchWidget;
    private static String title;
    private static Forecast.Response lastForecastResponse;
    private static int navItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fragment cFrag, wFrag, mFrag;
        ActionBar actionBar;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        actionBar = getActionBar();
        assert actionBar != null;

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
            cListen = (FragmentListener) cFrag;

            wFrag = new WeekFragment();
            ft.add(R.id.main_fragment, wFrag, "week");
            ft.detach(wFrag);
            wListen = (FragmentListener) wFrag;

            mFrag = new MapFragment();
            ft.add(R.id.main_fragment, mFrag, "map");
            ft.detach(mFrag);
            mListen = (FragmentListener) mFrag;

            ft.commit();

        } else {

            cFrag = getFragmentManager().findFragmentByTag("current");
            cListen = (FragmentListener) cFrag;
            wFrag = getFragmentManager().findFragmentByTag("week");
            wListen = (FragmentListener) wFrag;
            mFrag = getFragmentManager().findFragmentByTag("map");
            mListen = (FragmentListener) mFrag;

            if(lastForecastResponse != null) {
                cListen.onRestoreData(lastForecastResponse);
                wListen.onRestoreData(lastForecastResponse);
                mListen.onRestoreData(lastForecastResponse);
                changeVisibility(VISIBILITY_REQUEST_FINISHED);
            }

        }

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

        ActionBar actionBar = getActionBar();
        assert actionBar != null;

        if (title == null)
            actionBar.setTitle(getString(R.string.app_name));
        else
            actionBar.setTitle(title);

        if (lastForecastResponse != null && lastForecastResponse.getExpiration().before(new Date())) {
            Network.getInstance(getApplicationContext()).getForecast(lastForecastResponse.getLatitude(), lastForecastResponse.getLongitude(),
                    Locale.getDefault().getLanguage(), this);
            changeVisibility(VISIBILITY_REQUEST_STARTED_NO_SEARCH);
        }

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
            changeVisibility(VISIBILITY_REQUEST_STARTED);
            String query = intent.getStringExtra(SearchManager.QUERY);
            Network.getInstance(getApplicationContext()).getAutocomplete(query, Locale.getDefault().getLanguage(), this);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            changeVisibility(VISIBILITY_REQUEST_STARTED);
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

    private void changeVisibility(int code) {
        switch (code) {
            case VISIBILITY_SEARCH_STARTED:
                findViewById(R.id.main_fragment).setVisibility(View.INVISIBLE);
                findViewById(R.id.search_logo).setVisibility(View.VISIBLE);
                break;
            case VISIBILITY_SEARCH_CANCELED:
                findViewById(R.id.search_logo).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
                break;
            case VISIBILITY_REQUEST_STARTED:
                findViewById(R.id.search_logo).setVisibility(View.INVISIBLE);
                findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
                break;
            case VISIBILITY_REQUEST_FINISHED:
                findViewById(R.id.progress_spinner).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
                break;
            case VISIBILITY_REQUEST_STARTED_NO_SEARCH:
                findViewById(R.id.main_fragment).setVisibility(View.INVISIBLE);
                findViewById(R.id.search_logo).setVisibility(View.INVISIBLE);
                findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (getIntent() == null) {
            if (hasFocus)
                changeVisibility(VISIBILITY_SEARCH_STARTED);
            else
                changeVisibility(VISIBILITY_SEARCH_CANCELED);
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
            assert getActionBar() != null;
            getActionBar().setTitle(title);
            changeVisibility(VISIBILITY_REQUEST_FINISHED);
            setIntent(null);

            lastForecastResponse = forecast;
        } else if (Places.AutocompleteResponse.class.isInstance(response)) {
            Places.AutocompleteResponse autocompleteResponse = (Places.AutocompleteResponse) response;

            if (autocompleteResponse.getStatus().equals("OK")) {
                Network.getInstance(getApplicationContext()).getDetails(autocompleteResponse.getPredictions()[0].getPlace_id(),
                        Locale.getDefault().getLanguage(), this);
            } else {
                setIntent(null);
                changeVisibility(VISIBILITY_REQUEST_FINISHED);
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
                Places.Location location = details.getResult().getGeometry().getLocation();
                Network.getInstance(getApplicationContext()).getForecast(location.getLat(), location.getLng(), Locale.getDefault().getLanguage(), this);
            } else {
                setIntent(null);
                changeVisibility(VISIBILITY_REQUEST_FINISHED);
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

}