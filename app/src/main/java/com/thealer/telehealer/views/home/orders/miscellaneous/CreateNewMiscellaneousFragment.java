package com.thealer.telehealer.views.home.orders.miscellaneous;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.CreateMiscellaneousRequestModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousDetailBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

/**
 * Created by Aswin on 05,March,2019
 */
public class CreateNewMiscellaneousFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv;
    private TextView notesLabel;
    private EditText notesEt;
    private Button sendBtn;

    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel patientModel;
    private CommonUserApiResponseModel selectedPatientModel;
    private OrdersCreateApiViewModel ordersCreateApiViewModel;

    private boolean isFromHome;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.miscellaneous_success), patientModel.getUserDisplay_name()));
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
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.miscellaneous_failure), patientModel.getUserDisplay_name()));
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
        View view = inflater.inflate(R.layout.fragment_miscellaneous_create_new, container, false);
        setTitle(getString(R.string.new_miscellaneous));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        notesLabel = (TextView) view.findViewById(R.id.notes_label);
        notesEt = (EditText) view.findViewById(R.id.notes_et);
        sendBtn = (Button) view.findViewById(R.id.send_btn);

        patientOcv.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        notesEt.addTextChangedListener(new TextWatcher() {
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

        if (getArguments() != null) {
            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {
                patientOcv.setArrow_visible(false);
                patientOcv.setClickable(false);

                patientModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            }
        }

        if (selectedPatientModel != null) {
            patientModel = selectedPatientModel;
        }

        enableOrDisableSend();

        setPatientInfo();

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

    public void enableOrDisableSend() {

        boolean enable = false;

        if (patientModel != null &&
                !notesEt.getText().toString().isEmpty()) {
            enable = true;
        }

        sendBtn.setEnabled(enable);
    }

    @Override
    public void onAuthenticated() {
        createMiscellaneousOrder();
    }

    private void createMiscellaneousOrder() {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.posting_your_miscellaneous_request));
        successViewDialogFragment.setArguments(bundle);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

        MiscellaneousDetailBean miscellaneousDetailBean = new MiscellaneousDetailBean();
        miscellaneousDetailBean.setNotes(notesEt.getText().toString());

        CreateMiscellaneousRequestModel requestModel = new CreateMiscellaneousRequestModel();
        requestModel.setDetail(miscellaneousDetailBean);
        requestModel.setUser_guid(patientModel.getUser_guid());
        requestModel.setName("Miscellaneous Referral");

        ordersCreateApiViewModel.createMiscellaneousOrder(requestModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.patient_ocv:
                showAssociationSelection(1000, ArgumentKeys.SEARCH_ASSOCIATION);
                break;
            case R.id.send_btn:
                showQuickLogin();
                break;
        }
    }

    private void showAssociationSelection(int requestCode, String searchType) {
        Bundle bundle = getArguments();

        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putString(ArgumentKeys.SEARCH_TYPE, searchType);

        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();

        selectAssociationFragment.setArguments(bundle);

        selectAssociationFragment.setTargetFragment(this, requestCode);

        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

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
                }
            }
        }

    }

}
