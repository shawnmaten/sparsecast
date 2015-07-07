package com.shawnaten.simpleweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.ui.widget.ObservableScrollView;
import com.shawnaten.simpleweather.ui.widget.ScrollCallbacks;
import com.shawnaten.tools.Forecast;
import com.shawnaten.tools.LocationSettings;

public class Tab extends BaseFragment implements ScrollCallbacks,
        MainActivity.ScrollListener, MainActivity.FragmentDataListener {
    public static final String TAB_LAYOUT = "tabLayout";

    private View toolbar;
    private View photoContainer;
    private View photo;
    private ObservableScrollView scroll;
    private View content;

    private int screenWidth;

    public static Tab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        Tab tab = new Tab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tab, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(getArguments().getInt(TAB_LAYOUT), container, false);

        toolbar = getBaseActivity().findViewById(R.id.toolbar);
        photoContainer = getBaseActivity().findViewById(R.id.photo_container);
        photo = getBaseActivity().findViewById(R.id.photo);
        scroll = (ObservableScrollView) root.findViewById(R.id.scroll);

        if (scroll != null) {
            scroll.addCallbacks(this);
            scroll.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                    onOtherScrollChanged(((MainActivity) getBaseActivity()).getScrollPosition()));
        }

        if ((content = root.findViewById(R.id.content)) != null) {
            content.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                View space = content.findViewById(R.id.top_space);
                ViewGroup.LayoutParams layoutParams = space.getLayoutParams();
                layoutParams.height = screenWidth;
                space.setLayoutParams(layoutParams);

                space = content.findViewById(R.id.bottom_space);
                layoutParams = space.getLayoutParams();
                int minContentHeight = screenWidth / 2 + getResources()
                        .getDimensionPixelSize(R.dimen.header_space);
                int bottomSpaceHeight = Math.max(0, minContentHeight - content.getHeight());
                layoutParams.height = bottomSpaceHeight;
                space.setLayoutParams(layoutParams);
            });
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        getBaseActivity().checkForData(this);
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        onScrollChangedHelper(0);
    }

    private void onScrollChangedHelper(int headerHeightDelta) {
        int minPhotoHeight = getResources().getDisplayMetrics().widthPixels / 2
                + getResources().getDimensionPixelSize(R.dimen.header_space);
        float density = getResources().getDisplayMetrics().density;
        int scrollAmount = Math.min(screenWidth - minPhotoHeight, scroll.getScrollY());
        float scrollPercent = scrollAmount / (float) (screenWidth - minPhotoHeight);
        float elevation = scrollPercent * 4 * density;

        photoContainer.setY(-scrollAmount);
        toolbar.setElevation(elevation);
        photoContainer.setElevation(elevation);
        photo.setTranslationY(scrollAmount * 0.5f);

        ((MainActivity) getBaseActivity()).setScrollPosition(scrollAmount);
    }

    @Override
    public void addCallbacks(ScrollCallbacks callbacks) {

    }

    @Override
    public void onOtherScrollChanged(int otherScrollAmount) {
        if (!getUserVisibleHint()) {
            int minPhotoHeight = getResources().getDisplayMetrics().widthPixels / 2
                    + getResources().getDimensionPixelSize(R.dimen.header_space);
            if (scroll != null) {
                int thisScrollAmount = Math.min(screenWidth - minPhotoHeight, scroll.getScrollY());
                if (otherScrollAmount != thisScrollAmount)
                    scroll.setScrollY(otherScrollAmount);
            }
        }
    }

    @Override
    public void onNewData(Object data) {

        if (scroll != null && getUserVisibleHint() && Forecast.Response.class.isInstance(data)) {
            scroll.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            scroll.smoothScrollTo(0, 0);
                            scroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
        }
    }

}
