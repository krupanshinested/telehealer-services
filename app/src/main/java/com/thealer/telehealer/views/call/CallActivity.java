package com.thealer.telehealer.views.call;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opentok.android.AudioDeviceManager;
import com.opentok.android.BaseAudioDevice;
import com.skyfishjy.library.RippleBackground;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.Animation.AnimationUtil;
import com.thealer.telehealer.common.Animation.ConstrainSetUtil;
import com.thealer.telehealer.common.Animation.MoveViewTouchListener;
import com.thealer.telehealer.common.Animation.OnSwipeTouchListener;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.CallMinimizeService;
import com.thealer.telehealer.common.OpenTok.CustomAudioDevice;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.TokBoxUIInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoQRCapture;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.call.Adapter.VitalCallAdapter;
import com.thealer.telehealer.views.call.Interfaces.LiveVitalCallBack;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.QRCodeReaderActivity;
import com.thealer.telehealer.views.common.RoundCornerConstraintLayout;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.ViewPagerAdapter;
import com.thealer.telehealer.views.home.vitals.BluetoothEnableActivity;
import com.thealer.telehealer.views.home.vitals.measure.BPMeasureFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import iHealth.iHealthVitalManager;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.GET_DEVICES_ALL;
import static android.media.AudioManager.STREAM_VOICE_CALL;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static com.thealer.telehealer.common.pubNub.models.APNSPayload.text;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * Created by rsekar on 12/25/18.
 */

