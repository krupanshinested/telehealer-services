package com.thealer.telehealer.views.home.orders.specialist;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.base.OrdersBaseFragment;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

/**
 * Created by Aswin on 29,November,2018
 */
public class CreateNewSpecialistFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv;
    private OrdersCustomView specialistOcv;
    private TextInputLayout instructionTil;
    private EditText instructionEt;
    private Button sendBtn;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel patientModel;
    private CommonUserApiResponseModel selectedPatientModel;
    private GetDoctorsApiResponseModel.DataBean specialistModel;
    private boolean isFromHome;

    private OrdersCreateApiViewModel ordersCreateApiViewModel;
    private ChangeTitleInterface changeTitleInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        ordersCreateApiViewModel = ViewModelProviders.of(this).get(OrdersCreateApiViewModel.class);
        ordersCreateApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, "Your referral for " + patientModel.getUserDisplay_name() + " has been posted successfully.");
                        LocalBroadcastManager
                                .getInstance(getActivity())
                                .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                        .putExtras(bundle));

                    }
                }
            }
        });

        ordersCreateApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, "Oops Something went wrong");
                    LocalBroadcastManager
                            .getInstance(getActivity())
                            .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                    .putExtras(bundle));
                }
            }
        });
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
        specialistOcv = (OrdersCustomView) view.findViewById(R.id.specialist_ocv);
        instructionTil = (TextInputLayout) view.findViewById(R.id.instruction_til);
        instructionEt = (EditText) view.findViewById(R.id.instruction_et);
        sendBtn = (Button) view.findViewById(R.id.send_btn);

        patientOcv.setOnClickListener(this);
        specialistOcv.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

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

        sendBtn.setEnabled(enable);
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
            case R.id.send_btn:
                showQuickLogin();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (authResponse == ArgumentKeys.AUTH_SUCCESS) {
            assignSpecialist();
            authResponse = ArgumentKeys.AUTH_NONE;
        }
    }

    private void assignSpecialist() {
        AssignSpecialistRequestModel.DetailBean.CopyToBean copyToBean = new AssignSpecialistRequestModel.DetailBean.CopyToBean(specialistModel.getDoctorDisplayName(),
                specialistModel.getDoctorAddress(), specialistModel.getDoctorPhone(), specialistModel.getNpi());

        AssignSpecialistRequestModel.DetailBean detailBean = new AssignSpecialistRequestModel.DetailBean(instructionEt.getText().toString(),
                specialistModel.getDoctorDisplayName(),
                copyToBean);

        AssignSpecialistRequestModel assignSpecialistRequestModel = new AssignSpecialistRequestModel(patientModel.getUser_guid(), detailBean);


        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

        ordersCreateApiViewModel.assignSpecialist(assignSpecialistRequestModel, false);
    }

    private void showAssociationSelection(int requestCode, String searchType) {
        Bundle bundle = getArguments();

        if (bundle == null){
            bundle = new Bundle();
        }

        bundle.putString(ArgumentKeys.SEARCH_TYPE, searchType);

        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();

        selectAssociationFragment.setArguments(bundle);

        selectAssociationFragment.setTargetFragment(this, requestCode);

        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK &&
                requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW) {
            getActivity().finish();
        }

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
