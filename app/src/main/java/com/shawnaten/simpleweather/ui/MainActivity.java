package com.shawnaten.simpleweather.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.instabug.library.Instabug;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.gcmAPI.GcmAPI;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;
import com.shawnaten.simpleweather.backend.imagesApi.model.Image;
import com.shawnaten.simpleweather.backend.prefsAPI.PrefsAPI;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.lib.model.APIKeys;
import com.shawnaten.simpleweather.lib.model.Forecast;
import com.shawnaten.simpleweather.module.ImagesApiModule;
import com.shawnaten.simpleweather.services.GCMNotificationService;
import com.shawnaten.simpleweather.tools.AnalyticsCodes;
import com.shawnaten.simpleweather.tools.Attributions;
import com.shawnaten.simpleweather.tools.ForecastIconSelector;
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
import java.util.Map;

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

    @Inject Tracker tracker;

    @Inject SharedPreferences prefs;

    @Inject ReactiveLocationProvider locationProvider;

    @Inject GoogleApiClient googleApiClient;

    @Inject Forecast.Service forecastService;
    @Inject Instagram.Service instagramService;

    @Inject ImagesApi imagesApi;
    @Inject SavedPlaceApi savedPlaceApi;
    @Inject GcmAPI gcmAPI;
    @Inject PrefsAPI prefsAPI;

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

        getApp().getMainComponent().inject(this);

        if (savedInstanceState != null)
            scrollPos = savedInstanceState.getInt(SCROLL_POSITION);

        photoContainer.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, screenWidth));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        LocalizationSettings.configure(this, prefsAPI, gcmAPI);

        Subscriber<Forecast.Response> notifySub = new Subscriber<Forecast.Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Forecast.Response response) {
                if (response != null && response.getMinutely() != null) {
                    NotificationManager manager = (NotificationManager) getApplicationContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addParentStack(SettingsActivity.class);
                    stackBuilder.addNextIntent(settingsIntent);

                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            getBaseContext(),
                            0,
                            settingsIntent,
                            PendingIntent.FLAG_ONE_SHOT
                    );

                    NotificationCompat.Builder builder = new NotificationCompat
                            .Builder(getApplicationContext())
                                .setSmallIcon(ForecastIconSelector.getNotifyIcon("rain"))
                                .setContentTitle(getString(R.string.message_8_23_15_title))
                                .setContentText(getString(R.string.message_8_23_15_content))
                                .setContentIntent(pendingIntent)
                                .setSound(soundUri)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setAutoCancel(true);

                    manager.notify(GCMNotificationService.getID(), builder.build());
                }

                prefs.edit().putBoolean("message_8_23_15", true).apply();
            }
        };

        if (!prefs.getBoolean("message_8_23_15", false)) {
            subs.add(locationProvider.getLastKnownLocation()
                    .flatMap(new Func1<Location, Observable<Forecast.Response>>() {
                        @Override
                        public Observable<Forecast.Response> call(Location location) {
                            if (location == null) {
                                return null;
                            } else {
                                return forecastService.notifyCheckVersion(
                                        APIKeys.FORECAST,
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        LocalizationSettings.getLangCode(),
                                        LocalizationSettings.getUnitCode()
                                );
                            }
                        }
                    }).subscribe(notifySub));
        }

        final long startLoad = System.currentTimeMillis();

        Observable<Forecast.Response> forecastObservable;

        if (LocationSettings.getMode() == LocationSettings.Mode.CURRENT) {
            final Observable<Location> locationObservable = locationProvider.getLastKnownLocation();

            final LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setNumUpdates(5)
                    .setInterval(100);

            forecastObservable = locationProvider
                    .checkLocationSettings(
                            new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest)
                            .setAlwaysShow(true)
                            .build()
                    )
                    .doOnNext(new Action1<LocationSettingsResult>() {
                        @Override
                        public void call(LocationSettingsResult locationSettingsResult) {
                            Status status = locationSettingsResult.getStatus();
                            if (status.getStatusCode() != LocationSettingsStatusCodes.SUCCESS) {
                                Intent intent;
                                intent = new Intent(getApplicationContext(), SearchActivity.class);
                                startActivity(intent);
                            }
                        }
                    })
                    .flatMap(new Func1<LocationSettingsResult, Observable<Location>>() {
                        @Override
                        public Observable<Location> call(LocationSettingsResult locationSettingsResult) {
                            return locationProvider.getLastKnownLocation();
                        }
                    })
                    .flatMap(new Func1<Location, Observable<Forecast.Response>>() {
                        @Override
                        public Observable<Forecast.Response> call(Location location) {
                            return forecastService.getForecast(
                                    APIKeys.FORECAST,
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
                    .subscribe(new Subscriber<List<Address>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<Address> addresses) {
                            locationView.setText(addresses.get(0).getLocality());
                        }
                    });
        } else {

            forecastObservable = forecastService.getForecast(
                    APIKeys.FORECAST,
                    LocationSettings.getLat(),
                    LocationSettings.getLng(),
                    LocalizationSettings.getLangCode(),
                    LocalizationSettings.getUnitCode()
            ).observeOn(AndroidSchedulers.mainThread());

            locationView.setText(LocationSettings.getName());
        }

        Subscriber<Forecast.Response> subscriber = new Subscriber<Forecast.Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Forecast.Response response) {
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

                        Map<String, String> hit = new HitBuilders.TimingBuilder()
                                .setCategory(AnalyticsCodes.CATEGORY_FULL_LOAD)
                                .setValue(System.currentTimeMillis() - startLoad)
                                .build();

                        tracker.send(hit);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                ImagesApiModule
                        .getImage(imagesApi, category)
                        .flatMap(new Func1<Image, Observable<Instagram.Response>>() {
                            @Override
                            public Observable<Instagram.Response> call(Image image) {
                                return instagramService.getMedia(
                                        APIKeys.INSTAGRAM,
                                        image.getShortcode()
                                );
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Instagram.Response>() {
                            @Override
                            public void call(Instagram.Response res) {
                                Instagram.MediaData data = res.getData();
                                Instagram.MediaData.Images images = data.getImages();

                                String url = images.getStandardResolution().getUrl();
                                String post = data.getLink();
                                String user = data.getUser().getUsername();

                                Attributions.setInstagramUser(user);
                                Attributions.setInstagramUrl(post);

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
        };

        subs.add(forecastObservable.subscribe(subscriber));

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
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    LocationSettings.setIsFavorite(false);

                    subs.add(Observable.create(new Observable.OnSubscribe<Void>() {
                        @Override
                        public void call(Subscriber<? super Void> subscriber) {
                            try {
                                savedPlaceApi.delete(LocationSettings.getSavedPlace()).execute();
                                subscriber.onCompleted();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).subscribeOn(Schedulers.io()).subscribe());
                } else {
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    LocationSettings.setIsFavorite(true);

                    Observable.OnSubscribe<Void> onSubscribe;
                    onSubscribe = new Observable.OnSubscribe<Void>() {
                        @Override
                        public void call(Subscriber<? super Void> subscriber) {
                            try {
                                savedPlaceApi.insert(LocationSettings.getSavedPlace()).execute();
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
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_feedback:
                Instabug.getInstance().invoke();
                return true;
            /*case R.id.action_ad:
                startActivity(new Intent(this, AdActivity.class));
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
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