package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        ViewPager viewPager;
        SlidingTabLayout slidingTabLayout;
        TabAdapter tabAdapter;

        super.onCreate(savedInstanceState);

        SavedPlaceApi savedPlaceApi = getApp().getNetworkComponent().savedPlaceApi();

        setContentView(R.layout.activity_search);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        tabAdapter = new TabAdapter(getSupportFragmentManager(),
                SavedPlacesTab.newInstance(getString(R.string.saved), R.layout.tab_saved),
                SearchTab.newInstance(getString(R.string.search), R.layout.tab_search));
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setCustomTabView(R.layout.tab_title, R.id.title);
        slidingTabLayout.setSelectedIndicatorColors(0xFF000000);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.SimpleTabColorizer(
                getResources().getColor(R.color.text_primary),
                getResources().getColor(R.color.text_primary),
                getResources().getColor(R.color.text_secondary),
                getResources().getColor(R.color.text_primary)
        ));
        slidingTabLayout.setViewPager(viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        Observable.create(subscriber -> {
            try {
                subscriber.onNext(savedPlaceApi.list().execute().getItems());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    sendDataToFragments(result);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
