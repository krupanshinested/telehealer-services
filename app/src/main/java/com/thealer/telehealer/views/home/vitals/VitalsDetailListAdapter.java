package com.thealer.telehealer.views.home.vitals;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;

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
    public Object getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
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

        vitalIv = (ImageView) convertView.findViewById(R.id.vital_iv);
        valueTv = (TextView) convertView.findViewById(R.id.value_tv);
        unitTv = (TextView) convertView.findViewById(R.id.unit_tv);
        descriptionTv = (TextView) convertView.findViewById(R.id.description_tv);
        timeTv = (TextView) convertView.findViewById(R.id.time_tv);

        valueTv.setText(childList.get(headerList.get(groupPosition)).get(childPosition).getValue());
        descriptionTv.setText(childList.get(headerList.get(groupPosition)).get(childPosition).getCapturedBy());
        timeTv.setText(Utils.getFormatedTime(childList.get(headerList.get(groupPosition)).get(childPosition).getCreated_at()));
        unitTv.setText(SupportedMeasurementType.getVitalUnit(childList.get(headerList.get(groupPosition)).get(childPosition).getType()));

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
