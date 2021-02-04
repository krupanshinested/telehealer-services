package com.thealer.telehealer.views.home.schedules;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestLoginApiResponseModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestloginViewModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.guestlogin.screens.GuestLoginScreensActivity;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.chat.ChatActivity;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Aswin on 18,December,2018
 */
public class ScheduleDetailViewFragment extends BaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OrdersCustomView doctorOcv;
    private ImageView doctorChatIv;
    private View doctorView;
    private OrdersCustomView appointmentTimeOcv;
    private OrdersCustomView patientOcv;
    private ImageView patientChatIv;
    private ImageView patientCallIv;
    private View patientView;
    private LinearLayout cancelLl;
    private View topView;
    private TextView cancelTv;
    private OrdersCustomView reasonOcv;

    private SchedulesApiViewModel schedulesApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private SchedulesApiResponseModel.ResultBean resultBean;
    private RecyclerView patientHistoryRv;
    private TextView statusTv;
    private String userGuid = null, doctorGuid = null, doctorName = null;
    private Button waitingRoomBtn;
    private TextView historyLabel;
    boolean cancelIsClicked = false;
    private GuestloginViewModel guestloginViewModel;
    private GuestLoginApiResponseModel guestLoginApiResponseModel;
    private PatientInvite patientInvite;

    private boolean isNotWantToAddCard;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        schedulesApiViewModel = new ViewModelProvider(this).get(SchedulesApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof SchedulesApiResponseModel.ResultBean) {
                                scheduleDetails(baseApiResponseModel);
                            } else {
                                if (baseApiResponseModel.isSuccess()) {
                                    Utils.showAlertDialog(getActivity(), getString(R.string.success).toUpperCase(), getString(R.string.schedule_deleted), getString(R.string.ok).toUpperCase(), null,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    onCloseActionInterface.onClose(true);
                                                }
                                            }, null);
                                }
                            }

                        }

                    }
                });

        guestloginViewModel = new ViewModelProvider(this).get(GuestloginViewModel.class);
        guestloginViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof GuestLoginApiResponseModel) {

                        guestLoginApiResponseModel = (GuestLoginApiResponseModel) baseApiResponseModel;
                        if (guestLoginApiResponseModel.isSuccess()) {
                            if (!AppPaymentCardUtils.hasValidPaymentCard(guestLoginApiResponseModel.getPayment_account_info()) && !isNotWantToAddCard) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
                                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                                bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, "");
                                bundle.putBoolean(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
                                LocalBroadcastManager
                                        .getInstance(getActivity())
                                        .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                                .putExtras(bundle));
                                AppPaymentCardUtils.handleCardCasesFromPaymentInfo(ScheduleDetailViewFragment.this, guestLoginApiResponseModel.getPayment_account_info(), null);
                            } else {
                                callSuccessDialogBroadcast();
                                Patientinfo patientDetails = new Patientinfo(UserDetailPreferenceManager.getPhone(), UserDetailPreferenceManager.getEmail(), "", UserDetailPreferenceManager.getUserDisplayName(), UserDetailPreferenceManager.getUser_guid(), guestLoginApiResponseModel.getApiKey(), guestLoginApiResponseModel.getSessionId(), guestLoginApiResponseModel.getToken(), false);
                                patientDetails.setHasValidCard(AppPaymentCardUtils.hasValidPaymentCard(guestLoginApiResponseModel.getPayment_account_info()));
                                patientInvite = new PatientInvite();
                                patientInvite.setPatientinfo(patientDetails);
                                patientInvite.setDoctorDetails(guestLoginApiResponseModel.getDoctor_details());
                                patientInvite.setApiKey(guestLoginApiResponseModel.getApiKey());
                                patientInvite.setToken(guestLoginApiResponseModel.getToken());
                            }
                        } else {
                            showToast(guestLoginApiResponseModel.getMessage());
                        }

                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.failure), Toast.LENGTH_SHORT).show();
                }
            }
        });

        guestloginViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        Log.d("ErrorModel", "whoAmIApiViewModel");
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, false);
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_detail_view, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(callStartReceiver, new IntentFilter(Constants.CALL_STARTED_BROADCAST));
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(callEndReceiver, new IntentFilter(Constants.CALL_ENDED_BROADCAST));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(callEndReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(callStartReceiver);
        }
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        doctorOcv = (OrdersCustomView) view.findViewById(R.id.doctor_ocv);
        doctorChatIv = (ImageView) view.findViewById(R.id.doctor_chat_iv);
        doctorView = (View) view.findViewById(R.id.doctor_view);
        appointmentTimeOcv = (OrdersCustomView) view.findViewById(R.id.appointment_time_ocv);
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        patientChatIv = (ImageView) view.findViewById(R.id.patient_chat_iv);
        patientCallIv = (ImageView) view.findViewById(R.id.patient_call_iv);
        patientView = (View) view.findViewById(R.id.patient_view);
        cancelLl = (LinearLayout) view.findViewById(R.id.cancel_ll);
        topView = (View) view.findViewById(R.id.top_view);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        reasonOcv = (OrdersCustomView) view.findViewById(R.id.reason_ocv);
        patientHistoryRv = (RecyclerView) view.findViewById(R.id.patient_history_rv);
        statusTv = (TextView) view.findViewById(R.id.status_tv);
        waitingRoomBtn = (Button) view.findViewById(R.id.waiting_room_btn);
        historyLabel = (TextView) view.findViewById(R.id.history_label);

        cancelTv.setText(getString(R.string.cancel_appointment));
        toolbarTitle.setText(getString(R.string.appointment_detail));

        backIv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        doctorChatIv.setOnClickListener(this);
        patientCallIv.setOnClickListener(this);
        patientChatIv.setOnClickListener(this);
        waitingRoomBtn.setOnClickListener(this);

        if (UserType.isUserPatient()) {
            doctorChatIv.setVisibility(View.VISIBLE);
        } else {
            patientChatIv.setVisibility(View.VISIBLE);
        }

        patientCallIv.setVisibility(View.GONE);

        if (CallManager.shared.isActiveCallPresent()) {
            patientCallIv.setEnabled(false);
        } else {
            patientCallIv.setEnabled(true);
        }

        waitingRoomBtn.setVisibility(View.GONE);


        if (getArguments() != null) {
            resultBean = (SchedulesApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.SCHEDULE_DETAIL);

            if (UserType.isUserAssistant()) {
                doctorGuid = resultBean.getDoctor().getUser_guid();
                doctorName = resultBean.getDoctor().getUserDisplay_name();
            }

            if (resultBean != null) {
                schedulesApiViewModel.getScheduleDetail(resultBean.getSchedule_id(), doctorGuid, true);
            }

        }

    }

    private BroadcastReceiver callStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            patientCallIv.setEnabled(false);
        }
    };

    private BroadcastReceiver callEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            patientCallIv.setEnabled(true);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                cancelIsClicked = false;
                onCloseActionInterface.onClose(false);
                break;
            case R.id.cancel_tv:
                Utils.showAlertDialog(getActivity(), getString(R.string.cancel_schedule), getString(R.string.cancel_schedule_conformation), getString(R.string.yes), getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelIsClicked = true;
                                dialog.dismiss();
                                schedulesApiViewModel.deleteSchedule(resultBean.getSchedule_id(), resultBean.getStart(), resultBean.getScheduled_by_user().getUser_guid(), doctorGuid, true);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
            case R.id.doctor_chat_iv:
            case R.id.patient_chat_iv:
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(ArgumentKeys.USER_GUID, UserType.isUserPatient() ? resultBean.getDoctor().getUser_guid() : resultBean.getPatient().getUser_guid()));
                break;
            case R.id.patient_call_iv:

                ArrayList<String> callTypes = new ArrayList<>();
                if (resultBean.getDoctor().getApp_details() != null) {
                    if (!resultBean.getDoctor().getApp_details().isWebUser()) {
                        callTypes.add(getString(R.string.audio_call));
                        callTypes.add(getString(R.string.video_call));
                    }
                }
                callTypes.add(getString(R.string.one_way_call));
                ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_call_type), callTypes, new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {

                        String callType;
                        switch (position) {
                            case 0:
                                callType = OpenTokConstants.audio;
                                break;
                            case 1:
                                callType = OpenTokConstants.video;
                                break;
                            default:
                                callType = OpenTokConstants.oneWay;
                                break;
                        }

                        CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(),
                                resultBean.getPatient().getUser_guid(), resultBean.getPatient(), doctorGuid, doctorName, String.valueOf(resultBean.getSchedule_id()), callType, true, String.valueOf(resultBean.getSchedule_id()));

                        Intent intent = new Intent(getActivity(), CallPlacingActivity.class);
                        intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callRequest);
                        startActivity(intent);

                    }

                    @Override
                    public void didCancelled() {

                    }
                });
                itemPickerDialog.setCancelable(false);
                itemPickerDialog.show();

                break;
            case R.id.waiting_room_btn:
                Utils.showAlertDialog(getActivity(), getString(R.string.waiting_room), getString(R.string.guest_login_bottom_title), getString(R.string.proceed), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                proceedForWaitingRoom();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
        }
    }

    private void proceedForWaitingRoom() {
        showDialog();
        guestloginViewModel.registerUserEnterWatingRoom(resultBean.getDoctor().getUser_guid());
    }

    public void scheduleDetails(BaseApiResponseModel baseApiResponseModel) {
        SchedulesApiResponseModel.ResultBean resultBean = (SchedulesApiResponseModel.ResultBean) baseApiResponseModel;
        Log.d("Received_schedule", "" + resultBean.getSchedule_id());
        if (resultBean != null) {
            userGuid = resultBean.getPatient().getUser_guid();

            if (UserType.isUserPatient() && resultBean.isStartAndEndBetweenCurrentTime()) {
                Log.d("waitingRoomEnable", "True");
                waitingRoomBtn.setVisibility(View.VISIBLE);
            } else
                Log.d("waitingRoomEnable", "False");

            String statusInfo = getString(R.string.patient_has_been_updated);
            String detail = "";
            if (resultBean.getDetail().isChange_medical_info() && resultBean.getDetail().isChange_demographic() && resultBean.getDetail().isInsurance_to_date()) {
                detail = getString(R.string.demographic_history_insurance);
            } else if (resultBean.getDetail().isChange_medical_info() && resultBean.getDetail().isChange_demographic()) {
                detail = getString(R.string.demographic_history);
            } else if (resultBean.getDetail().isChange_medical_info() && resultBean.getDetail().isInsurance_to_date()) {
                detail = getString(R.string.history_insurance);
            } else if (resultBean.getDetail().isChange_demographic() && resultBean.getDetail().isInsurance_to_date()) {
                detail = getString(R.string.demographic_insurance);
            } else if (resultBean.getDetail().isChange_medical_info()) {
                detail = getString(R.string.history);
            } else if (resultBean.getDetail().isChange_demographic()) {
                detail = getString(R.string.demographic);
            } else if (resultBean.getDetail().isInsurance_to_date()) {
                detail = getString(R.string.insurance);
            }

            if (!detail.isEmpty()) {
                statusInfo = String.format(statusInfo, detail);
                statusTv.setText(statusInfo);
                statusTv.setVisibility(View.VISIBLE);

                List<HistoryBean> historyList = new ArrayList<>();
                if (resultBean.getScheduled_with_user().getRole().equals(Constants.ROLE_PATIENT)) {
                    historyList = resultBean.getScheduled_with_user().getHistory();
                } else if (resultBean.getScheduled_by_user().getRole().equals(Constants.ROLE_PATIENT)) {
                    historyList = resultBean.getScheduled_by_user().getHistory();
                }
                patientHistoryRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                PatientHistoryAdapter patientHistoryAdapter = new PatientHistoryAdapter(getActivity(), false, historyList);
                patientHistoryRv.setAdapter(patientHistoryAdapter);
                historyLabel.setVisibility(View.VISIBLE);
                patientHistoryRv.setVisibility(View.VISIBLE);
            } else {
                historyLabel.setVisibility(View.GONE);
                patientHistoryRv.setVisibility(View.GONE);
            }

            reasonOcv.setTitleTv(resultBean.getDetail().getReason());
            appointmentTimeOcv.setTitleTv(Utils.getDayMonth(resultBean.getStart()) + " - " + Utils.getFormatedTime(resultBean.getStart()));

            String doctorName = null, doctorSpecialist = null, patientName = null, patientDob = null;

            CommonUserApiResponseModel scheduleWith = resultBean.getScheduled_with_user();
            CommonUserApiResponseModel scheduleBy = resultBean.getScheduled_by_user();

            if (scheduleWith.getRole().equals(Constants.ROLE_PATIENT)) {
                patientName = scheduleWith.getUserDisplay_name();
                patientDob = scheduleWith.getDob();
            } else {
                doctorName = scheduleWith.getDoctorDisplayName();
                doctorSpecialist = scheduleWith.getDoctorSpecialist();
            }
            if (scheduleBy.getRole().equals(Constants.ROLE_PATIENT)) {
                patientName = scheduleBy.getUserDisplay_name();
                patientDob = scheduleBy.getDob();
            } else {
                doctorName = scheduleBy.getDoctorDisplayName();
                doctorSpecialist = scheduleBy.getDoctorSpecialist();
            }

            doctorOcv.setTitleTv(doctorName);
            doctorOcv.setSubtitleTv(doctorSpecialist);
            patientOcv.setTitleTv(patientName);
            patientOcv.setSubtitleTv(patientDob);

            if (Utils.isDateTimeExpired(resultBean.getStart())) {
                cancelLl.setVisibility(View.GONE);
            } else {
                cancelLl.setVisibility(View.VISIBLE);
            }

            if (Utils.isDateTimeExpired(resultBean.getEnd())) {
                doctorChatIv.setVisibility(View.GONE);
                patientCallIv.setVisibility(View.GONE);
                patientChatIv.setVisibility(View.GONE);
            } else if (!UserType.isUserPatient()) {
                if (resultBean.getPatient().isAvailable()) {
                    patientCallIv.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    private void showDialog() {
        Bundle succesBundle = new Bundle();
        succesBundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
        succesBundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.validating_your_waiting_room));

        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setArguments(succesBundle);
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
    }

    private void callSuccessDialogBroadcast() {
        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, true);
        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
        intent.putExtra(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void goToWaitingScreen() {
        if (PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            Intent i = new Intent(getActivity(), GuestLoginScreensActivity.class);
            i.putExtra(ArgumentKeys.GUEST_INFO, patientInvite);
            i.putExtra(ArgumentKeys.GUEST_SCREENTYPE, ArgumentKeys.WAITING_SCREEN);
            startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode", "" + requestCode);
        switch (requestCode) {
            case PermissionConstants.PERMISSION_CAM_MIC:
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == RESULT_OK) {
                    goToWaitingScreen();
                }
                break;
            case RequestID.REQ_CARD_INFO:
            case RequestID.REQ_CARD_EXPIRE:
                if (!UserType.isUserAssistant()) {
                    if (resultCode == Activity.RESULT_OK) {
                        AppPaymentCardUtils.startPaymentIntent(getActivity(), UserType.isUserDoctor());
                    } else {
                        if (UserType.isUserPatient()) {
                            isNotWantToAddCard = true;
                            proceedForWaitingRoom();
                        }
                    }
                }
                break;

        }
    }
}
