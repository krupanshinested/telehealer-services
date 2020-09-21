package com.thealer.telehealer.views.home.vitals;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalDetailListAdapter extends BaseExpandableListAdapter {
    private Context context;

    public VitalDetailListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_expandable_list_header_view, parent, false);

        TextView recentHeaderTv = (TextView) convertView.findViewById(R.id.recent_header_tv);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_vital_detail_list_view, parent, false);

        CardView itemCv;
        TextView titleTv, subTitleTv, contentTv, timeTv;

        itemCv = (CardView) convertView.findViewById(R.id.item_cv);
        titleTv = (TextView) convertView.findViewById(R.id.title_tv);
        subTitleTv = (TextView) convertView.findViewById(R.id.sub_title_tv);
        contentTv = (TextView) convertView.findViewById(R.id.content_tv);
        timeTv = (TextView) convertView.findViewById(R.id.time_tv);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
