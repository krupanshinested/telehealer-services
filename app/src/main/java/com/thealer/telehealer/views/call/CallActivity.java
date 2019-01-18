package com.thealer.telehealer.views.call;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
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
import com.skyfishjy.library.RippleBackground;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.common.Animation.AnimationUtil;
import com.thealer.telehealer.common.Animation.ConstrainSetUtil;
import com.thealer.telehealer.common.Animation.MoveViewTouchListener;
import com.thealer.telehealer.common.Animation.OnSwipeTouchListener;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
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
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoQRCapture;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.CallPlacingFragment;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.QRCodeReaderActivity;
import com.thealer.telehealer.views.common.RoundCornerConstraintLayout;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.vitals.BluetoothEnableActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.GET_DEVICES_ALL;
import static android.media.AudioManager.STREAM_VOICE_CALL;
import static com.thealer.telehealer.common.pubNub.models.APNSPayload.text;

/**
 * Created by rsekar on 12/25/18.
 */

public class CallActivity extends BaseActivity implements TokBoxUIInterface,View.OnClickListener {

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

    @Nullable
    private CommonUserApiResponseModel otherPersonDetail;

    private Boolean isCameraFlipped = false;

    @Nullable
    private MediaSession audioSession;

    @Nullable
    private TimerRunnable uiToggleTimer;

    private String currentCallQuality = OpenTokConstants.none;
    private Boolean isDisclaimerDismissed = false;

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

        initView();

        if (savedInstanceState != null) {
            currentCallQuality = savedInstanceState.getString(ArgumentKeys.CALL_QUALITY);
            isDisclaimerDismissed = savedInstanceState.getBoolean(ArgumentKeys.IS_DISCLAIMER_DISMISSED);
        }

        if (TokBox.shared.getConnectedDate() != null) {
            long lastSuccess = TokBox.shared.getConnectedDate().getTime();
            long elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            recording_tv.setBase(lastSuccess - elapsedRealtimeOffset);
            callDidStarted();
        } else {
            recording_tv.stop();
            profile_background.startRippleAnimation();
        }

        TokBox.shared.setup(remoteView,localView);
        updateCallQuality(currentCallQuality);

        if (TokBox.shared.getTempToken() != null) {
            minimize.setVisibility(View.GONE);
        } else {
            minimize.setVisibility(View.VISIBLE);
        }

        audioSession = new MediaSession(getApplicationContext(), "TAG11");

        MediaSession.Callback MsCallback = new MediaSession.Callback() {

            @Override
            public boolean onMediaButtonEvent(final Intent mediaButtonIntent) {
                String intentAction = mediaButtonIntent.getAction();
                Log.i("onMediaButtonEvent",  "receieved");
                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                    KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {

                        if (TokBox.shared.getCallState() == OpenTokConstants.waitingForUserAction) {
                            onClick(answer_iv);
                        } else {
                            onClick(hang_iv);
                        }

                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent);
            }


        };
        audioSession.setCallback(MsCallback);

