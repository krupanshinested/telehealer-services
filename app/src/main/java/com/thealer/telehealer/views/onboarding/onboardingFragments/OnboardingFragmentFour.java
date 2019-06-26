package com.thealer.telehealer.views.onboarding.onboardingFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;

/**
 * Created by Aswin on 04,February,2019
 */
public class OnboardingFragmentFour extends BaseFragment {
    private ImageView prescriptionIv;
    private ImageView phoneIv;
    private ImageView kitepathIv;
    private ImageView kiteIv;

    boolean isResumed = false;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_four, container, false);
        this.view = view;
        return view;
    }

    private void initView(View view) {
        prescriptionIv = (ImageView) view.findViewById(R.id.prescription_iv);
        phoneIv = (ImageView) view.findViewById(R.id.phone_iv);
        kitepathIv = (ImageView) view.findViewById(R.id.kitepath_iv);
        kiteIv = (ImageView) view.findViewById(R.id.kite_iv);

        prescriptionIv.clearAnimation();
        phoneIv.clearAnimation();
        kitepathIv.clearAnimation();
        kiteIv.clearAnimation();

        prescriptionIv.setVisibility(View.VISIBLE);
        phoneIv.setVisibility(View.INVISIBLE);
        kitepathIv.setVisibility(View.INVISIBLE);
        kiteIv.setVisibility(View.INVISIBLE);

        animatePrescription();
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed) {
            if (isVisibleToUser) {
                initView(view);
            }
        }
    }

    private void animatePrescription() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(750);
        scaleAnimation.setFillAfter(true);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                prescriptionIv.setVisibility(View.INVISIBLE);
                animatePhone();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        prescriptionIv.startAnimation(scaleAnimation);
    }

    private void animatePhone() {
        Animation animation = new TranslateAnimation(0, 0, 1000, 0);
        animation.setDuration(750);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                phoneIv.setVisibility(View.VISIBLE);
                animateKitePath();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        phoneIv.startAnimation(animation);

    }

    private void animateKitePath() {
        AnimationSet animationSet = new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        Animation translateAnimation = new TranslateAnimation(-10, 0, 0, 0);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);

        animationSet.setDuration(500);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                kitepathIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animateKite();

        kitepathIv.startAnimation(animationSet);
    }

    private void animateKite() {
        Animation translateAnimation = new TranslateAnimation(-100, 0, 0, 0);
        translateAnimation.setDuration(750);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                kiteIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        kiteIv.startAnimation(translateAnimation);
    }
}
