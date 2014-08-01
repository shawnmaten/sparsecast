package com.shawnaten.tools;

import android.view.View;

import com.shawnaten.networking.Forecast;

/**
 * Created by shawnaten on 7/11/14.
 */
public interface FragmentListener {
    public void onNewData(Forecast.Response data);
    public void onButtonClick(View view);
}
