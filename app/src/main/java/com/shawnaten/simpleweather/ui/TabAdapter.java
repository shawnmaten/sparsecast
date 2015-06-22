package com.shawnaten.simpleweather.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;
import android.view.View;

public class TabAdapter extends FragmentPagerAdapter implements View.OnTouchListener {
    public static final String TAB_TITLE = "tab_title_light";

    private Fragment tabs[];

    public TabAdapter(FragmentManager fragmentManager, Fragment... tabs) {
        super(fragmentManager);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position].getArguments().getString(TAB_TITLE);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
