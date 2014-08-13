package com.shawnaten.tools;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Created by Shawn Aten on 8/12/14.
 */
public class ClimaconTypefaceSpan extends TypefaceSpan {
    private static Typeface typeface;

    public ClimaconTypefaceSpan() {
        super("font");
    }

    public static void setTypeface (Typeface newTypeface) {
        typeface = newTypeface;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.setTypeface(typeface);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.setTypeface(typeface);
    }

}
