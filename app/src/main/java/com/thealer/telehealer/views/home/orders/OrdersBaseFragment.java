package com.thealer.telehealer.views.home.orders;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersBaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.CreateMiscellaneousRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.common.RecentsSelectionActivity;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 04,December,2018
 */
public class OrdersBaseFragment extends BaseFragment {
    private ChangeTitleInterface changeTitleInterface;

    public OrdersCreateApiViewModel ordersCreateApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private RecentsApiViewModel recentsApiViewModel;
    private ShowSubFragmentInterface showSubFragmentInterface;

    private String currentOrder = "";
    private String patientName = "";
    private String userGuid = "", doctorGuid = "";
    private boolean isSendFax = false;
    public boolean onFaxSent = false;
    public boolean isShowSuccess = false;
    private static boolean onAuthenticated = false;
    private int selectedRecent = -1;
    private String vistOrderId = null;
    private OrdersCustomView visitOcv;
    private RecentsApiResponseModel recentsApiResponseModel;
    private OrdersApiViewModel ordersApiViewModel;
    private boolean isCancelOrder = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(quickLoginBroadcastReceiver, new IntentFilter(getString(R.string.quick_login_broadcast_receiver)));

        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        attachObserverInterface = (AttachObserverInterface) getActivity();

        ordersCreateApiViewModel = new ViewModelProvider(this).get(OrdersCreateApiViewModel.class);

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
                            onBackPressed();
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
                    if(!errorModel.isCCCaptured() || !errorModel.isDefaultCardValid()) {
                        sendSuccessViewBroadCast(getActivity(), false, title, description);
                        PaymentInfo paymentInfo = new PaymentInfo();
                        paymentInfo.setCCCaptured(errorModel.isCCCaptured());
                        paymentInfo.setSavedCardsCount(errorModel.getSavedCardsCount());
                        paymentInfo.setDefaultCardValid(errorModel.isDefaultCardValid());
                        AppPaymentCardUtils.handleCardCasesFromPaymentInfo(getActivity(), paymentInfo, patientName);
                    }else {
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

            }
        });

        recentsApiViewModel = new ViewModelProvider(this).get(RecentsApiViewModel.class);

        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    recentsApiResponseModel = (RecentsApiResponseModel) baseApiResponseModel;
                    onRecentsListReceived(getRecentCall(recentsApiResponseModel));
                }
            }
        });

        recentsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {

            }
        });

        ordersApiViewModel = new ViewModelProvider(this).get(OrdersApiViewModel.class);

        attachObserverInterface.attachObserver(ordersApiViewModel);

        ordersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                onDetailReceived(baseApiResponseModels);
            }
        });

        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof OrdersIdListApiResponseModel) {
                        onDetailReceived((OrdersIdListApiResponseModel) baseApiResponseModel);
                    } else if (isCancelOrder) {
                        isCancelOrder = false;
                        if (baseApiResponseModel.isSuccess()) {
                            onBackPressed();
                        }
                    }
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
            if (status == ArgumentKeys.AUTH_FAILED || status == ArgumentKeys.AUTH_CANCELLED) {
                Utils.showAlertDialog(getActivity(), getString(R.string.validation_failed), getString(R.string.user_validation_failed_cannot_process_the_current_order), getString(R.string.ok), null,
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

        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.positing_your_order), currentOrder));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);
    }

    public void assignSpecialist(Boolean status, AssignSpecialistRequestModel requestModel, String userName, String doctorGuid, boolean sendFax) {
        currentOrder = OrderConstant.ORDER_REFERRALS;
        patientName = userName;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.assignSpecialist(status, requestModel, doctorGuid, false);
    }

    public void createPrescription(Boolean status, CreatePrescriptionRequestModel prescriptionModel, String userDisplay_name, String doctorGuid, boolean sendFax) {

        currentOrder = OrderConstant.ORDER_PRESCRIPTIONS;
        patientName = userDisplay_name;
        isSendFax = sendFax;

       showSuccessView();

        ordersCreateApiViewModel.createPrescription(userGuid,status, prescriptionModel, doctorGuid);
    }

    public void createNewRadiologyOrder(Boolean status, CreateRadiologyRequestModel requestModel, String userDisplay_name, String doctorGuid, boolean sendFax) {
        currentOrder = OrderConstant.ORDER_RADIOLOGY;
        patientName = userDisplay_name;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createRadiologyOrder(status, requestModel, doctorGuid);
    }

    public void createNewLabOrder(Boolean status, CreateTestApiRequestModel createTestApiRequestModel, String username, String doctorGuid, boolean sendFax) {

        currentOrder = OrderConstant.ORDER_LABS;
        patientName = username;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createLabOrder(status, createTestApiRequestModel, doctorGuid);
    }

    public void createNewMiscellaneousOrder(CreateMiscellaneousRequestModel miscellaneousOrderRequest, String userDisplay_name, String doctorGuid, boolean sendFax) {
        currentOrder = OrderConstant.ORDER_MISC;
        patientName = userDisplay_name;
        isSendFax = sendFax;

        showSuccessView();

        ordersCreateApiViewModel.createMiscellaneousOrder(miscellaneousOrderRequest, doctorGuid);
    }

    public void getPatientsRecentsList(String userGuid, String doctorGuid) {
        boolean isApiCallNeeded = true;
        if (this.userGuid.equals(userGuid) && recentsApiResponseModel != null) {
            isApiCallNeeded = false;
            if (UserType.isUserAssistant() && !this.doctorGuid.equals(doctorGuid)) {
                isApiCallNeeded = true;
            }
        }

        if (isApiCallNeeded) {
            this.userGuid = userGuid;
            this.doctorGuid = doctorGuid;
            String month = Utils.getCurrentFomatedDate(Utils.yyyy_mm);
            recentsApiViewModel.getUserCorrespondentList(userGuid, doctorGuid, month, 1, true, false);
        } else {
            onRecentsListReceived(getRecentCall(recentsApiResponseModel));
        }
    }

    private RecentsApiResponseModel.ResultBean getRecentCall(RecentsApiResponseModel recentsApiResponseModel) {
        if (!recentsApiResponseModel.getResult().isEmpty()) {
            for (int i = 0; i < recentsApiResponseModel.getResult().size(); i++) {
                if (recentsApiResponseModel.getResult().get(i).getDurationInSecs() > 0 &&
                        !Utils.isOneDayBefore(recentsApiResponseModel.getResult().get(i).getUpdated_at())) {
                    return recentsApiResponseModel.getResult().get(i);
                }
            }
        }

        return null;
    }

    public void setVisitsView(OrdersCustomView visitOcv, String user_guid, String doctorGuid) {
        this.visitOcv = visitOcv;
        vistOrderId = null;
        visitOcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), RecentsSelectionActivity.class)
                                .putExtra(ArgumentKeys.USER_GUID, user_guid)
                                .putExtra(ArgumentKeys.DOCTOR_GUID, doctorGuid)
                                .putExtra(ArgumentKeys.SELECTED_TRANSCRIPTION_ID, selectedRecent)
                        , RequestID.REQ_VISIT_RECENT);
            }
        });

    }

    public void onRecentsListReceived(RecentsApiResponseModel.ResultBean recentData) {
        Log.e(TAG, "onRecentsListReceived: " + (recentData == null));
        if (visitOcv != null) {
            if (recentData != null) {
                visitOcv.setTitleTv(Utils.getDayMonthYearTime(recentData.getUpdated_at()));
            } else {
                visitOcv.setTitleTv(getString(R.string.associate_this_order_to_a_visit));
            }
        }
        if (recentData != null) {
            vistOrderId = recentData.getOrder_id();
            selectedRecent = recentData.getTranscription_id();
        } else {
            vistOrderId = null;
            selectedRecent = -1;
        }
    }

    public String getVistOrderId() {
        return vistOrderId;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    if (getActivity() instanceof HomeActivity) {
                        getActivity().onBackPressed();
                        getActivity().onBackPressed();
                    } else {
                        getActivity().finish();
                    }
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
            case RequestID.REQ_VISIT_RECENT:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getExtras() != null) {
                        RecentsApiResponseModel.ResultBean recentModel = (RecentsApiResponseModel.ResultBean) data.getExtras().getSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL);
                        onRecentsListReceived(recentModel);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onDetailReceived(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {

    }

    public void onDetailReceived(@Nullable OrdersIdListApiResponseModel idListApiResponseModel) {

    }

    public void getFormsDetail(@NonNull String userGuid, @Nullable String doctorGuid, @NonNull List<Integer> idList, @NonNull boolean isShowProgress) {
        ordersApiViewModel.getFormsDetail(userGuid, doctorGuid, idList, isShowProgress);
    }

    public void getOrdersDetail(@NonNull String userGuid, @Nullable String doctorGuid, @NonNull List<Integer> idList, @NonNull boolean isShowProgress) {
        if (UserType.isUserPatient()) {
            doctorGuid = null;
            userGuid = null;
        }
        ordersApiViewModel.getOrderdsDetail(userGuid, doctorGuid, idList, isShowProgress);
    }

    public void cancelOrder(String orderType, int referral_id, String doctorGuid) {
        String message = null;
        switch (orderType) {
            case OrderConstant.ORDER_PRESCRIPTIONS:
                message = getString(R.string.cancel_prescription_order);
                break;
            case OrderConstant.ORDER_REFERRALS:
                message = getString(R.string.cancel_specialist_order);
                break;
            case OrderConstant.ORDER_LABS:
                message = getString(R.string.cancel_lab_order);
                break;
            case OrderConstant.ORDER_RADIOLOGY:
                message = getString(R.string.cancel_xray_order);
                break;
            case OrderConstant.ORDER_MISC:
                message = getString(R.string.cancel_miscellaneous_order);
                break;
        }
        Utils.showAlertDialog(getActivity(), getString(R.string.cancel_caps),
                message,
                getString(R.string.yes),
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCancelOrder = true;
                        ordersApiViewModel.cancelOrder(orderType, referral_id, doctorGuid);
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public void onBackPressed() {
        onCloseActionInterface.onClose(false);
    }
}
