package com.shawnaten.tools;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

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
            Log.e("onNavigation", String.format("toDetach: %s, toAttach %s", toDetach.getTag(), toAttach.getTag()));
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

}
