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
public class OnboardingFragmentOne extends BaseFragment {
    private ImageView phoneIv;
    private ImageView doctorIv;
    private ImageView patientIv;

    private boolean isResumed = false;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_one, container, false);
        this.view = view;
        return view;
    }

    private void initView(View view) {
        phoneIv = (ImageView) view.findViewById(R.id.phone_iv);
        doctorIv = (ImageView) view.findViewById(R.id.doctor_iv);
        patientIv = (ImageView) view.findViewById(R.id.patient_iv);

        patientIv.clearAnimation();
        phoneIv.clearAnimation();
        doctorIv.clearAnimation();

        doctorIv.setVisibility(View.INVISIBLE);
        patientIv.setVisibility(View.INVISIBLE);
        phoneIv.setVisibility(View.INVISIBLE);

        animateDoctor();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isResumed) {
            if (isVisibleToUser) {
                initView(view);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        setUserVisibleHint(isResumed);
    }

    private void animateDoctor() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(750);
        animationSet.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 1,
                2f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                patientIv.setVisibility(View.INVISIBLE);
                phoneIv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doctorIv.setVisibility(View.VISIBLE);
                animatePhone();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        doctorIv.startAnimation(animationSet);


    }

    private void animatePhone() {
        Animation animation = new TranslateAnimation(0, 0, 1000, 0);
        animation.setDuration(750);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                patientIv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                phoneIv.setVisibility(View.VISIBLE);
                animatePatient();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        phoneIv.startAnimation(animation);
    }

    private void animatePatient() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(750);
        animationSet.setFillAfter(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);

        ScaleAnimation scaleAnimation = new ScaleAnimation(2, 1, 2, 1,
                0.5f,
                0.75f);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                patientIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        patientIv.startAnimation(animationSet);
    }
}
