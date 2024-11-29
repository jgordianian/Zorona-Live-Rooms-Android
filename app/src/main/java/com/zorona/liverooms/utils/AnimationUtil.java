package com.app.liverooms.utils;

import android.view.View;

final public class AnimationUtil {

    public static void toggleVisibilityToBottom(View view, boolean show) {
        view.setAlpha(show ? 0 : 1);
        if (show) {
            view.animate()
                    .translationY(0)
                    .alpha(1);
        } else {
            view.animate()
                    .translationY(view.getHeight())
                    .alpha(0);
        }
    }

    public static void toggleVisibilityToLeft(View view, boolean show) {
        view.setAlpha(show ? 0 : 1);
        if (show) {
            view.animate()
                    .translationX(0)
                    .alpha(1);
        } else {
            view.animate()
                    .translationX(-view.getWidth())
                    .alpha(0);
        }
    }

    public static void toggleVisibilityToRight(View view, boolean show) {
        view.setAlpha(show ? 0 : 1);
        if (show) {
            view.animate()
                    .translationX(0)
                    .alpha(1);
        } else {
            view.animate()
                    .translationX(view.getWidth())
                    .alpha(0);
        }
    }

    public static void toggleVisibilityToTop(View view, boolean show) {
        view.setAlpha(show ? 0 : 1);
        if (show) {
            view.animate()
                    .translationY(0)
                    .alpha(1);
        } else {
            view.animate()
                    .translationY(-view.getHeight())
                    .alpha(0);
        }
    }
}
