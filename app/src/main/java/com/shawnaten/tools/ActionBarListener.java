package com.shawnaten.tools;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.shawnaten.simpleweather.R;

public class ActionBarListener implements ActionBar.OnNavigationListener {
    private FragmentManager fm;
    private String[] fragments;
    private boolean enabled = false;
	
	public ActionBarListener(FragmentManager fm, String[] fragments) {
        this.fm = fm;
        this.fragments = fragments;
	}

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (enabled) {
            Fragment toDetach, toAttach;
            toDetach = fm.findFragmentById(R.id.main_fragment);
            toAttach = fm.findFragmentByTag(fragments[itemPosition]);
            if (!toDetach.equals(toAttach)) {
                fm.beginTransaction()
                        .detach(toDetach)
                        .attach(toAttach)
                        .commit();
            }
            return true;
        }
        return false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
