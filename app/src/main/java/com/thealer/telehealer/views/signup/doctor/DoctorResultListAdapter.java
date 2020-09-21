package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 23,October,2018
 */
class DoctorResultListAdapter extends RecyclerView.Adapter<DoctorResultListAdapter.ViewHolder> {
    private OnActionCompleteInterface onActionCompleteInterface;
    private FragmentActivity fragmentActivity;
    private GetDoctorsApiResponseModel getDoctorsApiResponseModel;

    public DoctorResultListAdapter(FragmentActivity activity) {
        this.fragmentActivity = activity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        GetDoctorsApiViewModel getDoctorsApiViewModel = ViewModelProviders.of(fragmentActivity).get(GetDoctorsApiViewModel.class);

        getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<GetDoctorsApiResponseModel>() {
            @Override
            public void onChanged(@Nullable GetDoctorsApiResponseModel doctorsApiResponseModel) {
                getDoctorsApiResponseModel = doctorsApiResponseModel;
                notifyDataSetChanged();
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String name = getDoctorsApiResponseModel.getData().get(i).getProfile().getFirst_name() + " " +
                getDoctorsApiResponseModel.getData().get(i).getProfile().getLast_name() + ", " +
                getDoctorsApiResponseModel.getData().get(i).getProfile().getTitle();

        viewHolder.doctorNameTv.setText(name);

        if (getDoctorsApiResponseModel.getData().get(i).getSpecialties() != null &&
                !getDoctorsApiResponseModel.getData().get(i).getSpecialties().isEmpty()) {
            viewHolder.doctorSpecialityTv.setText(getDoctorsApiResponseModel.getData().get(i).getSpecialties().get(0).getActor());
        }

        if (getDoctorsApiResponseModel.getData().get(i).getProfile().getImage_url() != null &&
                !getDoctorsApiResponseModel.getData().get(i).getProfile().getImage_url().isEmpty()) {

            Glide.with(fragmentActivity.getApplicationContext())
                    .load(getDoctorsApiResponseModel.getData().get(i).getProfile().getImage_url())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.profile_placeholder))
                    .into(viewHolder.listIv);
        }

        viewHolder.doctorListCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDoctorsApiResponseModel.getData().get(i).getPractices().size() < 2) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.IS_CREATE_MANUALLY, false);
                    bundle.putInt(Constants.PRACTICE_ID, 0);
                    bundle.putSerializable(Constants.DOCTOR_ID, i);

                    onActionCompleteInterface.onCompletionResult(null, true, bundle);

                } else {
                    DoctorSelectPracticeBottomSheetFragment doctorSelectPracticeBottomSheetFragment = new DoctorSelectPracticeBottomSheetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SELECTE_POSITION, i);
                    doctorSelectPracticeBottomSheetFragment.setArguments(bundle);

                    doctorSelectPracticeBottomSheetFragment.show(fragmentActivity.getSupportFragmentManager(), doctorSelectPracticeBottomSheetFragment.getClass().getSimpleName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (getDoctorsApiResponseModel == null) {
            return 0;
        } else
            return getDoctorsApiResponseModel.getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView listIv;
        private TextView doctorNameTv;
        private TextView doctorSpecialityTv;
        private CardView doctorListCv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listIv = (CircleImageView) itemView.findViewById(R.id.list_iv);
            doctorNameTv = (TextView) itemView.findViewById(R.id.doctor_name_tv);
            doctorSpecialityTv = (TextView) itemView.findViewById(R.id.doctor_speciality_tv);
            doctorListCv = (CardView) itemView.findViewById(R.id.doctor_list_cv);
        }
    }
}
