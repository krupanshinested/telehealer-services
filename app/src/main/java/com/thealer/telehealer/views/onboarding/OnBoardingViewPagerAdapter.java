package com.thealer.telehealer.views.onboarding;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.imagePreview.ImagePreviewDialogFragment;
import com.thealer.telehealer.views.common.imagePreview.ImagePreviewViewModel;

import java.util.List;


public class OnBoardingViewPagerAdapter extends PagerAdapter {

    private Context context;
    private int count;
    private List<String> insuranceImageList;
    private ImageView pagerItemIv;
    private boolean isPreviewAvailable;
    private FragmentActivity activity;
    private List<Bitmap> imageBitmapList;

    public OnBoardingViewPagerAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
    }

    public OnBoardingViewPagerAdapter(FragmentActivity context, List<String> insuranceImageList, boolean isPreviewAvailable) {
        this.context = context;
        this.activity = context;
        this.count = insuranceImageList.size();
        this.insuranceImageList = insuranceImageList;
        this.isPreviewAvailable = isPreviewAvailable;
    }

    public OnBoardingViewPagerAdapter(FragmentActivity activity, List<Bitmap> imageBitmapList) {
        this.context = activity;
        this.count = imageBitmapList.size();
        this.imageBitmapList = imageBitmapList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_item, container, false);
        container.addView(view);

        pagerItemIv = (ImageView) view.findViewById(R.id.pager_item_iv);

        if (imageBitmapList != null) {
            Glide.with(view).load(imageBitmapList.get(position)).into(pagerItemIv);
        }
        if (insuranceImageList != null) {
            Utils.setImageWithGlide(context, pagerItemIv, insuranceImageList.get(position), context.getDrawable(R.drawable.placeholder_license), true);
            if (isPreviewAvailable) {
                pagerItemIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePreviewViewModel imagePreviewViewModel = ViewModelProviders.of(activity).get(ImagePreviewViewModel.class);
                        imagePreviewViewModel.setImageList(insuranceImageList);
                        ImagePreviewDialogFragment imagePreviewDialogFragment = new ImagePreviewDialogFragment();
                        imagePreviewDialogFragment.show(activity.getSupportFragmentManager(), ImagePreviewDialogFragment.class.getSimpleName());

                    }
                });
            }
        }

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
