package com.thealer.telehealer.views.home.orders.radiology;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
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
import com.thealer.telehealer.views.home.orders.labs.IcdCodeListAdapter;
import com.thealer.telehealer.views.home.orders.labs.IcdCodesDataViewModel;
import com.thealer.telehealer.views.home.orders.labs.SelectIcdCodeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 10,December,2018
 */
public class CreateNewRadiologyFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv, visitOcv;
    private OrdersCustomView copyResultOcv;
    private ConstraintLayout radiologyLl;
    private RecyclerView radiologyListRv;
    private ImageView radiologyListIv;
    private ConstraintLayout icdListLl;
    private RecyclerView icdListRv;
    private ImageView icdListIv;
    private Switch statSwitch;
    private OrdersCustomView dateOcv;
    private EditText commentsEt;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private CommonUserApiResponseModel userModel;
    private CommonUserApiResponseModel selectedModel;
    private GetDoctorsApiResponseModel.DataBean copyToModel;
    private IcdCodesDataViewModel icdCodesDataViewModel;
    private RadiologyListViewModel radiologyListViewModel;

    private List<String> selectedIcdCodeList = new ArrayList<>();
    private HashMap<String, IcdCodeApiResponseModel.ResultsBean> icdCodeDetailHashMap = new HashMap<>();
    private IcdCodeListAdapter icdCodeListAdapter;
    private RadiologyListPreviewAdapter radiologyListPreviewAdapter;
    private List<RadiologyListModel> radiologyModelList = new ArrayList<>();

    private DateBroadcastReceiver dateBroadcastReceiver = new DateBroadcastReceiver() {
        @Override
        public void onDateReceived(String formatedDate) {
            setDate(formatedDate);
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
        icdCodesDataViewModel = new ViewModelProvider(getActivity()).get(IcdCodesDataViewModel.class);
        radiologyListViewModel = new ViewModelProvider(getActivity()).get(RadiologyListViewModel.class);

        icdCodesDataViewModel.getSelectedIcdCodeList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {
                if (list != null) {
                    selectedIcdCodeList = list;
                    enableOrDisableSend();
                }
            }
        });

        icdCodesDataViewModel.getIcdCodeDetailHashMap().observe(this, new Observer<HashMap<String, IcdCodeApiResponseModel.ResultsBean>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, IcdCodeApiResponseModel.ResultsBean> resultsBeanHashMap) {
                if (resultsBeanHashMap != null) {
                    icdCodeDetailHashMap = resultsBeanHashMap;
                    if (icdCodeListAdapter != null) {
                        icdCodeListAdapter.setData(selectedIcdCodeList, icdCodeDetailHashMap);
                    }
                }
            }
        });

        radiologyListViewModel.getSelectedRadiologyListLiveData().observe(this, new Observer<List<RadiologyListModel>>() {
            @Override
            public void onChanged(@Nullable List<RadiologyListModel> radiologyListModels) {
                radiologyModelList = radiologyListModels;
                enableOrDisableSend();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radiology_create_new, container, false);
        setTitle(getString(R.string.new_radiology));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        visitOcv = (OrdersCustomView) view.findViewById(R.id.visit_ocv);
        copyResultOcv = (OrdersCustomView) view.findViewById(R.id.copy_result_ocv);
        radiologyLl = (ConstraintLayout) view.findViewById(R.id.radiology_ll);
        radiologyListRv = (RecyclerView) view.findViewById(R.id.radiology_list_rv);
        radiologyListIv = (ImageView) view.findViewById(R.id.radiology_list_iv);
        icdListLl = (ConstraintLayout) view.findViewById(R.id.icd_list_ll);
        icdListRv = (RecyclerView) view.findViewById(R.id.icd_list_rv);
        icdListIv = (ImageView) view.findViewById(R.id.icd_list_iv);
        statSwitch = (Switch) view.findViewById(R.id.stat_switch);
        dateOcv = (OrdersCustomView) view.findViewById(R.id.date_ocv);
        commentsEt = (EditText) view.findViewById(R.id.comments_et);
        saveBtn = (Button) view.findViewById(R.id.save_btn);
        saveFaxBtn = (Button) view.findViewById(R.id.save_fax_btn);

        patientOcv.setOnClickListener(this);
        copyResultOcv.setOnClickListener(this);
        radiologyLl.setOnClickListener(this);
        radiologyListIv.setOnClickListener(this);
        icdListLl.setOnClickListener(this);
        icdListIv.setOnClickListener(this);
        dateOcv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveFaxBtn.setOnClickListener(this);

        if (appConfig.isIndianUser(getActivity())) {
            copyResultOcv.setVisibility(View.GONE);
        }
        icdListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        icdCodeListAdapter = new IcdCodeListAdapter(getActivity(), selectedIcdCodeList, icdCodeDetailHashMap);
        icdListRv.setAdapter(icdCodeListAdapter);

        radiologyListRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (radiologyListViewModel.getSelectedRadiologyListLiveData().getValue() != null) {
            radiologyModelList = radiologyListViewModel.getSelectedRadiologyListLiveData().getValue();
        }

        radiologyListPreviewAdapter = new RadiologyListPreviewAdapter(getActivity(), radiologyModelList);
        radiologyListRv.setAdapter(radiologyListPreviewAdapter);

        if (getArguments() != null) {
            boolean isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);
            if (!isFromHome) {

                userModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                if (userModel != null) {
                    patientOcv.setArrow_visible(false);
                    patientOcv.setClickable(false);
                }

                CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                if (doctorModel != null) {
                    doctorGuid = doctorModel.getUser_guid();
                }

            }

            if (selectedModel != null) {
                userModel = selectedModel;
            }
        }
        setUserData();
        setCopytoData();
        setDate(Utils.getCurrentFomatedDate());
        enableOrDisableSend();

    }

    private void enableOrDisableSend() {
        boolean enable = false;

        if (userModel != null &&
                !radiologyModelList.isEmpty() &&
                !selectedIcdCodeList.isEmpty()) {
            enable = true;
        }

        saveBtn.setEnabled(enable);
        saveFaxBtn.setEnabled(enable);
    }

    private void setDate(String date) {
        dateOcv.setTitleTv(date);
    }

    private void setCopytoData() {
        if (copyToModel != null) {
            copyResultOcv.setTitleTv(copyToModel.getDoctorDisplayName());
            copyResultOcv.setSubtitleTv(copyToModel.getDoctorSpecialist());
            copyResultOcv.setSub_title_visible(true);
        }
    }

    private void setUserData() {
        if (userModel != null) {
            patientOcv.setTitleTv(userModel.getUserDisplay_name());
            patientOcv.setSubtitleTv(userModel.getDob());
            patientOcv.setSub_title_visible(true);
            setVisitsView(visitOcv, userModel.getUser_guid(), doctorGuid);
            getPatientsRecentsList(userModel.getUser_guid(), doctorGuid);
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.patient_ocv:
                openSelectAssociationFragment(ArgumentKeys.SEARCH_ASSOCIATION, RequestID.REQ_SELECT_ASSOCIATION);
                break;
            case R.id.copy_result_ocv:
                openSelectAssociationFragment(ArgumentKeys.SEARCH_COPY_TO, RequestID.REQ_SELECT_ASSOCIATION_DOCTOR);
                break;
            case R.id.radiology_ll:
            case R.id.radiology_list_iv:
                SelectRadiologyTestFragment selectRadiologyTestFragment = new SelectRadiologyTestFragment();
                showSubFragmentInterface.onShowFragment(selectRadiologyTestFragment);
                break;
            case R.id.icd_list_ll:
            case R.id.icd_list_iv:
                SelectIcdCodeFragment selectIcdCodeFragment = new SelectIcdCodeFragment();
                showSubFragmentInterface.onShowFragment(selectIcdCodeFragment);
                break;
            case R.id.date_ocv:
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                bundle = new Bundle();
                bundle.putInt(Constants.DATE_PICKER_TYPE, Constants.TYPE_ORDER_CREATION);
                datePickerDialogFragment.setArguments(bundle);
                datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), datePickerDialogFragment.getClass().getSimpleName());
                break;
            case R.id.save_btn:
                showQuickLogin();
                break;
            case R.id.save_fax_btn:
                SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.ORDER_DATA, getRequest());
                bundle.putString(ArgumentKeys.USER_NAME, userModel.getUserDisplay_name());
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                sendFaxByNumberFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(sendFaxByNumberFragment);
                break;
        }
    }

    private CreateRadiologyRequestModel getRequest() {
        List<CreateRadiologyRequestModel.DetailBean.LabsBean> labsBeanList = new ArrayList<>();
        labsBeanList.add(new CreateRadiologyRequestModel.DetailBean.LabsBean(statSwitch.isChecked(),
                selectedIcdCodeList,
                radiologyModelList));


        CreateRadiologyRequestModel.DetailBean.CopyToBean copyToBean = null;
        if (copyToModel != null) {
            copyToBean = new CreateRadiologyRequestModel.DetailBean.CopyToBean(copyToModel.getDoctorDisplayName(),
                    copyToModel.getDoctorAddress(),
                    copyToModel.getDoctorPhone(),
                    copyToModel.getNpi());
        }

        return new CreateRadiologyRequestModel(userModel.getUser_guid(), getVistOrderId(),
                new CreateRadiologyRequestModel.DetailBean(commentsEt.getText().toString().trim(),
                        dateOcv.getTitleText(),
                        copyToBean,
                        labsBeanList));
    }

    private void openSelectAssociationFragment(String type, int reqCode) {
        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(ArgumentKeys.SEARCH_TYPE, type);
        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
        selectAssociationFragment.setArguments(bundle);
        selectAssociationFragment.setTargetFragment(this, reqCode);
        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));
    }

    @Override
    public void onAuthenticated() {
        createNewRadiologyOrder(false,getRequest(), userModel.getUserDisplay_name(), doctorGuid, false);
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
                case RequestID.REQ_SELECT_ASSOCIATION:
                    selectedModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                    userModel = selectedModel;
                    setUserData();
                    break;
                case RequestID.REQ_SELECT_ASSOCIATION_DOCTOR:
                    copyToModel = (GetDoctorsApiResponseModel.DataBean) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                    setCopytoData();
                    break;
                case RequestID.REQ_SHOW_SUCCESS_VIEW:
                    onCloseActionInterface.onClose(false);
                    break;

            }
        }
        enableOrDisableSend();
    }
}
