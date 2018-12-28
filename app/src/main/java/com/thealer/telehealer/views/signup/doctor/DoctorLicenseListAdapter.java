package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.CurrentModeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 26,October,2018
 */
class DoctorLicenseListAdapter extends RecyclerView.Adapter<DoctorLicenseListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private CreateUserRequestModel createUserRequestModel;
    private CurrentModeInterface currentModeInterface;
    private List<LicensesBean> licensesBeanList = new ArrayList<>();

    public DoctorLicenseListAdapter(FragmentActivity activity, CurrentModeInterface currentModeInterface) {
        fragmentActivity = activity;
        this.currentModeInterface = currentModeInterface;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        createUserRequestModel = ViewModelProviders.of(fragmentActivity).get(CreateUserRequestModel.class);
        licensesBeanList = createUserRequestModel.getUser_detail().getData().getLicenses();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_license_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (isValidData(licensesBeanList.get(i).getNumber())) {
            viewHolder.licenseNumberTv.setText(licensesBeanList.get(i).getNumber());
            showError(viewHolder, viewHolder.licenseNumberTv, false, null, i);
        } else {
            viewHolder.licenseNumberTv.setText("License no : N/A");
            showError(viewHolder, viewHolder.licenseNumberTv, true, fragmentActivity.getString(R.string.license_number_empty_error), i);
        }

        if (isValidData(licensesBeanList.get(i).getState())) {
            viewHolder.stateTv.setText("State : " + licensesBeanList.get(i).getState());
            showError(viewHolder, viewHolder.stateTv, false, null, i);
        } else {
            viewHolder.stateTv.setText("State : N/A");
            showError(viewHolder, viewHolder.stateTv, true, fragmentActivity.getString(R.string.state_empty_error), i);
        }

        if (isValidData(licensesBeanList.get(i).getEnd_date())) {
            viewHolder.expirationTv.setText("Exp : " + licensesBeanList.get(i).getEnd_date());
            if (!Utils.isDateExpired(licensesBeanList.get(i).getEnd_date())) {
                showError(viewHolder, viewHolder.expirationTv, true, fragmentActivity.getString(R.string.expired_date_error), i);
            } else {
                showError(viewHolder, viewHolder.expirationTv, false, null, i);
            }
        } else {
            viewHolder.expirationTv.setText("Exp : N/A");
            showError(viewHolder, viewHolder.expirationTv, true, fragmentActivity.getString(R.string.expiration_empty_error), i);
        }

        if (!isValidData(licensesBeanList.get(i).getNumber()) &&
                !isValidData(licensesBeanList.get(i).getState())) {
            viewHolder.errorTv.setText(fragmentActivity.getString(R.string.license_state_empty_error));
        } else if (!isValidData(licensesBeanList.get(i).getNumber()) &&
                !isValidData(licensesBeanList.get(i).getEnd_date())) {
            viewHolder.errorTv.setText(fragmentActivity.getString(R.string.license_date_empty_error));
        } else if (!isValidData(licensesBeanList.get(i).getState()) &&
                !isValidData(licensesBeanList.get(i).getEnd_date())) {
            viewHolder.errorTv.setText(fragmentActivity.getString(R.string.state_date_empty_error));
        }

        viewHolder.licenseCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentModeInterface.getCurrentMode() != Constants.VIEW_MODE) {
                    DoctorLicenseBottomSheetFragment doctorLicenseBottomSheetFragment = new DoctorLicenseBottomSheetFragment();

                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.LICENSE_ID, i);
                    doctorLicenseBottomSheetFragment.setArguments(bundle);

                    doctorLicenseBottomSheetFragment.setTargetFragment(fragmentActivity.getSupportFragmentManager().getFragments().get(0), RequestID.REQ_LICENSE);
                    doctorLicenseBottomSheetFragment.show(fragmentActivity.getSupportFragmentManager(), doctorLicenseBottomSheetFragment.getClass().getSimpleName());
                }
            }
        });
    }

    private boolean isValidData(java.lang.String data) {
        return data != null && !data.isEmpty();
    }

    private void showError(ViewHolder viewHolder, TextView textView, boolean isShow, java.lang.String error, int position) {
        viewHolder.errorTv.setText(error);
        if (isShow) {
            textView.setTextColor(fragmentActivity.getColor(android.R.color.holo_red_light));
            viewHolder.view.setBackgroundColor(fragmentActivity.getColor(android.R.color.holo_red_light));
            viewHolder.errorTv.setVisibility(View.VISIBLE);
            Log.e("aswin", "showError: " + position);
            Log.e("aswin", "showError: " + new Gson().toJson(createUserRequestModel));
            if (createUserRequestModel.getHasValidLicensesList().size() != 0 &&
                    createUserRequestModel.getHasValidLicensesList().size() > position) {
                createUserRequestModel.getHasValidLicensesList().set(position, false);
            } else {
                createUserRequestModel.getHasValidLicensesList().add(false);
            }
        } else {
            textView.setTextColor(fragmentActivity.getColor(R.color.colorBlack));
            viewHolder.view.setBackgroundColor(fragmentActivity.getColor(R.color.colorGrey));
            viewHolder.errorTv.setVisibility(View.GONE);
            if (createUserRequestModel.getHasValidLicensesList().size() > position) {
                createUserRequestModel.getHasValidLicensesList().set(position, true);
            } else {
                createUserRequestModel.getHasValidLicensesList().add(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return licensesBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView licenseNumberTv;
        private TextView stateTv;
        private TextView expirationTv;
        private ImageView viewIv;
        private ConstraintLayout licenseCl;
        private View view;
        private TextView errorTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            licenseNumberTv = (TextView) itemView.findViewById(R.id.license_number_tv);
            stateTv = (TextView) itemView.findViewById(R.id.state_tv);
            expirationTv = (TextView) itemView.findViewById(R.id.expiration_tv);
            viewIv = (ImageView) itemView.findViewById(R.id.view_iv);
            licenseCl = (ConstraintLayout) itemView.findViewById(R.id.license_cl);
            view = (View) itemView.findViewById(R.id.view);
            errorTv = (TextView) itemView.findViewById(R.id.error_tv);
        }
    }
}
