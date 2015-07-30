package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.SavedPlaceApi;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        ViewPager viewPager;
        SlidingTabLayout slidingTabLayout;
        TabAdapter tabAdapter;

        super.onCreate(savedInstanceState);

        final SavedPlaceApi savedPlaceApi = getApp().mainComponent.savedPlaceApi();

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
                getResources().getColor(R.color.text_primary_light),
                getResources().getColor(R.color.text_primary_light),
                getResources().getColor(R.color.text_secondary_light),
                getResources().getColor(R.color.text_primary_light)
        ));
        slidingTabLayout.setViewPager(viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    subscriber.onNext(savedPlaceApi.list().execute().getItems());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Object>() {
                            @Override
                            public void call(Object result) {
                                SearchActivity.this.sendDataToFragments(result);
                            }
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
