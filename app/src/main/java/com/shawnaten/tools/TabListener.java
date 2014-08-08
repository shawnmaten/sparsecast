package com.shawnaten.tools;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;

public class TabListener implements ActionBar.TabListener {
	private Fragment fragment;
    private Boolean enabled = false;
	
	public TabListener(Fragment fragment) {
        this.fragment = fragment;
	}

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // do nothing
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (enabled) {
            fragment.getFragmentManager().beginTransaction()
                    .attach(fragment)
                    .commit();
        }
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (enabled) {
            fragment.getFragmentManager().beginTransaction()
                    .detach(fragment)
                    .commit();
        }
	}

}
