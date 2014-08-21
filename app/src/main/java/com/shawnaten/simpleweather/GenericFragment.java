package com.shawnaten.simpleweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnaten.tools.FragmentListener;

/**
 * Created by Shawn Aten on 8/6/14.
 */
public class GenericFragment extends Fragment implements FragmentListener {
    public static final String LAYOUT = "layout";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getArguments().getInt(LAYOUT), container, false);
    }

    @Override
    public void onNewData() {

    }
}
