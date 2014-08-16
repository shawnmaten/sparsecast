package com.shawnaten.tools;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.shawnaten.simpleweather.R;

public class ActionBarListener implements ActionBar.OnNavigationListener {
    private Boolean enabled = false;
    private FragmentActivity activity;
    private String[] fragments;
	
	public ActionBarListener(FragmentActivity activity) {
        fragments = activity.getResources().getStringArray(R.array.main_fragments);
        this.activity = activity;
	}

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (enabled) {
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft
                    .detach(fm.findFragmentById(R.id.main_fragment))
                    .attach(fm.findFragmentByTag(fragments[itemPosition]))
                    .commit();
            return true;
        }
        return false;
    }

    /*
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
	*/

}
