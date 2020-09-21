package com.thealer.telehealer.views.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 15,October,2018
 */
public class RoleSelectionFragment extends BaseFragment implements View.OnClickListener, DoCurrentTransactionInterface {
    private ImageView doctorIv;
    private ImageView medicalAssistantIv;
    private TextView doctorTv;
    private TextView medicalAssistantTv;
    private OnViewChangeInterface viewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private CreateUserRequestModel createUserRequestModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_role, container, false);

        createUserRequestModel = ViewModelProviders.of(this).get(CreateUserRequestModel.class);

        initView(view);

        return view;
    }

    private void initView(View view) {
        doctorIv = (ImageView) view.findViewById(R.id.doctor_iv);
        doctorTv = (TextView) view.findViewById(R.id.doctor_tv);
        medicalAssistantIv = (ImageView) view.findViewById(R.id.medical_assistant_iv);
        medicalAssistantTv = (TextView) view.findViewById(R.id.medical_assistant_tv);

        doctorTv.setOnClickListener(this);
        doctorIv.setOnClickListener(this);
        medicalAssistantTv.setOnClickListener(this);
        medicalAssistantIv.setOnClickListener(this);

        selectDoctor();

        viewChangeInterface.enableNext(true);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doctor_iv:
            case R.id.doctor_tv:
                selectDoctor();
                break;
            case R.id.medical_assistant_iv:
            case R.id.medical_assistant_tv:
                selectAssistant();
                break;
        }
    }

    private void selectAssistant() {
        appPreference.setInt(Constants.USER_TYPE, Constants.TYPE_MEDICAL_ASSISTANT);
        enableOrDisableAssistant(true);
        enableOrDisableDoctor(false);
        createUserRequestModel.getUser_data().setRole(Constants.ROLE_ASSISTANT);
    }

    private void selectDoctor() {
        appPreference.setInt(Constants.USER_TYPE, Constants.TYPE_DOCTOR);
        enableOrDisableDoctor(true);
        enableOrDisableAssistant(false);
        createUserRequestModel.getUser_data().setRole(Constants.ROLE_DOCTOR);
    }

    private void enableOrDisableAssistant(boolean b) {
        if (b){
            ImageViewCompat.setImageTintList(medicalAssistantIv, ColorStateList.valueOf(getResources().getColor(R.color.app_gradient_start)));
            medicalAssistantTv.setTextColor(getResources().getColor(R.color.app_gradient_start));
        }else {
            ImageViewCompat.setImageTintList(medicalAssistantIv, ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
            medicalAssistantTv.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    private void enableOrDisableDoctor(boolean b) {
        if (b){
            ImageViewCompat.setImageTintList(doctorIv, ColorStateList.valueOf(getResources().getColor(R.color.app_gradient_start)));
            doctorTv.setTextColor(getResources().getColor(R.color.app_gradient_start));
        }else {
            ImageViewCompat.setImageTintList(doctorIv, ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
            doctorTv.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    @Override
    public void doCurrentTransaction() {
        onActionCompleteInterface.onCompletionResult(null, true, null);
    }
}
