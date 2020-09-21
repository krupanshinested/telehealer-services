package com.thealer.telehealer.views.home.recents;

import android.content.Context;
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
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 15,November,2018
 */
public class RecentListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listHeader;
    private HashMap<String, List<RecentsApiResponseModel.ResultBean>> listChild;
    private ShowSubFragmentInterface showSubFragmentInterface;

    public RecentListAdapter(FragmentActivity context, List<String> listHeader, HashMap<String, List<RecentsApiResponseModel.ResultBean>> listChild) {
        this.context = context;
        this.listChild = listChild;
        this.listHeader = listHeader;
        this.showSubFragmentInterface = (ShowSubFragmentInterface) context;
    }

    @Override
    public int getGroupCount() {
        return listChild.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeader.get(groupPosition);
    }

    @Override
    public RecentsApiResponseModel.ResultBean getChild(int groupPosition, int childPosition) {
        return listChild.get(listHeader.get(groupPosition)).get(childPosition);
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

        recentHeaderTv.setText(listHeader.get(groupPosition).replace("-c", ""));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_recent_list_child_view, parent, false);

        CircleImageView userAvatarCiv = (CircleImageView) convertView.findViewById(R.id.user_avatar_civ);
        TextView userNameTv = (TextView) convertView.findViewById(R.id.user_name_tv);
        TextView timeTv = (TextView) convertView.findViewById(R.id.time_tv);
        TextView durationTv = (TextView) convertView.findViewById(R.id.duration_tv);
        ImageView labelIv;
        TextView labelTv;
        ImageView infoIv;
        CardView itemCv;

        labelIv = (ImageView) convertView.findViewById(R.id.label_iv);
        labelTv = (TextView) convertView.findViewById(R.id.label_tv);
        infoIv = (ImageView) convertView.findViewById(R.id.info_iv);
        itemCv = (CardView) convertView.findViewById(R.id.item_cv);

        RecentsApiResponseModel.ResultBean resultBean = getChild(groupPosition, childPosition);

        int userType = TeleHealerApplication.appPreference.getInt(Constants.USER_TYPE);
        boolean isChat = resultBean.getCorr_type().equals("chat");
        String userName = null, avatar = null, time, duration;

        if (resultBean.getCategory() != null) {
            labelTv.setText(resultBean.getCategory());
            labelTv.setVisibility(View.VISIBLE);
            labelIv.setVisibility(View.VISIBLE);
        } else {
            labelTv.setVisibility(View.GONE);
            labelIv.setVisibility(View.GONE);
        }

        itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultBean.getDurationInSecs() > 0) {
                    RecentDetailView recentDetailView = new RecentDetailView();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL, resultBean);
                    recentDetailView.setArguments(bundle);
                    showSubFragmentInterface.onShowFragment(recentDetailView);
                }
            }
        });

        infoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (userType == Constants.TYPE_PATIENT) {

            if (resultBean.getDoctor() != null && resultBean.getCaller_id() == resultBean.getDoctor().getUser_id()) {
                userName = resultBean.getDoctor().getFirst_name() + " " + resultBean.getDoctor().getLast_name();
                avatar = resultBean.getDoctor().getUser_avatar();

            } else if (resultBean.getMedical_assistant() != null && resultBean.getCaller_id() == resultBean.getMedical_assistant().getUser_id()) {
                userName = resultBean.getMedical_assistant().getFirst_name() + " " + resultBean.getMedical_assistant().getLast_name();
                avatar = resultBean.getMedical_assistant().getUser_avatar();
            }

        } else {

            userName = resultBean.getPatient().getFirst_name() + " " + resultBean.getPatient().getLast_name();
            avatar = resultBean.getPatient().getUser_avatar();

        }

        Utils.setImageWithGlide(context, userAvatarCiv, avatar, context.getDrawable(R.drawable.profile_placeholder), true);
        userNameTv.setText(userName);
        timeTv.setText(Utils.getFormatedTime(resultBean.getUpdated_at()));

        if (isChat) {
            durationTv.setVisibility(View.GONE);
            timeTv.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_chat_bubble_outline_black_24dp), null, null, null);
        } else {
            int seconds = resultBean.getDurationInSecs();
            if (seconds < 60) {
                durationTv.setText(resultBean.getDurationInSecs() + " sec");
            } else {
                durationTv.setText((seconds / 60) + " min " + (seconds % 60) + " sec");
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> listHeader, HashMap<String, List<RecentsApiResponseModel.ResultBean>> listChild) {
        this.listHeader = listHeader;
        this.listChild = listChild;
        notifyDataSetChanged();
    }
}
