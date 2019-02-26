package com.thealer.telehealer.views.signup.doctor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 23,October,2018
 */
class DoctorResultListAdapter extends RecyclerView.Adapter<DoctorResultListAdapter.ViewHolder> {
    private OnActionCompleteInterface onActionCompleteInterface;
    private FragmentActivity fragmentActivity;
    private List<GetDoctorsApiResponseModel.DataBean> doctorsList = new ArrayList<>();

    public DoctorResultListAdapter(FragmentActivity activity) {
        this.fragmentActivity = activity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_user_card_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String name = doctorsList.get(i).getProfile().getFirst_name() + " " +
                doctorsList.get(i).getProfile().getLast_name() + ", " +
                doctorsList.get(i).getProfile().getTitle();

        viewHolder.doctorNameTv.setText(name);

        if (doctorsList.get(i).getSpecialties() != null &&
                !doctorsList.get(i).getSpecialties().isEmpty()) {
            viewHolder.doctorSpecialityTv.setText(doctorsList.get(i).getSpecialties().get(0).getActor());
        }

        if (doctorsList.get(i).getProfile().getImage_url() != null &&
                !doctorsList.get(i).getProfile().getImage_url().isEmpty()) {

            Glide.with(fragmentActivity.getApplicationContext())
                    .load(doctorsList.get(i).getProfile().getImage_url())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.profile_placeholder))
                    .into(viewHolder.listIv);
        }

        viewHolder.doctorListCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("aswin", "onClick: " + new Gson().toJson(doctorsList.get(i)));
                if (doctorsList.get(i).getPractices().size() < 2) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.IS_CREATE_MANUALLY, false);
                    bundle.putInt(Constants.PRACTICE_ID, 0);
                    bundle.putSerializable(Constants.DOCTOR_ID, i);
                    GetDoctorsApiResponseModel.DataBean dataBean = doctorsList.get(i);
                    bundle.putSerializable(Constants.USER_DETAIL, dataBean);

                    onActionCompleteInterface.onCompletionResult(null, true, bundle);

                } else {
                    DoctorSelectPracticeBottomSheetFragment doctorSelectPracticeBottomSheetFragment = new DoctorSelectPracticeBottomSheetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SELECTE_POSITION, i);
                    bundle.putSerializable(Constants.USER_DETAIL, doctorsList.get(i));
                    doctorSelectPracticeBottomSheetFragment.setArguments(bundle);

                    doctorSelectPracticeBottomSheetFragment.show(fragmentActivity.getSupportFragmentManager(), doctorSelectPracticeBottomSheetFragment.getClass().getSimpleName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }

    public void setData(List<GetDoctorsApiResponseModel.DataBean> data, int page) {
        if (page == 1) {
            doctorsList = data;
        } else {
            doctorsList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView listIv;
        private TextView doctorNameTv;
        private TextView doctorSpecialityTv;
        private CardView doctorListCv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listIv = (CircleImageView) itemView.findViewById(R.id.list_iv);
            doctorNameTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            doctorSpecialityTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            doctorListCv = (CardView) itemView.findViewById(R.id.list_cv);
        }
    }
}
