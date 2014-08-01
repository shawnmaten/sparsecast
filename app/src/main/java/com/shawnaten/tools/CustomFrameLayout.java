package com.shawnaten.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by shawnaten on 7/20/14.
 */
public class CustomFrameLayout extends FrameLayout {
    private KeyboardStateListener listener;
    private Boolean keyboardState = false;

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyboardStateListener(KeyboardStateListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (h != oldh) {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            if (h < (screenHeight / 2)) {
                if (!keyboardState)
                    listener.onKeyboardShown();
                keyboardState = true;
            }
            else {
                if (keyboardState)
                    listener.onKeyboardHidden();
                keyboardState = false;
            }
        }

    }

    public interface KeyboardStateListener {
        public void onKeyboardShown();
        public void onKeyboardHidden();
    }

}