        PlaybackState state = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY_PAUSE)
                .setState(PlaybackState.STATE_PLAYING, 0, 0, 0)
                .build();
        audioSession.setPlaybackState(state);
        audioSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        audioSession.setActive(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TokBox.shared.setUIListener(this);
        localView.setOnTouchListener(new MoveViewTouchListener(localView));
        didChangedAudioInput(TokBox.shared.getCurrentAudioState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deinit();
    }

    private void deinit() {
        TokBox.shared.setUIListener(null);
        TokBox.shared.removeRemoteandLocalView();

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }

        if (audioSession != null) {
            audioSession.release();
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
        saveInstance.putBoolean(ArgumentKeys.IS_DISCLAIMER_DISMISSED,isDisclaimerDismissed);
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
        vitalsView.setOnClickListener(this);
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
                isDisclaimerDismissed = true;
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
    }

    private void toggleVisibility() {
        if (top_option.getVisibility() == View.VISIBLE) {

            top_option.setVisibility(View.GONE);
            user_info_lay.setVisibility(View.GONE);
            hang_iv.setVisibility(View.GONE);
            vitalHeaderView.setVisibility(View.GONE);

            if (uiToggleTimer != null) {
                uiToggleTimer.setStopped(true);
                uiToggleTimer = null;
            }

        } else {

            top_option.setVisibility(View.VISIBLE);
            user_info_lay.setVisibility(View.VISIBLE);
            hang_iv.setVisibility(View.VISIBLE);
            vitalHeaderView.setVisibility(View.VISIBLE);

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
            if (UserType.isUserPatient() && !isDisclaimerDismissed) {
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

        ConstrainSetUtil.assignLeftAndRightToMain(constraintSet, mainLay.getId(), hang_iv.getId(), 0);
        constraintSet.connect(hang_iv.getId(), ConstraintSet.BOTTOM, vitalsView.getId(), ConstraintSet.TOP);

        constraintSet.connect(answer_iv.getId(), ConstraintSet.LEFT, mainLay.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(answer_iv.getId(), ConstraintSet.BOTTOM, vitalsView.getId(), ConstraintSet.TOP);

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

        if (TokBox.shared.isVideoCall()) {

            if (TokBox.shared.getConnectedDate() != null) {
                localView.setVisibility(View.VISIBLE);
                remoteView.setVisibility(View.VISIBLE);
            }

            video_switch.setVisibility(View.GONE);
            video_mute.setVisibility(View.VISIBLE);
            flip_camera.setVisibility(View.VISIBLE);

            vitalsView.setVisibility(View.VISIBLE);
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
                        //TODO : add local tray notification)
                        PermissionChecker.with(this).checkPermission(requiredPermission);
                    }
                }
                break;
            case R.id.minimize:
                EventRecorder.recordCallUpdates("callview_minimized",null);
                break;
            case R.id.speaker_type:
                ArrayList<AudioDeviceInfo> deviceInfos =  TokBox.shared.getConnectedAudioDevices();

                ArrayList<String> audioTypes = new ArrayList<>();
                final ArrayList<AudioDeviceInfo> selectedTypes = new ArrayList<>();
                int selectedPosition = 0;
                for (int i = 0;i<deviceInfos.size();i++) {

                    AudioDeviceInfo audioDeviceInfo = deviceInfos.get(i);

                    switch (audioDeviceInfo.getType()) {
                        case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                            audioTypes.add(getString(R.string.speaker));
                            selectedTypes.add(audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.speaker) {
                                selectedPosition = i;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                            audioTypes.add(getString(R.string.earPiece));
                            selectedTypes.add(audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.inEarSpeaker) {
                                selectedPosition = i;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                            audioTypes.add(getString(R.string.headphones));
                            selectedTypes.add(audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.headPhones) {
                                selectedPosition = i;
                            }

                            break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                            audioTypes.add(getString(R.string.bluetooth));
                            selectedTypes.add(audioDeviceInfo);

                            if (TokBox.shared.getCurrentAudioState() == OpenTokConstants.bluetooth) {
                                selectedPosition = i;
                            }

                            break;
                    }
                }

                Set<AudioDeviceInfo> foo = new HashSet<AudioDeviceInfo>(selectedTypes);
                selectedTypes.clear();
                selectedTypes.addAll(foo);

                ItemPickerDialog itemPickerDialog = new ItemPickerDialog(this, getString(R.string.choose_audio_input),audioTypes, new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
                        switch (selectedTypes.get(position).getType()) {
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
                    @Override
                    public void didCancelled() {

                    }
                },selectedPosition);
                itemPickerDialog.setCancelable(false);
                itemPickerDialog.show();


                break;
            case R.id.audio_mute:
                TokBox.shared.toggleAudio();
                if (!TokBox.shared.isPublisherAudioOn()) {
                    EventRecorder.recordCallUpdates("audio_muted", null);
                }
                updateAudioMuteUI();
                break;
            case R.id.video_mute:
                TokBox.shared.toggleVideo();

                if (!TokBox.shared.isPublisherVideoOn()) {
                    EventRecorder.recordCallUpdates("video_muted", null);
                }

                updateVidioMuteUI();
                break;
            case R.id.video_switch:

                showAlertDialog(getString(R.string.video_call_request), getString(R.string.video_call_request_confirmation), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
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
                isCameraFlipped = !isCameraFlipped;

                if (isCameraFlipped) {
                    flip_camera.setImageDrawable(getDrawable(R.drawable.ic_camera_rotate_activate));
                } else {
                    flip_camera.setImageDrawable(getDrawable(R.drawable.switch_camera));
                }

                EventRecorder.recordCallUpdates("toggle_camera", null);
                TokBox.shared.flipVideo();
                break;
            case R.id.vitalsView:
                if (viewPager.getVisibility() == View.GONE) {
                    arrow_iv.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_24dp));
                    viewPager.setVisibility(View.VISIBLE);
                } else {
                    arrow_iv.setImageDrawable(getDrawable(R.drawable.ic_arrow_up));
                    viewPager.setVisibility(View.GONE);
                }
                break;
            case R.id.main_container:
                if (TokBox.shared.getConnectedDate() != null)
                    toggleVisibility();
                break;
        }
    }

    private void callDidStarted() {
        recording_tv.start();

        if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
            profile_iv.setVisibility(View.GONE);
        }

        updateStateView();

        profile_background.stopRippleAnimation();
        Animation blinkAnimation = AnimationUtils.loadAnimation(CallActivity.this, R.anim.blinking);
        recording_dot.startAnimation(blinkAnimation);

        setToggleTimer();
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
        String sessionId = TokBox.shared.getSessionId();
        String to_guid = "";
        String to_name = "";
        if (otherPersonDetail != null) {
            to_guid = otherPersonDetail.getUser_guid();
            to_name = otherPersonDetail.getDisplayName();
        }
        Date startedTime = TokBox.shared.getConnectedDate();

        switch (callRejectionReason) {
            case OpenTokConstants.endCallPressed:
            case OpenTokConstants.other:
            case OpenTokConstants.badNetwork:
            case OpenTokConstants.timedOut:
                Date endedTime = new Date();
                if (startedTime != null && !TextUtils.isEmpty(sessionId) && endedTime.getTime() - startedTime.getTime() > 5) {
                    Intent feedBackIntent = new Intent(CallActivity.this,CallFeedBackActivity.class);
                    feedBackIntent.putExtra(ArgumentKeys.ORDER_ID,sessionId);
                    feedBackIntent.putExtra(ArgumentKeys.TO_USER_GUID,to_guid);
                    feedBackIntent.putExtra(ArgumentKeys.STARTED_DATE,startedTime);
                    feedBackIntent.putExtra(ArgumentKeys.ENDED_DATE,endedTime);

                    startActivity(feedBackIntent);
                }
                break;
            case OpenTokConstants.notPickedUp:
            case OpenTokConstants.busyInAnotherLine:
                if (!UserType.isUserPatient()) {
                    Intent intent = new Intent(CallActivity.this, ContentActivity.class);
                    intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok));
                    intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);
                    intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_missed_call);
                    intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);

                    intent.putExtra(ArgumentKeys.TITLE, "");

                    if (callRejectionReason.equals(OpenTokConstants.notPickedUp)) {
                        intent.putExtra(ArgumentKeys.DESCRIPTION, to_name+" "+getString(R.string.not_available));
                    } else {
                        intent.putExtra(ArgumentKeys.DESCRIPTION, to_name+" "+getString(R.string.busy_another_call));
                    }

                    intent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false);
                    intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);

                    startActivity(intent);
                }
                break;
        }

        deinit();
        finish();
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

            showAlertDialog(getString(R.string.video_call_request), message, getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
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

            showAlertDialog(getString(R.string.request_rejected), message, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
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
    public String getCurrentCallQuality() {
        return currentCallQuality;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode) {
           case PermissionConstants.PERMISSION_CAM_MIC:
                if (resultCode == RESULT_OK) {
                    TokBox.shared.requestForVideoSwap();
                }
                break;
        }
    }
}
