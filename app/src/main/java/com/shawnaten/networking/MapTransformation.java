package com.shawnaten.networking;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.squareup.picasso.Transformation;

/**
 * Created by Shawn Aten on 8/31/14.
 */
public class MapTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {

        Bitmap mBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        int mPixels[] = new int[source.getWidth() * source.getHeight()];

        source.getPixels(mPixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        for (int i = 0; i < mPixels.length; i++) {
            if (mPixels[i] == Color.WHITE)
                mPixels[i] = Color.TRANSPARENT;
        }
        mBitmap.setPixels(mPixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());

        source.recycle();

        return mBitmap;
    }

    @Override
    public String key() {
        return "Map";
    }
}
