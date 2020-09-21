package com.thealer.telehealer.views.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseFragment;

/**
 * Created by Aswin on 10,October,2018
 */
public class OnBoardingViewPagerFragment extends BaseFragment implements ViewPager.OnPageChangeListener{
    private ViewPager viewPager;
    private LinearLayout pagerIndicator;
    private OnBoardingViewPagerAdapter onBoardingViewPagerAdapter;
    private ImageView[] indicators;
    private OnViewPageChangeListener onViewPageChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_viewpager, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        pagerIndicator = (LinearLayout) view.findViewById(R.id.pager_indicator);
        onBoardingViewPagerAdapter = new OnBoardingViewPagerAdapter(getContext(), 5);
        viewPager.setAdapter(onBoardingViewPagerAdapter);
        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(this);
        createIndicator();
    }

    private void createIndicator() {
        int count = onBoardingViewPagerAdapter.getCount();
        indicators = new ImageView[count];

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4,0);

            pagerIndicator.addView(indicators[i], params);
        }

        indicators[0].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));

        if (onViewPageChangeListener != null)
            onViewPageChangeListener.onPageChanged(0);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        for (int j = 0; j < onBoardingViewPagerAdapter.getCount(); j++) {
            indicators[j].setImageDrawable(getResources().getDrawable(R.drawable.circular_unselected_indicator));
        }
        indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));

        if (onViewPageChangeListener != null)
            onViewPageChangeListener.onPageChanged(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void setInterface(OnViewPageChangeListener onViewPageChangeListener) {
        this.onViewPageChangeListener = onViewPageChangeListener;
    }
}