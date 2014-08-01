package com.shawnaten.tools;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;

public class TabListener implements ActionBar.TabListener {
	private Fragment fragment;
	
	public TabListener(Fragment fragment) {
        this.fragment = fragment;
	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// do nothing
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        fragment.getFragmentManager().beginTransaction()
            .attach(fragment)
            .commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        fragment.getFragmentManager().beginTransaction()
                .detach(fragment)
                .commit();
	}

}
