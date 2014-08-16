package com.shawnaten.tools;

import android.content.Context;
import android.util.SparseArray;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

/**
 * Created by Shawn Aten on 8/15/14.
 */
public class SVGManager {
    private static SparseArray<SVG> images = new SparseArray<>();

    public static SVG getSVG(Context context, int resId) {
        int i = images.indexOfKey(resId);
        if (i >= 0) {
            return images.valueAt(i);
        }
        else {
            try {
                SVG svg = SVG.getFromResource(context, resId);
                images.put(resId, svg);
                return svg;
            } catch (SVGParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
