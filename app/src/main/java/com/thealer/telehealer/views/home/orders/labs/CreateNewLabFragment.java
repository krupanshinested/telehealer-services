package com.thealer.telehealer.views.home.orders.labs;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.lab.LabsBean;
import com.thealer.telehealer.apilayer.models.orders.lab.LabsDetailBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.SendFaxByNumberFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 30,November,2018
 */
public class CreateNewLabFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv, visitOcv;
    private TextView labLabel;
    private RecyclerView labDescriptionRv;
    private TextView addTestTv;
    private View bottomView;
    private OrdersCustomView dateOcv;
    private OrdersCustomView copyOcv;

    private CommonUserApiResponseModel commonUserApiResponseModel;
    private CommonUserApiResponseModel selectedUserModel;
    private GetDoctorsApiResponseModel.DataBean specialistModel;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private LabTestDataViewModel labTestDataViewModel;
    private IcdCodesDataViewModel icdCodesDataViewModel;
    private LabDescriptionListAdapter labDescriptionListAdapter;
    private List<LabsBean> labsBeanList = new ArrayList<>();

    private DateBroadcastReceiver dateBroadcastReceiver = new DateBroadcastReceiver() {
        @Override
        public void onDateReceived(String formatedDate) {
            super.onDateReceived(formatedDate);
            dateOcv.setTitleTv(formatedDate);
        }
    };
    private Button saveBtn;
    private Button saveFaxBtn;
    private String doctorGuid;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        labTestDataViewModel = ViewModelProviders.of(getActivity()).get(LabTestDataViewModel.class);
        icdCodesDataViewModel = ViewModelProviders.of(getActivity()).get(IcdCodesDataViewModel.class);
        labTestDataViewModel.getLabsBeanLiveData().observe(this,
                new Observer<List<LabsBean>>() {
                    @Override
                    public void onChanged(@Nullable List<LabsBean> labsBeans) {
                        if (labsBeans != null) {
                            labsBeanList = labsBeans;
                            setDescriptionList();
                            enableOrDisableSend();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab_create_new, container, false);
        setTitle(getString(R.string.new_lab_order));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        visitOcv = (OrdersCustomView) view.findViewById(R.id.visit_ocv);
        labLabel = (TextView) view.findViewById(R.id.lab_label);
        labDescriptionRv = (RecyclerView) view.findViewById(R.id.lab_description_rv);
        addTestTv = (TextView) view.findViewById(R.id.add_test_tv);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        dateOcv = (OrdersCustomView) view.findViewById(R.id.date_ocv);
        copyOcv = (OrdersCustomView) view.findViewById(R.id.copy_ocv);
        saveBtn = (Button) view.findViewById(R.id.save_btn);
        saveFaxBtn = (Button) view.findViewById(R.id.save_fax_btn);

        patientOcv.setOnClickListener(this);
        addTestTv.setOnClickListener(this);
        dateOcv.setOnClickListener(this);
        copyOcv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveFaxBtn.setOnClickListener(this);

        if (isHideSendFax()) {
            saveFaxBtn.setVisibility(View.GONE);
            copyOcv.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            boolean isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {

                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                if (commonUserApiResponseModel != null){
                    patientOcv.setArrow_visible(false);
                    patientOcv.setClickable(false);
                }

                CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                if (doctorModel != null) {
                    doctorGuid = doctorModel.getUser_guid();
                }

            }

            if (selectedUserModel != null) {
                commonUserApiResponseModel = selectedUserModel;
            }
        }

        setPatientInfo();
        setCopyToInfo();

        labDescriptionRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        dateOcv.setTitleTv(Utils.getCurrentFomatedDate());

        enableOrDisableSend();
    }

    private void setDescriptionList() {
        labDescriptionListAdapter = new LabDescriptionListAdapter(getActivity(),
                labsBeanList,
                icdCodesDataViewModel.getIcdCodeDetailHashMap().getValue());
        labDescriptionRv.setAdapter(labDescriptionListAdapter);
    }

    private void setPatientInfo() {
        if (commonUserApiResponseModel != null) {
            patientOcv.setTitleTv(commonUserApiResponseModel.getUserDisplay_name());
            patientOcv.setSubtitleTv(commonUserApiResponseModel.getDob());
            patientOcv.setSub_title_visible(true);
            setVisitsView(visitOcv, commonUserApiResponseModel.getUser_guid(), doctorGuid);
            getPatientsRecentsList(commonUserApiResponseModel.getUser_guid(), doctorGuid);
        } else {
            patientOcv.setTitleTv(getString(R.string.Click_here_to_select_patient));
            patientOcv.setSub_title_visible(false);
        }
    }

    private void setCopyToInfo() {
        if (specialistModel != null) {
            copyOcv.setTitleTv(specialistModel.getDoctorDisplayName());
            copyOcv.setSubtitleTv(specialistModel.getDoctorSpecialist());
            copyOcv.setSub_title_visible(true);
        } else {
            copyOcv.setTitleTv(null);
            copyOcv.setSub_title_visible(false);
        }
    }

    public void enableOrDisableSend() {

        boolean enable = false;

        if (commonUserApiResponseModel != null &&
                labsBeanList.size() > 0) {
            enable = true;
        }

        saveBtn.setEnabled(enable);
        saveFaxBtn.setEnabled(enable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                showQuickLogin();
                break;
            case R.id.save_fax_btn:
                SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.ORDER_DATA, getLabRequestModel());
                bundle.putString(ArgumentKeys.USER_NAME, commonUserApiResponseModel.getUserDisplay_name());
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                sendFaxByNumberFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(sendFaxByNumberFragment);
                break;
            case R.id.copy_ocv:
                showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_DOCTOR, ArgumentKeys.SEARCH_COPY_TO);
                break;
            case R.id.date_ocv:
                showDatePicker();
                break;
            case R.id.add_test_tv:
                clearDataViewModel();
                SelectLabTestFragment selectLabTestFragment = new SelectLabTestFragment();
                showSubFragmentInterface.onShowFragment(selectLabTestFragment);
                break;
            case R.id.patient_ocv:
                showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_PATIENT, ArgumentKeys.SEARCH_ASSOCIATION);
                break;
        }
    }

    private CreateTestApiRequestModel getLabRequestModel() {

        CreateTestApiRequestModel createTestApiRequestModel = new CreateTestApiRequestModel();

        createTestApiRequestModel.setName(commonUserApiResponseModel.getUserDisplay_name());
        createTestApiRequestModel.setUser_guid(commonUserApiResponseModel.getUser_guid());
        createTestApiRequestModel.setOrder_id(getVistOrderId());

        if (specialistModel != null) {
            LabsDetailBean.CopyToBean copyToBean = new LabsDetailBean.CopyToBean(specialistModel.getDoctorDisplayName(),
                    specialistModel.getDoctorAddress(),
                    specialistModel.getDoctorPhone(),
                    specialistModel.getNpi());
            createTestApiRequestModel.getDetail().setCopy_to(copyToBean);
        }

        createTestApiRequestModel.getDetail().setLabs(labsBeanList);
        createTestApiRequestModel.getDetail().setRequested_date(dateOcv.getTitleText());

        return createTestApiRequestModel;
    }

    private void clearDataViewModel() {
        icdCodesDataViewModel.getSelectedIcdCodeList().setValue(null);
        labTestDataViewModel.setTestTitle(null);
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

    private void showDatePicker() {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATE_PICKER_TYPE, Constants.TYPE_ORDER_CREATION);
        datePickerDialogFragment.setArguments(bundle);
        datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), DatePickerDialogFragment.class.getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));
        setDescriptionList();
        enableOrDisableSend();
    }

    @Override
    public void onAuthenticated() {
        createNewLabOrder(getLabRequestModel(), commonUserApiResponseModel.getUserDisplay_name(), doctorGuid, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestID.REQ_SELECT_ASSOCIATION_PATIENT:
                    if (data != null && data.getExtras() != null) {
                        selectedUserModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                    }
                    break;
                case RequestID.REQ_SELECT_ASSOCIATION_DOCTOR:
                    if (data != null && data.getExtras() != null) {
                        specialistModel = (GetDoctorsApiResponseModel.DataBean) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                    }
                    break;
                case RequestID.REQ_SHOW_SUCCESS_VIEW:
                    onCloseActionInterface.onClose(false);
                    break;
            }
        }
    }
}
