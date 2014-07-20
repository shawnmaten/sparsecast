package com.shawnaten.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by shawnaten on 7/20/14.
 */
public class AnimationTools {
    private static View in, out;

    public static void crossFadeViews(View fadeIn, View fadeOut, int duration) {
        in = fadeIn;
        out = fadeOut;

        in.setAlpha(0f);
        in.setVisibility(View.VISIBLE);
        in.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        out.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        out.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public static void fadeViewIn(View fadeIn, int duration) {
        in = fadeIn;

        in.setAlpha(0f);
        in.setVisibility(View.VISIBLE);
        in.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

    }

    public static void fadeViewOut(View fadeOut, int duration) {
        out = fadeOut;

        out.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        out.setVisibility(View.INVISIBLE);
                    }
                });

    }
}
