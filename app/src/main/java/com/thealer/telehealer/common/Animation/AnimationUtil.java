package com.thealer.telehealer.common.Animation;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by rsekar on 12/27/18.
 */

public class AnimationUtil {

    public static void animateToVisible(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(600);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public static void animateToHide(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(600);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }
}
