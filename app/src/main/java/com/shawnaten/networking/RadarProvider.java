package com.shawnaten.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Shawn Aten on 9/1/14.
 */
public class RadarProvider implements TileProvider {
    private static final double DEGREES_PER_PIXEL = 0.017971305190311, X_LNG = -127.620375523875420, Y_LAT = 50.406626367301044;
    private static final LatLngBounds radarBounds = new LatLngBounds(new LatLng(21.652538062803444, -127.620375523875420),
            new LatLng(50.406626367301044, -66.51793787681802));

    private Context context;
    private String[] radarFiles;
    private int tileSize;

    public RadarProvider(Context context, String[] radarFiles, double density) {
        this.context = context;
        this.radarFiles = radarFiles;
        this.tileSize = (int) (256 * density);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {

        LatLngBounds bounds = boundsOfTile(x, y, zoom);

        if (radarBounds.contains(bounds.southwest) && radarBounds.contains(bounds.northeast)) {
            Tile tile;

            double x1Lng = bounds.southwest.longitude;
            double y1Lat = bounds.northeast.latitude;
            double x2Lng = bounds.northeast.longitude;
            double y2Lat = bounds.southwest.latitude;

            int x1Pix = (int) (Math.abs(X_LNG - x1Lng) / DEGREES_PER_PIXEL);
            int y1Pix = (int) (Math.abs(Y_LAT - y1Lat) / DEGREES_PER_PIXEL);
            int x2Pix = (int) (Math.abs(X_LNG - x2Lng) / DEGREES_PER_PIXEL);
            int y2Pix = (int) (Math.abs(Y_LAT - y2Lat) / DEGREES_PER_PIXEL);

            Log.e("subImageBounds", String.format("x1: %d y1: %d x2: %d y2: %d", x1Pix, y1Pix, x2Pix, y2Pix));

        try {
            Bitmap full = Picasso.with(context).load(radarFiles[radarFiles.length-1]).transform(new RemoveBackground()).get();
            Bitmap sub = Bitmap.createBitmap(full, x1Pix, y1Pix, x2Pix - x1Pix, y2Pix - y1Pix);

            Bitmap subScaled = Bitmap.createScaledBitmap(sub, tileSize, tileSize, false);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            subScaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            tile = new Tile(x2Pix - x1Pix, y2Pix - y1Pix, byteArray);

            sub.recycle();
            subScaled.recycle();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

            return tile;
        } else
            return NO_TILE;
    }

    private LatLngBounds boundsOfTile(int x, int y, int zoom) {
        int noTiles = (1 << zoom);
        double longitudeSpan = 360.0 / noTiles;
        double longitudeMin = -180.0 + x * longitudeSpan;

        double mercatorMax = 180 - (((double) y) / noTiles) * 360;
        double mercatorMin = 180 - (((double) y + 1) / noTiles) * 360;
        double latitudeMax = toLatitude(mercatorMax);
        double latitudeMin = toLatitude(mercatorMin);

        return new LatLngBounds(new LatLng(latitudeMin, longitudeMin), new LatLng(latitudeMax, longitudeMin + longitudeSpan));
    }

    private double toLatitude(double mercator) {
        double radians = Math.atan(Math.exp(Math.toRadians(mercator)));
        return Math.toDegrees(2 * radians) - 90;
    }

}
