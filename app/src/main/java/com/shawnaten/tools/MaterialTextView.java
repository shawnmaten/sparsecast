package com.shawnaten.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;

import java.util.ArrayList;

/**
 * Created by Shawn Aten on 8/14/14.
 */
public class MaterialTextView extends TextView {
    private static final int NORMAL = 0, CONDENSED = 1,
            THIN = 0, LIGHT = 1, REGULAR = 2, MEDIUM = 3, BOLD = 4, BLACK = 5;

    private static ArrayList<TypeFaceHolder> typeFaces = new ArrayList<>();

    public MaterialTextView(Context context) {
        super(context);
    }

    public MaterialTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int family, style;
        boolean italicized;
        TypeFaceHolder holder;
        Typeface toSet = null;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialTextView, 0, 0);

        try {
            family = a.getInteger(R.styleable.MaterialTextView_family, NORMAL);
            style = a.getInteger(R.styleable.MaterialTextView_style, REGULAR);
            italicized = a.getBoolean(R.styleable.MaterialTextView_italicized, false);
        } finally {
            a.recycle();
        }

        holder = new TypeFaceHolder(family, style, italicized);
        int i = typeFaces.indexOf(holder);

        if (i >= 0) {
            toSet = typeFaces.get(i).getTypeface();
        } else {
            AssetManager mgr = getContext().getAssets();

            switch (family) {
                case NORMAL:
                    switch (style) {
                        case THIN:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-ThinItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Thin.ttf");
                            break;
                        case LIGHT:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-LightItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Light.ttf");
                            break;
                        case MEDIUM:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-MediumItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Medium.ttf");
                            break;
                        case BOLD:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-BoldItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Bold.ttf");
                            break;
                        case BLACK:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-BlackItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Black.ttf");
                            break;
                        default:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Italic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/Roboto-Regular.ttf");

                    }
                    break;
                case CONDENSED:
                    switch (style) {
                        case LIGHT:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/RobotoCondensed-LightItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/RobotoCondensed-Light.ttf");
                            break;
                        case BOLD:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/RobotoCondensed-BoldItalic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/RobotoCondensed-Bold.ttf");
                            break;
                        default:
                            if (italicized)
                                toSet = Typeface.createFromAsset(mgr, "fonts/RobotoCondensed-Italic.ttf");
                            else
                                toSet = Typeface.createFromAsset(mgr, "fonts/RobotoCondensed-Regular.ttf");
                    }
                    break;
            }

            holder.setTypeface(toSet);
            typeFaces.add(holder);

        }

        setTypeface(toSet);

    }

}
