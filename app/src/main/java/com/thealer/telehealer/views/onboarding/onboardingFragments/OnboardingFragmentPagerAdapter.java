package com.thealer.telehealer.views.onboarding.onboardingFragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 05,February,2019
 */
public class OnboardingFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList = Arrays.asList(new OnboardingFragmentOne(), new OnboardingFragmentTwo(), new OnboardingFragmentThree(), new OnboardingFragmentFour(), new OnboardingFragmentFive());

    public OnboardingFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
