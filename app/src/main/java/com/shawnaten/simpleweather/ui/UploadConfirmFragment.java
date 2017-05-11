package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.tools.CategoryInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UploadConfirmFragment extends BaseFragment implements View.OnClickListener {

    private static ArrayList<CategoryInfo> categories = new ArrayList<>();
    static {
        categories.add(new CategoryInfo(R.drawable.clear_day, "Clear Day", 0));
        categories.add(new CategoryInfo(R.drawable.clear_night, "Clear Night", 0));
        categories.add(new CategoryInfo(R.drawable.partly_cloudy_day, "Partly Cloudy Day", 0));
        categories.add(new CategoryInfo(R.drawable.partly_cloudy_night, "Partly Cloudy Night", 0));
        categories.add(new CategoryInfo(R.drawable.cloudy, "Cloudy", 0));
        categories.add(new CategoryInfo(R.drawable.rain, "Rain", 0));
        categories.add(new CategoryInfo(R.drawable.thunderstorm, "Thunderstorm", 0));
        categories.add(new CategoryInfo(R.drawable.fog, "Fog", 0));
        categories.add(new CategoryInfo(R.drawable.sleet, "Sleet", 0));
        categories.add(new CategoryInfo(R.drawable.snow, "Snow", 0));
        categories.add(new CategoryInfo(R.drawable.wind, "Wind", 0));
    }

    public static UploadConfirmFragment newInstance() {
        Bundle args = new Bundle();
        UploadConfirmFragment tab = new UploadConfirmFragment();
        tab.setArguments(args);
        return tab;
    }

    private View category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_upload_confirm, container, false);

        LinearLayout holder = (LinearLayout) root.findViewById(R.id.category_selector);

        for (int i = 0; i < categories.size(); i++) {
            View category = inflater.inflate(R.layout.button_category, holder, false);
            ImageView icon = (ImageView) category.findViewById(R.id.icon);
            TextView text = (TextView) category.findViewById(R.id.text);
            CategoryInfo info = categories.get(i);
            icon.setImageResource(info.icon);
            text.setText(info.text);
            category.setOnClickListener(this);
            if (i == 0) {
                this.category = category;
                category.setBackgroundResource(R.drawable.category_button_bg_selected);
            }
            holder.addView(category);
        }

        return root;
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view.findViewById(R.id.text);
        String text = (String) textView.getText();
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        category.setBackgroundResource(R.drawable.category_button_bg);
        view.setBackgroundResource(R.drawable.category_button_bg_selected);
        category = view;
    }
}
