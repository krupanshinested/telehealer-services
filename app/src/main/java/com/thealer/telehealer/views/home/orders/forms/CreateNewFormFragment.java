package com.thealer.telehealer.views.home.orders.forms;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.forms.CreateFormRequestModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersFormsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 28,November,2018
 */
public class CreateNewFormFragment extends OrdersBaseFragment implements View.OnClickListener, OnListItemSelectInterface {
    private OrdersCustomView patientOcv;
    private LinearLayout formsLl;
    private RecyclerView formsRv;
    private Button sendBtn;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel selectedPatientDetail;
    private OrdersApiViewModel ordersApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private FormsListAdapter formsListAdapter;
    private ArrayList<OrdersFormsApiResponseModel> formsList = new ArrayList<>();
    private ArrayList<OrdersFormsApiResponseModel> remainingFormsList = new ArrayList<>();
    private String userGuid = null;
    private List<String> selectedFormIds = new ArrayList<>();
    private OrdersCreateApiViewModel ordersCreateApiViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();

        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);
        ordersCreateApiViewModel = ViewModelProviders.of(this).get(OrdersCreateApiViewModel.class);

        attachObserverInterface.attachObserver(ordersApiViewModel);
        attachObserverInterface.attachObserver(ordersCreateApiViewModel);

        ordersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {

                    if (baseApiResponseModels.size() > 0) {

                        formsList.clear();
                        formsList = (ArrayList<OrdersFormsApiResponseModel>) (Object) baseApiResponseModels;

                        formsListAdapter.setFormsApiResponseModelArrayList(formsList);
                        formsLl.setVisibility(View.VISIBLE);

                    } else {
                        formsLl.setVisibility(View.GONE);
                        Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.no_forms_to_send), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, null);
                    }
                }
            }
        });

        ordersCreateApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        if (selectedFormIds.size() > 0) {
                            assignForms();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.selected_form_submitted_successfully));
                            LocalBroadcastManager
                                    .getInstance(getActivity())
                                    .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                            .putExtras(bundle));
                        }
                    }
                }
            }
        });

        ordersCreateApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                sendSuccessViewBroadCast(getActivity(), false, getString(R.string.failure), getString(R.string.create_form_failure));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_create_new, container, false);
        setTitle(getString(R.string.new_form));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        formsLl = (LinearLayout) view.findViewById(R.id.forms_ll);
        formsRv = (RecyclerView) view.findViewById(R.id.forms_rv);
        sendBtn = (Button) view.findViewById(R.id.send_btn);

        sendBtn.setOnClickListener(this);
        patientOcv.setOnClickListener(this);

        formsLl.setVisibility(View.GONE);

        enableOrDisableSend();

        formsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        formsListAdapter = new FormsListAdapter(getActivity(), this, remainingFormsList, selectedFormIds);
        formsRv.setAdapter(formsListAdapter);

        String title = getString(R.string.Click_here_to_select_patient), subtitle = null;
        boolean isSubtitleVisible = false;

        if (getArguments() != null) {
            CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (commonUserApiResponseModel != null) {

                patientOcv.setArrow_visible(false);
                patientOcv.setClickable(false);

                title = commonUserApiResponseModel.getUserDisplay_name();
                subtitle = commonUserApiResponseModel.getDob();
                isSubtitleVisible = true;
                userGuid = commonUserApiResponseModel.getUser_guid();
            }
        }

        if (selectedPatientDetail != null) {
            title = selectedPatientDetail.getUserDisplay_name();
            subtitle = selectedPatientDetail.getDob();
            isSubtitleVisible = true;

            userGuid = selectedPatientDetail.getUser_guid();
        }


        patientOcv.setTitleTv(title);
        patientOcv.setSubtitleTv(subtitle);
        patientOcv.setSub_title_visible(isSubtitleVisible);

        if (userGuid != null) {
            getAllForms();
        }

    }

    private void getAllForms() {
        ordersApiViewModel.getAllForms(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                showQuickLogin();
                break;
            case R.id.patient_ocv:
                Bundle bundle = getArguments();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString(ArgumentKeys.SEARCH_TYPE, ArgumentKeys.SEARCH_ASSOCIATION);
                SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
                selectAssociationFragment.setArguments(bundle);
                selectAssociationFragment.setTargetFragment(this, 1000);
                showSubFragmentInterface.onShowFragment(selectAssociationFragment);
                break;
        }
    }

    @Override
    public void onAuthenticated() {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
        assignForms();
    }

    private void assignForms() {
        ordersCreateApiViewModel.createForm(new CreateFormRequestModel(selectedFormIds.get(0), userGuid), false);
        selectedFormIds.remove(0);
    }

    public void enableOrDisableSend() {
        if (selectedFormIds.size() > 0) {
            sendBtn.setEnabled(true);
        } else {
            sendBtn.setEnabled(false);
        }
    }

    @Override
    public void onListItemSelected(int id, Bundle bundle) {
        if (selectedFormIds.contains(String.valueOf(id))) {
            selectedFormIds.remove(String.valueOf(id));
        } else {
            selectedFormIds.add(String.valueOf(id));
        }

        enableOrDisableSend();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW) {
            getAllForms();
            selectedFormIds.clear();
        } else if (requestCode == 1000) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                selectedPatientDetail = (CommonUserApiResponseModel) bundle.getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
            }
        }
    }
}
