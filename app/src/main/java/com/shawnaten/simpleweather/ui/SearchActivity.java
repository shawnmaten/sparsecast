package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.Response;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.simpleweather.tools.Attributions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SearchActivity extends BaseActivity {
    @Inject SavedPlaceApi savedPlaceApi;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Toolbar toolbar;
        ViewPager viewPager;
        SlidingTabLayout slidingTabLayout;
        TabAdapter tabAdapter;

        super.onCreate(savedInstanceState);

        getApp().getMainComponent().inject(this);

        setContentView(R.layout.activity_search);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        tabAdapter = new TabAdapter(getSupportFragmentManager(),
                SavedPlacesTab.newInstance(getString(R.string.saved), R.layout.tab_saved),
                SearchTab.newInstance(getString(R.string.search), R.layout.tab_search));
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setCustomTabView(R.layout.tab_title_light, R.id.title);
        slidingTabLayout.setSelectedIndicatorColors(0xFF000000);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.SimpleTabColorizer(
                getResources().getColor(R.color.text_primary_light),
                getResources().getColor(R.color.text_primary_light),
                getResources().getColor(R.color.text_secondary_light),
                getResources().getColor(R.color.text_primary_light)
        ));
        slidingTabLayout.setViewPager(viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        Action1<Response> subscriber;
        subscriber = new Action1<Response>() {
            @Override
            public void call(Response response) {
                sendDataToFragments(response);
            }
        };

        Observable.OnSubscribe<Response> onSubscribe;
        onSubscribe = new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    ArrayList<String> attr = Attributions.getSavedPlaces();
                    attr.clear();

                    Response response = savedPlaceApi.get().execute();
                    List<SavedPlace> places = response.getData();

                    if (places != null) {
                        for (SavedPlace place : places)
                            if (place.getAttributions() != null
                                    && !attr.contains(place.getAttributions()))
                                attr.add(place.getAttributions());
                    }

                    subscriber.onNext(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Observable.create(onSubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }
}
