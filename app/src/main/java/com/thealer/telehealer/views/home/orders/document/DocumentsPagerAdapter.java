package com.thealer.telehealer.views.home.orders.document;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.Utils;

import java.util.List;

/**
 * Created by Aswin on 29,November,2018
 */
public class DocumentsPagerAdapter extends PagerAdapter {

    private ImageView pagerItemIv;
    private Context context;
    private List<DocumentsApiResponseModel.ResultBean> result;

    public DocumentsPagerAdapter(FragmentActivity activity, List<DocumentsApiResponseModel.ResultBean> result) {
        this.context = activity;
        this.result = result;
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

        Utils.setImageWithGlide(context, pagerItemIv, result.get(position).getPath(), context.getDrawable(R.drawable.document_placeholder_drawable), true, true);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
