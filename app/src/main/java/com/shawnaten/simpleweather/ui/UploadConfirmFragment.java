package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.imagesApi.ImagesApi;
import com.shawnaten.simpleweather.backend.imagesApi.model.Image;
import com.shawnaten.simpleweather.tools.CategoryInfo;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class UploadConfirmFragment extends BaseFragment implements View.OnClickListener {

    private static String ARG_KEY = "key";
    private static String ARG_URL = "url";

    private static ArrayList<CategoryInfo> categories = new ArrayList<>();
    static {
        categories.add(new CategoryInfo(R.drawable.clear_day, "Clear Day", "clear-day"));
        categories.add(new CategoryInfo(R.drawable.clear_night, "Clear Night", "clear-night"));
        categories.add(new CategoryInfo(R.drawable.partly_cloudy_day, "Partly Cloudy Day", "partly-cloudy-day"));
        categories.add(new CategoryInfo(R.drawable.partly_cloudy_night, "Partly Cloudy Night", "partly-cloudy-night"));
        categories.add(new CategoryInfo(R.drawable.cloudy, "Cloudy", "cloudy"));
        categories.add(new CategoryInfo(R.drawable.rain, "Rain", "rain"));
        categories.add(new CategoryInfo(R.drawable.thunderstorm, "Thunderstorm", "thunderstorm"));
        categories.add(new CategoryInfo(R.drawable.fog, "Fog", "fog"));
        categories.add(new CategoryInfo(R.drawable.sleet, "Sleet", "sleet"));
        categories.add(new CategoryInfo(R.drawable.snow, "Snow", "snow"));
        categories.add(new CategoryInfo(R.drawable.wind, "Wind", "wind"));
    }

    public static UploadConfirmFragment newInstance(String key, String url) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putString(ARG_URL, url);
        UploadConfirmFragment tab = new UploadConfirmFragment();
        tab.setArguments(args);
        return tab;
    }

    private View categoryView;

    @Inject ImagesApi imagesAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApp().getMainComponent().inject(this);
        Log.d("imageAPI", Boolean.toString(imagesAPI != null));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = getArguments();
        String url = args.getString(ARG_URL);
        ImageView imageView = (ImageView) getView().findViewById(R.id.photo);

        Picasso.with(getContext()).load(url).into(imageView);
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
            category.setTag(info.category);
            icon.setImageResource(info.icon);
            text.setText(info.text);
            category.setOnClickListener(this);
            if (i == 0) {
                this.categoryView = category;
                category.setBackgroundResource(R.drawable.category_button_bg_selected);
            }
            holder.addView(category);
        }

        Button submitButton = (Button) root.findViewById(R.id.button_confirm);
        submitButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_category:
                Toast.makeText(getActivity(), (String) view.getTag(), Toast.LENGTH_SHORT).show();
                categoryView.setBackgroundResource(R.drawable.category_button_bg);
                view.setBackgroundResource(R.drawable.category_button_bg_selected);
                categoryView = view;
                break;
            case R.id.button_confirm:
                Bundle args = getArguments();
                final Image image = new Image();
                image.setCategory((String) categoryView.getTag());
                image.setShortcode(args.getString(ARG_KEY));

                Observable.fromCallable(new Func0() {
                    @Override
                    public Object call() {
                        try {
                            imagesAPI.insertImage(image).execute();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber() {
                            @Override
                            public void onCompleted() {
                                getActivity().finish();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Object o) {

                            }
                        });
                break;
        }

    }
}
