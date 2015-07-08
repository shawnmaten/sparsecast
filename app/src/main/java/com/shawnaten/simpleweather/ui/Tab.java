package com.shawnaten.simpleweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
    protected int screenWidth;
    protected int screenHeight;
    private View toolbar;
    private View photoContainer;
    private View photo;
    private ObservableScrollView scroll;
    private View content;
    private View header;

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
        screenHeight = getResources().getDisplayMetrics().heightPixels;
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
        header = getBaseActivity().findViewById(R.id.header);

        if (scroll != null) {
            scroll.addCallbacks(this);
            scroll.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                    onOtherScrollChanged(((MainActivity) getBaseActivity()).getScrollPosition()));
        }

        if ((content = root.findViewById(R.id.content)) != null) {
            View topSpace = content.findViewById(R.id.top_space);
            ViewGroup.LayoutParams topParams = topSpace.getLayoutParams();
            topParams.height = screenWidth;
            topSpace.setLayoutParams(topParams);

            content.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                View bottomSpace = content.findViewById(R.id.bottom_space);
                ViewGroup.LayoutParams bottomParams = bottomSpace.getLayoutParams();
                int newBottomHeight = Math.max(0, screenHeight - getMinPhotoHeight()
                        - (content.getHeight() - topParams.height - bottomParams.height));
                if (bottomParams.height != newBottomHeight) {
                    bottomParams.height = newBottomHeight;
                    bottomSpace.setLayoutParams(bottomParams);
                }
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
        float density = getResources().getDisplayMetrics().density;
        int scrollAmount = Math.min(screenWidth - getMinPhotoHeight(), scroll.getScrollY());
        float scrollPercent = scrollAmount / (float) (screenWidth - getMinPhotoHeight());
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
            if (scroll != null) {
                int thisScrollAmount = Math.min(screenWidth - getMinPhotoHeight(), scroll.getScrollY());
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

    protected int getMinPhotoHeight() {
        return toolbar.getHeight() + header.getHeight() + getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android"));
    }

}
