package com.thealer.telehealer.views.home;

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
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 13,November,2018
 */
public class DoctorPatientListAdapter extends RecyclerView.Adapter<DoctorPatientListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private List<CommonUserApiResponseModel> associationApiResponseModelResult;
    private OnActionCompleteInterface onActionCompleteInterface;

    public DoctorPatientListAdapter(FragmentActivity activity) {
        fragmentActivity = activity;
        associationApiResponseModelResult = new ArrayList<>();
        onActionCompleteInterface = (OnActionCompleteInterface) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.titleTv.setText(associationApiResponseModelResult.get(i).getUserDisplay_name());
        loadAvatar(viewHolder.avatarCiv, associationApiResponseModelResult.get(i).getUser_avatar());

        if (UserType.isUserDoctor()) {
            viewHolder.actionIv.setVisibility(View.VISIBLE);
            Utils.setGenderImage(fragmentActivity, viewHolder.actionIv, associationApiResponseModelResult.get(i).getGender());
        } else if (UserType.isUserPatient() || UserType.isUserAssistant()) {
            viewHolder.actionIv.setVisibility(View.GONE);
        }
        viewHolder.subTitleTv.setText(associationApiResponseModelResult.get(i).getDisplayInfo());

        viewHolder.patientTemplateCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(fragmentActivity);
                proceed(associationApiResponseModelResult.get(i));
            }
        });

    }

    private void proceed(CommonUserApiResponseModel resultBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.USER_DETAIL, resultBean);
        onActionCompleteInterface.onCompletionResult(RequestID.REQ_SHOW_DETAIL_VIEW, true, bundle);
    }

    private void loadAvatar(ImageView imageView, String user_avatar) {
        Utils.setImageWithGlide(fragmentActivity.getApplicationContext(), imageView, user_avatar, fragmentActivity.getDrawable(R.drawable.profile_placeholder), true);
    }

    @Override
    public int getItemCount() {
        return associationApiResponseModelResult.size();
    }

    public void setData(List<CommonUserApiResponseModel> associationApiResponseModelResult, int page) {
        if (page == 1) {
            this.associationApiResponseModelResult = associationApiResponseModelResult;
        } else {
            this.associationApiResponseModelResult.addAll(associationApiResponseModelResult);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView patientTemplateCv;
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientTemplateCv = (CardView) itemView.findViewById(R.id.patient_template_cv);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            titleTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            subTitleTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            actionIv = (ImageView) itemView.findViewById(R.id.action_iv);

        }
    }
}
