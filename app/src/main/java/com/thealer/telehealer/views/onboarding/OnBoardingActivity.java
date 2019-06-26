package com.thealer.telehealer.views.onboarding;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 10,October,2018
 */
public class OnBoardingActivity extends BaseActivity {
    private OnBoardingActionFragment onBoardingActionFragment;
    private OnBoardingViewPagerFragment onBoardingViewPagerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        initView();
    }

    /**
     * This method is used to initialize all the variables.
     */
    private void initView() {
        onBoardingActionFragment = (OnBoardingActionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_action_holder);
        onBoardingViewPagerFragment = (OnBoardingViewPagerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_viewpager_holder);

        //This will set the current text in OnBoardingActionFragment textview with list array position
        onBoardingActionFragment.setCurrentText(0);

        //This will listen to the change in view pager scroll in OnBoardingViewPagerFragment
        onBoardingViewPagerFragment.setInterface(new OnViewPageChangeListener() {
            @Override
            public void onPageChanged(int position) {
                onBoardingActionFragment.setCurrentText(position);
            }
        });

    }
}