package com.shawnaten.simpleweather.ui.widget;

public interface ScrollCallbacks {
    void onScrollChanged(int deltaX, int deltaY);
    void addCallbacks(ScrollCallbacks callbacks);
}
