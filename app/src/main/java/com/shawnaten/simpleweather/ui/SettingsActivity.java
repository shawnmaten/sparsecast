package com.shawnaten.simpleweather.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.shawnaten.simpleweather.R;

import java.util.List;

public class SettingsActivity extends BaseActivity {
    private FrameLayout adFrame;
    private AdLoader adLoader;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FragmentManager fm = getFragmentManager();
        Toolbar toolbar;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        fm.beginTransaction().add(R.id.content, new SettingsFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        //adView = (AdView) findViewById(R.id.ad_view);

        /*
        adFrame = (FrameLayout) findViewById(R.id.ad_frame);

        adLoader = new AdLoader.Builder(this, getString(R.string.admob_settings_native))
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd contentAd) {
                        // Show the content ad.
                        displayContentAd(adFrame, contentAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, etc.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
                */
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        if (LocationSettings.currentLocation != null)
            adRequestBuilder.setLocation(LocationSettings.currentLocation);
        adView.loadAd(adRequestBuilder.build());
        */
    }

    private void displayContentAd(ViewGroup parent, final NativeContentAd contentAd) {
        NativeContentAdView adView;
        View adSpace;

        // Inflate a layout and add it to the parent ViewGroup.
        if (parent.getChildCount() == 0) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            adSpace = inflater.inflate(R.layout.native_content_ad, parent, false);
            adView = (NativeContentAdView) adSpace.findViewById(R.id.ad_view);
            // Place the AdView into the parent.
            parent.addView(adSpace);
        } else {
            adSpace = parent.getChildAt(0);
            adView = (NativeContentAdView) adSpace.findViewById(R.id.ad_view);
        }

        ImageView logoView = (ImageView) adView.findViewById(R.id.logo);
        //List<NativeAd.Image> images = contentAd.getImages();
        //NativeAd.Image image = images.get(0);
        logoView.setImageURI(contentAd.getLogo().getUri());
        logoView.setImageDrawable(contentAd.getLogo().getDrawable());
        adView.setLogoView(logoView);

        // Locate the view that will hold the headline, set its text, and call the
        // NativeContentAdView's setHeadlineView method to register it.
        TextView headlineView = (TextView) adView.findViewById(R.id.headline);
        headlineView.setText(contentAd.getHeadline());
        adView.setHeadlineView(headlineView);

        TextView bodyView = (TextView) adView.findViewById(R.id.body);
        bodyView.setText(contentAd.getBody());
        adView.setBodyView(bodyView);

        TextView advertiserView = (TextView) adView.findViewById(R.id.advertiser);
        advertiserView.setText(contentAd.getAdvertiser());
        advertiserView.setMovementMethod(new LinkMovementMethod());
        adView.setAdvertiserView(advertiserView);

        ImageView imageView = (ImageView) adView.findViewById(R.id.image);
        List<NativeAd.Image> images = contentAd.getImages();
        NativeAd.Image image = images.get(0);

        if (!image.getUri().equals(contentAd.getLogo().getUri())) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(image.getUri());
            imageView.setImageDrawable(image.getDrawable());
            adView.setLogoView(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }

        adFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = contentAd.getCallToAction().toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(browserIntent);
            }
        });
        adView.setCallToActionView(adFrame);

        // Repeat the above process for the other assets in the NativeContentAd using
        // additional view objects (Buttons, ImageViews, etc).

        // Call the NativeContentAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(contentAd);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
