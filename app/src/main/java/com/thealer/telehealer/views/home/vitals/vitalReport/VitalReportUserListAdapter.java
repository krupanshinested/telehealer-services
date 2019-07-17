package com.thealer.telehealer.views.home.vitals.vitalReport;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
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

    private OnListItemSelectInterface onListItemSelectInterface;

    public VitalReportUserListAdapter(FragmentActivity activity, OnListItemSelectInterface onListItemSelectInterface) {
        this.fragmentActivity = activity;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Utils.setImageWithGlide(fragmentActivity.getApplicationContext(), viewHolder.avatarCiv, commonUserApiResponseModelList.get(i).getUser_avatar(), fragmentActivity.getDrawable(R.drawable.profile_placeholder), true, true);

        viewHolder.titleTv.setText(commonUserApiResponseModelList.get(i).getUserDisplay_name());
        viewHolder.subTitleTv.setText(commonUserApiResponseModelList.get(i).getDisplayInfo());
        viewHolder.actionIv.setImageDrawable(fragmentActivity.getDrawable(commonUserApiResponseModelList.get(i).getSexDrawable()));

        viewHolder.patientTemplateCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DETAIL, commonUserApiResponseModelList.get(i));

                onListItemSelectInterface.onListItemSelected(i, bundle);

                Utils.hideKeyboard(fragmentActivity);
            }
        });

        viewHolder.userListIv.setStatus(commonUserApiResponseModelList.get(i).getStatus(), commonUserApiResponseModelList.get(i).getLast_active());
        viewHolder.userListIv.hasAbnormalVital(commonUserApiResponseModelList.get(i).isHas_abnormal_vitals());
    }

    @Override
    public int getItemCount() {
        return commonUserApiResponseModelList.size();
    }

    public void setData(List<CommonUserApiResponseModel> vitalReportApiReponseModel) {
        this.commonUserApiResponseModelList = vitalReportApiReponseModel;
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
