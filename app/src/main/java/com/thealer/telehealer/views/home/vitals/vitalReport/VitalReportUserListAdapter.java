package com.thealer.telehealer.views.home.vitals.vitalReport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalReportUserListAdapter extends RecyclerView.Adapter<VitalReportUserListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private List<CommonUserApiResponseModel> commonUserApiResponseModelList = new ArrayList<>();

    private ShowSubFragmentInterface showSubFragmentInterface;
    private String filter;
    private String doctorGuid;

    public VitalReportUserListAdapter(FragmentActivity activity, String doctorGuid) {
        this.fragmentActivity = activity;
        this.showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        this.doctorGuid = doctorGuid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Utils.setImageWithGlide(fragmentActivity, viewHolder.avatarCiv, commonUserApiResponseModelList.get(i).getUser_avatar(), fragmentActivity.getDrawable(R.drawable.profile_placeholder), true);

        viewHolder.titleTv.setText(commonUserApiResponseModelList.get(i).getUserDisplay_name());
        viewHolder.subTitleTv.setText(commonUserApiResponseModelList.get(i).getDisplayInfo());
        viewHolder.actionIv.setImageDrawable(fragmentActivity.getDrawable(commonUserApiResponseModelList.get(i).getSexDrawable()));

        viewHolder.patientTemplateCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VitalUserReportListFragment vitalUserReportListFragment = new VitalUserReportListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.USER_DETAIL, commonUserApiResponseModelList.get(i));
                bundle.putString(ArgumentKeys.SEARCH_TYPE, filter);
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                vitalUserReportListFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(vitalUserReportListFragment);

                Utils.hideKeyboard(fragmentActivity);
            }
        });

        viewHolder.userListIv.setStatus(commonUserApiResponseModelList.get(i).getStatus());
    }

    @Override
    public int getItemCount() {
        return commonUserApiResponseModelList.size();
    }

    public void setData(List<CommonUserApiResponseModel> vitalReportApiReponseModel, String filter) {
        this.commonUserApiResponseModelList = vitalReportApiReponseModel;
        this.filter = filter;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;
        private CardView patientTemplateCv;
        private CustomUserListItemView userListIv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
            patientTemplateCv = userListIv.getListItemCv();
            avatarCiv = userListIv.getAvatarCiv();
            titleTv = userListIv.getListTitleTv();
            subTitleTv = userListIv.getListSubTitleTv();
            actionIv = userListIv.getActionIv();
        }
    }
}
