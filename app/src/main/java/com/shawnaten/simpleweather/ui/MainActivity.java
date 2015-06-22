package com.shawnaten.simpleweather.ui;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;
import com.shawnaten.simpleweather.backend.imagesApi.model.Image;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.Geocoding;
import com.shawnaten.tools.GeocodingService;
import com.shawnaten.tools.Instagram;
import com.shawnaten.tools.LocationSettings;
import com.shawnaten.tools.PlaceLikelihoodService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    public static final int PLACE_SEARCH_CODE = RESULT_FIRST_USER + 1;
    public static final int PLACE_ADD_CODE = PLACE_SEARCH_CODE + 1;

    private static final String SCROLL_POSITION = "scrollPosition";
    private int scrollPosition;

    private static final String FORECAST_DATA = "forecastData";

    @Inject Observable<Forecast.Response> forecast;
    @Inject PlaceLikelihoodService placeLikelihoodService;
    @Inject GeocodingService geocodingService;
    @Inject Observable<Location> locationObservable;
    @Inject ImagesApi imagesApi;

    private ImageView photo;
    private View header;
    private View instagramAttribution;

    private SavedPlaceApi savedPlaceApi;

    private ArrayList<ScrollListener> scrollListeners = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SCROLL_POSITION, scrollPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewPager viewPager;
        SlidingTabLayout slidingTabLayout;
        TabAdapter tabAdapter;
        Toolbar toolbar;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
        }

        setContentView(R.layout.activity_main);
        getApp().getNetworkComponent().injectMainActivity(this);

        savedPlaceApi = getApp().getNetworkComponent().savedPlaceApi();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        instagramAttribution = getLayoutInflater()
                .inflate(R.layout.instagram_attribution, toolbar, false);
        toolbar.addView(instagramAttribution);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        tabAdapter = new TabAdapter(getSupportFragmentManager(),
                NowTab.newInstance(getString(R.string.now), R.layout.tab_now),
                TodayTab.newInstance(getString(R.string.today), R.layout.tab_today),
                WeekTab.newInstance(getString(R.string.week), R.layout.tab_week));
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setCustomTabView(R.layout.tab_title_light, R.id.title);
        slidingTabLayout.setSelectedIndicatorColors(0xFFFFFFFF);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.SimpleTabColorizer(
                getResources().getColor(R.color.text_primary_light),
                getResources().getColor(R.color.text_primary_light),
                getResources().getColor(R.color.text_secondary_light),
                getResources().getColor(R.color.text_primary_light)
        ));
        slidingTabLayout.setViewPager(viewPager);

        photo = (ImageView) findViewById(R.id.photo);
        header = findViewById(R.id.header);

        findViewById(R.id.photo_container).setLayoutParams(
                new FrameLayout.LayoutParams(screenWidth, screenWidth));
    }

    @Override
    protected void onResume() {
        View decorView;

        super.onResume();

        invalidateOptionsMenu();

        header.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) ->
                view.setY(getResources().getDisplayMetrics().widthPixels - header.getHeight()
                - scrollPosition));

        decorView = getWindow().getDecorView();
        getWindow().setStatusBarColor(0x66000000);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        int result;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.toolbar).setTranslationY(result);
        }

        if (LocationSettings.getMode() == LocationSettings.Mode.CURRENT) {
            subs.add(forecast.subscribe(forecast -> {
                dataMap.put(FORECAST_DATA, forecast);
                sendDataToFragments(FORECAST_DATA, forecast);
            }));

            Observable.zip(locationObservable, geocodingService.getAddresses(),
                    placeLikelihoodService.getPlaceLikelihood(),
                    (location, addresses, likelihoods) -> {

                        Geocoding.Result address = addresses.getResults()[0];

                        LatLng actualLocation = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        LatLng addressLocation = new LatLng(
                                address.getGeometry().getLocation().getLat(),
                                address.getGeometry().getLocation().getLng()
                        );

                        double addressDistance = SphericalUtil.computeDistanceBetween(
                                actualLocation, addressLocation);
                        Place place = likelihoods.get(0).getPlace();
                        double placeDistance = SphericalUtil.computeDistanceBetween(
                                actualLocation, place.getLatLng());

                        /*
                        for (PlaceLikelihood likelihood : likelihoods) {
                            Place newPlace = likelihood.getPlace();
                            double newDistance = SphericalUtil.computeDistanceBetween(
                                    actualLocation, newPlace.getLatLng());
                            if (newDistance < placeDistance) {
                                place = newPlace;
                                placeDistance = newDistance;
                            }
                        }
                        */

                        String returns[] = new String[2];

                        String streetNumber = null;
                        String route = null;

                        for (Geocoding.AddressComponents component :
                                address.getAddressComponents()) {
                            if (Arrays.asList(component.getTypes()).contains("street_number")) {
                                streetNumber = component.getShortName();
                            }
                            if (Arrays.asList(component.getTypes()).contains("route")) {
                                route = component.getShortName();
                            }
                            if (Arrays.asList(component.getTypes()).contains("locality"))
                                returns[0] = component.getLongName();
                        }

                        if (placeDistance < addressDistance) {
                            returns[1] = place.getName().toString();
                        } else {
                            if (streetNumber != null) {
                                returns[1] = streetNumber + " " + route;
                            } else {
                                returns[1] = address.getAddressComponents()[0].getShortName();
                            }
                        }

                        likelihoods.release();

                        return returns;
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(strings -> {
                        ((TextView) findViewById(R.id.main_location)).setText(strings[0]);
                        findViewById(R.id.secondary_location).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.secondary_location)).setText(strings[1]);
                    });
        } else {
            forecast.subscribe(forecast -> {
                sendDataToFragments(FORECAST_DATA, forecast);
                ((TextView) findViewById(R.id.main_location))
                        .setText(LocationSettings.getName());
                findViewById(R.id.secondary_location).setVisibility(View.GONE);
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem actionFavorite = menu.findItem(R.id.action_favorite);
        MenuItem actionCurrentLocation = menu.findItem(R.id.action_current_location);

        if (LocationSettings.getMode() == LocationSettings.Mode.SAVED) {
            actionFavorite.setVisible(true);
            actionCurrentLocation.setVisible(true);
            if (LocationSettings.getSavedPlace() != null)
                actionFavorite.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            actionFavorite.setVisible(false);
            actionCurrentLocation.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if (LocationSettings.getSavedPlace() != null) {
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    LocationSettings.setSavedPlace(null);
                    subs.add(Observable.create(new Observable.OnSubscribe<Void>() {
                        @Override
                        public void call(Subscriber<? super Void> subscriber) {
                            try {
                                subscriber.onNext(savedPlaceApi
                                        .delete(LocationSettings.getSavedPlace()).execute());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).subscribeOn(Schedulers.io()).subscribe());

                } else {
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    subs.add(Observable.create(new Observable.OnSubscribe<SavedPlace>() {
                        @Override
                        public void call(Subscriber<? super SavedPlace> subscriber) {
                            SavedPlace savedPlace = new SavedPlace();
                            savedPlace.setPlaceId(LocationSettings.getPlaceId());
                            try {
                                subscriber.onNext(savedPlaceApi.insert(savedPlace).execute());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).subscribeOn(Schedulers.io()).subscribe());
                }
                return true;
            case R.id.action_current_location:
                LocationSettings.setMode(LocationSettings.Mode.CURRENT);
                onResume();
                return true;
            case R.id.action_change_location:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            ScrollListener scrollListener = (ScrollListener) fragment;
            scrollListeners.add(scrollListener);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
        for (ScrollListener scrollListener : scrollListeners)
            scrollListener.onOtherScrollChanged(scrollPosition);
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    public interface ScrollListener {
        void onOtherScrollChanged(int otherScrollAmount);
    }

    @Override
    protected void sendDataToFragments(String key, Object data) {
        super.sendDataToFragments(key, data);

        if (Forecast.Response.class.isInstance(data)) {
            Forecast.Response forecast = (Forecast.Response) data;

            DecimalFormat tf = ForecastTools.getTempForm();
            Forecast.DataPoint currently = forecast.getCurrently();
            TextView temp = (TextView) findViewById(R.id.temp);
            TextView status = (TextView) findViewById(R.id.current_condition);
            TextView feelsLikes = (TextView) findViewById(R.id.feels_like);

            temp.setText(tf.format(forecast.getCurrently().getTemperature()));
            status.setText(forecast.getCurrently().getSummary());

            if (Math.round(currently.getTemperature()) != Math.round(currently.getApparentTemperature())) {
                feelsLikes.setVisibility(View.VISIBLE);
                //((LinearLayout) findViewById(R.placeId.temp_holder)).setBaselineAlignedChildIndex(1);
                feelsLikes.setText(String.format("%s %s", getString(R.string.feels_like),
                        tf.format(forecast.getCurrently().getApparentTemperature())));
            } else {
                feelsLikes.setVisibility(View.GONE);
                //((LinearLayout) findViewById(R.placeId.temp_holder)).setBaselineAlignedChildIndex(0);
            }

            Observable<Image> imageObservable =  Observable.create(new Observable.OnSubscribe<Image>() {
                @Override
                public void call(Subscriber<? super Image> subscriber) {
                    try {
                        subscriber.onNext(imagesApi.getImage(forecast.getCurrently().getIcon()).execute());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Observable<Keys> keysObservable = getApp().getNetworkComponent().keys();
            Instagram.Service instagramService = getApp().getNetworkComponent().instagramService();
            Observable.zip(keysObservable, imageObservable, (keys, imageData) ->
                    instagramService.getMedia(keys.getInstagramAPIKey(), imageData.getShortcode()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            .subscribe(instagramData -> {
                TextView instagramUserView = (TextView)
                        instagramAttribution.findViewById(R.id.user);
                String url = instagramData.getData()
                        .getImages().getStandardResolution().getUrl();
                String username = instagramData.getData()
                        .getUser().getUsername();
                Picasso.with(photo.getContext()).load(url).into(photo);
                instagramUserView.setText(username);
                instagramAttribution.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(instagramData.getData().getLink()));
                        startActivity(browserIntent);
                    }
                });
            });
        }
    }
}