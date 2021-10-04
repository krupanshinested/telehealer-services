package com.thealer.telehealer.views.call;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.media.AudioDeviceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.skyfishjy.library.RippleBackground;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.Animation.ConstrainSetUtil;
import com.thealer.telehealer.common.Animation.MoveViewTouchListener;
import com.thealer.telehealer.common.Animation.OnSwipeTouchListener;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CompleteListener;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.CallMinimizeService;
import com.thealer.telehealer.common.OpenTok.CallNotificationService;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.TokBoxUIInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.Array.ArrayListFilter;
import com.thealer.telehealer.common.Util.Array.ArrayListUtil;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.call.Adapter.VitalCallAdapter;
import com.thealer.telehealer.views.call.Interfaces.LiveVitalCallBack;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.RoundCornerConstraintLayout;
import com.thealer.telehealer.views.guestlogin.screens.GuestUserSignupActivity;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.UserDetailActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static com.thealer.telehealer.common.ArgumentKeys.CALL_INITIATE_MODEL;

/**
 * Created by rsekar on 12/25/18.
 */

public class CallActivity extends BaseActivity implements TokBoxUIInterface,
        View.OnClickListener,
        VitalManagerInstance,
        LiveVitalCallBack,
        AttachObserverInterface {


    public static final int VOIP_NOTIFICATION_ID = 303030303;


    public static Intent getIntent(Application application, CallRequest callRequest) {
        Intent intent = new Intent(application, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(CALL_INITIATE_MODEL, callRequest);
        return intent;
    }

    public static Intent getCallIntent(Application application, Boolean isWaitingRoom, @Nullable Boolean accept, CallRequest callRequest) {
        Intent fullScreenIntent = CallActivity.getIntent(application, callRequest);
        fullScreenIntent.putExtra(ArgumentKeys.TRIGGER_ANSWER, isWaitingRoom);
        if (accept != null) {
            if (accept) {
                fullScreenIntent.putExtra(ArgumentKeys.TRIGGER_ANSWER, true);
            } else {
                fullScreenIntent.putExtra(ArgumentKeys.TRIGGER_END, true);
            }
        }

        return fullScreenIntent;
    }

    public static void createNotificationBarCall(Application application, Boolean isWaitingRoom, String doctorName, CallRequest callRequest) {
        Log.d("CallActivity", "createNotificationBarCall");
        final Class<? extends Service> notificationService = CallNotificationService.class;
        final Intent notificationIntent = new Intent(application, notificationService);
        ContextCompat.startForegroundService(application, notificationIntent);
    }

    private LinearLayout bigView;
    private FrameLayout smallView;
    private LinearLayout top_option, recording_view, vitalsView, vitalHeaderView;
    private ImageView minimize, speaker_type, audio_mute, video_mute, video_switch, flip_camera, hang_iv, answer_iv, arrow_iv, name_arrow;
    private TextView name_tv, user_info_tv, status_tv, call_quality_tv;
    private Chronometer recording_tv;
    private RoundCornerConstraintLayout recording_dot, quality_disclaimer, call_quality_lay, patient_disclaimer;
    private CircleImageView profile_iv;
    private ViewPager viewPager;
    private ConstraintLayout mainLay, user_info_lay, incoming_view;
    private RippleBackground profile_background;
    private ImageView audio_disabled_iv, video_disabled_iv;
    private TextView quality_disclaimer_tv, display_name, call_type_tv;
    private ImageView[] indicators = new ImageView[0];
    private LinearLayout pager_indicator_container;
    private ConstraintLayout pager_container;
    private TextView vital_empty_tv;

    private final int CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE = 120;

    @Nullable
    public Dialog currentShowingDialog;

    @Nullable
    private VitalsManager vitalsManager;

    @Nullable
    private CommonUserApiResponseModel otherPersonDetail;

    @Nullable
    private Observer<ErrorModel> errorModelObserver;

    @Nullable
    private VitalCallAdapter vitalCallAdapter;

    private Boolean isCameraFlipped = false;

    @Nullable
    private TimerRunnable uiToggleTimer;

    private String currentCallQuality = OpenTokConstants.none;

    private boolean triggerAnswer = false;

    @Nullable
    private CompleteListener lastTaskToDo;

    private OpenTok activeCall;
    private CallRequest callRequest;
    private boolean isScreenBroadcastRegistered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestFullScreenMode();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null)
                keyguardManager.requestDismissKeyguard(this, null);
        } else {
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_call);

        activeCall = CallManager.shared.getActiveCallToShow();

        if(activeCall != null)
            otherPersonDetail = activeCall.getOtherPersonDetail();

        stopNotificationService();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(VOIP_NOTIFICATION_ID);


        Object object = getIntent().getSerializableExtra(ArgumentKeys.CALL_INITIATE_MODEL);
        if (object != null) {
            callRequest = (CallRequest) object;
        }

        if (callRequest == null && activeCall != null) {
            callRequest = activeCall.getCallRequest();
        }

        if (callRequest.isCalling()) {
            Log.d("CallActivity", "connectToSession");
            activeCall.connectToSession();
        }

        if (savedInstanceState != null) {
            currentCallQuality = savedInstanceState.getString(ArgumentKeys.CALL_QUALITY);
        }

        triggerAnswer = getIntent().getBooleanExtra(ArgumentKeys.TRIGGER_ANSWER, false);
        initView();

        if(activeCall != null) {
            if (activeCall.getConnectedDate() != null) {
                long lastSuccess = activeCall.getConnectedDate().getTime();
                long elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();
                recording_tv.setBase(lastSuccess - elapsedRealtimeOffset);
                callDidStarted();
            } else {
                recording_tv.stop();
                if (!activeCall.getCallType().equals(OpenTokConstants.education)) {
                    profile_background.startRippleAnimation();
                }
            }
        }

        updateMinimizeButton(Constants.idle);

        if(activeCall != null)
            activeCall.setup(getRemoteView(), getLocalView());

        updateCallQuality(currentCallQuality);

        final Class<? extends Service> service = CallMinimizeService.class;
        final Intent intent = new Intent(CallActivity.this, service);
        stopService(intent);

        int callAction = getIntent().getIntExtra(ArgumentKeys.CALL_REQUEST_ACTION, 0);
        if (callAction != 0) {
            switch (callAction) {
                case OpenTokConstants.receivedRequestForVideoSwap:
                    receivedRequestForVideoSwap();
                    break;
                case OpenTokConstants.receivedResponseForVideoSwap:
                    receivedResponseForVideoSwap(getIntent().getBooleanExtra(ArgumentKeys.CALL_REQUEST_DATA, false));
                    break;
                case OpenTokConstants.didReceiveVitalData:
                    didReceiveVitalData(getIntent().getStringExtra(ArgumentKeys.CALL_REQUEST_DATA), getIntent().getStringExtra(ArgumentKeys.CALL_REQUEST_DATA_1));
                    break;
            }
        }
        registerReceiver(screenbroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(screenbroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(screenbroadcast, new IntentFilter(Intent.ACTION_USER_PRESENT));
        isScreenBroadcastRegistered = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean triggerAnswer = intent.getBooleanExtra(ArgumentKeys.TRIGGER_ANSWER, false);
        boolean triggerReject = intent.getBooleanExtra(ArgumentKeys.TRIGGER_END, false);
        getIntent().putExtra(ArgumentKeys.TRIGGER_ANSWER, triggerAnswer);
        getIntent().putExtra(ArgumentKeys.TRIGGER_END, triggerReject);
        this.triggerAnswer = triggerAnswer;
        Log.d("CallActivity", "onNewIntent " + triggerAnswer + " " + triggerReject);
    }

    //Create a receiver for screen-on/screen-off
    BroadcastReceiver screenbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String strAction = intent.getAction();
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (strAction.equals(Intent.ACTION_USER_PRESENT) || strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON))
                    if (myKM.inKeyguardRestrictedInputMode()) {

                    } else {
                        if (activeCall.getCallState() == OpenTokConstants.waitingForUserAction) {
                            stopNotificationService();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CallActivity.createNotificationBarCall(getApplication(), false, callRequest.getDoctorName(), callRequest);
                                }
                            }, 1500);
                        }
                        System.out.println("Screen off " + "UNLOCKED");
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopNotificationService();
            }
        }, 1000);
        activeCall.setUIListener(this);
        smallView.setOnTouchListener(new MoveViewTouchListener(smallView));
        didChangedAudioInput(activeCall.getCurrentAudioState());
        LocalBroadcastManager.getInstance(application).sendBroadcast(new Intent(Constants.CALL_ACTIVITY_RESUMED));

        Log.d("CallActivity", "onResume");
        if (triggerAnswer) {
            Log.d("CallActivity", "triggerAnswer true");
            getIntent().putExtra(ArgumentKeys.TRIGGER_ANSWER, false);
            triggerAnswer = false;
            answerTheCall();
        } else if (getIntent().getBooleanExtra(ArgumentKeys.TRIGGER_END, false)) {
            activeCall.endCall(OpenTokConstants.endCallPressed);
            Log.d("CallActivity", "TRIGGER_END true");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (currentShowingDialog != null && currentShowingDialog.isShowing())
            currentShowingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deinit();
    }

    private void deinit() {
        activeCall.resetUIListener(this);
        activeCall.removeRemoteandLocalView(getRemoteView(), getLocalView());

        if (errorModelObserver != null) {
            activeCall.getOpenTokViewModel().getErrorModelLiveData().removeObserver(errorModelObserver);
        }

        try {
            if (isScreenBroadcastRegistered)
                unregisterReceiver(screenbroadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        //disable back press
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstance) {
        super.onSaveInstanceState(saveInstance);

        saveInstance.putString(ArgumentKeys.CALL_QUALITY, currentCallQuality);
    }

    private void initView() {
        bigView = findViewById(R.id.bigView);
        smallView = findViewById(R.id.smallView);
        top_option = findViewById(R.id.top_option);
        recording_view = findViewById(R.id.recording_view);
        vitalsView = findViewById(R.id.vitalsView);
        minimize = findViewById(R.id.minimize);
        speaker_type = findViewById(R.id.speaker_type);
        audio_mute = findViewById(R.id.audio_mute);
        video_mute = findViewById(R.id.video_mute);
        video_switch = findViewById(R.id.video_switch);
        flip_camera = findViewById(R.id.flip_camera);
        answer_iv = findViewById(R.id.answer_iv);
        hang_iv = findViewById(R.id.hang_iv);
        arrow_iv = findViewById(R.id.arrow_iv);
        recording_tv = findViewById(R.id.recording_tv);
        name_tv = findViewById(R.id.name_tv);
        user_info_tv = findViewById(R.id.user_info_tv);
        status_tv = findViewById(R.id.status_tv);
        recording_dot = findViewById(R.id.recording_dot);
        profile_iv = findViewById(R.id.profile_iv);
        viewPager = findViewById(R.id.viewPager);
        mainLay = findViewById(R.id.main_container);
        profile_background = findViewById(R.id.profile_background);
        user_info_lay = findViewById(R.id.user_info_lay);
        incoming_view = findViewById(R.id.incoming_view);
        display_name = findViewById(R.id.display_name);
        call_type_tv = findViewById(R.id.call_type_tv);
        pager_indicator_container = findViewById(R.id.pager_indicator_container);
        pager_container = findViewById(R.id.pager_container);
        vital_empty_tv = findViewById(R.id.vital_empty_tv);
        name_arrow = findViewById(R.id.name_arrow);

        quality_disclaimer = findViewById(R.id.quality_disclaimer);
        audio_disabled_iv = findViewById(R.id.audio_disabled_iv);
        video_disabled_iv = findViewById(R.id.video_disabled_iv);
        quality_disclaimer_tv = findViewById(R.id.quality_disclaimer_tv);

        call_quality_tv = findViewById(R.id.call_quality_tv);
        call_quality_lay = findViewById(R.id.call_quality_lay);
        patient_disclaimer = findViewById(R.id.patient_disclaimer);

        vitalHeaderView = findViewById(R.id.vitalHeaderView);

        hang_iv.setOnClickListener(this);
        answer_iv.setOnClickListener(this);
        minimize.setOnClickListener(this);
        speaker_type.setOnClickListener(this);
        audio_mute.setOnClickListener(this);
        video_mute.setOnClickListener(this);
        video_switch.setOnClickListener(this);
        flip_camera.setOnClickListener(this);
        vitalHeaderView.setOnClickListener(this);
        mainLay.setOnClickListener(this);

        updateUI();
        showOtherPersonDetails();

        patient_disclaimer.setOnTouchListener(new OnSwipeTouchListener(CallActivity.this) {
            public void onSwipeTop() {

            }

            public void onSwipeRight() {
                dismissPatientDisclaimer(false);
            }

            public void onSwipeLeft() {
                dismissPatientDisclaimer(true);
            }

            public void onSwipeBottom() {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (indicators.length <= 1) {
                    return;
                }

                for (int i = 0; i < indicators.length; i++) {
                    if (position == i) {
                        indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));
                    } else {
                        indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_unselected_indicator));
                    }
                }

                if (vitalCallAdapter != null) {
                    vitalCallAdapter.didScrolled(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void stopNotificationService() {
        final Class<? extends Service> notificationService = CallNotificationService.class;
        final Intent notificationIntent = new Intent(CallActivity.this, notificationService);
        stopService(notificationIntent);
    }

    private void updateMinimizeButton(int state) {
        if (callRequest.isForGuestUser() && UserType.isUserPatient()) {
            minimize.setVisibility(View.GONE);
        } else if (callRequest.getCallSettings().expiredTokenWhileCallPlaced != null) {
            minimize.setVisibility(View.GONE);
        } else {

            if (activeCall != null && activeCall.getCallType() != null  && activeCall.getCallType().equals(OpenTokConstants.video) && UserType.isUserPatient() && state == Constants.measuring) {
                minimize.setVisibility(View.GONE);
            } else {
                minimize.setVisibility(View.VISIBLE);
            }

        }
    }

    private void dismissPatientDisclaimer(Boolean toLeft) {
        activeCall.setPatientDisclaimerDismissed(true);
        Transition transition = new ChangeBounds();
        transition.setDuration(800);
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));

        TransitionManager.beginDelayedTransition(mainLay, transition);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLay);
        ConstrainSetUtil.clearAllConstraint(constraintSet, patient_disclaimer.getId());

        if (toLeft) {
            constraintSet.connect(patient_disclaimer.getId(), ConstraintSet.RIGHT, mainLay.getId(), ConstraintSet.LEFT);
        } else {
            constraintSet.connect(patient_disclaimer.getId(), ConstraintSet.LEFT, vitalsView.getId(), ConstraintSet.RIGHT);
        }

        constraintSet.connect(patient_disclaimer.getId(), ConstraintSet.BOTTOM, mainLay.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(patient_disclaimer.getId(), ConstraintSet.TOP, mainLay.getId(), ConstraintSet.TOP);


        constraintSet.applyTo(mainLay);
    }

    private void createIndicator(int count) {
        if (!UserType.isUserPatient() || count <= 1) {
            pager_indicator_container.setVisibility(View.GONE);
            return;
        }

        pager_indicator_container.removeAllViews();

        indicators = new ImageView[count];

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(CallActivity.this);

            if (i == viewPager.getCurrentItem()) {
                indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));
            } else {
                indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_unselected_indicator));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            pager_indicator_container.addView(indicators[i], params);
        }

    }

    private void toggleVisibility() {
        if (top_option.getVisibility() == View.VISIBLE) {

            top_option.setVisibility(View.GONE);
            user_info_lay.setVisibility(View.GONE);
            hang_iv.setVisibility(View.GONE);
            vitalHeaderView.setVisibility(View.GONE);
            profile_iv.setVisibility(View.GONE);

            if (uiToggleTimer != null) {
                uiToggleTimer.setStopped(true);
                uiToggleTimer = null;
            }

        } else {

            top_option.setVisibility(View.VISIBLE);
            user_info_lay.setVisibility(View.VISIBLE);
            hang_iv.setVisibility(View.VISIBLE);
            showVitalHeader();

            if (activeCall != null && activeCall.getCallType().equals(OpenTokConstants.audio)) {
                profile_iv.setVisibility(View.VISIBLE);
            }

            setToggleTimer();
        }
    }

    private void setToggleTimer() {
        if (activeCall != null && activeCall.getConnectedDate() == null) {
            return;
        }

        if (uiToggleTimer != null) {
            uiToggleTimer.setStopped(true);
            uiToggleTimer = null;
        }

        final int interval = 5000; // 5 Second
        Handler handler = new Handler();
        TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleVisibility();
                    }
                });

            }
        });

        uiToggleTimer = runnable;
        handler.postDelayed(runnable, interval);
    }

    private void updateState(int currentState) {
        Log.d("CallActivity", "updateState " + currentState);
        if(activeCall != null )
            activeCall.setCallState(currentState);

            updateUI();

    }

    private void updateUI() {
        if(activeCall != null ) {
            switch (activeCall.getCallState()) {
                case OpenTokConstants.waitingForUserAction:

                    if (activeCall.getCallType().equals(OpenTokConstants.education)) {
                        hang_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
                        hang_iv.setVisibility(View.GONE);
                        answer_iv.setVisibility(View.GONE);
                        call_type_tv.setText("");
                        incoming_view.setVisibility(View.GONE);
                        moveHangButtonToCenter();
                    } else {
                        hang_iv.setVisibility(View.VISIBLE);
                        answer_iv.setVisibility(View.VISIBLE);

                        if (activeCall.isVideoCall()) {
                            call_type_tv.setText(String.format(getString(R.string.telehealer_video), getString(R.string.app_name)));
                        } else {
                            call_type_tv.setText(String.format(getString(R.string.telehealer_audio), getString(R.string.app_name)));
                        }

                        incoming_view.setVisibility(View.VISIBLE);

                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        answer_iv.startAnimation(shake);
                    }

                    top_option.setVisibility(View.GONE);
                    recording_view.setVisibility(View.GONE);
                    vitalsView.setVisibility(View.GONE);
                    patient_disclaimer.setVisibility(View.GONE);

                    smallView.setVisibility(View.GONE);
                    bigView.setVisibility(View.GONE);

                    user_info_lay.setVisibility(View.GONE);

                    if (otherPersonDetail != null) {
                        display_name.setText(otherPersonDetail.getDisplayName());
                    }

                    break;
                case OpenTokConstants.outGoingCallGoingOn:

                    if (activeCall.getCallType().equals(OpenTokConstants.education)) {
                        hang_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
                        hang_iv.setVisibility(View.GONE);
                        call_type_tv.setText("");
                        moveHangButtonToCenter();
                        profile_iv.setVisibility(View.GONE);
                    } else {
                        hang_iv.setVisibility(View.VISIBLE);
                    }

                    answer_iv.clearAnimation();

                    top_option.setVisibility(View.VISIBLE);
                    recording_view.setVisibility(View.VISIBLE);

                    if (activeCall.isVideoCall() && !callRequest.isForGuestUser()) {
                        vitalsView.setVisibility(View.VISIBLE);
                    } else {
                        vitalsView.setVisibility(View.GONE);
                    }

                    smallView.setVisibility(View.VISIBLE);
                    bigView.setVisibility(View.VISIBLE);

                    answer_iv.setVisibility(View.GONE);

                    user_info_lay.setVisibility(View.VISIBLE);
                    incoming_view.setVisibility(View.GONE);

                    moveHangButtonToCenter();
                case OpenTokConstants.incomingCallGoingOn:
                    animation();
                    break;
            }

            if (activeCall.getCallState() != OpenTokConstants.waitingForUserAction) {
                if (UserType.isUserPatient() && !activeCall.getPatientDisclaimerDismissed()) {
                    patient_disclaimer.setVisibility(View.VISIBLE);
                } else {
                    patient_disclaimer.setVisibility(View.GONE);
                }

                updateUIForAudioVideo();
            }

            if (!callRequest.getCallSettings().isRecording_enabled()) {
                findViewById(R.id.recording_dot_parent).setVisibility(View.GONE);
            } else {
                findViewById(R.id.recording_dot_parent).setVisibility(View.VISIBLE);
            }
        }
    }

    private void animation() {
        switch (activeCall.getCallState()) {
            case OpenTokConstants.incomingCallGoingOn:

                hang_iv.setVisibility(View.VISIBLE);
                answer_iv.clearAnimation();

                Transition transition = new ChangeBounds();
                transition.setDuration(800);
                transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));

                TransitionManager.beginDelayedTransition(mainLay, transition);
                moveHangButtonToCenter();

                top_option.setVisibility(View.VISIBLE);
                user_info_lay.setVisibility(View.VISIBLE);
                recording_view.setVisibility(View.VISIBLE);
                bigView.setVisibility(View.VISIBLE);
                smallView.setVisibility(View.VISIBLE);
                incoming_view.setVisibility(View.GONE);

                if (activeCall.isVideoCall()) {
                    vitalsView.setVisibility(View.VISIBLE);
                } else {
                    vitalsView.setVisibility(View.GONE);
                }
                break;
        }
    }

    private ViewGroup getLocalView() {
        if (activeCall.isViewSwapped()) {
            return bigView;
        } else {
            return smallView;
        }
    }

    private ViewGroup getRemoteView() {
        if (!activeCall.isViewSwapped()) {
            return bigView;
        } else {
            return smallView;
        }
    }

    private void moveHangButtonToCenter() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLay);

        ConstrainSetUtil.clearAllConstraint(constraintSet, answer_iv.getId());
        ConstrainSetUtil.clearAllConstraint(constraintSet, hang_iv.getId());

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());

        ConstrainSetUtil.assignLeftAndRightToMain(constraintSet, mainLay.getId(), hang_iv.getId(), 0);
        constraintSet.connect(hang_iv.getId(), ConstraintSet.BOTTOM, mainLay.getId(), ConstraintSet.BOTTOM, px);

        constraintSet.connect(answer_iv.getId(), ConstraintSet.LEFT, mainLay.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(answer_iv.getId(), ConstraintSet.BOTTOM, mainLay.getId(), ConstraintSet.BOTTOM, px);

        constraintSet.applyTo(mainLay);
    }

    private void showOtherPersonDetails() {
        if (activeCall != null && activeCall.getCallType().equals(OpenTokConstants.education)) {
            name_tv.setText(callRequest.getEducationTitle());
            user_info_tv.setText(callRequest.getEducationDescription());
        } else if (otherPersonDetail != null) {
            name_tv.setText(otherPersonDetail.getDisplayName());
            user_info_tv.setText(otherPersonDetail.getDisplayInfo());
            Utils.setImageWithGlide(getApplicationContext(), profile_iv, otherPersonDetail.getUser_avatar(), getDrawable(R.drawable.profile_placeholder), true, true);
        }
    }

    private void switchToVideo() {
        if(activeCall != null) {
            activeCall.startPublishVideo();
            activeCall.setCallType(OpenTokConstants.video);
            updateUIForAudioVideo();
            activeCall.setup(getRemoteView(), getLocalView());
        }
    }

    private void updateUIForAudioVideo() {

        updateAudioMuteUI();
        updateVidioMuteUI();

        if (activeCall.getConnectedDate() != null) {
            callDidStarted();
        }

        if (!activeCall.getCallType().equals(OpenTokConstants.audio)) {

            if (activeCall.getConnectedDate() != null) {

                if (activeCall.getCallType().equals(OpenTokConstants.video)) {
                    smallView.setVisibility(View.VISIBLE);
                }

                bigView.setVisibility(View.VISIBLE);
            }

            video_switch.setVisibility(View.GONE);
            video_mute.setVisibility(View.VISIBLE);
            flip_camera.setVisibility(View.VISIBLE);

            if (activeCall.getCallType().equals(OpenTokConstants.video)) {
                vitalsView.setVisibility(View.VISIBLE);
                if (viewPager.getAdapter() == null) {
                    if (vitalCallAdapter == null) {

                        ArrayList<VitalDevice> devices = new ArrayList<>();
                        if (UserType.isUserPatient()) {
                            ArrayList<VitalDevice> items = VitalPairedDevices.getAllPairedDevices(appPreference).getDevices();

                            ArrayListUtil util = new ArrayListUtil<VitalDevice, VitalDevice>();
                            devices = util.filterList(items, new ArrayListFilter<VitalDevice>() {
                                @Override
                                public Boolean needToAddInFilter(VitalDevice model) {
                                    return !model.getType().equals("BG5");
                                }
                            });

                        }

                        vitalCallAdapter = new VitalCallAdapter(getSupportFragmentManager(), devices, this);
                    }

                    viewPager.setAdapter(vitalCallAdapter);
                    viewPager.setCurrentItem(0);
                }
            } else {
                smallView.setVisibility(View.GONE);
            }

        } else {

            if (!UserType.isUserPatient()) {
                if (PermissionChecker.with(this).isGranted(PermissionConstants.PERMISSION_CAM_MIC)) {
                    video_switch.setAlpha(1f);
                    video_switch.setEnabled(true);
                } else {
                    video_switch.setAlpha(0.5f);
                    video_switch.setEnabled(false);
                }
                video_switch.setVisibility(View.VISIBLE);
            } else {
                video_switch.setVisibility(View.GONE);
            }

            video_mute.setVisibility(View.GONE);
            flip_camera.setVisibility(View.GONE);
        }
    }

    private void updateAudioMuteUI() {
        if (activeCall.getConnectedDate() != null && !activeCall.isPublisherAudioOn()) {
            audio_mute.setImageDrawable(getDrawable(R.drawable.chat_audio_mute_active));
        } else {
            audio_mute.setImageDrawable(getDrawable(R.drawable.mic_off));
        }
    }

    private void updateVidioMuteUI() {
        if (activeCall.getConnectedDate() != null && !activeCall.isPublisherVideoOn()) {
            video_mute.setImageDrawable(getDrawable(R.drawable.chat_video_mute_active));
        } else {
            video_mute.setImageDrawable(getDrawable(R.drawable.video_mute));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_iv:

                if (activeCall.getCallType().equals(OpenTokConstants.education)) {

                    if (activeCall.getConnectedDate() == null) {
                        hang_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                        activeCall.startArchiving();
                    } else {
                        activeCall.endCall(OpenTokConstants.endCallPressed);
                    }

                } else {
                    hang_iv.setClickable(false);
                    if (!callRequest.isCallForDirectWaitingRoom() && !activeCall.getCalling() && activeCall.getConnectedDate() == null) {
                        activeCall.endCall(OpenTokConstants.notPickedUp);
                        EventRecorder.recordCallUpdates("declined_call", null);

                        APNSPayload payload = new APNSPayload();
                        HashMap<String, Object> aps = new HashMap<>();
                        if (activeCall.getCallType().equals(OpenTokConstants.video)) {
                            aps.put(PubNubNotificationPayload.ALERT, getString(R.string.video_missed_call));
                        } else {
                            aps.put(PubNubNotificationPayload.ALERT, getString(R.string.audio_missed_call));
                        }
                        if (activeCall.getOtherPersonDetail() != null) {
                            aps.put(PubNubNotificationPayload.TITLE, activeCall.getOtherPersonDetail().getDisplayName());
                            aps.put(PubNubNotificationPayload.MEDIA_URL, activeCall.getOtherPersonDetail().getUser_avatar());
                        } else {
                            aps.put(PubNubNotificationPayload.TITLE, "Missed");
                        }
                        payload.setAps(aps);

                        Intent intent = new Intent(application, HomeActivity.class);
                        intent.putExtra(ArgumentKeys.SELECTED_MENU_ITEM, R.id.menu_recent);

                        Utils.createNotification(payload, intent);

                    } else {
                        EventRecorder.recordCallUpdates("end_call_pressed", null);
                        activeCall.endCall(OpenTokConstants.endCallPressed);
                    }
                }
                break;
            case R.id.answer_iv:
                if (activeCall.getCallState() == OpenTokConstants.waitingForUserAction) {
                    answerTheCall();
                }
                break;
            case R.id.minimize:
                if (activeCall.getConnectedDate() == null) {
                    return;
                }

                EventRecorder.recordCallUpdates("callview_minimized", null);
                minimizeView(null);
                break;
            case R.id.speaker_type:
                if (activeCall.getConnectedDate() == null) {
                    return;
                }

                ArrayList<AudioDeviceInfo> deviceInfos = activeCall.getConnectedAudioDevices();

                Set<String> audioTypes = new HashSet<>();
                HashMap<String, AudioDeviceInfo> audioMap = new HashMap<>();
                int selectedPosition = 0;
                for (int i = 0; i < deviceInfos.size(); i++) {

                    AudioDeviceInfo audioDeviceInfo = deviceInfos.get(i);

                    switch (audioDeviceInfo.getType()) {
                        case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                            audioTypes.add(getString(R.string.speaker));
                            audioMap.put(getString(R.string.speaker), audioDeviceInfo);

                            if (activeCall.getCurrentAudioState() == OpenTokConstants.speaker) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                            audioTypes.add(getString(R.string.earPiece));
                            audioMap.put(getString(R.string.earPiece), audioDeviceInfo);

                            if (activeCall.getCurrentAudioState() == OpenTokConstants.inEarSpeaker) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                            audioTypes.add(getString(R.string.headphones));
                            audioMap.put(getString(R.string.headphones), audioDeviceInfo);

                            if (activeCall.getCurrentAudioState() == OpenTokConstants.headPhones) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                            audioTypes.add(getString(R.string.bluetooth));
                            audioMap.put(getString(R.string.bluetooth), audioDeviceInfo);

                            if (activeCall.getCurrentAudioState() == OpenTokConstants.bluetooth) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                    }
                }

                ArrayList<String> types = new ArrayList<>(audioTypes);

                ItemPickerDialog itemPickerDialog = new ItemPickerDialog(this, getString(R.string.choose_audio_input), types, new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
                        AudioDeviceInfo audioDeviceInfo = audioMap.get(types.get(position));
                        if (audioDeviceInfo != null) {
                            switch (audioDeviceInfo.getType()) {
                                case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                                    activeCall.setAudioInput(OpenTokConstants.speaker);
                                    break;
                                case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                                    activeCall.setAudioInput(OpenTokConstants.inEarSpeaker);
                                    break;
                                case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                                    activeCall.setAudioInput(OpenTokConstants.headPhones);
                                    break;
                                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                                case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                                    activeCall.setAudioInput(OpenTokConstants.bluetooth);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void didCancelled() {

                    }
                }, selectedPosition);
                itemPickerDialog.setCancelable(false);
                itemPickerDialog.show();

                this.currentShowingDialog = itemPickerDialog;

                break;
            case R.id.audio_mute:
                if (activeCall.getConnectedDate() == null) {
                    return;
                }

                activeCall.toggleAudio();
                if (!activeCall.isPublisherAudioOn()) {
                    EventRecorder.recordCallUpdates("audio_muted", null);
                }
                updateAudioMuteUI();
                break;
            case R.id.video_mute:
                if (activeCall.getConnectedDate() == null) {
                    return;
                }

                activeCall.toggleVideo();

                if (!activeCall.isPublisherVideoOn()) {
                    EventRecorder.recordCallUpdates("video_muted", null);
                }

                updateVidioMuteUI();
                break;
            case R.id.video_switch:
                if (activeCall.getConnectedDate() == null) {
                    return;
                }

                Log.v("CallActivity", "onclick");

                this.currentShowingDialog = Utils.showAlertDialog(this, getString(R.string.video_call_request), getString(R.string.video_call_request_confirmation), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (PermissionChecker.with(CallActivity.this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
                            activeCall.requestForVideoSwap();
                        }

                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.flip_camera:
                if (activeCall.getConnectedDate() == null) {
                    return;
                }

                isCameraFlipped = !isCameraFlipped;

                if (isCameraFlipped) {
                    flip_camera.setImageDrawable(getDrawable(R.drawable.ic_camera_rotate_activate));
                } else {
                    flip_camera.setImageDrawable(getDrawable(R.drawable.switch_camera));
                }

                EventRecorder.recordCallUpdates("toggle_camera", null);
                activeCall.flipVideo();
                break;
            case R.id.vitalHeaderView:
                if (pager_container.getVisibility() == View.GONE) {
                    showVitalContainer();
                } else {
                    hideVitalContainer();
                }
                break;
            case R.id.main_container:
                if (activeCall.getConnectedDate() != null)
                    toggleVisibility();
                break;
            case R.id.name_arrow:
            case R.id.name_tv:
                minimizeView(new CompleteListener() {
                    @Override
                    public void didComplete(boolean isSuccess) {
                        if (isSuccess) {
                            Intent intent = new Intent(CallActivity.this, UserDetailActivity.class);
                            intent.putExtra(Constants.USER_DETAIL, otherPersonDetail);
                            intent.putExtra(ArgumentKeys.FROM_CALL, true);
                            startActivity(intent);
                        }
                    }
                });
                break;
            case R.id.smallView:
                Log.d("CallActivity", "small View click");
                activeCall.setViewSwapped(!activeCall.isViewSwapped());
                activeCall.setup(getRemoteView(), getLocalView());
                break;
        }
    }

    private void minimizeView(@Nullable CompleteListener completeListener) {
        if (canDrawOverlays(CallActivity.this)) {
            startFloatingViewService();

            if (!isAnActivityPresentUnder()) {
                Intent intent = new Intent(CallActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            finish();
            if (completeListener != null)
                completeListener.didComplete(true);
        } else {
            Utils.showAlertDialog(this, getString(R.string.enable_overflow), getString(R.string.enable_overflow_description), getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    lastTaskToDo = completeListener;
                    final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (completeListener != null)
                        completeListener.didComplete(false);
                }
            });

        }
    }

    private void answerTheCall() {
        Log.d("CallActivity", "answerTheCall");
        EventRecorder.recordCallUpdates("CALL_ACCEPTED", null);

        if (activeCall.isVideoCall()) {
            EventRecorder.recordCallUpdates("opening_video_call", null);
        } else {
            EventRecorder.recordCallUpdates("opening_audio_call", null);
        }

        int requiredPermission;
        if (activeCall.isVideoCall()) {
            requiredPermission = PermissionConstants.PERMISSION_CAM_MIC;
        } else {
            requiredPermission = PermissionConstants.PERMISSION_MICROPHONE;
        }

        if (PermissionChecker.with(this).isGranted(requiredPermission)) {
            activeCall.connectToSession();
            updateState(OpenTokConstants.incomingCallGoingOn);
        } else {
            activeCall.endCall(OpenTokConstants.other);

            String description;
            if (activeCall.isVideoCall()) {
                if (!PermissionChecker.with(this).isGranted(PermissionConstants.PERMISSION_CAM_MIC)) {
                    description = getString(R.string.microphone_camera_permission);
                } else if (!PermissionChecker.with(this).isGranted(PermissionConstants.PERMISSION_MICROPHONE)) {
                    description = getString(R.string.microphone_permission);
                } else {
                    description = getString(R.string.camera_permission);
                }
            } else {
                description = getString(R.string.microphone_permission);
            }

            String path = null;

            if (otherPersonDetail != null) {
                path = otherPersonDetail.getUser_avatar();
            }

            PushPayLoad pushPayLoad = PubNubNotificationPayload.getCallDismissedPermissionLocalPayload(path, getString(R.string.call_cancelled), description);
            Intent intent = PermissionChecker.with(this).checkAndReturn(requiredPermission);
            Utils.createNotification(pushPayLoad.getPn_apns(), intent);
        }
    }

    private void startFloatingViewService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (getWindow().getAttributes().layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER) {
                throw new RuntimeException("'windowLayoutInDisplayCutoutMode' do not be set to 'never'");
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                throw new RuntimeException("Do not set Activity to landscape");
            }
        }

        final Class<? extends Service> service = CallMinimizeService.class;
        final String key = CallMinimizeService.EXTRA_CUTOUT_SAFE_AREA;
        final Intent intent = new Intent(CallActivity.this, service);
        intent.putExtra(key, FloatingViewManager.findCutoutSafeArea(CallActivity.this));
        ContextCompat.startForegroundService(CallActivity.this, intent);
    }

    private void hideVitalContainer() {
        arrow_iv.setImageDrawable(getDrawable(R.drawable.ic_arrow_up));
        if (pager_container.getVisibility() != View.GONE)
            pager_container.setVisibility(View.GONE);
    }

    private void showVitalContainer() {
        arrow_iv.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_24dp));
        if (pager_container.getVisibility() != View.VISIBLE)
            pager_container.setVisibility(View.VISIBLE);
    }

    private void callDidStarted() {
        recording_tv.start();

        if (activeCall.getCallType().equals(OpenTokConstants.video)) {
            profile_iv.setVisibility(View.GONE);
            if (!callRequest.isForGuestUser()) {
                showVitalHeader();
            }
        } else if (activeCall.getCallType().equals(OpenTokConstants.oneWay) || activeCall.getCallType().equals(OpenTokConstants.education)) {
            profile_iv.setVisibility(View.GONE);
        }

        updateStateView();

        profile_background.stopRippleAnimation();
        Animation blinkAnimation = AnimationUtils.loadAnimation(CallActivity.this, R.anim.blinking);

        if (callRequest.getCallSettings().isRecording_enabled()) {
            recording_dot.startAnimation(blinkAnimation);
        }

        top_option.setAlpha(1.0f);

        setToggleTimer();

        if (!UserType.isUserPatient()) {
            if (!activeCall.getCallType().equals(OpenTokConstants.education)) {
                name_arrow.setVisibility(View.VISIBLE);
                name_arrow.setOnClickListener(this);
                name_tv.setOnClickListener(this);
            }
        } else {
            name_arrow.setVisibility(View.GONE);
        }

        smallView.setOnClickListener(this);

        activeCall.setZOrderTopForLocalView();

        if (UserType.isUserPatient()) {
            final int interval = 15000; // 15 Seconds
            Handler handler = new Handler();
            TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (patient_disclaimer.getVisibility() == View.VISIBLE) {
                                dismissPatientDisclaimer(true);
                            }
                        }
                    });

                }
            });

            handler.postDelayed(runnable, interval);
        }
    }

    private void showVitalHeader() {
        if (callRequest.isForGuestUser()) {
            vitalHeaderView.setVisibility(View.GONE);
        } else {
            vitalHeaderView.setVisibility(View.VISIBLE);
        }
    }

    private void updateStateView() {
        if (activeCall.getPatientCurrentState() != null) {
            didUpdatedPatientLocation(activeCall.getPatientCurrentState());
        } else {
            status_tv.setVisibility(View.GONE);
        }
    }

    //TokBoxUIInterface methods
    @Override
    public void startedCall() {
        recording_tv.setBase(SystemClock.elapsedRealtime());
        callDidStarted();
    }

    @Override
    public void didEndCall(String callRejectionReason) {
        if (activeCall.getCallType().equals(OpenTokConstants.education)) {
            deinit();
            finish();
            return;
        }

        if (isAnActivityPresentUnder()) {
            openFeedBackIfNeeded(callRejectionReason);
        } else {
            openHomeAndFinish(callRejectionReason);
        }
    }

    private boolean isAnActivityPresentUnder() {
        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (mngr != null) {
                return mngr.getAppTasks().get(0).getTaskInfo().numActivities > 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void openHomeAndFinish(String callRejectionReason) {
        /*Intent intent = new Intent(CallActivity.this, HomeActivity.class);
        startActivity(intent);
*/
        openFeedBackIfNeeded(callRejectionReason);
    }

    private void openFeedBackIfNeeded(String callRejectionReason) {
        openFeedBackIfNeeded(callRequest, activeCall.getConnectedDate(), callRejectionReason, CallActivity.this);
        deinit();
        finish();
    }

    public static void openFeedBackIfNeeded(CallRequest callRequest,
                                            Date connectedDate,
                                            String callRejectionReason, Context context) {
        String sessionId = callRequest.getSessionId();
        String to_guid = null;
        String to_name = "";
        String doctor_guid = callRequest.getDoctorGuid();


        if (callRequest.getOtherPersonDetail() != null) {
            to_guid = callRequest.getOtherPersonDetail().getUser_guid();
            to_name = callRequest.getOtherPersonDetail().getDisplayName();
        }
        Date startedTime = connectedDate;

        if (callRequest.isForGuestUser() && UserDetailPreferenceManager.getRole().equals(Constants.ROLE_PATIENT)) {
            Intent feedBackIntent = new Intent(context, GuestUserSignupActivity.class);
            feedBackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(feedBackIntent);
        } else {
            switch (callRejectionReason) {
                case OpenTokConstants.endCallPressed:
                case OpenTokConstants.other:
                case OpenTokConstants.badNetwork:
                case OpenTokConstants.timedOut:
                    Date endedTime = new Date();
                    if (startedTime != null && !TextUtils.isEmpty(sessionId) && endedTime.getTime() - startedTime.getTime() > 5) {
                        Intent feedBackIntent = new Intent(context, CallFeedBackActivity.class);
                        feedBackIntent.putExtra(ArgumentKeys.ORDER_ID, sessionId);
                        feedBackIntent.putExtra(ArgumentKeys.TO_USER_GUID, to_guid);
                        feedBackIntent.putExtra(ArgumentKeys.DOCTOR_GUID, callRequest.getDoctorGuid());
                        feedBackIntent.putExtra(ArgumentKeys.STARTED_DATE, startedTime);
                        feedBackIntent.putExtra(ArgumentKeys.ENDED_DATE, endedTime);
                        if (!UserType.isUserPatient())
                            feedBackIntent.putExtra(ArgumentKeys.PATIENT_ID, callRequest.getOtherPersonDetail().getUser_id());
                        feedBackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(feedBackIntent);
                    }
                    break;
                case OpenTokConstants.notPickedUp:
                case OpenTokConstants.busyInAnotherLine:
                    if (!UserType.isUserPatient()) {
                        Intent intent = new Intent(context, CallMessageActivity.class);
                        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, context.getString(R.string.send_message));
                        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);
                        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_missed_call);
                        intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ArgumentKeys.TITLE, "");

                        if (callRejectionReason.equals(OpenTokConstants.notPickedUp)) {
                            intent.putExtra(ArgumentKeys.DESCRIPTION, to_name + " " + context.getString(R.string.not_available));
                        } else {
                            intent.putExtra(ArgumentKeys.DESCRIPTION, to_name + " " + context.getString(R.string.busy_another_call));
                        }

                        intent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false);
                        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);
                        intent.putExtra(ArgumentKeys.USER_GUID, to_guid);
                        intent.putExtra(ArgumentKeys.DOCTOR_GUID, doctor_guid);

                        context.startActivity(intent);
                    }
                    break;
            }
        }
    }

    @Override
    public void updateCallInfo(String message) {
        status_tv.setText(message);
    }

    @Override
    public void didUpdatedPatientLocation(String state) {
        if (!isValidPatientLocation(state)) {

            if (state.equals(Constants.inValidState)) {
                status_tv.setText(getString(R.string.unable_to_determine_patient_location));
            } else {
                status_tv.setText(getString(R.string.calling_from_outside));
            }

            status_tv.setVisibility(View.VISIBLE);

            Animation blinkAnimation = AnimationUtils.loadAnimation(CallActivity.this, R.anim.blinking);
            status_tv.startAnimation(blinkAnimation);

        } else {
            status_tv.setText("");
            status_tv.setVisibility(View.GONE);
        }
    }

    private Boolean isValidPatientLocation(String otherLocation) {
        List<LicensesBean> licenses = UserDetailPreferenceManager.getLicenses();

        if (licenses.size() > 0) {
            Boolean isValidLocation = false;
            for (LicensesBean licensesBean : licenses) {

                if (licensesBean.getState().toLowerCase().equals(otherLocation.toLowerCase())) {
                    isValidLocation = true;
                    break;
                }
            }
            return isValidLocation;

        } else {
            return true;
        }
    }

    @Override
    public void didReceivedOtherUserDetails(CommonUserApiResponseModel commonUserApiResponseModel) {
        otherPersonDetail = commonUserApiResponseModel;
        showOtherPersonDetails();
    }

    @Override
    public void receivedRequestForVideoSwap() {
        String name = otherPersonDetail != null ? otherPersonDetail.getDisplayName() : getString(R.string.other_person);

        if (PermissionChecker.with(CallActivity.this).isGranted(PermissionConstants.PERMISSION_CAM_MIC)) {
            String message = getString(R.string.requested_video_call, name);

            this.currentShowingDialog = Utils.showAlertDialog(this, getString(R.string.video_call_request), message, getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventRecorder.recordCallUpdates("audio_to_video_accepted", null);
                    if(activeCall != null)
                        activeCall.updateVideoSwapRequest(true);
                    switchToVideo();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventRecorder.recordCallUpdates("audio_to_video_rejected", null);
                    if(activeCall != null)
                        activeCall.updateVideoSwapRequest(false);
                }
            });
        } else {

            EventRecorder.recordCallUpdates("audio_to_video_rejected", null);

            String message = getString(R.string.requested_video_no_camera, name);

            this.currentShowingDialog = Utils.showAlertDialog(this, getString(R.string.request_rejected), message, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, null);

            activeCall.updateVideoSwapRequest(false);
        }
    }

    @Override
    public void receivedResponseForVideoSwap(Boolean isAccepted) {
        if (isAccepted) {
            switchToVideo();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.video_switch_request_declined), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateCallQuality(String callQuality) {
        currentCallQuality = callQuality;
        switch (callQuality) {
            case OpenTokConstants.none:
                call_quality_tv.setText("");
                call_quality_lay.setVisibility(View.GONE);
                break;
            case OpenTokConstants.hd:
                call_quality_tv.setText(OpenTokConstants.hd);
                call_quality_lay.setVisibility(View.VISIBLE);
                break;
            case OpenTokConstants.sd:
                call_quality_tv.setText(OpenTokConstants.sd);
                call_quality_lay.setVisibility(View.VISIBLE);
                break;
            case OpenTokConstants.poorConnection:
                call_quality_tv.setText(OpenTokConstants.poorConnection);
                call_quality_lay.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void updateVideoQuality(String qualityString, Boolean isMuted) {
        showQualityView(qualityString, activeCall.getSubscriberAudioMuted(), isMuted);
    }

    @Override
    public void didSubscribeVideoDisabled() {
        updateQualityView();
    }

    @Override
    public void didSubscribeAudioDisabled() {
        updateQualityView();
    }

    @Override
    public void didSubscribeVideoEnabled() {
        updateQualityView();
    }

    @Override
    public void didSubscribeAudioEnabled() {
        updateQualityView();
    }

    @Override
    public void bluetoothMediaAction(Boolean forEnd) {
        if (forEnd) {
            onClick(hang_iv);
        } else {
            onClick(answer_iv);
        }
    }

    private void updateBatterInfo() {
        if (activeCall.isOtherPersonBatteryLow() && !activeCall.getCallType().equals(OpenTokConstants.education) && !activeCall.getCallType().equals(OpenTokConstants.oneWay)) {
            String remoteuserDisplayName = otherPersonDetail != null ? otherPersonDetail.getDisplayName() : getString(R.string.other_person);
            audio_disabled_iv.setVisibility(View.VISIBLE);
            video_disabled_iv.setVisibility(View.GONE);
            audio_disabled_iv.setImageResource(R.drawable.ic_battery_low);
            quality_disclaimer_tv.setText(getString(R.string.battery_low_message, remoteuserDisplayName));
            quality_disclaimer.setVisibility(View.VISIBLE);
        } else {
            quality_disclaimer.setVisibility(View.GONE);
        }
    }

    private void updateQualityView() {
        if (!activeCall.getSubscriberAudioMuted() && !activeCall.getSubscriberVideoMuted()) {
            updateBatterInfo();
        } else {

            audio_disabled_iv.setImageResource(R.drawable.mic_off);
            String remoteuserDisplayName = otherPersonDetail != null ? otherPersonDetail.getDisplayName() : getString(R.string.other_person);

            if (activeCall.getSubscriberAudioMuted() && activeCall.getSubscriberVideoMuted()) {

                String message = getString(R.string.mute_audio_video, remoteuserDisplayName);
                showQualityView(message, activeCall.getSubscriberAudioMuted(), activeCall.getSubscriberVideoMuted());

            } else if (activeCall.getSubscriberAudioMuted()) {

                String message = getString(R.string.mute_audio, remoteuserDisplayName);
                showQualityView(message, activeCall.getSubscriberAudioMuted(), activeCall.getSubscriberVideoMuted());

            } else if (activeCall.getSubscriberVideoMuted()) {

                String message = getString(R.string.disabled_video, remoteuserDisplayName);
                showQualityView(message, activeCall.getSubscriberAudioMuted(), activeCall.getSubscriberVideoMuted());

            }
        }
    }

    private void showQualityView(String qualityString, Boolean isAudioMuted, Boolean isVideoMuted) {
        quality_disclaimer_tv.setText(qualityString);

        if (isAudioMuted && isVideoMuted) {
            audio_disabled_iv.setVisibility(View.VISIBLE);
            video_disabled_iv.setVisibility(View.VISIBLE);
        } else if (isAudioMuted) {
            audio_disabled_iv.setVisibility(View.VISIBLE);
            video_disabled_iv.setVisibility(View.GONE);
        } else if (isVideoMuted) {
            audio_disabled_iv.setVisibility(View.GONE);
            video_disabled_iv.setVisibility(View.VISIBLE);
        } else {
            quality_disclaimer.setVisibility(View.GONE);
            return;
        }

        quality_disclaimer.setVisibility(View.VISIBLE);
    }

    @Override
    public void didReceiveVitalData(String data, String type) {
        Log.d("CallActivity", "didReceiveVitalData " + type);

        if (pager_container.getVisibility() != View.VISIBLE)
            showVitalContainer();

        if (vitalCallAdapter != null)
            vitalCallAdapter.didReceivedVitalData(type, data);
    }

    @Override
    public void didPublishStarted() {
        hang_iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void didChangedAudioInput(int state) {
        switch (state) {
            case OpenTokConstants.bluetooth:
                speaker_type.setImageResource(R.drawable.bluetooth);
                break;
            case OpenTokConstants.headPhones:
                speaker_type.setImageResource(R.drawable.headphone);
                break;
            case OpenTokConstants.speaker:
                speaker_type.setImageResource(R.drawable.speaker);
                break;
            case OpenTokConstants.inEarSpeaker:
                speaker_type.setImageResource(R.drawable.speaker);
                break;
        }
    }

    @Override
    public void didChangedBattery() {
        updateQualityView();
    }

    @Override
    public void assignTokBoxApiViewModel(OpenTokViewModel openTokViewModel) {

        if (errorModelObserver != null) {
            openTokViewModel.getErrorModelLiveData().removeObserver(errorModelObserver);
        }

        errorModelObserver = new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null && (errorModel.getMessage() != null || !errorModel.getMessage().isEmpty())  ) {
                    String message = errorModel.getMessage();
                    Log.e("neem", "onChanged: "+message );
                    currentShowingDialog = Utils.showAlertDialog(CallActivity.this, getString(R.string.app_name), message, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activeCall.endCall(OpenTokConstants.other);
                        }
                    }, null);
                }

            }
        };

        openTokViewModel.getErrorModelLiveData().observe(this, errorModelObserver);
    }

    @Override
    public String getCurrentCallQuality() {
        return currentCallQuality;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("CallActivity", "onActivityResult");

        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        switch (requestCode) {
            case PermissionConstants.PERMISSION_CAM_MIC:
                if (resultCode == RESULT_OK) {
                    activeCall.requestForVideoSwap();
                }
                break;
            case CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE:
                Log.v("CallActivity", "CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE " + resultCode);

                if (canDrawOverlays(CallActivity.this)) {
                    startFloatingViewService();
                    finish();
                    if (lastTaskToDo != null)
                        lastTaskToDo.didComplete(true);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (canDrawOverlays(CallActivity.this)) {
                                startFloatingViewService();
                                finish();
                                if (lastTaskToDo != null)
                                    lastTaskToDo.didComplete(true);
                            }
                        }
                    }, 1000);
                }
                break;
        }
    }

    public boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
            return true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {//USING APP OPS MANAGER
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (manager != null) {
                try {
                    int result = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                    return result == AppOpsManager.MODE_ALLOWED;
                } catch (Exception ignore) {
                }
            }
        }

        try {//IF This Fails, we definitely can't do it
            WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (mgr == null) return false; //getSystemService might return null
            View viewToAdd = new View(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            mgr.addView(viewToAdd, params);
            mgr.removeView(viewToAdd);
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public VitalsManager getInstance() {
        if (vitalsManager == null) {
            if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_DOCTOR)) {

            } else {
                checkVitalPermission();
            }
            vitalsManager = VitalsManager.getInstance();
        }

        return vitalsManager;
    }

    private void checkVitalPermission() {
        PermissionChecker permissionChecker = PermissionChecker.with(this);
        if (permissionChecker.isGranted(PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS)) {
            //nothing to do
        } else if (getLifecycle().getCurrentState() != Lifecycle.State.RESUMED || getLifecycle().getCurrentState() != Lifecycle.State.STARTED) {
            permissionChecker.checkPermission(PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS);
        }
    }

    @Override
    public void updateBatteryView(int visibility, int battery) {

    }

    @Override
    public void closeVitalController() {
        hideVitalContainer();
    }

    @Override
    public void didInitiateMeasure(String type) {
        showVitalContainer();
    }

    @Override
    public void didChangedNumberOfScreens(int count) {

        if (count != indicators.length) {
            createIndicator(count);
        }

        Log.d("CallActivity", "didChangedNumberOfScreens " + count);

        if (count == 0) {
            vital_empty_tv.setVisibility(View.VISIBLE);

            if (UserType.isUserPatient()) {
                vital_empty_tv.setText(getString(R.string.vital_empty_pateint));
            } else {
                vital_empty_tv.setText(getString(R.string.vital_empty_non_pateint));
            }

        } else {
            vital_empty_tv.setVisibility(View.GONE);
        }

    }

    @Override
    public void didChangeStreamingState(int state) {
        updateMinimizeButton(state);
    }

}
