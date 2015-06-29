package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.ui.widget.ScrollCallbacks;
import com.shawnaten.tools.LocationSettings;

public class Tab extends BaseFragment implements ScrollCallbacks,
        MainActivity.ScrollListener, MainActivity.FragmentDataListener {
    public static final String TAB_LAYOUT = "tabLayout";

    private View toolbar;
    private View photoContainer;
    private View photo;
    private View header;
    private View scroll;
    private TextView thirdParty;

    private int screenWidth, screenHeight;

    private int maxScroll;

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
        View content;
        View attributions;

        toolbar = getBaseActivity().findViewById(R.id.toolbar);
        photoContainer = getBaseActivity().findViewById(R.id.photo_container);
        photo = getBaseActivity().findViewById(R.id.photo);
        header = getBaseActivity().findViewById(R.id.header);
        scroll = root.findViewById(R.id.scroll);
        //attributions = getBaseActivity().findViewById(R.id.attributions);

        if (scroll != null) {
            ((ScrollCallbacks) scroll).addCallbacks(this);
            scroll.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                    onOtherScrollChanged(((MainActivity) getBaseActivity()).getScrollPosition()));
        }

        if ((content = root.findViewById(R.id.content)) != null) {
            content.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                Space space = (Space) content.findViewById(R.id.top_space);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        screenWidth
                );
                space.setLayoutParams(layoutParams);

                space = (Space) content.findViewById(R.id.bottom_space);
                layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ((screenHeight - screenWidth / 2)
                                - (content.getHeight() - screenWidth - space.getHeight()))
                );
                space.setLayoutParams(layoutParams);
            });
        }

        if ((thirdParty = (TextView) root.findViewById(R.id.third_party)) != null)
            thirdParty.setMovementMethod(LinkMovementMethod.getInstance());

        /*
        if (attributions != null)
            attributions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.forecast_link)));
                    startActivity(browserIntent);
                }
            });
        */

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        getBaseActivity().checkForData(this);

        if (LocationSettings.getMode() == LocationSettings.Mode.SAVED
                && LocationSettings.getSavedPlace() != null
                && LocationSettings.getAttributions() != null) {
            thirdParty.setText(Html.fromHtml(LocationSettings.getAttributions().toString()));
        }
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        float density = getResources().getDisplayMetrics().density;
        int scrollAmount = Math.min(screenWidth / 2, scroll.getScrollY());
        float scrollPercent = scrollAmount / (screenWidth / 2f);
        float elevation = scrollPercent * 4 * density;

        photoContainer.setY(-scrollAmount);
        header.setY(screenWidth - header.getHeight() - scrollAmount);
        toolbar.setElevation(elevation);
        photoContainer.setElevation(elevation);
        header.setElevation(elevation);
        photo.setTranslationY(scrollAmount * 0.5f);

        ((MainActivity) getBaseActivity()).setScrollPosition(scrollAmount);
    }

    @Override
    public void addCallbacks(ScrollCallbacks callbacks) {

    }

    @Override
    public void onOtherScrollChanged(int otherScrollAmount) {
        if (scroll != null) {
            int thisScrollAmount = Math.min(screenWidth / 2, scroll.getScrollY());
            if (otherScrollAmount != thisScrollAmount)
                scroll.setScrollY(otherScrollAmount);
        }
    }

    @Override
    public void onNewData(Object data) {

    }
}
