package com.shawnaten.tools;

import android.app.ActionBar;
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
    public static final int SEARCH = 0, LOAD = 1;

    private ActionBar ab;
    private FragmentManager fm;
    private ArrayMap<Integer, Boolean> states;
    private boolean childFragState;
    private int actionBarIndex;

    public StatusAnimations(ActionBar ab, FragmentManager fm, ArrayMap<Integer, Boolean> states) {
        this.ab = ab;
        this.fm = fm;
        if (states != null)
            this.states = states;
        else
            this.states = new ArrayMap<>();
    }

    public void setChildFragState(boolean state) {
        this.childFragState = state;
    }

    public void changeState(int name, boolean state) {
        switch (name) {
            case SEARCH:
                states.put(SEARCH, state);
                // TODO these shouldn't be hardcoded in
                setState("searching", state);
                break;
            case LOAD:
                states.put(LOAD, state);
                if (state) {
                    actionBarIndex = ab.getSelectedNavigationIndex();
                    ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                } else {
                    ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                    ab.setSelectedNavigationItem(actionBarIndex);
                }
                // TODO these shouldn't be hardcoded in
                setState("loading", state);
                break;
        }
    }

    public Boolean checkState(int name) {
        return  states.get(name);
    }

    private void setState(String tag, boolean state) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment toDetach, toAttach;

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

}
