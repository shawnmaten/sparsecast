package com.shawnaten.tools;

import com.shawnaten.networking.Forecast;

/**
 * Created by shawnaten on 7/11/14.
 */
public interface FragmentListener {
    public void onReceiveData(Forecast.Response data);
    public void onButtonClick(int id);
}
