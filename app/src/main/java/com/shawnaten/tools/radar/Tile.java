package com.shawnaten.tools.radar;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by Shawn Aten on 9/3/14.
 */
public class Tile implements Transformation {

    public Tile(double x_units_pixel, double y_units_pixel, double... pts) {

        /*
        int x1Pix = (int) (Math.abs(X_LNG - x1Lng) / DEGREES_PER_PIXEL);
        int y1Pix = (int) (Math.abs(Y_LAT - y1Lat) / DEGREES_PER_PIXEL);
        int x2Pix = (int) (Math.abs(X_LNG - x2Lng) / DEGREES_PER_PIXEL);
        int y2Pix = (int) (Math.abs(Y_LAT - y2Lat) / DEGREES_PER_PIXEL);
        */

    }

    @Override
    public Bitmap transform(Bitmap source) {
        return null;
    }

    @Override
    public String key() {
        return null;
    }
}
