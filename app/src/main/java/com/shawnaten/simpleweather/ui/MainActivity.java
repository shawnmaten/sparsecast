package com.shawnaten.simpleweather.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.module.ImagesApiModule;
import com.shawnaten.simpleweather.tools.ForecastTools;
import com.shawnaten.simpleweather.tools.Instagram;
import com.shawnaten.simpleweather.tools.LocalizationSettings;
import com.shawnaten.simpleweather.tools.LocationSettings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    public static final int PLACE_SEARCH_CODE = RESULT_FIRST_USER + 1;
    public static final int PLACE_SELECTED_CODE = RESULT_FIRST_USER + 2;

    private static final String SCROLL_POSITION = "scrollPos";
    private static final String FORECAST_DATA = "forecastData";

    @Inject ReactiveLocationProvider locationProvider;

    @Inject GoogleApiClient googleApiClient;

    @Inject Forecast.Service forecastService;
    @Inject Instagram.Service instagramService;

    @Inject ImagesApi imagesApi;
    @Inject SavedPlaceApi savedPlaceApi;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.overlay) View overlay;
    @Bind(R.id.photo) ImageView photo;

    @Bind(R.id.temp) TextView temp;
    @Bind(R.id.main_loc) TextView locationView;
    @Bind(R.id.current_condition) TextView status;
    @Bind(R.id.feels_like) TextView feelsLikes;

    private int scrollPos;

    private ArrayList<ScrollListener> scrollListeners = new ArrayList<>();

    private View decorView;

    private float statBarSize;

    private Window window;

    private Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();

        int screenWidth = res.getDisplayMetrics().widthPixels;
        statBarSize = res.getDimension(res.getIdentifier("status_bar_height", "dimen", "android"));

        window = getWindow();
        decorView = window.getDecorView();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        ViewPager viewPager = ButterKnife.findById(this, R.id.view_pager);
        SlidingTabLayout slidingTabLayout = ButterKnife.findById(this, R.id.sliding_tabs);
        View photoContainer = ButterKnife.findById(this, R.id.photo_container);

        getApp().getMainComponent().injectMainActivity(this);

        if (savedInstanceState != null)
            scrollPos = savedInstanceState.getInt(SCROLL_POSITION);

        photoContainer.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, screenWidth));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        TabAdapter tabAdapter = new TabAdapter(
                getSupportFragmentManager(),
                Next24HoursTab.create(
                        getString(R.string.next_24_hours),
                        R.layout.tab_next_24_hours
                ),
                Next7DaysTab.create(
                        getString(R.string.next_7_days),
                        R.layout.tab_next_7_days
                )
        );

        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setCustomTabView(R.layout.tab_title_light, R.id.title);
        slidingTabLayout.setSelectedIndicatorColors(0xFFFFFFFF);
        slidingTabLayout.setCustomTabColorizer(
                new SlidingTabLayout.SimpleTabColorizer(
                        getResources().getColor(R.color.text_primary_light),
                        getResources().getColor(R.color.text_primary_light),
                        getResources().getColor(R.color.text_secondary_light),
                        getResources().getColor(R.color.text_primary_light)
                )
        );
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Observable<Forecast.Response> forecastObservable;

        if (LocationSettings.getMode() == LocationSettings.Mode.CURRENT) {
            Observable<Location> locationObservable = locationProvider.getLastKnownLocation();

            forecastObservable = locationObservable
                    .flatMap(new Func1<Location, Observable<Forecast.Response>>() {
                        @Override
                        public Observable<Forecast.Response> call(Location location) {
                            return forecastService.getForecast(
                                    APIKeys.FORECAST_API_KEY,
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    LocalizationSettings.getLangCode(),
                                    LocalizationSettings.getUnitCode()
                            );
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread());

            locationObservable
                    .flatMap(new Func1<Location, Observable<List<Address>>>() {
                        @Override
                        public Observable<List<Address>> call(Location location) {
                            return locationProvider.getReverseGeocodeObservable(
                                    location.getLatitude(), location.getLongitude(), 1);
                        }
                    })
                    .subscribe(new Action1<List<Address>>() {
                        @Override
                        public void call(List<Address> addresses) {
                            locationView.setText(addresses.get(0).getLocality());
                        }
                    });
        } else {

            forecastObservable = forecastService.getForecast(
                    APIKeys.FORECAST_API_KEY,
                    LocationSettings.getLatLng().latitude,
                    LocationSettings.getLatLng().longitude,
                    LocalizationSettings.getLangCode(),
                    LocalizationSettings.getUnitCode()
            ).observeOn(AndroidSchedulers.mainThread());

            locationView.setText(LocationSettings.getName());
        }

        forecastObservable
                .subscribe(new Action1<Forecast.Response>() {
                    @Override
                    public void call(Forecast.Response response) {
                        sendDataToFragments(FORECAST_DATA, response);

                        String category = response.getCurrently().getIcon();

                        target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                Palette palette = Palette.from(bitmap).generate();
                                int color = palette.getMutedColor(0x000000);

                                color = Color.argb(
                                        102,
                                        Color.red(color),
                                        Color.green(color),
                                        Color.blue(color)
                                );

                                photo.setImageBitmap(bitmap);
                                overlay.setBackgroundColor(color);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };

                        ImagesApiModule
                                .getImage(imagesApi, instagramService, category)
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String url) {
                                        Picasso.with(photo.getContext()).load(url).into(target);
                                    }
                                });

                        DecimalFormat tf = ForecastTools.getTempForm();

                        temp.setText(tf.format(response.getCurrently().getTemperature()));
                        status.setText(response.getCurrently().getSummary());

                        String feelsLikeText;

                        feelsLikeText = String.format("%s %s", getString(R.string.feels_like),
                                tf.format(response.getCurrently().getApparentTemperature()));

                        feelsLikes.setText(feelsLikeText);

                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColor(0x00000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        toolbar.setTranslationY(statBarSize);

        invalidateOptionsMenu();
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
            if (LocationSettings.isFavorite())
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
                if (LocationSettings.isFavorite()) {
                    final SavedPlace savedPlace = new SavedPlace();
                    savedPlace.setPlaceId(LocationSettings.getPlaceId());
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    LocationSettings.setIsFavorite(false);
                    subs.add(Observable.create(new Observable.OnSubscribe<Void>() {
                        @Override
                        public void call(Subscriber<? super Void> subscriber) {
                            try {
                                subscriber.onNext(savedPlaceApi
                                        .delete(savedPlace).execute());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).subscribeOn(Schedulers.io()).subscribe());

                } else {
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    LocationSettings.setIsFavorite(true);

                    final SavedPlace savedPlace = new SavedPlace();
                    savedPlace.setPlaceId(LocationSettings.getPlaceId());
                    savedPlace.setName(LocationSettings.getName());
                    savedPlace.setLat(LocationSettings.getLatLng().latitude);
                    savedPlace.setLng(LocationSettings.getLatLng().longitude);

                    Observable.OnSubscribe<Void> onSubscribe;
                    onSubscribe = new Observable.OnSubscribe<Void>() {
                        @Override
                        public void call(Subscriber<? super Void> subscriber) {
                            try {
                                savedPlaceApi.insert(savedPlace).execute();
                                subscriber.onCompleted();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    subs.add(Observable.create(onSubscribe)
                            .subscribeOn(Schedulers.io())
                            .subscribe());
                }
                return true;
            case R.id.action_current_location:
                LocationSettings.setMode(LocationSettings.Mode.CURRENT);
                onResume();
                return true;
            case R.id.action_change_location:
                startActivityForResult(new Intent(this, SearchActivity.class), PLACE_SEARCH_CODE);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_ad:
                startActivity(new Intent(this, AdActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (ScrollListener.class.isInstance(fragment))
            scrollListeners.add((ScrollListener) fragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SCROLL_POSITION, scrollPos);
    }

    public void setScrollPos(int scrollPos) {
        this.scrollPos = scrollPos;
        for (ScrollListener scrollListener : scrollListeners)
            scrollListener.onOtherScrollChanged(scrollPos);
    }

    public interface ScrollListener {
        void onOtherScrollChanged(int otherScrollAmount);
    }
}