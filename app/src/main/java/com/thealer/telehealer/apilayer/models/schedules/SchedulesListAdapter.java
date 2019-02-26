package com.thealer.telehealer.apilayer.models.schedules;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.schedules.ScheduleDetailViewFragment;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 18,December,2018
 */
public class SchedulesListAdapter extends BaseExpandableListAdapter {
    private FragmentActivity activity;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private List<String> headerList;
    private HashMap<String, List<SchedulesApiResponseModel.ResultBean>> childList = new HashMap<>();

    public SchedulesListAdapter(FragmentActivity activity, List<String> headerList, HashMap<String, List<SchedulesApiResponseModel.ResultBean>> childList) {
        this.activity = activity;
        this.headerList = headerList;
        this.childList = childList;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
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
        convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_expandable_list_header_view, parent, false);

        TextView recentHeaderTv = (TextView) convertView.findViewById(R.id.recent_header_tv);

        recentHeaderTv.setText(headerList.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_order_detail_list_view, parent, false);

        CardView itemCv;
        CircleImageView itemCiv;
        TextView itemTitleTv, itemSubTitleTv;
        ImageView statusIv;

        itemCv = (CardView) convertView.findViewById(R.id.item_cv);
        itemCiv = (CircleImageView) convertView.findViewById(R.id.item_civ);
        itemTitleTv = (TextView) convertView.findViewById(R.id.list_title_tv);
        itemSubTitleTv = (TextView) convertView.findViewById(R.id.list_sub_title_tv);
        statusIv = (ImageView) convertView.findViewById(R.id.status_iv);
        statusIv.setVisibility(View.GONE);

        CommonUserApiResponseModel commonUserApiResponseModel = null;
        String title = null;

        if (UserType.isUserPatient()) {
            if (childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_by_user().getRole().equals(Constants.ROLE_DOCTOR)) {
                commonUserApiResponseModel = childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_by_user();
            } else if (childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_with_user().getRole().equals(Constants.ROLE_DOCTOR)) {
                commonUserApiResponseModel = childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_with_user();
            }

            if (commonUserApiResponseModel != null) {
                title = commonUserApiResponseModel.getDoctorDisplayName();
            }

        } else {
            if (childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_by_user().getRole().equals(Constants.ROLE_PATIENT)) {
                commonUserApiResponseModel = childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_by_user();
            } else if (childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_with_user().getRole().equals(Constants.ROLE_PATIENT)) {
                commonUserApiResponseModel = childList.get(headerList.get(groupPosition)).get(childPosition).getScheduled_with_user();
            }

            if (commonUserApiResponseModel != null) {
                title = commonUserApiResponseModel.getUserDisplay_name();
            }

        }

        if (commonUserApiResponseModel != null) {
            Utils.setImageWithGlide(activity, itemCiv, commonUserApiResponseModel.getUser_avatar(), activity.getDrawable(R.drawable.profile_placeholder), true);
            itemSubTitleTv.setText(childList.get(headerList.get(groupPosition)).get(childPosition).getDetail().getReason());
        }
        itemTitleTv.setText(title);

        itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleDetailViewFragment scheduleDetailViewFragment = new ScheduleDetailViewFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.SCHEDULE_DETAIL, childList.get(headerList.get(groupPosition)).get(childPosition));
                scheduleDetailViewFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(scheduleDetailViewFragment);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> headerList, HashMap<String, List<SchedulesApiResponseModel.ResultBean>> childList, int page) {
        if (page == 1) {
            this.headerList = headerList;
            this.childList = childList;
        } else {
            this.headerList.addAll(headerList);
            this.childList.putAll(childList);
        }
        notifyDataSetChanged();
    }
}
