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
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.monitoring.diet.DietDetailFragment;
import com.thealer.telehealer.views.home.monitoring.diet.DietListingFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 13,November,2018
 */
public class DoctorPatientListAdapter extends RecyclerView.Adapter<DoctorPatientListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private List<CommonUserApiResponseModel> associationApiResponseModelResult;
    private OnActionCompleteInterface onActionCompleteInterface;
    private boolean isDietView;
    private Bundle bundle;

    public DoctorPatientListAdapter(FragmentActivity activity, boolean isDietView, Bundle bundle) {
        fragmentActivity = activity;
        associationApiResponseModelResult = new ArrayList<>();
        onActionCompleteInterface = (OnActionCompleteInterface) activity;
        this.isDietView = isDietView;
        this.bundle = bundle;
        if (bundle == null) {
            this.bundle = new Bundle();
        }
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

        viewHolder.userListIv.setStatus(associationApiResponseModelResult.get(i).getStatus());
    }

    private void proceed(CommonUserApiResponseModel resultBean) {
        bundle.putSerializable(Constants.USER_DETAIL, resultBean);
        if (!isDietView) {
            onActionCompleteInterface.onCompletionResult(RequestID.REQ_SHOW_DETAIL_VIEW, true, bundle);
        } else {
            ShowSubFragmentInterface showSubFragmentInterface = (ShowSubFragmentInterface) fragmentActivity;
            DietListingFragment dietListingFragment = new DietListingFragment();
            dietListingFragment.setArguments(bundle);
            showSubFragmentInterface.onShowFragment(dietListingFragment);
        }
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

        Collections.sort(associationApiResponseModelResult,
                new Comparator<CommonUserApiResponseModel>() {
                    @Override
                    public int compare(CommonUserApiResponseModel o1, CommonUserApiResponseModel o2) {
                        return o1.getFirst_name().compareToIgnoreCase(o2.getFirst_name());
                    }
                });

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView patientTemplateCv;
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;
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
