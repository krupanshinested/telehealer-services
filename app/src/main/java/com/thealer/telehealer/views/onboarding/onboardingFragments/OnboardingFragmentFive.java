package com.thealer.telehealer.views.onboarding.onboardingFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;

/**
 * Created by Aswin on 04,February,2019
 */
public class OnboardingFragmentFive extends BaseFragment {
    private ImageView appIconIv;
    private TextView appNameTv;

    private boolean isResumed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_five, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appIconIv = (ImageView) view.findViewById(R.id.app_icon_iv);
        appNameTv = (TextView) view.findViewById(R.id.app_name_tv);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (isResumed) {
            if (isVisibleToUser) {
                animateViews();
            } else {
                appIconIv.clearAnimation();
                appNameTv.clearAnimation();

                appIconIv.setVisibility(View.INVISIBLE);
                appNameTv.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void animateViews() {

        AnimationSet iconSet = getAnimationSet(500);
        iconSet.addAnimation(getAlphaAnimation(0, 1));

        ScaleAnimation iconScaleAnimation = new ScaleAnimation(2f, 1,
                2f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        iconSet.addAnimation(iconScaleAnimation);

        iconSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appIconIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        AnimationSet nameSet = getAnimationSet(500);

        nameSet.addAnimation(getAlphaAnimation(0, 1));

        ScaleAnimation nameAnimation = new ScaleAnimation(2, 1, 2, 1,
                0.5f,
                0.75f);
        nameSet.addAnimation(nameAnimation);

        nameSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appNameTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        appIconIv.startAnimation(iconSet);
        appNameTv.startAnimation(nameSet);

    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    private AnimationSet getAnimationSet(long durationInMills) {
        AnimationSet animationSet = new AnimationSet(false);

        animationSet.setFillAfter(true);
        animationSet.setDuration(durationInMills);
        animationSet.setRepeatCount(Animation.INFINITE);
        animationSet.setRepeatMode(Animation.REVERSE);

        return animationSet;
    }

    private AlphaAnimation getAlphaAnimation(float fromAlpha, float toAlpha) {
        return new AlphaAnimation(fromAlpha, toAlpha);
    }

}
