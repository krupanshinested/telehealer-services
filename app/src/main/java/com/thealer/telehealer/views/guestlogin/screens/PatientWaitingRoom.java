package com.thealer.telehealer.views.guestlogin.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileApiResponseModel;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.transaction.AskToAddCardViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.guestlogin.adapter.PaitentWaitingListRecyclerAdaper;
import com.thealer.telehealer.views.guestlogin.viewmodel.PatientWaitingRoomModel;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class PatientWaitingRoom extends BaseActivity implements View.OnClickListener {

    private PaitentWaitingListRecyclerAdaper adaper;
    private CustomRecyclerView paitentWaitingRecyclerview;
    private List<Patientinfo> itemList = new ArrayList<>();
    private String doctorGuuid, doctorName;
    private ImageView back_iv;
    private TextView toolbar_title;
    private PatientWaitingRoomModel patientWaitingRoomModel;
    private AskToAddCardViewModel askToAddCardViewModel;
    private Patientinfo selectedPaitentinfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_waiting_room);
        initview();

        Bundle bundle = this.getIntent().getExtras();
        itemList = (List<Patientinfo>) bundle.getSerializable(ArgumentKeys.GUEST_INFO_LIST);
        doctorGuuid = bundle.getString(ArgumentKeys.DOCTOR_GUID);
        doctorName = bundle.getString(ArgumentKeys.DOCTOR_NAME);

        addItemsInAdapter(itemList);

        patientWaitingRoomModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                Log.d("PatientWaitingRoom", "onChanged");
                if (baseApiResponseModel != null) {
                    Log.d("PatientWaitingRoom", "callKickOut");
                    if (selectedPaitentinfo != null) {
                        PushPayLoad pushPayLoad = PubNubNotificationPayload.getKickOutPayload(selectedPaitentinfo);
                        PubnubUtil.shared.publishPushMessage(pushPayLoad, null);
                    }
                }
            }
        });
    }

    private void addItemsInAdapter(List<Patientinfo> itemList) {
        if (itemList != null)
            adaper.updateItems(itemList);
    }

    private void initview() {
        LocalBroadcastManager.getInstance(this).registerReceiver(patient_count_update, new IntentFilter(getString(R.string.patient_count_update)));
        paitentWaitingRecyclerview = (CustomRecyclerView) findViewById(R.id.paitent_Waiting_Recyclerview);
        paitentWaitingRecyclerview.getSwipeLayout().setEnabled(false);
        back_iv = findViewById(R.id.back_iv);
        toolbar_title = findViewById(R.id.toolbar_title);
        adaper = new PaitentWaitingListRecyclerAdaper(PatientWaitingRoom.this, new PaitentWaitingListRecyclerAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(Patientinfo data) {
                messagePopup(data, PatientWaitingRoom.this);
            }

            @Override
            public void onAdmitClick(Patientinfo data) {
                CommonUserApiResponseModel patient = new CommonUserApiResponseModel();
                patient.setUser_guid(data.getUserGuid());
                patient.setFirst_name(data.getDisplayName());
                patient.setRole(Constants.ROLE_PATIENT);
                patient.setEmail(data.getEmail());
                patient.setPhone(data.getPhone());

                CallRequest callRequest = new CallRequest(data.getId(), data.getUserGuid(), patient, null, doctorName, null, OpenTokConstants.video, true, null);
                callRequest.setCallForDirectWaitingRoom(true);
                CallSettings callSettings = new CallSettings();
                callSettings.sessionId = data.getSessionId();
                callRequest.update(callSettings);

                Intent intent = new Intent(PatientWaitingRoom.this, CallPlacingActivity.class);
                intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callRequest);
                startActivity(intent);
            }

            @Override
            public void onKikcoutClick(Patientinfo data) {
                Log.d("PatientWaitingRoom", "onKikcoutClick" + data.getSessionId());
                Utils.showAlertDialog(PatientWaitingRoom.this, getString(R.string.confirmation), getString(R.string.are_you_sure_to_kickout), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedPaitentinfo = data;
                        patientWaitingRoomModel.kickOutPatient(data);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onAskToAddCardClick(Patientinfo data) {
                selectedPaitentinfo = data;
                askToAddCardViewModel.askToAddCard(data.getUserGuid(), doctorGuuid);
            }
        });

        paitentWaitingRecyclerview.getRecyclerView().setAdapter(adaper);

        patientWaitingRoomModel = new ViewModelProvider(this).get(PatientWaitingRoomModel.class);

        askToAddCardViewModel = new ViewModelProvider(this).get(AskToAddCardViewModel.class);
        askToAddCardViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (selectedPaitentinfo != null) {
                    PushPayLoad pushPayLoad = PubNubNotificationPayload.getKickOutPayload(selectedPaitentinfo);
                    PubnubUtil.shared.publishPushMessage(pushPayLoad, null);
                }
            }
        });
        attachObserver(askToAddCardViewModel);
        askToAddCardViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (errorModel.geterrorCode() == null){
                    Utils.showAlertDialog(PatientWaitingRoom.this, getString(R.string.app_name),
                            errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                            null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                    Utils.showAlertDialog(PatientWaitingRoom.this, getString(R.string.app_name),
                            errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                            null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });


        back_iv.setOnClickListener(this::onClick);
        toolbar_title.setText(R.string.waiting_room);
    }

    private BroadcastReceiver patient_count_update = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(ArgumentKeys.GUEST_INFO_LIST)) {
                Log.d("PatientWaitingRoomAct", "didSubscribersChanged-");
                Bundle bundle = intent.getExtras();
                List<Patientinfo> guestinfoList = (List<Patientinfo>) bundle.getSerializable(ArgumentKeys.GUEST_INFO_LIST);
                Log.d("PatientWaitingRoomAct", "-" + guestinfoList.size());
                if (guestinfoList != null && guestinfoList.size() > 0) {
                    adaper.updateItems(guestinfoList);
                    paitentWaitingRecyclerview.showOrhideEmptyState(false);
                } else {
                    paitentWaitingRecyclerview.showOrhideEmptyState(true);
                    paitentWaitingRecyclerview.setEmptyState(EmptyViewConstants.EMPTY_WAITING_ROOM);
                }

            }
        }
    };


    public void messagePopup(Patientinfo guestinfo, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View alertView = LayoutInflater.from(context).inflate(R.layout.message_send_popup, null);
        builder.setView(alertView);

        AlertDialog alertDialog = builder.create();

        EditText et_msg = alertView.findViewById(R.id.et_msg);
        TextView tv_cancel = (TextView) alertView.findViewById(R.id.tv_cancel);
        TextView tv_done = (TextView) alertView.findViewById(R.id.tv_done);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushPayLoad pushPayLoad = PubNubNotificationPayload.getWaitingRoomChatPayload(et_msg.getText().toString(), guestinfo.getUserGuid());
                Log.d("PushPayLoad", "" + pushPayLoad.getPn_apns().getAps().get("alert"));
                PubnubUtil.shared.publishPushMessage(pushPayLoad, null);
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.CENTER);
        }
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(patient_count_update);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
        }
    }
}