public class CallActivity extends BaseActivity implements TokBoxUIInterface,
        View.OnClickListener,
        VitalManagerInstance,
        LiveVitalCallBack {

    private LinearLayout remoteView;
    private FrameLayout localView;
    private LinearLayout top_option,recording_view,vitalsView,vitalHeaderView;
    private ImageView minimize,speaker_type,audio_mute,video_mute,video_switch,flip_camera,hang_iv,answer_iv,arrow_iv;
    private TextView name_tv,user_info_tv,status_tv,call_quality_tv;
    private Chronometer recording_tv;
    private RoundCornerConstraintLayout recording_dot,quality_disclaimer,call_quality_lay,patient_disclaimer;
    private CircleImageView profile_iv;
    private ViewPager viewPager;
    private ConstraintLayout mainLay,user_info_lay,incoming_view;
    private RippleBackground profile_background;
    private ImageView audio_disabled_iv,video_disabled_iv;
    private TextView quality_disclaimer_tv,display_name,call_type_tv;
    private ImageView[] indicators = new ImageView[0];
    private LinearLayout pager_indicator_container;
    private ConstraintLayout pager_container;
    private TextView vital_empty_tv;

    private final int CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE = 120;

    @Nullable
    public Dialog currentShowingDialog;

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

    @Nullable
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFullScreenMode();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Telehealer");
            if (wakeLock != null && !wakeLock.isHeld())
                wakeLock.acquire(Constants.callCapTime);
        }

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        setContentView(R.layout.activity_call);
        otherPersonDetail = TokBox.shared.getOtherPersonDetail();

        Object object = getIntent().getSerializableExtra(ArgumentKeys.CALL_INITIATE_MODEL);
        if (object != null) {
            CallInitiateModel callInitiateModel = (CallInitiateModel) object;
            if (!TokBox.shared.isActiveCallPreset()) {
                TokBox.shared.initialSetUp(callInitiateModel,true, UUID.randomUUID().toString());
            }
        }

        if (savedInstanceState != null) {
            currentCallQuality = savedInstanceState.getString(ArgumentKeys.CALL_QUALITY);
        }

        initView();

        if (TokBox.shared.getConnectedDate() != null) {
            long lastSuccess = TokBox.shared.getConnectedDate().getTime();
            long elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            recording_tv.setBase(lastSuccess - elapsedRealtimeOffset);
            callDidStarted();
        } else {
            recording_tv.stop();
            profile_background.startRippleAnimation();
        }

        updateMinimizeButton(Constants.idle);

        TokBox.shared.setup(remoteView,localView);
        updateCallQuality(currentCallQuality);

        final Class<? extends Service> service = CallMinimizeService.class;
        final Intent intent = new Intent(CallActivity.this, service);
        stopService(intent);


        int callAction = getIntent().getIntExtra(ArgumentKeys.CALL_REQUEST_ACTION,0);
        if (callAction != 0) {
            switch (callAction) {
                case OpenTokConstants.receivedRequestForVideoSwap:
                    receivedRequestForVideoSwap();
                    break;
                case OpenTokConstants.receivedResponseForVideoSwap:
                    receivedResponseForVideoSwap(getIntent().getBooleanExtra(ArgumentKeys.CALL_REQUEST_DATA,false));
                    break;
                case OpenTokConstants.didReceiveVitalData:
                    didReceiveVitalData(getIntent().getStringExtra(ArgumentKeys.CALL_REQUEST_DATA),getIntent().getStringExtra(ArgumentKeys.CALL_REQUEST_DATA_1));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        TokBox.shared.setUIListener(this);
        localView.setOnTouchListener(new MoveViewTouchListener(localView));
        didChangedAudioInput(TokBox.shared.getCurrentAudioState());
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
        TokBox.shared.resetUIListener(this);
        TokBox.shared.removeRemoteandLocalView(remoteView,localView);

        if (errorModelObserver != null) {
            TokBox.shared.getOpenTokViewModel().getErrorModelLiveData().removeObserver(errorModelObserver);
        }

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public void onBackPressed() {
        //disable back press
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstance) {
        super.onSaveInstanceState(saveInstance);

        saveInstance.putString(ArgumentKeys.CALL_QUALITY,currentCallQuality);
    }

    private void initView() {
        remoteView = findViewById(R.id.remoteView);
        localView = findViewById(R.id.localView);
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
                animate(false);
            }
            public void onSwipeLeft() {
                animate(true);
            }
            public void onSwipeBottom() {

            }

            void animate(Boolean toLeft) {
                TokBox.shared.setPatientDisclaimerDismissed(true);
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

                for (int i = 0;i<indicators.length;i++){
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

    private void updateMinimizeButton(int state) {
        if (TokBox.shared.getTempToken() != null) {
            minimize.setVisibility(View.GONE);
        } else {

            if (TokBox.shared.getCallType().equals(OpenTokConstants.video) && UserType.isUserPatient() && state == Constants.measuring) {
                minimize.setVisibility(View.GONE);
            } else {
                minimize.setVisibility(View.VISIBLE);
            }

        }
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

            if (TokBox.shared.getCallType().equals(OpenTokConstants.audio)) {
                profile_iv.setVisibility(View.VISIBLE);
            }

            setToggleTimer();
        }
    }

    private void setToggleTimer() {

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
        TokBox.shared.setCallState(currentState);
        updateUI();
    }

    private void updateUI() {
        switch (TokBox.shared.getCallState()) {
            case OpenTokConstants.waitingForUserAction:
                top_option.setVisibility(View.GONE);
                recording_view.setVisibility(View.GONE);
                vitalsView.setVisibility(View.GONE);
                patient_disclaimer.setVisibility(View.GONE);

                remoteView.setVisibility(View.GONE);
                localView.setVisibility(View.GONE);

                hang_iv.setVisibility(View.VISIBLE);
                answer_iv.setVisibility(View.VISIBLE);

                user_info_lay.setVisibility(View.GONE);
                incoming_view.setVisibility(View.VISIBLE);

                if (otherPersonDetail != null) {
                    display_name.setText(otherPersonDetail.getDisplayName());
                }

                if (TokBox.shared.isVideoCall()) {
                    call_type_tv.setText(getString(R.string.telehealer_video));
                } else {
                    call_type_tv.setText(getString(R.string.telehealer_audio));
                }

                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                answer_iv.startAnimation(shake);

                break;
            case OpenTokConstants.outGoingCallGoingOn:
                answer_iv.clearAnimation();

                top_option.setVisibility(View.VISIBLE);
                recording_view.setVisibility(View.VISIBLE);

                if (TokBox.shared.isVideoCall()) {
                    vitalsView.setVisibility(View.VISIBLE);
                } else {
                    vitalsView.setVisibility(View.GONE);
                }

                remoteView.setVisibility(View.VISIBLE);
                localView.setVisibility(View.VISIBLE);

                hang_iv.setVisibility(View.VISIBLE);
                answer_iv.setVisibility(View.GONE);

                user_info_lay.setVisibility(View.VISIBLE);
                incoming_view.setVisibility(View.GONE);

                moveHangButtonToCenter();
            case OpenTokConstants.incomingCallGoingOn:
                animation();
                break;
        }

        if (TokBox.shared.getCallState() != OpenTokConstants.waitingForUserAction) {
            if (UserType.isUserPatient() && !TokBox.shared.getPatientDisclaimerDismissed()) {
                patient_disclaimer.setVisibility(View.VISIBLE);
            } else {
                patient_disclaimer.setVisibility(View.GONE);
            }

            updateUIForAudioVideo();
        }

    }

    private void animation() {
        switch (TokBox.shared.getCallState()) {
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
                remoteView.setVisibility(View.VISIBLE);
                localView.setVisibility(View.VISIBLE);
                incoming_view.setVisibility(View.GONE);

                if (TokBox.shared.isVideoCall()) {
                    vitalsView.setVisibility(View.VISIBLE);
                } else {
                    vitalsView.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void moveHangButtonToCenter() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLay);

        ConstrainSetUtil.clearAllConstraint(constraintSet, answer_iv.getId());
        ConstrainSetUtil.clearAllConstraint(constraintSet, hang_iv.getId());

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,r.getDisplayMetrics());

        ConstrainSetUtil.assignLeftAndRightToMain(constraintSet, mainLay.getId(), hang_iv.getId(), 0);
        constraintSet.connect(hang_iv.getId(), ConstraintSet.BOTTOM, mainLay.getId(), ConstraintSet.BOTTOM,px);

        constraintSet.connect(answer_iv.getId(), ConstraintSet.LEFT, mainLay.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(answer_iv.getId(), ConstraintSet.BOTTOM, mainLay.getId(), ConstraintSet.BOTTOM,px);

        constraintSet.applyTo(mainLay);
    }

    private void showOtherPersonDetails() {
        if (otherPersonDetail != null) {
            name_tv.setText(otherPersonDetail.getDisplayName());
            user_info_tv.setText(otherPersonDetail.getDisplayInfo());
            Utils.setImageWithGlide(getApplicationContext(), profile_iv,otherPersonDetail.getUser_avatar(), getDrawable(R.drawable.profile_placeholder), true);
        }
    }

    private void switchToVideo() {
        TokBox.shared.startPublishVideo();
        TokBox.shared.setCallType(OpenTokConstants.video);
        updateUIForAudioVideo();
        TokBox.shared.setup(remoteView,localView);
    }

    private void updateUIForAudioVideo(){

        updateAudioMuteUI();
        updateVidioMuteUI();

        if (TokBox.shared.getConnectedDate() != null) {
            callDidStarted();
        }

        if (!TokBox.shared.getCallType().equals(OpenTokConstants.audio)) {

            if (TokBox.shared.getConnectedDate() != null) {

                if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
                    localView.setVisibility(View.VISIBLE);
                }

                remoteView.setVisibility(View.VISIBLE);
            }

            video_switch.setVisibility(View.GONE);
            video_mute.setVisibility(View.VISIBLE);
            flip_camera.setVisibility(View.VISIBLE);

            if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
                vitalsView.setVisibility(View.VISIBLE);
                if (viewPager.getAdapter() == null) {
                    if (vitalCallAdapter == null) {

                        ArrayList<VitalDevice> devices = new ArrayList<>();
                        if (UserType.isUserPatient()) {
                            devices = VitalPairedDevices.getAllPairedDevices(appPreference).getDevices();
                        /*devices.add(new VitalDevice("", VitalsConstant.TYPE_PO3,false,""));
                        devices.add(new VitalDevice("",VitalsConstant.TYPE_BP3L,false,""));
                        devices.add(new VitalDevice("",VitalsConstant.TYPE_PO3,false,""));
                        devices.add(new VitalDevice("",VitalsConstant.TYPE_PO3,false,""));
                        devices.add(new VitalDevice("",VitalsConstant.TYPE_TS28B,false,""));*/
                        }

                        vitalCallAdapter = new VitalCallAdapter(getSupportFragmentManager(), devices, this);
                    }

                    viewPager.setAdapter(vitalCallAdapter);
                    viewPager.setCurrentItem(0);
                }
            } else {
                localView.setVisibility(View.GONE);
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

    private void updateAudioMuteUI(){
        if (TokBox.shared.getConnectedDate() != null && !TokBox.shared.isPublisherAudioOn()) {
            audio_mute.setImageDrawable(getDrawable(R.drawable.chat_audio_mute_active));
        } else {
            audio_mute.setImageDrawable(getDrawable(R.drawable.mic_off));
        }
    }
    private void updateVidioMuteUI(){
        if (TokBox.shared.getConnectedDate() != null && !TokBox.shared.isPublisherVideoOn()) {
            video_mute.setImageDrawable(getDrawable(R.drawable.chat_video_mute_active));
        } else {
            video_mute.setImageDrawable(getDrawable(R.drawable.video_mute));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_iv:
                hang_iv.setClickable(false);
                if (!TokBox.shared.getCalling() && TokBox.shared.getConnectedDate() == null) {
                    TokBox.shared.endCall(OpenTokConstants.notPickedUp);
                    EventRecorder.recordCallUpdates("declined_call", null);

                    APNSPayload payload = new APNSPayload();
                    HashMap<String,String> aps = new HashMap<>();
                    if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
                        aps.put(PubNubNotificationPayload.ALERT,getString(R.string.video_missed_call));
                    } else {
                        aps.put(PubNubNotificationPayload.ALERT,getString(R.string.audio_missed_call));
                    }
                    if (TokBox.shared.getOtherPersonDetail() != null) {
                        aps.put(PubNubNotificationPayload.TITLE, TokBox.shared.getOtherPersonDetail().getDisplayName());
                        aps.put(PubNubNotificationPayload.MEDIA_URL, TokBox.shared.getOtherPersonDetail().getUser_avatar());
                    } else {
                        aps.put(PubNubNotificationPayload.TITLE, "Missed");
                    }
                    payload.setAps(aps);

                    Intent intent = new Intent(application, HomeActivity.class);
                    intent.putExtra(ArgumentKeys.SELECTED_MENU_ITEM,R.id.menu_recent);

                    Utils.createNotification(payload,intent);

                } else  {
                    EventRecorder.recordCallUpdates("end_call_pressed",null);
                    TokBox.shared.endCall(OpenTokConstants.endCallPressed);
                }

                break;
            case R.id.answer_iv:
                if (TokBox.shared.getCallState() == OpenTokConstants.waitingForUserAction) {

                    if (TokBox.shared.isVideoCall()) {
                        EventRecorder.recordCallUpdates("opening_video_call", null);
                    } else {
                        EventRecorder.recordCallUpdates("opening_audio_call", null);
                    }
                    
                    int requiredPermission;
                    if (TokBox.shared.isVideoCall()) {
                        requiredPermission = PermissionConstants.PERMISSION_CAM_MIC;
                    } else {
                        requiredPermission = PermissionConstants.PERMISSION_MICROPHONE;
                    }

                    if (PermissionChecker.with(this).isGranted(requiredPermission)) {
                        TokBox.shared.connectToSession();
                        updateState(OpenTokConstants.incomingCallGoingOn);
                    } else {
                        TokBox.shared.endCall(OpenTokConstants.other);

                        String description;
                        if (TokBox.shared.isVideoCall()) {
                            if (!PermissionChecker.with(this).isGranted(PermissionConstants.PERMISSION_CAM_MIC)) {
                                description = getString(R.string.microphone_camera_permission);
                            } else if (!PermissionChecker.with(this).isGranted(PermissionConstants.PERMISSION_MICROPHONE)) {
                                description = getString(R.string.microphone_permission);
                            } else {
                                description = getString(R.string.camera_permission);
                            }
                        }else {
                            description = getString(R.string.microphone_permission);
                        }

                        String path = null;

                        if (otherPersonDetail != null) {
                            path = otherPersonDetail.getUser_avatar();
                        }

                        PushPayLoad pushPayLoad = PubNubNotificationPayload.getCallDismissedPermissionLocalPayload(path,getString(R.string.call_cancelled),description);
                        Intent intent = PermissionChecker.with(this).checkAndReturn(requiredPermission);
                        Utils.createNotification(pushPayLoad.getPn_apns(),intent);
                    }
                }
                break;
            case R.id.minimize:
                EventRecorder.recordCallUpdates("callview_minimized",null);

                if (canDrawOverlays(CallActivity.this)) {
                    startFloatingViewService();
                    finish();
                    return;
                } else {
                    Utils.showAlertDialog(this, getString(R.string.enable_overflow), getString(R.string.enable_overflow_description), getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                }
            break;
            case R.id.speaker_type:
                if (TokBox.shared.getConnectedDate() == null) {
                    return;
                }

                ArrayList<AudioDeviceInfo> deviceInfos =  TokBox.shared.getConnectedAudioDevices();

                Set<String> audioTypes = new HashSet<>();
                HashMap<String,AudioDeviceInfo> audioMap = new HashMap<>();
                int selectedPosition = 0;
                for (int i = 0;i<deviceInfos.size();i++) {

                    AudioDeviceInfo audioDeviceInfo = deviceInfos.get(i);

                    switch (audioDeviceInfo.getType()) {
                        case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                            audioTypes.add(getString(R.string.speaker));
                            audioMap.put(getString(R.string.speaker),audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.speaker) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                            audioTypes.add(getString(R.string.earPiece));
                            audioMap.put(getString(R.string.earPiece),audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.inEarSpeaker) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                            audioTypes.add(getString(R.string.headphones));
                            audioMap.put(getString(R.string.headphones),audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.headPhones) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                            audioTypes.add(getString(R.string.bluetooth));
                            audioMap.put(getString(R.string.bluetooth),audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.bluetooth) {
                                selectedPosition = audioTypes.size() - 1;
                            }

                            break;
                    }
                }

                ArrayList<String> types = new ArrayList<>(audioTypes);

                ItemPickerDialog itemPickerDialog = new ItemPickerDialog(this, getString(R.string.choose_audio_input),types, new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
                        AudioDeviceInfo audioDeviceInfo = audioMap.get(types.get(position));
                        if (audioDeviceInfo != null) {
                            switch (audioDeviceInfo.getType()) {
                                case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                                    TokBox.shared.setAudioInput(OpenTokConstants.speaker);
                                    break;
                                case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                                    TokBox.shared.setAudioInput(OpenTokConstants.inEarSpeaker);
                                    break;
                                case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                                    TokBox.shared.setAudioInput(OpenTokConstants.headPhones);
                                    break;
                                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                                case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                                    TokBox.shared.setAudioInput(OpenTokConstants.bluetooth);
                                    break;
                            }
                        }
                    }
                    @Override
                    public void didCancelled() {

                    }
                },selectedPosition);
                itemPickerDialog.setCancelable(false);
                itemPickerDialog.show();

                this.currentShowingDialog = itemPickerDialog;

                break;
            case R.id.audio_mute:
                if (TokBox.shared.getConnectedDate() == null) {
                    return;
                }

                TokBox.shared.toggleAudio();
                if (!TokBox.shared.isPublisherAudioOn()) {
                    EventRecorder.recordCallUpdates("audio_muted", null);
                }
                updateAudioMuteUI();
                break;
            case R.id.video_mute:
                if (TokBox.shared.getConnectedDate() == null) {
                    return;
                }

                TokBox.shared.toggleVideo();

                if (!TokBox.shared.isPublisherVideoOn()) {
                    EventRecorder.recordCallUpdates("video_muted", null);
                }

                updateVidioMuteUI();
                break;
            case R.id.video_switch:
                if (TokBox.shared.getConnectedDate() == null) {
                    return;
                }

                Log.v("CallActivity","onclick");

                this.currentShowingDialog = Utils.showAlertDialog(this, getString(R.string.video_call_request), getString(R.string.video_call_request_confirmation), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (PermissionChecker.with(CallActivity.this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
                            TokBox.shared.requestForVideoSwap();
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
                if (TokBox.shared.getConnectedDate() == null) {
                    return;
                }

                isCameraFlipped = !isCameraFlipped;

                if (isCameraFlipped) {
                    flip_camera.setImageDrawable(getDrawable(R.drawable.ic_camera_rotate_activate));
                } else {
                    flip_camera.setImageDrawable(getDrawable(R.drawable.switch_camera));
                }

                EventRecorder.recordCallUpdates("toggle_camera", null);
                TokBox.shared.flipVideo();
                break;
            case R.id.vitalHeaderView:
                if (pager_container.getVisibility() == View.GONE) {
                    showVitalContainer();
                } else {
                    hideVitalContainer();
                }
                break;
            case R.id.main_container:
                if (TokBox.shared.getConnectedDate() != null)
                    toggleVisibility();
                break;
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

        if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
            profile_iv.setVisibility(View.GONE);
            showVitalHeader();
        } else if (TokBox.shared.getCallType().equals(OpenTokConstants.oneWay)) {
            profile_iv.setVisibility(View.GONE);
        }

        updateStateView();

        profile_background.stopRippleAnimation();
        Animation blinkAnimation = AnimationUtils.loadAnimation(CallActivity.this, R.anim.blinking);
        recording_dot.startAnimation(blinkAnimation);

        top_option.setAlpha(1.0f);

        setToggleTimer();

        TokBox.shared.setZOrderTopForLocalView();
    }

    private void showVitalHeader() {
        vitalHeaderView.setVisibility(View.VISIBLE);
    }

    private void updateStateView() {
        if (TokBox.shared.getPatientCurrentState() != null) {
            didUpdatedPatientLocation(TokBox.shared.getPatientCurrentState());
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

        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
           if (mngr != null) {
               if  (mngr.getAppTasks().get(0).getTaskInfo().numActivities > 1) {
                   openFeedBackIfNeeded(callRejectionReason);
               } else {
                   openHomeAndFinish(callRejectionReason);
               }
           }
        } catch (Exception e) {
            e.printStackTrace();
            openHomeAndFinish(callRejectionReason);
        }
    }

    private void openHomeAndFinish(String callRejectionReason) {
        Intent intent = new Intent(CallActivity.this,HomeActivity.class);
        startActivity(intent);

        openFeedBackIfNeeded(callRejectionReason);
    }

    private void openFeedBackIfNeeded(String callRejectionReason) {
        openFeedBackIfNeeded(callRejectionReason,CallActivity.this);
        deinit();
        finish();
    }

    public static void openFeedBackIfNeeded(String callRejectionReason,Context context) {
        String sessionId = TokBox.shared.getSessionId();
        String to_guid = "";
        String to_name = "";
        if (TokBox.shared.getOtherPersonDetail() != null) {
            to_guid = TokBox.shared.getOtherPersonDetail().getUser_guid();
            to_name = TokBox.shared.getOtherPersonDetail().getDisplayName();
        }
        Date startedTime = TokBox.shared.getConnectedDate();

        switch (callRejectionReason) {
            case OpenTokConstants.endCallPressed:
            case OpenTokConstants.other:
            case OpenTokConstants.badNetwork:
            case OpenTokConstants.timedOut:
                Date endedTime = new Date();
                if (startedTime != null && !TextUtils.isEmpty(sessionId) && endedTime.getTime() - startedTime.getTime() > 5) {
                    Intent feedBackIntent = new Intent(context,CallFeedBackActivity.class);
                    feedBackIntent.putExtra(ArgumentKeys.ORDER_ID,sessionId);
                    feedBackIntent.putExtra(ArgumentKeys.TO_USER_GUID,to_guid);
                    feedBackIntent.putExtra(ArgumentKeys.STARTED_DATE,startedTime);
                    feedBackIntent.putExtra(ArgumentKeys.ENDED_DATE,endedTime);
                    feedBackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(feedBackIntent);
                }
                break;
            case OpenTokConstants.notPickedUp:
            case OpenTokConstants.busyInAnotherLine:
                if (!UserType.isUserPatient()) {
                    Intent intent = new Intent(context, ContentActivity.class);
                    intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, context.getString(R.string.ok));
                    intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);
                    intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_missed_call);
                    intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ArgumentKeys.TITLE, "");

                    if (callRejectionReason.equals(OpenTokConstants.notPickedUp)) {
                        intent.putExtra(ArgumentKeys.DESCRIPTION, to_name+" "+context.getString(R.string.not_available));
                    } else {
                        intent.putExtra(ArgumentKeys.DESCRIPTION, to_name+" "+context.getString(R.string.busy_another_call));
                    }

                    intent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false);
                    intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);

                    context.startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void updateCallInfo(String message) {
        status_tv.setText(message);
    }

    @Override
    public void didUpdatedPatientLocation(String state) {
        if (!isValidPatientLocation(state)) {
            status_tv.setText(getString(R.string.calling_from_outside));
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
            String message = getString(R.string.requested_video_call,name);

            this.currentShowingDialog =Utils.showAlertDialog(this, getString(R.string.video_call_request), message, getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventRecorder.recordCallUpdates("audio_to_video_accepted", null);
                    TokBox.shared.updateVideoSwapRequest(true);
                    switchToVideo();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventRecorder.recordCallUpdates("audio_to_video_rejected", null);
                    TokBox.shared.updateVideoSwapRequest(false);
                }
            });
        } else {

            EventRecorder.recordCallUpdates("audio_to_video_rejected", null);

            String message = getString(R.string.requested_video_no_camera,name);

            this.currentShowingDialog =Utils.showAlertDialog(this, getString(R.string.request_rejected), message, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, null);

            TokBox.shared.updateVideoSwapRequest(false);
        }
    }

    @Override
    public void receivedResponseForVideoSwap(Boolean isAccepted) {
        if (isAccepted) {
            switchToVideo();
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.video_switch_request_declined),Toast.LENGTH_LONG).show();
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
        showQualityView(qualityString, TokBox.shared.getSubscriberAudioMuted(), isMuted);
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

    private void updateQualityView() {
        if (!TokBox.shared.getSubscriberAudioMuted() && !TokBox.shared.getSubscriberVideoMuted()) {
            quality_disclaimer.setVisibility(View.GONE);
        } else {

            String remoteuserDisplayName = otherPersonDetail != null ? otherPersonDetail.getDisplayName() : getString(R.string.other_person);

            if (TokBox.shared.getSubscriberAudioMuted() && TokBox.shared.getSubscriberVideoMuted()) {

                String message = getString(R.string.mute_audio_video,remoteuserDisplayName);
                showQualityView(message,TokBox.shared.getSubscriberAudioMuted() ,TokBox.shared.getSubscriberVideoMuted());

            } else if (TokBox.shared.getSubscriberAudioMuted()) {

                String message = getString(R.string.mute_audio,remoteuserDisplayName);
                showQualityView(message,TokBox.shared.getSubscriberAudioMuted() ,TokBox.shared.getSubscriberVideoMuted());

            } else if (TokBox.shared.getSubscriberVideoMuted()) {

                String message = getString(R.string.disabled_video,remoteuserDisplayName);
                showQualityView(message,TokBox.shared.getSubscriberAudioMuted() ,TokBox.shared.getSubscriberVideoMuted());

            }
        }
    }

    private void showQualityView(String qualityString,Boolean isAudioMuted,Boolean isVideoMuted) {
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
        Log.d("CallActivity","didReceiveVitalData "+type);

        if (pager_container.getVisibility() != View.VISIBLE)
            showVitalContainer();

        if (vitalCallAdapter != null)
            vitalCallAdapter.didReceivedVitalData(type,data);
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
    public void assignTokBoxApiViewModel(OpenTokViewModel openTokViewModel) {

        if (errorModelObserver != null) {
            openTokViewModel.getErrorModelLiveData().removeObserver(errorModelObserver);
        }

        errorModelObserver = new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {

                if (errorModel != null && TextUtils.isEmpty(errorModel.getMessage())) {
                    String message = errorModel.getMessage();

                    currentShowingDialog = Utils.showAlertDialog(CallActivity.this, getString(R.string.error), message, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TokBox.shared.endCall(OpenTokConstants.other);
                        }
                    }, null);
                }

            }
        };

        openTokViewModel.getErrorModelLiveData().observe(this,errorModelObserver);
    }

    @Override
    public String getCurrentCallQuality() {
        return currentCallQuality;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.v("CallActivity","onActivityResult");

        switch (requestCode) {
           case PermissionConstants.PERMISSION_CAM_MIC:
                if (resultCode == RESULT_OK) {
                    TokBox.shared.requestForVideoSwap();
                }
                break;
            case CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE:
                Log.v("CallActivity","CALL_MINIMIZE_OVERLAY_PERMISSION_REQUEST_CODE "+resultCode);

                if (canDrawOverlays(CallActivity.this)) {
                    startFloatingViewService();
                    finish();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (canDrawOverlays(CallActivity.this)) {
                                startFloatingViewService();
                                finish();
                            }
                        }
                    }, 1000);
                }
                break;
        }
    }

    public boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) return true;
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
        if (VitalsManager.instance == null) {
            if (BuildConfig.FLAVOR.equals(Constants.BUILD_DOCTOR)) {
                VitalsManager.instance = new VitalsManager(getApplication());
            } else{
                VitalsManager.instance = new iHealthVitalManager(getApplication());
            }
        }

        return VitalsManager.instance;
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

        Log.d("CallActivity","didChangedNumberOfScreens "+count);

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
