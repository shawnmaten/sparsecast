package com.shawnaten.simpleweather.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.shawnaten.simpleweather.R;

public class AdActivity extends BaseActivity {

    private AdView adView;
    private AdLoader adLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ad);

        adView = (AdView) findViewById(R.id.ad);

        NativeAdOptions options;

        options = new NativeAdOptions.Builder()
                .setRequestMultipleImages(true)
                .setReturnUrlsForImageAssets(true)
                .build();

        adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
                        Log.e("AdActivity - " + appInstallAd.getHeadline(), appInstallAd.getIcon().getUri().toString());
                        /*
                        for (NativeAd.Image image : appInstallAd.getImages())
                            Log.e("app install ad", image.getUri().toString());
                            */
                        // Show the app install ad.
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd contentAd) {
                        Log.e("AdActivity - " + contentAd.getHeadline(), contentAd.getLogo().getUri().toString());
                        // Show the content ad.
                        /*
                        for (NativeAd.Image image : contentAd.getImages())
                            Log.e("content ad", image.getUri().toString());
                            */
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, etc.
                    }
                })
                .withNativeAdOptions(options)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void displayContentAd(ViewGroup parent, final NativeContentAd contentAd) {
        NativeContentAdView adView;

        // Inflate a layout and add it to the parent ViewGroup.
        if (parent.getChildCount() == 0) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            adView = (NativeContentAdView) inflater.inflate(R.layout.native_content_ad, parent, false);
            parent.addView(adView);
        } else {
            adView = (NativeContentAdView) parent.getChildAt(0);
        }

        /*
        ImageView logoView = (ImageView) adView.findViewById(R.id.logo);
        logoView.setImageURI(contentAd.getLogo().getUri());
        logoView.setImageDrawable(contentAd.getLogo().getDrawable());
        adView.setLogoView(logoView);
        */

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

        /*
        ImageView imageView = (ImageView) adView.findViewById(R.id.image);
        List<NativeAd.Image> images = contentAd.getImages();
        NativeAd.Image image = images.get(0);
        imageView.setImageURI(image.getUri());
        imageView.setImageDrawable(image.getDrawable());
        adView.setImageView(imageView);
        */

        for (NativeAd.Image image : contentAd.getImages())
            Log.e("ad image", image.getUri().toString());

        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = contentAd.getCallToAction().toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(browserIntent);
            }
        });
        adView.setCallToActionView(adView);

        // Repeat the above process for the other assets in the NativeContentAd using
        // additional view objects (Buttons, ImageViews, etc).

        // Call the NativeContentAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(contentAd);
    }

}
