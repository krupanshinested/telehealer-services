package com.thealer.telehealer.views.home.orders;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersBaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.CreateMiscellaneousRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

/**
 * Created by Aswin on 04,December,2018
 */
public class OrdersBaseFragment extends BaseFragment {
    private ChangeTitleInterface changeTitleInterface;

    public OrdersCreateApiViewModel ordersCreateApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private String currentOrder = "";
    private String patientName = "";
    private boolean isSendFax = false;
    public boolean onFaxSent = false;
    public boolean isShowSuccess = false;
    private static boolean onAuthenticated = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(quickLoginBroadcastReceiver, new IntentFilter(getString(R.string.quick_login_broadcast_receiver)));

        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        attachObserverInterface = (AttachObserverInterface) getActivity();

        ordersCreateApiViewModel = ViewModelProviders.of(this).get(OrdersCreateApiViewModel.class);

        attachObserverInterface.attachObserver(ordersCreateApiViewModel);

        ordersCreateApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    boolean status = true;
                    String title = getString(R.string.success);
                    String description = getString(R.string.order_posted_successfully);

                    OrdersBaseApiResponseModel ordersBaseApiResponseModel = null;
                    if (baseApiResponseModel instanceof OrdersBaseApiResponseModel) {
                        ordersBaseApiResponseModel = (OrdersBaseApiResponseModel) baseApiResponseModel;
                    }

                    switch (currentOrder) {
                        case OrderConstant.ORDER_PRESCRIPTIONS:
                            description = String.format(getString(R.string.create_prescription_success), patientName);
                            break;
                        case OrderConstant.ORDER_REFERRALS:
                            description = String.format(getString(R.string.referral_success), patientName);
                            break;
                        case OrderConstant.ORDER_LABS:
                            description = String.format(getString(R.string.create_lab_success), patientName);
                            break;
                        case OrderConstant.ORDER_RADIOLOGY:
                            description = String.format(getString(R.string.create_radiology_success), patientName);
                            break;
                        case OrderConstant.ORDER_MISC:
                            description = String.format(getString(R.string.miscellaneous_success), patientName);
                            break;
                    }

                    if (!isSendFax) {
                        sendSuccessViewBroadCast(getActivity(), status, title, description);
                        if (onFaxSent) {
                            onFaxSent = false;
                            if (isShowSuccess) {
                                description = getString(R.string.fax_sent_successfully);
                                sendSuccessViewBroadCast(getActivity(), status, title, description);
                            }
                            onCloseActionInterface.onClose(false);
                        }
                    } else {
                        isSendFax = false;
                        sendFax(ordersBaseApiResponseModel.getReferral_id(), false, false);
                    }
                }
            }
        });

        ordersCreateApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    boolean status = false;
                    String title = getString(R.string.failure);
                    String description = getString(R.string.order_posting_failed);

                    switch (currentOrder) {
                        case OrderConstant.ORDER_PRESCRIPTIONS:
                            description = String.format(getString(R.string.create_prescription_failure), patientName);
                            break;
                        case OrderConstant.ORDER_REFERRALS:
                            description = String.format(getString(R.string.referral_failure), patientName);
                            break;
                        case OrderConstant.ORDER_LABS:
                            description = String.format(getString(R.string.create_lab_failure), patientName);
                            break;
                        case OrderConstant.ORDER_RADIOLOGY:
                            description = String.format(getString(R.string.create_radiology_failure), patientName);
                            break;
                        case OrderConstant.ORDER_MISC:
                            description = String.format(getString(R.string.miscellaneous_failure), patientName);
                            break;
                    }
                    if (isSendFax) {
                        isSendFax = false;
                        description = errorModel.getMessage();
                    }
                    sendSuccessViewBroadCast(getActivity(), status, title, description);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
    }

    public void setTitle(String title) {
        changeTitleInterface.onTitleChange(title);
    }

    private QuickLoginBroadcastReceiver quickLoginBroadcastReceiver = new QuickLoginBroadcastReceiver() {
        @Override
        public void onQuickLogin(int status) {
            if (status == ArgumentKeys.AUTH_FAILED) {
                Utils.showAlertDialog(getActivity(), "Validation Failed", "User validation failed cannot process the current order", "OK", null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, null);
            } else {
                onAuthenticated = true;
            }
        }
    };

    public void showQuickLogin() {
        startActivity(new Intent(getActivity(), QuickLoginActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onAuthenticated) {
            onAuthenticated = false;
            onAuthenticated();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW && resultCode == Activity.RESULT_OK) {
            onCloseActionInterface.onClose(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(quickLoginBroadcastReceiver);
    }

    public void sendFax(int referral_id, boolean isShowProgress, boolean showSuccess) {
        onFaxSent = true;
        isShowSuccess = showSuccess;
        if (showSuccess) {
            showFaxSuccessView();
        }
    }

    private void showFaxSuccessView() {
        Bundle bundle = new Bundle();

        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));

        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.sending_your_fax));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);
    }

    public void onAuthenticated() {
    }

    private void showSuccessView() {
        Bundle bundle = new Bundle();

        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));

        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, String.format("Posting your %s order.", currentOrder));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);
    }

    public void assignSpecialist(AssignSpecialistRequestModel requestModel, String userName, String doctorGuid, boolean sendFax) {
        currentOrder = OrderConstant.ORDER_REFERRALS;
        patientName = userName;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.assignSpecialist(requestModel, doctorGuid, false);
    }

    public void createPrescription(CreatePrescriptionRequestModel prescriptionModel, String userDisplay_name, String doctorGuid, boolean sendFax) {

        currentOrder = OrderConstant.ORDER_PRESCRIPTIONS;
        patientName = userDisplay_name;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createPrescription(prescriptionModel, doctorGuid);
    }

    public void createNewRadiologyOrder(CreateRadiologyRequestModel requestModel, String userDisplay_name, String doctorGuid, boolean sendFax) {
        currentOrder = OrderConstant.ORDER_RADIOLOGY;
        patientName = userDisplay_name;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createRadiologyOrder(requestModel, doctorGuid);
    }

    public void createNewLabOrder(CreateTestApiRequestModel createTestApiRequestModel, String username, String doctorGuid, boolean sendFax) {

        currentOrder = OrderConstant.ORDER_LABS;
        patientName = username;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createLabOrder(createTestApiRequestModel, doctorGuid);
    }

    public void createNewMiscellaneousOrder(CreateMiscellaneousRequestModel miscellaneousOrderRequest, String userDisplay_name, String doctorGuid, boolean sendFax) {
        currentOrder = OrderConstant.ORDER_MISC;
        patientName = userDisplay_name;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createMiscellaneousOrder(miscellaneousOrderRequest, doctorGuid);
    }
}
