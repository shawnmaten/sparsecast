package com.shawnaten.tools;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;

/**
 * Sparsecast for Android.
 *
 * File created by Shawn Aten on 2014/12/19.
 */

/**
 * This class handles the logic of hiding and showing fragments based on "animation states",
 * currently this encompasses, loading, searching, and normal view.
 */
public class StatusAnimations {
    // TODO these shouldn't be hardcoded in
    public static final String SEARCH = "searching", LOAD = "loading";

    private ActionBar ab;
    private FragmentManager fm;
    private ArrayMap<String, Boolean> states;
    private boolean childFragState;
    private int actionBarIndex;

    public StatusAnimations(ActionBar ab, FragmentManager fm, Bundle savedState) {
        this.ab = ab;
        this.fm = fm;
        states = new ArrayMap<>();
        if (savedState == null) {
            states.put(SEARCH, false);
            states.put(LOAD, false);
        } else {
            states.put(SEARCH, savedState.getBoolean(SEARCH));
            states.put(LOAD, savedState.getBoolean(LOAD));
        }
    }

    public void setChildFragState(boolean state) {
        this.childFragState = state;
    }

    public void changeState(String name, boolean state) {
        /*
        switch (name) {
            case SEARCH:
                if (state != states.get(SEARCH)) {
                    states.put(SEARCH, state);
                    setState(SEARCH, state);
                }
                break;
            case LOAD:
                if (state != states.get(LOAD)) {
                    states.put(LOAD, state);
                    if (state) {
                        actionBarIndex = ab.getSelectedNavigationIndex();
                        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    } else {
                        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                        ab.setSelectedNavigationItem(actionBarIndex);
                    }
                    setState(LOAD, state);
                }
                break;
        }
        */
    }

    public Boolean checkState(String name) {
        return  states.get(name);
    }

    private void setState(String tag, boolean state) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment toDetach, toAttach;

        //Log.d("dew", "setState");

        if (childFragState) {
            if (state) {
                toAttach = fm.findFragmentByTag(tag);
                if (toAttach.isDetached()) {
                    ft.attach(toAttach).commit();
                }
            } else {
                toDetach = fm.findFragmentByTag(tag);
                ft.detach(toDetach);

                ft.commit();
            }
        }
    }

    public Bundle saveState() {
        Bundle outState = new Bundle();
        outState.putBoolean(SEARCH, states.get(SEARCH));
        outState.putBoolean(LOAD, states.get(LOAD));
        return outState;
    }

}
