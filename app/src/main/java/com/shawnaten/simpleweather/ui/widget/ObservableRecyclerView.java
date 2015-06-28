/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shawnaten.simpleweather.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * A custom ScrollView that can accept a scroll listener.
 */
public class ObservableRecyclerView extends RecyclerView implements ScrollCallbacks {
    private ArrayList<ScrollCallbacks> mCallbacks = new ArrayList<>();

    public ObservableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (ScrollCallbacks c : mCallbacks) {
            c.onScrollChanged(l - oldl, t - oldt);
        }
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {

    }

    @Override
    public void addCallbacks(ScrollCallbacks listener) {
        if (!mCallbacks.contains(listener)) {
            mCallbacks.add(listener);
        }
    }
}