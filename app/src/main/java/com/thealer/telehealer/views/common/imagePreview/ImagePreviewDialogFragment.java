package com.thealer.telehealer.views.common.imagePreview;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseDialogFragment;
import com.thealer.telehealer.views.onboarding.OnBoardingViewPagerAdapter;

/**
 * Created by Aswin on 06,December,2018
 */
public class ImagePreviewDialogFragment extends BaseDialogFragment {
    private ConstraintLayout imagePreviewCl;
    private ImageView backgroundIv;
    private ViewPager imagePreviewVp;
    private TextView countTv;
    private ImageView closeIv;
    private ImagePreviewViewModel imagePreviewViewModel;
    private OnBoardingViewPagerAdapter onBoardingViewPagerAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imagePreviewViewModel = ViewModelProviders.of(getActivity()).get(ImagePreviewViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_preview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        imagePreviewCl = (ConstraintLayout) view.findViewById(R.id.image_preview_cl);
        backgroundIv = (ImageView) view.findViewById(R.id.background_iv);
        imagePreviewVp = (ViewPager) view.findViewById(R.id.image_preview_vp);
        countTv = (TextView) view.findViewById(R.id.count_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        onBoardingViewPagerAdapter = new OnBoardingViewPagerAdapter(getActivity(), imagePreviewViewModel.getImageList(), false);
        imagePreviewVp.setAdapter(onBoardingViewPagerAdapter);
        imagePreviewVp.setCurrentItem(0);
        Utils.setImageWithGlide(getActivity().getApplicationContext(), backgroundIv, imagePreviewViewModel.getImageList().get(0), null, true);
        setCountTv(0);
        imagePreviewVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                setCountTv(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void setCountTv(int i) {
        countTv.setText(i + 1 + "/" + onBoardingViewPagerAdapter.getCount());
    }
}
