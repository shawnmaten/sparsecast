package com.shawnaten.simpleweather.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;
import com.shawnaten.simpleweather.backend.imagesApi.model.Image;
import com.shawnaten.simpleweather.backend.keysEndpoint.model.Keys;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.simpleweather.module.LocationModule;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.Geocoding;
import com.shawnaten.tools.Instagram;
import com.shawnaten.tools.LocalizationSettings;
import com.shawnaten.tools.LocationSettings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    public static final int PLACE_SEARCH_CODE = RESULT_FIRST_USER + 1;
    public static final int PLACE_SELECTED_CODE = RESULT_FIRST_USER + 2;

    private static final String SCROLL_POSITION = "scrollPosition";
    private static final String FORECAST_DATA = "forecastData";
    @Inject Observable<Forecast.Response> forecast;
    @Inject Observable<Location> locObser;
    @Inject ImagesApi imagesApi;
    @Inject
    GoogleApiClient googleApiClient;
    @Inject
    Forecast.Service forecastService;
    private int scrollPosition;
    private ImageView photo;
    private View overlay;
    private View instagramAttribution;
    //private FloatingActionMenu fam;
    //private ArrayList<FloatingActionButton> fabs;
    private SavedPlaceApi savedPlaceApi;
    private ArrayList<ScrollListener> scrollListeners = new ArrayList<>();
    @Inject
    Observable<Keys> keyObser;

    @Inject
    Geocoding.Service geocodingService;

    private LoadingFragment loadingFragment;

    private Target target;

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
        View photoContainer;
        int screenWidth;
        float density;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApp().mainComponent.injectMainActivity(this);
        savedPlaceApi = getApp().mainComponent.savedPlaceApi();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        photoContainer = findViewById(R.id.photo_container);
        photo = (ImageView) findViewById(R.id.photo);
        overlay = findViewById(R.id.overlay);
        /*
        fam = (FloatingActionMenu) findViewById(R.id.fam);
        fam.hideMenuButton(false);
        fabs = new ArrayList<>();
        */
        loadingFragment = new LoadingFragment();

        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
        }

        instagramAttribution = getLayoutInflater()
                .inflate(R.layout.instagram_attribution, toolbar, false);
        toolbar.addView(instagramAttribution);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        density = getResources().getDisplayMetrics().density;
        photoContainer.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, screenWidth));
        //fam.setY(screenWidth - 28 * density);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        tabAdapter = new TabAdapter(getSupportFragmentManager(),
                Next24HoursTab.newInstance(getString(R.string.next_24_hours), R.layout.tab_next_24_hours),
                Next7DaysTab.newInstance(getString(R.string.next_7_days), R.layout.tab_next_7_days));
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
    }

    @Override
    protected void onResume() {
        View decorView;
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .add(R.id.root, loadingFragment)
                .commit();

        super.onResume();

        invalidateOptionsMenu();

        decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(0x00000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        int resultCode;
        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            resultCode = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.toolbar).setTranslationY(resultCode);
        }

        if (LocationSettings.getMode() == LocationSettings.Mode.CURRENT) {
            Func2<Location, Keys, Forecast.Response> function;
            Subscriber<Forecast.Response> subscriber;
            final Subscription subscription;
            final PendingResult<PlaceLikelihoodBuffer> result;

            result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

            function = new Func2<Location, Keys, Forecast.Response>() {
                @Override
                public Forecast.Response call(Location l, Keys k) {
                    Geocoding.Response response;
                    ResultCallback<PlaceLikelihoodBuffer> callback;
                    final Geocoding.Result add;
                    final LatLng actLoc, addLoc;
                    final double addDist;

                    response = geocodingService.getAddresses(
                            k.getGoogleAPIKey(),
                            String.format("%f,%f", l.getLatitude(), l.getLongitude())
                    );

                    add = response.getResults()[0];

                    actLoc = new LatLng(l.getLatitude(), l.getLongitude());
                    addLoc = new LatLng(
                            add.getGeometry().getLocation().getLat(),
                            add.getGeometry().getLocation().getLng()
                    );

                    addDist = SphericalUtil.computeDistanceBetween(actLoc, addLoc);

                    callback = new ResultCallback<PlaceLikelihoodBuffer>() {
                        @Override
                        public void onResult(PlaceLikelihoodBuffer like) {
                            Place place;
                            double placeDist;
                            String streetNum = null, route = null, main = null, sec = null;

                            place = like.get(0).getPlace();

                            placeDist = SphericalUtil.computeDistanceBetween(
                                    actLoc,
                                    place.getLatLng()
                            );

                            for (Geocoding.AddressComponents comp : add.getAddressComponents()) {
                                if (Arrays.asList(comp.getTypes()).contains("street_number")) {
                                    streetNum = comp.getShortName();
                                }
                                if (Arrays.asList(comp.getTypes()).contains("route")) {
                                    route = comp.getShortName();
                                }
                                if (Arrays.asList(comp.getTypes()).contains("locality"))
                                    main = comp.getLongName();
                            }

                            if (placeDist < addDist) {
                                sec = place.getName().toString();
                            } else {
                                if (streetNum != null) {
                                    sec = streetNum + " " + route;
                                } else {
                                    sec = add.getAddressComponents()[0].getShortName();
                                }
                            }

                            like.release();

                            Observable.just(main)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            ((TextView) findViewById(R.id.main_loc)).setText(s);
                                        }
                                    });

                            Observable.just(sec)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            ((TextView) findViewById(R.id.sec_loc)).setText(s);
                                        }
                                    });
                        }
                    };

                    result.setResultCallback(callback);

                    return forecastService.getForecast(
                            k.getForecastAPIKey(),
                            l.getLatitude(),
                            l.getLongitude(),
                            LocalizationSettings.getLangCode(),
                            LocalizationSettings.getUnitCode()
                    );
                }
            };

            subscriber = new Subscriber<Forecast.Response>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    if (e.getMessage().equals(LocationModule.ERROR))
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    else
                        e.printStackTrace();
                }

                @Override
                public void onNext(Forecast.Response response) {
                    dataMap.put(FORECAST_DATA, response);
                    MainActivity.this.sendDataToFragments(FORECAST_DATA, response);
                }
            };

            subscription = Observable.zip(locObser, keyObser, function)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);

            subs.add(subscription);
        } else {
            subs.add(forecast.subscribe(new Action1<Forecast.Response>() {
                @Override
                public void call(Forecast.Response response) {
                    sendDataToFragments(FORECAST_DATA, response);
                    ((TextView) MainActivity.this.findViewById(R.id.main_loc))
                            .setText(LocationSettings.getName());
                    ((TextView) MainActivity.this.findViewById(R.id.sec_loc))
                            .setText(LocationSettings.getAddress());
                }
            }));
        }
    }

    @Override
    protected void onPause() {
        FragmentManager fm = getSupportFragmentManager();

        super.onPause();

        fm.beginTransaction()
                .remove(loadingFragment)
                .commit();
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
                startActivityForResult(new Intent(this, SearchActivity.class), PLACE_SEARCH_CODE);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
        for (ScrollListener scrollListener : scrollListeners)
            scrollListener.onOtherScrollChanged(scrollPosition);
    }

    @Override
    protected void sendDataToFragments(String key, Object data) {
        super.sendDataToFragments(key, data);

        if (Forecast.Response.class.isInstance(data)) {
            final Forecast.Response forecast = (Forecast.Response) data;

            DecimalFormat tf = ForecastTools.getTempForm();
            Forecast.DataPoint currently = forecast.getCurrently();
            TextView temp = (TextView) findViewById(R.id.temp);
            TextView status = (TextView) findViewById(R.id.current_condition);
            TextView feelsLikes = (TextView) findViewById(R.id.feels_like);

            temp.setText(tf.format(forecast.getCurrently().getTemperature()));
            status.setText(forecast.getCurrently().getSummary());

            feelsLikes.setText(String.format("%s %s", getString(R.string.feels_like),
                    tf.format(forecast.getCurrently().getApparentTemperature())));

            Observable<Image> imageObservable = Observable.create(new Observable.OnSubscribe<Image>() {
                @Override
                public void call(Subscriber<? super Image> subscriber) {
                    try {
                        subscriber.onNext(imagesApi.getImage(forecast.getCurrently().getIcon()).execute());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Observable<Keys> keysObservable = getApp().mainComponent.keys();
            final Instagram.Service instagramService = getApp().mainComponent.instagramService();
            Observable.zip(keysObservable, imageObservable, new Func2<Keys, Image, Instagram.SingleMediaResponse>() {
                @Override
                public Instagram.SingleMediaResponse call(Keys keys, Image imageData) {
                    return instagramService.getMedia(keys.getInstagramAPIKey(), imageData.getShortcode());
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Instagram.SingleMediaResponse>() {
                        @Override
                        public void call(final Instagram.SingleMediaResponse instagramData) {
                            TextView instagramUserView = (TextView)
                                    instagramAttribution.findViewById(R.id.user);
                            String url = instagramData.getData()
                                    .getImages().getStandardResolution().getUrl();
                            String username = instagramData.getData()
                                    .getUser().getUsername();

                            target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    FragmentManager fm = getSupportFragmentManager();

                                    photo.setImageBitmap(bitmap);
                                    Palette palette = Palette.from(bitmap).generate();
                                    int color = palette.getMutedColor(0x000000);
                                    color = Color.argb(102, Color.red(color), Color.green(color),
                                            Color.blue(color));
                                    overlay.setBackgroundColor(color);

                                    fm.beginTransaction()
                                            .remove(loadingFragment)
                                            .commitAllowingStateLoss();
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            };

                            Picasso.with(photo.getContext()).load(url).into(target);
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
                        }
                    });

        }
    }

    public interface ScrollListener {
        void onOtherScrollChanged(int otherScrollAmount);
    }
}