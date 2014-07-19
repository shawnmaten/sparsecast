package com.shawnaten.weather;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class TabListener implements ActionBar.TabListener {
	private Fragment fragment;
	
	public TabListener(Fragment fragment) {
        this.fragment = fragment;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// do nothing
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
        assert fragment != null;
        ft.attach(fragment);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.detach(fragment);
	}

}
