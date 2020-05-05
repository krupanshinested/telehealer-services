package com.thealer.telehealer.views.home.orders.specialist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.SendFaxByNumberFragment;

/**
 * Created by Aswin on 29,November,2018
 */
public class CreateNewSpecialistFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv, visitOcv;
    private OrdersCustomView specialistOcv;
    private TextInputLayout instructionTil;
    private EditText instructionEt;
    private Button saveFaxBtn;
    private Button saveBtn;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel patientModel;
    private CommonUserApiResponseModel selectedPatientModel;
    private GetDoctorsApiResponseModel.DataBean specialistModel;
    private boolean isFromHome;
    private String doctorGuid;

    private ChangeTitleInterface changeTitleInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_specialist_create_new, container, false);
        setTitle(getString(R.string.new_specialist));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        visitOcv = (OrdersCustomView) view.findViewById(R.id.visit_ocv);
        specialistOcv = (OrdersCustomView) view.findViewById(R.id.specialist_ocv);
        instructionTil = (TextInputLayout) view.findViewById(R.id.instruction_til);
        instructionEt = (EditText) view.findViewById(R.id.instruction_et);
        saveBtn = (Button) view.findViewById(R.id.save_btn);
        saveFaxBtn = (Button) view.findViewById(R.id.save_fax_btn);

        patientOcv.setOnClickListener(this);
        specialistOcv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveFaxBtn.setOnClickListener(this);

        instructionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableOrDisableSend();
            }
        });

        enableOrDisableSend();

        if (getArguments() != null) {

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {

                patientModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                if (patientModel != null) {
                    patientOcv.setArrow_visible(false);
                    patientOcv.setClickable(false);
                }

                CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                if (doctorModel != null) {
                    doctorGuid = doctorModel.getUser_guid();
                }
            }

        }

        if (selectedPatientModel != null) {
            patientModel = selectedPatientModel;
        }

        setPatientInfo();
        setSpecialistInfo();
    }

    private void setPatientInfo() {
        if (patientModel != null) {
            patientOcv.setTitleTv(patientModel.getUserDisplay_name());
            patientOcv.setSubtitleTv(patientModel.getDob());
            patientOcv.setSub_title_visible(true);
            setVisitsView(visitOcv, patientModel.getUser_guid(), doctorGuid);
            getPatientsRecentsList(patientModel.getUser_guid(), doctorGuid);
        } else {
            patientOcv.setTitleTv(getString(R.string.Click_here_to_select_patient));
            patientOcv.setSub_title_visible(false);
        }
    }

    private void setSpecialistInfo() {
        if (specialistModel != null) {
            specialistOcv.setTitleTv(specialistModel.getDoctorDisplayName());
            specialistOcv.setSubtitleTv(specialistModel.getDoctorSpecialist());
            specialistOcv.setSub_title_visible(true);
        } else {
            specialistOcv.setTitleTv(null);
            specialistOcv.setSub_title_visible(false);
        }
    }

    public void enableOrDisableSend() {

        boolean enable = false;

        if (patientModel != null &&
                specialistModel != null &&
                !instructionEt.getText().toString().isEmpty()) {
            enable = true;
        }

        saveBtn.setEnabled(enable);
        saveFaxBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.patient_ocv:
                showAssociationSelection(1000, ArgumentKeys.SEARCH_ASSOCIATION);
                break;
            case R.id.specialist_ocv:
                showAssociationSelection(2000, ArgumentKeys.SEARCH_DOCTOR);
                break;
            case R.id.save_btn:
                showQuickLogin();
                break;
            case R.id.save_fax_btn:
                AssignSpecialistRequestModel assignSpecialistRequestModel = getRequestModel();

                SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.ORDER_DATA, assignSpecialistRequestModel);
                bundle.putString(ArgumentKeys.USER_NAME, patientModel.getDisplayName());
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                sendFaxByNumberFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(sendFaxByNumberFragment);
                break;
        }
    }

    @Override
    public void onAuthenticated() {
        assignSpecialist(false,getRequestModel(), patientModel.getDisplayName(), doctorGuid, false);
    }

    private AssignSpecialistRequestModel getRequestModel() {
        AssignSpecialistRequestModel.DetailBean.CopyToBean copyToBean = new AssignSpecialistRequestModel.DetailBean.CopyToBean(specialistModel.getDoctorDisplayName(),
                specialistModel.getDoctorAddress(), specialistModel.getDoctorPhone(), specialistModel.getNpi());

        AssignSpecialistRequestModel.DetailBean detailBean = new AssignSpecialistRequestModel.DetailBean(instructionEt.getText().toString(),
                specialistModel.getDoctorDisplayName(),
                copyToBean);
        return new AssignSpecialistRequestModel(patientModel.getUser_guid(), getVistOrderId(), detailBean);
    }

    private void showAssociationSelection(int requestCode, String searchType) {
        Bundle bundle = getArguments();

        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putString(ArgumentKeys.SEARCH_TYPE, searchType);
        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();

        selectAssociationFragment.setArguments(bundle);

        selectAssociationFragment.setTargetFragment(this, requestCode);

        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                switch (requestCode) {
                    case 1000:
                        selectedPatientModel = (CommonUserApiResponseModel) bundle.getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        setPatientInfo();
                        enableOrDisableSend();
                        break;
                    case 2000:
                        specialistModel = (GetDoctorsApiResponseModel.DataBean) bundle.getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        setSpecialistInfo();
                        enableOrDisableSend();
                        break;
                }
            }
        }
    }
}
