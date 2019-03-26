package com.thealer.telehealer.views.home.vitals;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.StethBean;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 27,November,2018
 */
public class VitalsDetailListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerList;
    private HashMap<String, List<VitalsApiResponseModel>> childList;
    private String measurementType;
    private boolean imageVisible;

    public VitalsDetailListAdapter(Context context, List<String> headerList, HashMap<String, List<VitalsApiResponseModel>> childList, String measurementType) {
        this.context = context;
        this.headerList = headerList;
        this.childList = childList;
        this.measurementType = measurementType;
    }

    public VitalsDetailListAdapter(FragmentActivity activity, List<String> headerList, HashMap<String, List<VitalsApiResponseModel>> childList, boolean imageVisible) {
        this.context = activity;
        this.headerList = headerList;
        this.childList = childList;
        this.imageVisible = imageVisible;
    }

    @Override
    public int getGroupCount() {
        return childList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(headerList.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public VitalsApiResponseModel getChild(int groupPosition, int childPosition) {
        return childList.get(headerList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_expandable_list_header_view, parent, false);

        TextView recentHeaderTv = (TextView) convertView.findViewById(R.id.recent_header_tv);

        recentHeaderTv.setText(headerList.get(groupPosition));

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_vitals_list_view, parent, false);
        TextView valueTv;
        TextView unitTv;
        TextView descriptionTv;
        TextView timeTv;
        ImageView vitalIv;
        CardView itemCv;

        itemCv = (CardView) convertView.findViewById(R.id.item_cv);
        vitalIv = (ImageView) convertView.findViewById(R.id.vital_iv);
        valueTv = (TextView) convertView.findViewById(R.id.value_tv);
        unitTv = (TextView) convertView.findViewById(R.id.unit_tv);
        descriptionTv = (TextView) convertView.findViewById(R.id.description_tv);
        timeTv = (TextView) convertView.findViewById(R.id.time_tv);

        VitalsApiResponseModel vitalsApiResponseModel = getChild(groupPosition, childPosition);

        timeTv.setText(Utils.getFormatedTime(childList.get(getGroup(groupPosition)).get(childPosition).getCreated_at()));
        descriptionTv.setText(childList.get(headerList.get(groupPosition)).get(childPosition).getCapturedBy());

        if (!vitalsApiResponseModel.getType().equals(SupportedMeasurementType.stethoscope)) {
            valueTv.setText(childList.get(getGroup(groupPosition)).get(childPosition).getValue().toString());
            unitTv.setText(SupportedMeasurementType.getVitalUnit(childList.get(headerList.get(groupPosition)).get(childPosition).getType()));
        } else {

            Log.e("aswin", "getChildView: " + new Gson().toJson(vitalsApiResponseModel));

            StethBean stethBean = vitalsApiResponseModel.getStethBean();

            unitTv.setText(stethBean.getSegments().size() + " - Segment");

            boolean isContainsHeart = false, isContainsLung = false;

            for (int i = 0; i < stethBean.getSegments().size(); i++) {
                if (stethBean.getSegments().get(i).getFilter_type().equals(VitalsConstant.heart)) {
                    isContainsHeart = true;
                }
                if (stethBean.getSegments().get(i).getFilter_type().equals(VitalsConstant.lung)) {
                    isContainsLung = true;
                }

                if (isContainsHeart && isContainsLung) {
                    break;
                }
            }

            int drawable = R.drawable.steth_heart_lung;

            if (isContainsHeart && !isContainsLung) {
                drawable = R.drawable.steth_heart;
            } else if (!isContainsHeart && isContainsLung) {
                drawable = R.drawable.steth_lung;
            }

            vitalIv.setImageDrawable(context.getDrawable(drawable));
            vitalIv.setVisibility(View.VISIBLE);

            ShowSubFragmentInterface showSubFragmentInterface = (ShowSubFragmentInterface) context;

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StethoscopeDetailViewFragment stethoscopeDetailViewFragment = new StethoscopeDetailViewFragment();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ArgumentKeys.VITAL_DETAIL, vitalsApiResponseModel);
                    stethoscopeDetailViewFragment.setArguments(bundle);

                    showSubFragmentInterface.onShowFragment(stethoscopeDetailViewFragment);
                }
            });
        }

        if (imageVisible) {
            vitalIv.setVisibility(View.VISIBLE);
            int drawable = SupportedMeasurementType.getDrawable(childList.get(headerList.get(groupPosition)).get(childPosition).getType());
            if (drawable != 0) {
                vitalIv.setImageDrawable(context.getDrawable(drawable));
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> headerList, HashMap<String, List<VitalsApiResponseModel>> childList) {
        this.headerList = headerList;
        this.childList = childList;
        notifyDataSetChanged();
    }

}
