package com.thealer.telehealer.views.guestlogin.screens;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.CallMinimizeService;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.common.pubNub.pubinterface.PubNubDelagate;
import com.thealer.telehealer.common.pubNub.waitingroom.PatientInviteHandler;
import com.thealer.telehealer.common.pubNub.waitingroom.PatientInviteRoomInterface;
import com.thealer.telehealer.common.pubNub.waitingroom.PubnubHandler;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.guestlogin.WaitingRoomHearBeatService;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;

import java.util.Objects;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class WaitingScreenFragment extends Fragment implements PatientInviteRoomInterface,PubNubDelagate, View.OnClickListener {

    private PatientInvite patientInvite;
    private View view;
    private TextView tv_Position, tv_youare,tv_docotor_msg;
    private ImageView close_iv;
    private PubnubHandler pubnubHandler;
    private PatientInviteHandler patientInviteHandler;

    public WaitingScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_waiting_screen, container, false);
        initView();

        Bundle bundle = getArguments();
        patientInvite = (PatientInvite) bundle.getSerializable(ArgumentKeys.GUEST_INFO);

        patientInviteHandler = new PatientInviteHandler(getActivity(),patientInvite,this);
        patientInviteHandler.startToSubscribe();

        initPubnub();
        initalizeTokBox();

        return view;
    }

    private void initPubnub() {
        pubnubHandler = new PubnubHandler(patientInvite.patientinfo.getUserGuid(), patientInvite.patientinfo.getUserGuid(), this);
        pubnubHandler.subscribe();
        if (patientInvite.patientinfo.isGuestUser()) {
            if (!TextUtils.isEmpty(TelehealerFirebaseMessagingService.getCurrentToken())) {
                PubnubUtil.shared.enablePushOnChannel(TelehealerFirebaseMessagingService.getCurrentToken(), patientInvite.patientinfo.getUserGuid());
            }
        }
    }

    private void initView() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(applifecycylestatus, new IntentFilter(getString(R.string.APP_LIFECYCLE_STATUS)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(waitingRoomHeartBeat, new IntentFilter(ArgumentKeys.WAITING_ROOM_HEART_BEAT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(did_subscriber_connected, new IntentFilter(Constants.did_subscriber_connected));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(did_end_call, new IntentFilter(Constants.CALL_ENDED_BROADCAST));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(notNowRegister, new IntentFilter(ArgumentKeys.NOT_NOW_GUEST_LOGIN));
        tv_Position = view.findViewById(R.id.tv_Position);
        tv_youare = view.findViewById(R.id.tv_youare);
        close_iv = view.findViewById(R.id.im_Close);
        tv_docotor_msg = view.findViewById(R.id.tv_docotor_msg);
        close_iv.setOnClickListener(this::onClick);
    }

    @Override
    public void onResume() {
        super.onResume();
        addKeyListner();
    }

    private void initalizeTokBox() {
        String doctorName=patientInvite.getDoctorDetails().getDoctorDisplayName();
        CallRequest callRequest = new CallRequest(patientInvite.patientinfo.getId(), patientInvite.getDoctorDetails().getUser_guid(), patientInvite.getDoctorDetails(), null, doctorName, null, OpenTokConstants.video,false,null);
        callRequest.setCallForDirectWaitingRoom(true);
        callRequest.setForGuestUser(patientInvite.getPatientinfo().isGuestUser());

        CallSettings callSettings=new CallSettings();
        Log.d("waitingScreenFrag","sessionId"+patientInvite.getPatientinfo().getSessionId());
        Log.d("waitingScreenFrag","apiKey"+patientInvite.getApiKey());
        Log.d("waitingScreenFrag","token"+patientInvite.getToken());
        callSettings.sessionId=patientInvite.getPatientinfo().getSessionId();
        callSettings.apiKey=patientInvite.getApiKey();
        callSettings.token=patientInvite.getToken();
        callSettings.recording_enabled  = patientInvite.doctorDetails.getRecording_enabled();
        callSettings.transcription_enabled = patientInvite.doctorDetails.getTranscription_enabled();
        callSettings.canStartPublishAudio = false;
        callSettings.canStartPublishVideo = false;

        callRequest.update(callSettings);
        callRequest.setUserAdmitted(false);

        OpenTok tokBox = CallManager.shared.getCall(patientInvite.patientinfo.getId());
        if (tokBox == null) {
            tokBox=new OpenTok(callRequest);
        }
        tokBox.connectToSession();
        CallManager.shared.addCall(tokBox);
    }

    private void addKeyListner() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    showExitAlert(getString(R.string.exit_waiting_room),getString(R.string.exit_waiting_msg),getString(R.string.stayhere),getString(R.string.exit));
                }
                return false;
            }
        });
    }

    @Override
    public void didUpdateCurrentPosition(int position) {
        Log.d("waitingScreen", "didUpdateCurrentPosition" + position);
        getActivity().runOnUiThread(() -> {
            tv_youare.setVisibility(View.VISIBLE);
            tv_Position.setText("" + position);
            String posString;
            if (position == 1) {
                posString = position + "st";
            } else if (position == 2) {
                posString = position + "nd";
            } else if (position == 3) {
                posString = position + "rd";
            } else {
                posString = position + "th";
            }

            String wholeText = getResources().getString(R.string.youare) + " " + posString + " " + getString(R.string.inline);
            SpannableString ss = new SpannableString(wholeText);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            ss.setSpan(boldSpan, 8, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_youare.setText(ss);
        });

    }

    @Override
    public void didSubscribed() {

    }

    @Override
    public void didReceiveMessage(APNSPayload apnsPayload) {
        Log.d("Waitingscreenfrag","didReceiveMessage"+apnsPayload);
        if (apnsPayload!=null){
            (getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //if type waiting room message
                    if (apnsPayload.getType().equalsIgnoreCase(APNSPayload.waitingRoomMessage)) {
                        if (apnsPayload.getContent() != null) {
                            tv_docotor_msg.setVisibility(View.VISIBLE);
                            String doctor_msg = patientInvite.doctorDetails.getFirst_name() + " " + patientInvite.doctorDetails.getLast_name() +" , "+patientInvite.doctorDetails.getAssistantTitle()+ "  says  ";
                            tv_docotor_msg.setText(doctor_msg + " ' " + apnsPayload.getContent() + " '");
                        } else
                            tv_docotor_msg.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void didPostStateVeryFirstTime() {

    }

    @Override
    public void kickOut() {
        Log.d("kickout","waitingScreenFragment");
        dismiss(false);
    }

    @Override
    public void changeInState(PNPresenceEventResult pnPresenceEventResult) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy","waitingScreenFragment");
        onExit();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(applifecycylestatus);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(did_subscriber_connected);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(did_end_call);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(notNowRegister);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(waitingRoomHeartBeat);
        super.onDestroy();
    }

    private void onExit() {
        patientInviteHandler.unRegisterKickout();
        patientInviteHandler.unsubscribe();
        pubnubHandler.unsubscribe();

        OpenTok tokBox = CallManager.shared.getCall(patientInvite.patientinfo.getId());
        if (tokBox != null) {
            tokBox.endCall(OpenTokConstants.other);
        }

        if (patientInvite.patientinfo.isGuestUser()) {
            if (!TextUtils.isEmpty(TelehealerFirebaseMessagingService.getCurrentToken())) {
                PubnubUtil.shared.unsubscribe();
            }
        }
    }

    private void dismiss(boolean isAdmitted) {
        if (patientInvite.patientinfo.isGuestUser() && !isAdmitted) {
            appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, false);
            UserDetailPreferenceManager.deleteAllPreference();
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), OnBoardingActivity.class));
            }
        }

        onExit();

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.im_Close:
                showExitAlert(getString(R.string.exit_waiting_room),getString(R.string.exit_waiting_msg),getString(R.string.stayhere),getString(R.string.exit));
                break;
        }
    }

    private BroadcastReceiver applifecycylestatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra(ArgumentKeys.APP_LIFECYCLE_STATUS,false);
            if (status) {
                Log.d("WaitingScreenFragment", "Foreground");
                stopWaitingRoomService();
                if (patientInviteHandler != null)
                    patientInviteHandler.didAppBecameActive();

            } else {
                Log.d("WaitingScreenFragment", "Background");
                callWaitingRoomService();
                if (patientInviteHandler != null)
                    patientInviteHandler.didAppBecameInActive();
            }
        }
    };

    private BroadcastReceiver waitingRoomHeartBeat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (patientInviteHandler != null)
                patientInviteHandler.didAppBecameInActive();

        }
    };

    private BroadcastReceiver did_subscriber_connected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("did_subscriber_con","did_subscriber_connected");
            String docotorName=patientInvite.doctorDetails.getFirst_name()+" "+patientInvite.doctorDetails.getLast_name()+" , "+patientInvite.doctorDetails.getAssistantTitle()+" ";
            showJoinALert(docotorName + getString(R.string.waiting_for_you), docotorName + getString(R.string.has_enterted_waiting_room), getString(R.string.yes), getString(R.string.exit));

            if (!TeleHealerApplication.isInForeGround) {
                Utils.displyNotificationOnTop(docotorName+" "+getString(R.string.waiting_for_you),docotorName+  getString(R.string.has_enterted_waiting_room_open_ap),null,new Intent(getActivity(), GuestLoginScreensActivity.class));
            }
        }
    };

    private BroadcastReceiver did_end_call = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           dismiss(intent.getBooleanExtra(ArgumentKeys.IS_USER_ADMITTED,false));
        }
    };

    private BroadcastReceiver notNowRegister = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("WaitingScreenFragment","notNowRegister");
            onExit();
        }
    };

    private void showExitAlert(String title, String message, String postiveBtn, String negativeBtn) {

        Utils.showAlertDialog(getActivity(), title, message, postiveBtn, negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dismiss(false);
            }
        });

    }

    private void showJoinALert(String title,String message,String postiveBtn,String negativeBtn) {

        Utils.showAlertDialog(getActivity(), title, message, postiveBtn, negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenTok tokBox = CallManager.shared.getActiveCallToShow();
                if (tokBox != null) {
                    tokBox.startPublishing();
                    tokBox.getCallRequest().setUserAdmitted(true);
                    Intent intent = CallActivity.getIntent(getActivity().getApplication(), CallManager.shared.getActiveCallToShow().getCallRequest());
                    startActivity(intent);
                } else {
                    dismiss(false);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OpenTok tokBox = CallManager.shared.getActiveCallToShow();
                if (tokBox != null) {
                    tokBox.endCall(OpenTokConstants.other);
                }
                dialog.dismiss();
                dismiss(false);
            }
        });

    }

    private void callWaitingRoomService() {
        Log.e("application", "callWaitingRoomService");
        Class<? extends Service> service = WaitingRoomHearBeatService.class;
        Intent intent = new Intent(getActivity(), service);
        ContextCompat.startForegroundService(getActivity(), intent);
    }

    private void stopWaitingRoomService() {
        Log.e("application", "stopWaitingRoomService");
        final Class<? extends Service> service = WaitingRoomHearBeatService.class;
        final Intent intent = new Intent(getActivity(), service);
        getActivity().stopService(intent);
    }

}