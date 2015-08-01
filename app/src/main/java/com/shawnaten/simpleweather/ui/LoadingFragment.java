package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.shawnaten.simpleweather.R;

public class LoadingFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_loading, container, false);

        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            View statusBar = root.findViewById(R.id.status_bar);
            ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
            FrameLayout.LayoutParams layoutParams;

            layoutParams = (FrameLayout.LayoutParams) statusBar.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(resourceId);
            statusBar.setLayoutParams(layoutParams);

            progressBar.getIndeterminateDrawable()
                    .setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        return root;
    }
}
