package com.thealer.telehealer.views.home.orders.radiology;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.OrdersBaseFragment;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.labs.IcdCodeListAdapter;
import com.thealer.telehealer.views.home.orders.labs.IcdCodesDataViewModel;
import com.thealer.telehealer.views.home.orders.labs.SelectIcdCodeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 10,December,2018
 */
public class CreateNewRadiologyFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv;
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
    private Button sendBtn;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        icdCodesDataViewModel = ViewModelProviders.of(getActivity()).get(IcdCodesDataViewModel.class);
        radiologyListViewModel = ViewModelProviders.of(getActivity()).get(RadiologyListViewModel.class);

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
        sendBtn = (Button) view.findViewById(R.id.send_btn);

        patientOcv.setOnClickListener(this);
        copyResultOcv.setOnClickListener(this);
        radiologyLl.setOnClickListener(this);
        radiologyListIv.setOnClickListener(this);
        icdListLl.setOnClickListener(this);
        icdListIv.setOnClickListener(this);
        dateOcv.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

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

        sendBtn.setEnabled(enable);
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
        }
    }

    @Override
    public void onClick(View v) {
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
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DATE_PICKER_TYPE, Constants.TYPE_ORDER_CREATION);
                datePickerDialogFragment.setArguments(bundle);
                datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), datePickerDialogFragment.getClass().getSimpleName());
                break;
            case R.id.send_btn:
                showQuickLogin();
                break;
        }
    }

    private void createNewRadiology() {
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

        CreateRadiologyRequestModel createRadiologyRequestModel = new CreateRadiologyRequestModel(userModel.getUser_guid(),
                new CreateRadiologyRequestModel.DetailBean(commentsEt.getText().toString(),
                        dateOcv.getTitleText(),
                        copyToBean,
                        labsBeanList));

        OrdersCreateApiViewModel ordersCreateApiViewModel = ViewModelProviders.of(this).get(OrdersCreateApiViewModel.class);
        ordersCreateApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        sendSuccessViewBroadCast(getActivity(), baseApiResponseModel.isSuccess(), getString(R.string.success),
                                String.format(getString(R.string.create_radiology_success), userModel.getUserDisplay_name()));
                    }
                }
            }
        });

        ordersCreateApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), errorModel.isSuccess(), getString(R.string.failure),
                            String.format(getString(R.string.create_radiology_failure), userModel.getUserDisplay_name()));
                }
            }
        });
        ordersCreateApiViewModel.createRadiologyOrder(createRadiologyRequestModel);
        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        sendSuccessViewBroadCast(getActivity(), false, getString(R.string.posting), getString(R.string.posting_please_wait));
    }

    private void openSelectAssociationFragment(String type, int reqCode) {
        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(ArgumentKeys.SEARCH_TYPE, type);
        selectAssociationFragment.setArguments(bundle);
        selectAssociationFragment.setTargetFragment(this, reqCode);
        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));
        if (authResponse == ArgumentKeys.AUTH_SUCCESS) {
            createNewRadiology();
            authResponse = ArgumentKeys.AUTH_NONE;
        }
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
