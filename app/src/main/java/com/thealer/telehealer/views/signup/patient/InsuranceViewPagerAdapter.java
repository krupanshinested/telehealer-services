package com.thealer.telehealer.views.signup.patient;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;

import java.util.List;

/**
 * Created by Aswin on 15,March,2019
 */
public class InsuranceViewPagerAdapter extends PagerAdapter {
    private TextView titleTv;
    private CardView insuranceCv;
    private ImageView insuranceIv;

    private FragmentActivity activity;
    private List<String> labelList;
    private PickerListener pickerListener;
    private String frontImgPath, backImgPath;
    private List<String> imageList = null;
    private boolean isAuthRequired;

    public InsuranceViewPagerAdapter(FragmentActivity activity, List<String> labelList, PickerListener pickerListener) {
        this.activity = activity;
        this.labelList = labelList;
        this.pickerListener = pickerListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.insurance_viewpager_item, container, false);
        container.addView(view);

        titleTv = (TextView) view.findViewById(R.id.title_tv);
        insuranceCv = (CardView) view.findViewById(R.id.insurance_cv);
        insuranceIv = (ImageView) view.findViewById(R.id.insurance_iv);

        titleTv.setText(labelList.get(position));
        insuranceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerListener.didSelectedItem(position);
            }
        });

        if (frontImgPath != null && position == 0) {
            insuranceIv.setImageBitmap(BitmapFactory.decodeFile(frontImgPath));
        } else if (backImgPath != null && position == 1) {
            insuranceIv.setImageBitmap(BitmapFactory.decodeFile(backImgPath));
        } else {
            insuranceIv.setImageDrawable(activity.getDrawable(R.drawable.insurance_placeholder));
        }

        if (imageList != null && !imageList.isEmpty() && imageList.get(position) != null) {
            if (imageList.get(position).contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                insuranceIv.setImageBitmap(BitmapFactory.decodeFile(imageList.get(position)));
            } else {
                Utils.setImageWithGlide(activity.getApplicationContext(), insuranceIv, imageList.get(position), activity.getDrawable(R.drawable.insurance_placeholder), true, true);
            }
        }

        return view;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public void setFrontImgPath(String frontImgPath) {
        this.frontImgPath = frontImgPath;
        notifyDataSetChanged();
    }

    public void setBackImgPath(String backImgPath) {
        this.backImgPath = backImgPath;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return labelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void deleteImages() {
        this.frontImgPath = null;
        this.backImgPath = null;

        if (this.imageList != null)
            this.imageList.clear();

        notifyDataSetChanged();
    }

    public void setData(List<String> insurancePath, List<String> insuranceLabelList, boolean isAuthRequired) {
        this.imageList = insurancePath;
        this.labelList = insuranceLabelList;
        this.isAuthRequired = isAuthRequired;
        notifyDataSetChanged();
    }
}
