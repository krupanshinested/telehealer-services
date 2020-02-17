package com.thealer.telehealer.common.OpenTok;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.opentok.android.AudioDeviceManager;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.TokenFetchModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.LocationTracker;
import com.thealer.telehealer.common.LocationTrackerInterface;
import com.thealer.telehealer.common.OpenTok.Renders.BasicCustomVideoRenderer;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.AudioInterface;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.OnDSListener;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.OpenTokTokenFetcher;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.ScreenCapturerInterface;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.TokBoxUIInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailFetcher;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.Call.CallChannel;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubNubResult;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.home.schedules.WaitingRoomActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.media.AudioManager.GET_DEVICES_OUTPUTS;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * Created by rsekar on 12/25/18.
 */

public class TokBox extends SubscriberKit.SubscriberVideoStats implements Session.SessionListener,
        Session.ConnectionListener,
        Session.SignalListener,
        Session.StreamPropertiesListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener,
        SubscriberKit.AudioLevelListener,
        PublisherKit.VideoStatsListener,
        PublisherKit.AudioStatsListener,
        SubscriberKit.VideoStatsListener,
        SubscriberKit.AudioStatsListener,
        SubscriberKit.VideoListener,
        AudioInterface, AudioManager.OnAudioFocusChangeListener,
        OnDSListener {

    public static TokBox shared = new TokBox();

    @Nullable
    private Session mSession;
    @Nullable
    private Publisher mPublisher;
    @Nullable
    private Subscriber mSubscriber;
    @Nullable
    private Connection connection;

    @Nullable
    private String currentUUIDString;

    @Nullable
    private ViewGroup remoteView;
    @Nullable
    private ViewGroup localView;
    private String token = "", sessionId = "", apiKey = "",otherPersonUserGuid = "";
    private String callType = "";
    private Boolean isCalling;
    private Boolean isBadConnection = false;
    private Boolean isPatientDisclaimerDismissed = false;

    @Nullable
    private String doctor_guid, additionalId;

    private int callState = OpenTokConstants.idle;
    private Boolean isSubscriberAudioMuted = false, isSubscriberVideoMuted = false, isSubscriberQualityDegraded = false;

    @Nullable
    private CommonUserApiResponseModel otherPersonDetail;

    @Nullable
    private Date connectingDate, connectedDate;

    @Nullable
    private String patientCurrentState;

    @Nullable
    private String tempToken;

    @Nullable
    private TokBoxUIInterface tokBoxUIInterface;

    @Nullable
    private MediaPlayer incomingPlayer, outgoingPlayer;
    @Nullable
    private Vibrator vibrator;

    @Nullable
    private SubscriberKit.SubscriberVideoStats lastSubscriberVideoStat;
    private double subscriberVideoQualityScore = -1.0;

    @Nullable
    private SubscriberKit.SubscriberAudioStats lastSubscriberAudioStat;
    private double subscriberAudioQualityScore = -1.0;

    private double publisherVideoQualityScore = -1.0;
    private double publisherAudioQualityScore = -1.0;

    @Nullable
    private LocationTracker patientLocationTracker;

    @Nullable
    private TimerRunnable outGoingCallTimer, callCapTimer, screenCapturerTimer;

    private final OpenTokViewModel openTokViewModel = new OpenTokViewModel(application);

    private Boolean isTranscriptSent = false;
    private Boolean isScreenshotCaptured = false;
    private String currentTranscript = "";
    private GoogleSpeechRecognizer googleSpeechRecognizer;
    private TimerRunnable speechRunnable;

    @NonNull
    private CustomAudioDevice customAudioDevice;

    private boolean isBatteryObserverAdded = false;
    private boolean isViewSwapped = false;
    @Nullable
    private Float otherPersonBatteryLevel = null;
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1);
            sendBattery(level);
        }
    };

    @Nullable
    private CallInitiateModel callInitiateModel;

    public void didRecieveIncoming(APNSPayload apnsPayload) {
        EventRecorder.recordCallUpdates("CALL_RECEIVED",null);
        this.sessionId = apnsPayload.getSessionId();

        openTokViewModel.getTokenForSession(apnsPayload.getSessionId(), new OpenTokTokenFetcher() {
            @Override
            public void didFetched(TokenFetchModel tokenFetchModel) {

                if (tokenFetchModel == null) {
                    return;
                }

                CallInitiateModel callInitiateModel = new CallInitiateModel(apnsPayload.getFrom(), null, apnsPayload.getDoctor_guid(), apnsPayload.getFrom_name(), null, apnsPayload.getType());

                callInitiateModel.setSessionId(apnsPayload.getSessionId());
                callInitiateModel.setTokBoxApiKey(tokenFetchModel.getApiKey());
                callInitiateModel.setToken(tokenFetchModel.getToken());

                boolean isInWaitingRoom = WaitingRoomActivity.isActive;
                initialSetUp(callInitiateModel, false, apnsPayload.getUuid());

                GetUsersApiViewModel getUsersApiViewModel = new GetUsersApiViewModel(application);
                getUsersApiViewModel.getUserDetail(apnsPayload.getFrom(), new UserDetailFetcher() {
                    @Override
                    public void didFetchedDetails(CommonUserApiResponseModel commonUserApiResponseModel) {
                        if (commonUserApiResponseModel == null) {
                            return;
                        }

                        otherPersonDetail = commonUserApiResponseModel;

                        if (tokBoxUIInterface != null) {
                            tokBoxUIInterface.didReceivedOtherUserDetails(commonUserApiResponseModel);
                        }

                        Intent intent = new Intent(application, CallActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ArgumentKeys.TRIGGER_ANSWER,isInWaitingRoom);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        application.startActivity(intent);
                                    }
                                }, 100);

                                try {
                                    Log.d("TokBox", "startIncomingTone");
                                    MediaPlayer player = new MediaPlayer();

                                    Uri notification = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + application.getPackageName() + "/raw/music");
                                    player.setDataSource(application, notification);
                                    player.setLooping(true);

                                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .build();

                                    player.setAudioAttributes(audioAttributes);


                                    new AsyncTask<Void, Void, Boolean>() {
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            try {
                                                player.prepare();
                                                return true;
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            return false;
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean prepared) {
                                            try {
                                                if (prepared) {
                                                    player.start();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.execute();

                                    incomingPlayer = player;

                                    vibrator = (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(20000);

                                } catch (Exception e) {
                                    Log.d("TokBox", "startIncomingTone error " + e.getLocalizedMessage());
                                    e.printStackTrace();
                                }


                            }
                        });


                    }
                });
            }
        });
    }

    //Setup methods

    public TokBox() {
        CustomAudioDevice customAudioDevice = new CustomAudioDevice(application, this);
        AudioDeviceManager.setAudioDevice(customAudioDevice);
        this.customAudioDevice = customAudioDevice;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                googleSpeechRecognizer = new GoogleSpeechRecognizer(application);
                googleSpeechRecognizer.setOnDroidSpeechListener(TokBox.this);
            }
        });
    }

    public void initialSetUp(CallInitiateModel callInitiateModel, Boolean isCalling, String uuidString) {

        EventRecorder.recordLastUpdate("last_call_date");
        currentTranscript = "";
        isTranscriptSent = false;
        this.callInitiateModel = callInitiateModel;

        this.token = callInitiateModel.getToken();
        this.sessionId = callInitiateModel.getSessionId();
        this.apiKey = callInitiateModel.getTokBoxApiKey();
        this.callType = callInitiateModel.getCallType();
        this.isCalling = isCalling;
        this.currentUUIDString = uuidString;
        this.additionalId = callInitiateModel.getScheduleId();
        this.isPatientDisclaimerDismissed = false;
        this.otherPersonUserGuid = callInitiateModel.getToUserGuid();
        this.otherPersonBatteryLevel = null;
        this.isViewSwapped = false;
        this.isScreenshotCaptured = false;
        this.doctor_guid = callInitiateModel.getDoctorGuid();

        if (AudioDeviceManager.getAudioDevice() != null && AudioDeviceManager.getAudioDevice() instanceof CustomAudioDevice) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CustomAudioDevice customAudioDevice = (CustomAudioDevice) AudioDeviceManager.getAudioDevice();
                customAudioDevice.setRendererMute(false);
                AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setOnAudioFocusChangeListener(this, new Handler()).build();
                customAudioDevice.getAudioManager().requestAudioFocus(audioFocusRequest);
            }
        }

        if (isCalling & !callType.equals(OpenTokConstants.education)) {
            if (tokBoxUIInterface != null) {
                tokBoxUIInterface.updateCallInfo(application.getString(R.string.trying_to_call));
            }
        }

        if (isCalling) {
            if (tokBoxUIInterface != null) {
                tokBoxUIInterface.updateCallInfo(application.getString(R.string.trying_to_call));
            }

            this.callState = OpenTokConstants.outGoingCallGoingOn;
            connectToSession();

            GetUsersApiViewModel getUsersApiViewModel = new GetUsersApiViewModel(application);
            getUsersApiViewModel.getUserDetail(callInitiateModel.getToUserGuid(), new UserDetailFetcher() {
                @Override
                public void didFetchedDetails(CommonUserApiResponseModel commonUserApiResponseModel) {
                    if (commonUserApiResponseModel == null) {
                        return;
                    }

                    otherPersonDetail = commonUserApiResponseModel;

                    if (tokBoxUIInterface != null) {
                        tokBoxUIInterface.didReceivedOtherUserDetails(commonUserApiResponseModel);
                    }
                }
            });

        } else {
            this.callState = OpenTokConstants.waitingForUserAction;
        }

        this.connectingDate = null;
        this.connectedDate = null;
        this.patientCurrentState = null;
        this.isSubscriberAudioMuted = false;
        this.isSubscriberVideoMuted = false;

        lastSubscriberVideoStat = null;
        subscriberVideoQualityScore = -1.0;
        lastSubscriberAudioStat = null;
        subscriberAudioQualityScore = -1.0;

        publisherVideoQualityScore = -1.0;
        publisherAudioQualityScore = -1.0;

        patientLocationTracker = null;

        LocalBroadcastManager.getInstance(application).sendBroadcast(new Intent(Constants.CALL_STARTED_BROADCAST));
    }

    public void setup(ViewGroup remoteView, ViewGroup localView) {
        if (callType.equals(OpenTokConstants.oneWay) || callType.equals(OpenTokConstants.education)) {
            this.localView = remoteView;
            this.remoteView = localView;
        } else {
            this.localView = localView;
            this.remoteView = remoteView;
        }

        setupFeedViews();
    }

    public void setRemoteView(ViewGroup remoteView) {
        if (callType.equals(OpenTokConstants.oneWay) || callType.equals(OpenTokConstants.education)) {
            this.localView = remoteView;
            setupLocalFeedView();
        } else {
            this.remoteView = remoteView;
            setupRemoteFeedView();
        }
    }

    public void removeRemoteandLocalView(ViewGroup remoteView, ViewGroup localView) {
        removeRemoteView(remoteView);
        removeLocalView(localView);
    }

    public void removeRemoteView(ViewGroup remoteView) {
        if (remoteView == this.remoteView) {
            if (mSubscriber != null && mSubscriber.getView().getParent() != null)
                ((ViewGroup) mSubscriber.getView().getParent()).removeView(mSubscriber.getView());

            this.remoteView = null;
        }

    }

    public void removeLocalView(ViewGroup localView) {
        if (localView == this.localView) {
            if (mPublisher != null && mPublisher.getView().getParent() != null)
                ((ViewGroup) mPublisher.getView().getParent()).removeView(mPublisher.getView());

            this.localView = null;
        }
    }

    public boolean isViewSwapped() {
        return isViewSwapped;
    }

    public void setViewSwapped(boolean viewSwapped) {
        isViewSwapped = viewSwapped;
    }

    public void connectToSession() {
        if (!isCalling)
            stopIncomingRingtone();

        Log.d("TokBox","connectToSession");

        mSession = new Session.Builder(application, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.setConnectionListener(this);
        mSession.setSignalListener(this);
        mSession.setStreamPropertiesListener(this);
        mSession.connect(token);
    }

    public void setUIListener(TokBoxUIInterface tokBoxUIInterface) {
        this.tokBoxUIInterface = tokBoxUIInterface;
        this.tokBoxUIInterface.assignTokBoxApiViewModel(openTokViewModel);
    }

    public void resetUIListener(TokBoxUIInterface tokBoxUIInterface) {
        if (this.tokBoxUIInterface == tokBoxUIInterface) {
            this.tokBoxUIInterface = null;
        }
    }

    @Nullable
    public CallInitiateModel getCallInitiateModel() {
        return callInitiateModel;
    }

    @Nullable
    public TokBoxUIInterface getTokBoxUIInterface() {
        return tokBoxUIInterface;
    }

    public void startPublishVideo() {
        if (this.mPublisher != null)
            this.mPublisher.setPublishVideo(true);
    }

    private void setupFeedViews() {
        setupRemoteFeedView();
        setupLocalFeedView();
    }

    private void setupLocalFeedView() {
        if (mPublisher != null && mPublisher.getView() != null && localView != null) {
            if (mPublisher.getView().getParent() != null) {
                if (mPublisher.getView().getParent() == localView) {
                    return;
                } else {
                    ((ViewGroup) mPublisher.getView().getParent()).removeView(mPublisher.getView());
                }
            }
            localView.addView(mPublisher.getView());

            if (connectedDate != null) {
                mPublisher.getView().setVisibility(View.VISIBLE);
            } else {
                mPublisher.getView().setVisibility(View.GONE);
            }

            setZOrderTopForLocalView();

        } else {
            Log.d("TokBox", "localView not added");
        }
    }

    public void setZOrderTopForLocalView() {
        if (callType.equals(OpenTokConstants.oneWay) || callType.equals(OpenTokConstants.education)) {
            return;
        }

        if (mSubscriber != null && mSubscriber.getView() instanceof GLSurfaceView) {
            if (isViewSwapped) {
                ((GLSurfaceView) mSubscriber.getView()).setZOrderOnTop(true);
            } else {
                ((GLSurfaceView) mSubscriber.getView()).setZOrderOnTop(false);
            }
        }

        if (mPublisher != null && mPublisher.getView() instanceof GLSurfaceView) {
            if (isViewSwapped) {
                ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(false);
            } else {
                ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
            }
        }

    }

    public boolean isOtherPersonBatteryLow() {
        if (otherPersonBatteryLevel != null) {
            return otherPersonBatteryLevel <= 0.1;
        } else {
            return false;
        }
    }

    public void setAudioInput(int type) {
        if (AudioDeviceManager.getAudioDevice() instanceof CustomAudioDevice) {
            CustomAudioDevice customAudioDevice = (CustomAudioDevice) AudioDeviceManager.getAudioDevice();

            switch (type) {
                case OpenTokConstants.bluetooth:
                    customAudioDevice.connectBluetooth();
                    break;
                case OpenTokConstants.headPhones:
                    customAudioDevice.connectHeadPhone();
                    break;
                case OpenTokConstants.speaker:
                    customAudioDevice.connectSpeaker();
                    break;
                case OpenTokConstants.inEarSpeaker:
                    customAudioDevice.connectEarpiece();
                    break;
            }
        }
    }

    public int getCurrentAudioState() {
        if (AudioDeviceManager.getAudioDevice() instanceof CustomAudioDevice) {
            CustomAudioDevice customAudioDevice = (CustomAudioDevice) AudioDeviceManager.getAudioDevice();
            return customAudioDevice.getCurrentConnectedDeviceType();
        } else {
            return OpenTokConstants.speaker;
        }
    }

    public ArrayList<AudioDeviceInfo> getConnectedAudioDevices() {
        if (AudioDeviceManager.getAudioDevice() instanceof CustomAudioDevice) {
            AudioDeviceInfo[] deviceInfos = ((CustomAudioDevice) AudioDeviceManager.getAudioDevice()).getAudioManager().getDevices(GET_DEVICES_OUTPUTS);

            ArrayList<AudioDeviceInfo> arrayList = new ArrayList<AudioDeviceInfo>(Arrays.asList(deviceInfos));

            Boolean isExternalInputAvailable = false;
            int inEarPieceIndex = -1;
            for (int i = 0; i < deviceInfos.length; i++) {
                AudioDeviceInfo deviceInfo = deviceInfos[i];
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) {
                    inEarPieceIndex = i;
                } else if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                        deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    isExternalInputAvailable = true;
                }

            }

            if (isExternalInputAvailable && inEarPieceIndex != -1) {
                arrayList.remove(inEarPieceIndex);
            }

            return arrayList;
        } else {
            return new ArrayList<>();
        }
    }

    private void setupRemoteFeedView() {
        if (mSubscriber != null && mSubscriber.getView() != null && remoteView != null) {

            if (mSubscriber.getView().getParent() != null)
                ((ViewGroup) mSubscriber.getView().getParent()).removeView(mSubscriber.getView());

            remoteView.addView(mSubscriber.getView());
            mSubscriber.getView().setClipToOutline(true);

            setZOrderTopForLocalView();
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    @Nullable
    public Date getConnectingDate() {
        return connectingDate;
    }

    @Nullable
    public Date getConnectedDate() {
        return connectedDate;
    }

    @Nullable
    public String getCurrentUUID() {
        return currentUUIDString;
    }

    public Boolean isActiveCallPreset() {
        return callState != OpenTokConstants.idle && !TextUtils.isEmpty(sessionId);
    }

    public String getCallType() {
        return callType;
    }

    @Nullable
    public String getTempToken() {
        return tempToken;
    }

    public void setTempToken(@Nullable String tempToken) {
        this.tempToken = tempToken;
    }

    @Nullable
    public CommonUserApiResponseModel getOtherPersonDetail() {
        return otherPersonDetail;
    }

    public Boolean getCalling() {
        return isCalling;
    }

    public void setCallState(int callState) {
        this.callState = callState;
    }

    public int getCallState() {
        return callState;
    }

    public Boolean isActivityPresent() {
        return (tokBoxUIInterface != null);
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public Boolean isVideoCall() {
        return callType.equals(OpenTokConstants.video);
    }

    @Nullable
    public String getPatientCurrentState() {
        return patientCurrentState;
    }

    public Boolean getSubscriberAudioMuted() {
        return isSubscriberAudioMuted;
    }

    public Boolean getSubscriberVideoMuted() {
        return isSubscriberVideoMuted;
    }

    public OpenTokViewModel getOpenTokViewModel() {
        return openTokViewModel;
    }

    public Boolean getPatientDisclaimerDismissed() {
        return isPatientDisclaimerDismissed;
    }

    public void setPatientDisclaimerDismissed(Boolean patientDisclaimerDismissed) {
        isPatientDisclaimerDismissed = patientDisclaimerDismissed;
    }

    //Open tok Actions

    private void doPublish() {
        mPublisher = new Publisher.Builder(application)
                .renderer(new BasicCustomVideoRenderer(application))
                .build();
        mPublisher.setPublisherListener(this);
        mPublisher.setVideoStatsListener(this);
        mPublisher.setAudioStatsListener(this);

        switch (callType) {
            case OpenTokConstants.audio:
                mPublisher.setPublishVideo(false);
                mPublisher.setPublishAudio(true);
                break;
            case OpenTokConstants.video:
                mPublisher.setPublishVideo(true);
                mPublisher.setPublishAudio(true);
                break;
            case OpenTokConstants.oneWay:
            case OpenTokConstants.education:
                mPublisher.setPublishVideo(true);
                mPublisher.setPublishAudio(true);
                break;
        }

        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);

        setupLocalFeedView();

        if (mSession != null)
            mSession.publish(mPublisher);

        if (tokBoxUIInterface != null)
            tokBoxUIInterface.didPublishStarted();
    }

    private void startOutgoingTone() {
        Log.d("TokBox", "startOutgoingTone");
        try {
            MediaPlayer player = new MediaPlayer();

            Uri notification = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/music");
            player.setDataSource(application, notification);
            player.setLooping(true);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            player.setAudioAttributes(audioAttributes);


            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        player.prepare();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean prepared) {
                    if (prepared) {
                        player.start();
                    }
                }
            }.execute();

            outgoingPlayer = player;
        } catch (Exception e) {
            Log.d("TokBox", "error in outgoing player " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void doSubscribe(Stream stream) {
        if (mSession != null) {
            mSubscriber = new Subscriber.Builder(application, stream)
                    .renderer(new BasicCustomVideoRenderer(application))
                    .build();
            mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            mSubscriber.setSubscriberListener(this);
            mSubscriber.setAudioLevelListener(this);
            mSubscriber.setAudioStatsListener(this);
            mSubscriber.setVideoListener(this);
            mSubscriber.setVideoStatsListener(this);
            mSession.subscribe(mSubscriber);

            setupRemoteFeedView();
        }
    }

    private void doUnsubscribe() {
        if (mSubscriber != null && mSession != null) {
            mSession.unsubscribe(mSubscriber);
            mSubscriber = null;
        }
    }

    public void endCall(String callRejectionReason) {
        if (!isActiveCallPreset()) {
            Log.d("openTok", "*********Call end called already, returing back");
            return;
        }

        Log.d("openTok", "endCall");
        callState = OpenTokConstants.idle;

        stopIncomingRingtone();
        stopOutgoingRingtone();

        localView = null;
        remoteView = null;

        if (tokBoxUIInterface != null) {
            tokBoxUIInterface.didEndCall(callRejectionReason);
        } else {
            CallActivity.openFeedBackIfNeeded(callRejectionReason, application);
        }


        if (TextUtils.isEmpty(sessionId)) {
            return;
        }

        doUnsubscribe();

        if (mPublisher != null && mSession != null) {
            mSession.unpublish(mPublisher);
            mSession.disconnect();
        }

        VitalsManager.getInstance().disconnectAll();
        VitalsManager.getInstance().resetAll();

        sendEndCallNotification(callRejectionReason);

        sendTranscript();

        this.otherPersonBatteryLevel = null;
        if (isBatteryObserverAdded) {
            isBatteryObserverAdded = false;
            application.unregisterReceiver(batteryReceiver);
        }

        if (callType.equals(OpenTokConstants.education)) {
            stopArchive(sessionId);
        } else {
            if (!TextUtils.isEmpty(sessionId)) {
                if (isCalling) {
                    if (connectedDate == null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("no_answer", "true");
                        updateCallStatus(sessionId, map);
                        EventRecorder.recordCallUpdates("no_answer", null);
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("end", "true");
                        updateCallStatus(sessionId, map);
                    }
                }
            }
        }

        if (tempToken != null) {
            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, tempToken);
        }

        final Class<? extends Service> service = CallMinimizeService.class;
        final Intent intent = new Intent(application, service);
        application.stopService(intent);

        sessionId = "";
        currentUUIDString = null;
        otherPersonDetail = null;
        mSession = null;
        connection = null;
        connectedDate = null;
        connectingDate = null;
        tempToken = null;
        doctor_guid = null;
        additionalId = null;

        isSubscriberVideoMuted = false;
        isSubscriberAudioMuted = false;
        isSubscriberQualityDegraded = false;

        lastSubscriberVideoStat = null;
        subscriberVideoQualityScore = -1.0;
        lastSubscriberAudioStat = null;
        subscriberAudioQualityScore = -1.0;
        publisherVideoQualityScore = -1.0;
        publisherAudioQualityScore = -1.0;

        patientLocationTracker = null;

        if (outGoingCallTimer != null)
            outGoingCallTimer.setStopped(true);
        outGoingCallTimer = null;

        if (callCapTimer != null)
            callCapTimer.setStopped(true);

        callCapTimer = null;

        currentTranscript = "";
        LocalBroadcastManager.getInstance(application).sendBroadcast(new Intent(Constants.CALL_ENDED_BROADCAST));
    }

    @Nullable
    public String getDoctor_guid() {
        return doctor_guid;
    }

    private void sendTranscript() {

        if (speechRunnable != null)
            speechRunnable.setStopped(true);

        if (isTranscriptSent) {
            return;
        }

        Log.d("TokBox", "Google search sendTranscript : " + currentTranscript);
        googleSpeechRecognizer.closeDroidSpeechOperations();
        openTokViewModel.postTranscript(sessionId, currentTranscript, isCalling);
        isTranscriptSent = true;
    }

    private void stopIncomingRingtone() {
        if (incomingPlayer != null) {
            incomingPlayer.stop();
            incomingPlayer.release();
            incomingPlayer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    private void stopOutgoingRingtone() {
        if (outgoingPlayer != null) {
            outgoingPlayer.stop();
            outgoingPlayer.release();
            outgoingPlayer = null;
        }
    }

    public void sendEndCallNotification(String callRejectionReason) {
        if (callType.equals(OpenTokConstants.oneWay)  || callType.equals(OpenTokConstants.education)) {
            return;
        }

        if (otherPersonDetail != null) {
            Log.d("openTok", "endCall");
            CallChannel.shared.postEndCallToOtherPerson(otherPersonDetail.getUser_guid(),currentUUIDString,UserDetailPreferenceManager.getUserDisplayName(),UserDetailPreferenceManager.getUser_avatar(),callRejectionReason);
            EventRecorder.recordNotification("DECLINE_CALL");
        }
    }


    private void sendMessage(String type, String message) {
        Log.d("TokBox", "Message : " + message + " for type : " + type);
        if (mSession != null && connection != null) {
            mSession.sendSignal(type, message, connection);
        }
    }

    public void sendMessage(String type, HashMap<String, Object> message) {
        try {
            String value = Utils.serialize(message);
            sendMessage(type, value);
        } catch (Exception e) {
            Log.d("TokBox","sendMessage : "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    //Action Methods

    public void toggleVideo() {
        if (mPublisher != null)
            mPublisher.setPublishVideo(!mPublisher.getPublishVideo());
    }

    public void toggleAudio() {
        if (mPublisher != null) {
            Boolean isMuted = mPublisher.getPublishAudio();
            mPublisher.setPublishAudio(!isMuted);
            sendAudioMuteStatus(isMuted);
        }
    }

    public void flipVideo() {
        if (mPublisher != null) {
            mPublisher.cycleCamera();
        }
    }

    public Boolean isPublisherVideoOn() {
        return mPublisher != null && mPublisher.getPublishVideo();
    }

    public Boolean isPublisherAudioOn() {
        return mPublisher != null && mPublisher.getPublishAudio();
    }

    //Other methods

    private void assignCallCapTimer() {
        Handler handler = new Handler();
        TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
            @Override
            public void run() {
                if (isActiveCallPreset()) {
                    endCall(OpenTokConstants.timedOut);
                }

                if (callCapTimer != null)
                    callCapTimer.setStopped(true);

                callCapTimer = null;
            }
        });

        callCapTimer = runnable;
        handler.postDelayed(runnable, Constants.callCapTime);
    }

    private void assignScreenCapturerTime() {
        Handler handler = new Handler();
        TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
            @Override
            public void run() {
                captureScreenshot();

                if (screenCapturerTimer != null)
                    screenCapturerTimer.setStopped(true);

                screenCapturerTimer = null;
            }
        });

        screenCapturerTimer = runnable;
        handler.postDelayed(runnable, 30000);
    }

    private void captureScreenshot() {
        Log.d("Opentok","captureScreenshot");
        if (isScreenshotCaptured) {
           return;
        }

        if (callType.equals(OpenTokConstants.audio)) {
            return;
        }

        if (mPublisher == null) {
            return;
        }

        View publisherView = mPublisher.getView();
        if (publisherView == null) {
            return;
        }

        Log.d("Opentok","captureScreenshot");

        if (mSubscriber == null) {
            ((BasicCustomVideoRenderer) mPublisher.getRenderer()).saveScreenshot(true, new ScreenCapturerInterface() {
                @Override
                public void didCapturerScreenShot(Bitmap bitmap) {
                    Log.d("Opentok", "didCapturerScreenShot s");
                    mergeBitmap(bitmap, null);
                }
            });
        } else {
            ((BasicCustomVideoRenderer) mSubscriber.getRenderer()).saveScreenshot(true, new ScreenCapturerInterface() {
                @Override
                public void didCapturerScreenShot(Bitmap subscriber) {
                    Log.d("Opentok", "didCapturerScreenShot d");
                    ((BasicCustomVideoRenderer) mPublisher.getRenderer()).saveScreenshot(true, new ScreenCapturerInterface() {
                        @Override
                        public void didCapturerScreenShot(Bitmap bitmap) {
                            Log.d("Opentok", "didCapturerScreenShot s");
                            mergeBitmap(bitmap, subscriber);
                        }
                    });
                }
            });
        }


        isScreenshotCaptured = true;
    }

    private void mergeBitmap(Bitmap publisher,@Nullable Bitmap subscriber) {
        if (subscriber != null) {
            Bitmap combinedBitmap = Utils.mergeBitmap(publisher,subscriber);
            uploadScreenshot(sessionId,combinedBitmap);
        } else {
            uploadScreenshot(sessionId,publisher);
        }
    }

    //Api methods

    private void sendNotification(String sessionId, String toGuid) {
        if (callType.equals(OpenTokConstants.oneWay) || callType.equals(OpenTokConstants.education)) {
            return;
        }

        PushPayLoad pushPayLoad = PubNubNotificationPayload.getCallInvitePayload(
                UserDetailPreferenceManager.getUserDisplayName()
                , UserDetailPreferenceManager.getUser_guid(), toGuid,
                currentUUIDString, callType, sessionId, doctor_guid);
        PubnubUtil.shared.publishVoipMessage(pushPayLoad, new PubNubResult() {
            @Override
            public void didSend(Boolean isSuccess) {

                if (tokBoxUIInterface != null) {
                    Log.d("TokBox", "ringing");
                    tokBoxUIInterface.updateCallInfo(application.getString(R.string.ringing));
                }

                startOutgoingTone();
            }
        });

    }

    private void updateCallStatus(String sessionId, HashMap<String, String> params) {
        openTokViewModel.updateCallStatus(sessionId, params);
    }

    private void uploadScreenshot(String sessionId, Bitmap bitmap) {
        if (callType.equals(OpenTokConstants.education)) {
           new EducationalVideoViewModel(application).uploadScreenshot(additionalId,bitmap);
        } else {
            openTokViewModel.updateScreenshot(sessionId, bitmap);
        }
    }

    private void startArchive(String sessionId) {
        if (isCalling) {
            openTokViewModel.startArchieve(sessionId);
        }
    }

    private void stopArchive(String sessionId) {
        Log.d("openTok", "stopArchive");
        openTokViewModel.stopArchieve(sessionId);
    }

    private void updatePatientLocation() {
        if (!UserType.isUserPatient()) {
            return;
        }

        if (PermissionChecker.with(application).isGranted(PermissionConstants.PERMISSION_LOCATION)) {
            patientLocationTracker = new LocationTracker(application, new LocationTrackerInterface() {
                @Override
                public void onLocationUpdated(@Nullable String city, @Nullable String state) {
                    if (state != null) {
                        sendLocation(state);
                    } else {
                        sendLocation(Constants.inValidState);
                    }

                    patientLocationTracker = null;
                }
            });

        } else {
            sendLocation("NA");
        }
    }

    private void sendLocation(String state) {
        HashMap<String, String> message = new HashMap<>();
        message.put("sessionId", sessionId);
        message.put("state", state);
        try {
            String value = Utils.serialize(message);
            sendMessage(OpenTokConstants.patient_location, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Signal Message related methods
    private void validateUserLocation(String message) {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        try {
            HashMap<String, String> map = Utils.deserialize(message, type);
            String patientLocation = map.get("state");

            if (tokBoxUIInterface != null) {
                tokBoxUIInterface.didUpdatedPatientLocation(patientLocation);
            }

            patientCurrentState = patientLocation;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Trigger when user requested for video call,Send a request message to other end user for confirming of switching*/
    public void requestForVideoSwap() {
        HashMap<String, String> message = new HashMap<>();
        message.put("sessionId", sessionId);
        try {
            String value = Utils.serialize(message);
            sendMessage(OpenTokConstants.requestForVideoSwap, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveRequestForVideoSwap() {
        EventRecorder.recordCallUpdates("audio_to_video_request", null);

        if (tokBoxUIInterface != null)
            tokBoxUIInterface.receivedRequestForVideoSwap();
    }

    private Boolean isVideoSwapAccepted(String message) {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        try {
            HashMap<String, String> map = Utils.deserialize(message, type);
            String isAccepted = map.get(OpenTokConstants.isVideoSwapAccepted);
            return isAccepted != null && isAccepted.equals(OpenTokConstants.videoSwapAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updateVideoSwapRequest(Boolean isAccepted) {
        HashMap<String, String> message = new HashMap<>();

        if (isAccepted) {
            message.put(OpenTokConstants.isVideoSwapAccepted, OpenTokConstants.videoSwapAccepted);
        } else {
            message.put(OpenTokConstants.isVideoSwapAccepted, OpenTokConstants.videoSwapRejected);
        }

        try {
            String value = Utils.serialize(message);
            sendMessage(OpenTokConstants.responseForVideoSwap, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*APi call to update call status*/
    private void updateAudioToVideoCall() {
        EventRecorder.recordCallUpdates("audio_to_video_call", null);

        HashMap<String, String> params = new HashMap<>();
        params.put("type", OpenTokConstants.video);
        openTokViewModel.updateCallStatus(sessionId, params);
    }

    private void sendSubscriberStat(Double audioScore, Double videoScore) {
        if (tokBoxUIInterface == null) {
            return;
        }

        if (tokBoxUIInterface.getCurrentCallQuality().equals(OpenTokConstants.poorConnection)) {
            return;
        }

        double quality = isVideoCall() ? videoScore : audioScore;

        String callQuality;
        if (quality > 3.5) {
            callQuality = OpenTokConstants.hd;
        } else if (quality > 2.0) {
            callQuality = OpenTokConstants.sd;
        } else {
            callQuality = OpenTokConstants.none;
        }

        if (tokBoxUIInterface != null) {
            tokBoxUIInterface.updateCallQuality(callQuality);
        }
    }

    private void didVideoDisabledOrEnabledForSubscriberDueToQuality(Boolean enable) {
        updateCallQuality(enable);
    }

    private void didVideoDisabledOrEnabledForSubscriber(Boolean enable) {

    }

    private void didReceiveVideoSwitchMessage(Boolean isEnabled) {
        updateCallQuality(isEnabled);
    }

    private void updateCallQuality(Boolean isEnabled) {
        if (isEnabled) {
            if (tokBoxUIInterface != null)
                tokBoxUIInterface.didSubscribeVideoEnabled();
        } else if (tokBoxUIInterface != null) {
            if (isBadConnection) {
                tokBoxUIInterface.updateCallQuality(OpenTokConstants.poorConnection);
                tokBoxUIInterface.updateVideoQuality(application.getString(R.string.fallback_message), !isEnabled);
            } else {
                tokBoxUIInterface.updateCallQuality(OpenTokConstants.none);
                String otherPersonName = "Other";
                if (otherPersonDetail != null) {
                    otherPersonName = otherPersonDetail.getDisplayName();
                }
                tokBoxUIInterface.updateVideoQuality(String.format(application.getString(R.string.fallback_message_with_user), otherPersonName), !isEnabled);
            }
        }

    }

    private void statUpdate() {
        if (isVideoCall()) {

            if (subscriberVideoQualityScore != -1.0 && publisherVideoQualityScore != -1.0) {
                isBadConnection = subscriberVideoQualityScore > publisherVideoQualityScore;

                if (isSubscriberQualityDegraded) {
                    didVideoDisabledOrEnabledForSubscriberDueToQuality(!isSubscriberVideoMuted);
                }
            }

        } else {
            if (subscriberAudioQualityScore != -1.0 && publisherAudioQualityScore != -1.0) {
                isBadConnection = subscriberAudioQualityScore > publisherAudioQualityScore;
            }
        }

        sendSubscriberStat(subscriberAudioQualityScore, subscriberVideoQualityScore);
    }

    public void sendAudioMuteStatus(Boolean isMuted) {
        HashMap<String, Boolean> message = new HashMap<>();
        message.put("isAudioMuted", isMuted);
        try {
            String value = Utils.serialize(message);
            sendMessage(OpenTokConstants.audioMuteStatus, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void audioMuteStatusReceived(String message) {
        Type type = new TypeToken<HashMap<String, Boolean>>() {
        }.getType();

        try {
            HashMap<String, Boolean> map = Utils.deserialize(message, type);
            Boolean isMuted = map.get("isAudioMuted");

            if (isMuted != null && isMuted) {
                if (!isSubscriberAudioMuted) {
                    isSubscriberAudioMuted = true;

                    if (tokBoxUIInterface != null)
                        tokBoxUIInterface.didSubscribeAudioDisabled();
                }
            } else {
                if (isSubscriberAudioMuted) {
                    isSubscriberAudioMuted = false;
                    if (tokBoxUIInterface != null)
                        tokBoxUIInterface.didSubscribeAudioEnabled();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBattery(int battery) {
        Log.d("TokBox","battery "+battery);
        Float bvalue = Float.parseFloat(battery+"");
        bvalue = bvalue / 100;
        HashMap<String, Float> message = new HashMap<>();
        message.put(OpenTokConstants.battery, bvalue);
        try {
            String value = Utils.serialize(message);
            sendMessage(OpenTokConstants.subscriberBatteryLevel, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void didReceiveBatteryStatus(String message) {
        Type type = new TypeToken<HashMap<String, Float>>() {
        }.getType();

        try {
            HashMap<String, Float> map = Utils.deserialize(message, type);
            Float battery = map.get(OpenTokConstants.battery);
            this.otherPersonBatteryLevel = battery;
            Log.d("TokBox","other battery "+battery);
            if (tokBoxUIInterface != null)
                this.tokBoxUIInterface.didChangedBattery();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Listener Methods

    //SessionListener methods
    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.d("TokBox", "onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.d("TokBox", "onStreamDestroyed");

        if (tokBoxUIInterface != null)
            tokBoxUIInterface.updateCallInfo(application.getString(R.string.ended));

        if (mSession != null)
            mSession.disconnect();

        if (mSubscriber != null && mSubscriber.getStream().getStreamId().equals(stream.getStreamId())) {
            doUnsubscribe();
        }

        endCall(OpenTokConstants.other);
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.d("TokBox", "onError " + opentokError.getMessage());

        HashMap<String, String> detail = new HashMap<>();
        detail.put("status", "fail");
        detail.put("reason", opentokError.getErrorCode().getErrorCode() + "-" + opentokError.getMessage());

        switch (opentokError.getErrorDomain()) {
            case SessionErrorDomain:
                detail.put("event", "SessionErrorDomain");
                break;
            case SubscriberErrorDomain:
                detail.put("event", "SubscriberErrorDomain");
                break;
            case PublisherErrorDomain:
                detail.put("event", "PublisherErrorDomain");
                break;
        }

        TeleLogger.shared.log(TeleLogExternalAPI.opentok, detail);
    }

    @Override
    public void onConnected(Session session) {
        Log.d("TokBox", "onConnected session");

        HashMap<String, String> detail = new HashMap<>();
        detail.put("status", "success");
        detail.put("event", "onConnected");
        TeleLogger.shared.log(TeleLogExternalAPI.opentok, detail);

        if (this.isCalling && !TokBox.shared.callType.equals(OpenTokConstants.education)) {
            final int interval = 30000; // 30 Second
            Handler handler = new Handler();
            TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                @Override
                public void run() {
                    if (connectingDate == null && connectedDate == null) {
                        endCall(OpenTokConstants.notPickedUp);
                    }

                    if (outGoingCallTimer != null)
                        outGoingCallTimer.setStopped(true);
                    outGoingCallTimer = null;

                }
            });

            outGoingCallTimer = runnable;
            handler.postDelayed(runnable, interval);

            //startOutgoingTone();

            sendNotification(sessionId,otherPersonUserGuid);
        }

        doPublish();

        if (callType.equals(OpenTokConstants.oneWay) || callType.equals(OpenTokConstants.education)) {
            callStarted();
        }
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d("TokBox", "onDisconnected");

        HashMap<String, String> detail = new HashMap<>();
        detail.put("status", "success");
        detail.put("event", "disconnect");

        TeleLogger.shared.log(TeleLogExternalAPI.opentok, detail);
    }

    //PublisherListener methods
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.d("TokBox", "onStreamReceived");

        if (mSubscriber == null) {
            doSubscribe(stream);
        } else {
            Log.d("TokBox", "already subsciber existed");
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d("TokBox", "onStreamDropped");
        endCall(OpenTokConstants.other);

        if (mSubscriber != null && remoteView != null) {
            mSubscriber = null;
            remoteView.removeAllViews();
        }

        EventRecorder.recordCallUpdates("disconnected", null);
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.d("TokBox", "onError" + opentokError.getMessage());

        HashMap<String, String> detail = new HashMap<>();
        detail.put("status", "fail");
        detail.put("reason", opentokError.getErrorCode().getErrorCode() + "-" + opentokError.getMessage());

        switch (opentokError.getErrorDomain()) {
            case SessionErrorDomain:
                detail.put("event", "SessionErrorDomain");
                break;
            case SubscriberErrorDomain:
                detail.put("event", "SubscriberErrorDomain");
                break;
            case PublisherErrorDomain:
                detail.put("event", "PublisherErrorDomain");
                break;
        }

        TeleLogger.shared.log(TeleLogExternalAPI.opentok, detail);
    }


    private void callStarted() {
        if (!callType.equals(OpenTokConstants.oneWay) && !(callType.equals(OpenTokConstants.education)) && !isBatteryObserverAdded){
            isBatteryObserverAdded = true;
            Intent batteryStatus = application.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED) );
            if (batteryStatus != null)
                sendBattery(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        }

        if (mPublisher != null)
            mPublisher.getView().setVisibility(View.VISIBLE);

        if (!callType.equals(OpenTokConstants.education)) {
            if (isCalling) {
                assignCallCapTimer();
                HashMap<String, String> params = new HashMap<>();
                params.put("start", "true");
                updateCallStatus(sessionId, params);
            }

            startArchiving();
        }


    }

    public void startArchiving() {
        connectedDate = new Date();

        if (isCalling) {
            assignScreenCapturerTime();
        }

        if (tokBoxUIInterface != null) {
            tokBoxUIInterface.updateCallInfo(application.getString(R.string.connected));
            tokBoxUIInterface.startedCall();
        }

        startArchive(sessionId);
    }

    //SubscriberListener methods
    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        Log.d("TokBox", "onConnected subscriber");
        callStarted();

        setupRemoteFeedView();
        updatePatientLocation();

        googleSpeechRecognizer.startDroidSpeechRecognition();

        speechRunnable = new TimerRunnable(new TimerInterface() {
            @Override
            public void run() {
                Log.d("TokBox", "Google search sendTranscript trigger from timer");
                sendTranscript();
            }
        });
        new Handler().postDelayed(speechRunnable, 60000);
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.d("TokBox", "onDisconnected subscriber");

        HashMap<String, String> detail = new HashMap<>();
        detail.put("status", "success");
        detail.put("event", "disconnect-subscriber");

        TeleLogger.shared.log(TeleLogExternalAPI.opentok, detail);
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.d("TokBox", "onError subscriber " + opentokError.getMessage());

        HashMap<String, String> detail = new HashMap<>();
        detail.put("status", "fail");
        detail.put("reason", opentokError.getErrorCode().getErrorCode() + "-" + opentokError.getMessage());

        switch (opentokError.getErrorDomain()) {
            case SessionErrorDomain:
                detail.put("event", "SessionErrorDomain");
                break;
            case SubscriberErrorDomain:
                detail.put("event", "SubscriberErrorDomain");
                break;
            case PublisherErrorDomain:
                detail.put("event", "PublisherErrorDomain");
                break;
        }

        TeleLogger.shared.log(TeleLogExternalAPI.opentok, detail);
    }

    //AudioLevelListener methods
    @Override
    public void onAudioLevelUpdated(SubscriberKit subscriberKit, float v) {

    }

    //ConnectionListener methods
    @Override
    public void onConnectionCreated(Session session, Connection connection) {
        Log.d("TokBox", "onConnectionCreated");

        this.connection = connection;
        this.connectingDate = new Date();
        if (tokBoxUIInterface != null)
            this.tokBoxUIInterface.updateCallInfo(application.getString(R.string.connecting));

        stopOutgoingRingtone();
    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {
        Log.d("TokBox", "onConnectionDestroyed");
    }

    //SignalListener methods
    @Override
    public void onSignalReceived(Session session, String type, String value, Connection connection) {
        Log.d("TokBox", "didReceive message in tokbox type " + type + " value " + value);

        switch (type) {
            case OpenTokConstants.patient_location:
                validateUserLocation(value);
                break;
            case OpenTokConstants.requestForVideoSwap:
                receiveRequestForVideoSwap();
                break;
            case OpenTokConstants.responseForVideoSwap:
                if (value != null) {
                    if (isVideoSwapAccepted(value)) {
                        if (tokBoxUIInterface != null)
                            tokBoxUIInterface.receivedResponseForVideoSwap(true);

                        updateAudioToVideoCall();
                    } else {
                        if (tokBoxUIInterface != null)
                            tokBoxUIInterface.receivedResponseForVideoSwap(false);
                    }
                }
                break;
            case OpenTokConstants.videoEnabled:
                didReceiveVideoSwitchMessage(true);
                break;
            case OpenTokConstants.videoDisabled:
                didReceiveVideoSwitchMessage(false);
                break;
            case OpenTokConstants.audioMuteStatus:
                audioMuteStatusReceived(value);
                break;
            case OpenTokConstants.subscriberBatteryLevel:
                didReceiveBatteryStatus(value);
                break;
            default:
                if (value != null && tokBoxUIInterface != null) {
                    tokBoxUIInterface.didReceiveVitalData(value, type);
                }
                break;
        }

    }

    //StreamPropertiesListener methods
    @Override
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {
        Log.d("TokBox", "onStreamHasAudioChanged " + b);

    }

    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {
        Log.d("TokBox", "onStreamHasVideoChanged " + b);
    }

    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i1) {
        Log.d("TokBox", "onStreamVideoDimensionsChanged");
    }

    @Override
    public void onStreamVideoTypeChanged(Session session, Stream stream, Stream.StreamVideoType streamVideoType) {
        Log.d("TokBox", "onStreamVideoTypeChanged " + streamVideoType.getVideoType());
    }

    //Publisher AudioStatsListener
    @Override
    public void onAudioStats(PublisherKit publisherKit, PublisherKit.PublisherAudioStats[] publisherAudioStats) {

        if (publisherAudioStats.length <= 1) {
            return;
        }

        PublisherKit.PublisherAudioStats previousStat = publisherAudioStats[publisherAudioStats.length - 2];
        PublisherKit.PublisherAudioStats currentStat = publisherAudioStats[publisherAudioStats.length - 1];

        long totalAudioPackets =
                (currentStat.audioPacketsLost - previousStat.audioPacketsLost) +
                        (currentStat.audioPacketsSent - previousStat.audioPacketsSent);

        if (0 == totalAudioPackets) {
            publisherAudioQualityScore = 0.0;
            return;
        }

        double plr = (currentStat.audioPacketsLost - previousStat.audioPacketsLost) /
                totalAudioPackets;

        double rtt = 1.0;
        double d = rtt + 20;
        double h = (d - 177.3) < 0 ? 0 : 1;
        double Id = 0.024 * d + 0.11 * (d - 177.3) * h;
        double Ie = 0 + 19.8 * Math.log(1 + 29.7 * plr);
        double R = 94.2 - Id - Ie;


        if (R < 0) {
            publisherAudioQualityScore = 1;
        } else if (R > 100) {
            publisherAudioQualityScore = 4.5;
        } else {
            publisherAudioQualityScore = 1 + 0.035 * R + 7.10 / 1000000 * R * (R - 60) * (100 - R);
        }

        statUpdate();
    }

    //Publisher VideoStatsListener
    @Override
    public void onVideoStats(PublisherKit publisherKit, PublisherKit.PublisherVideoStats[] publisherVideoStats) {

        if (publisherVideoStats.length <= 1) {
            return;
        }

        PublisherKit.PublisherVideoStats previousStat = publisherVideoStats[publisherVideoStats.length - 2];
        PublisherKit.PublisherVideoStats currentStat = publisherVideoStats[publisherVideoStats.length - 1];

        double interval = currentStat.timeStamp - previousStat.timeStamp;
        double bytesIn = currentStat.videoBytesSent - previousStat.videoBytesSent;
        double bitrate = (bytesIn * 8) / (interval / 1000);

        // Discard bitrates below a reasonable floor
        if (bitrate < 30000) {
            publisherVideoQualityScore = 0.0;
        }
        // Cap the bitrate by a reasonable ceiling
        int pixelCount = publisherKit.getStream().getVideoHeight() * publisherKit.getStream().getVideoWidth();
        double y = 2.069924867 * Math.pow(Math.log10(pixelCount), 0.6250223771);
        double targetBitrate = Math.pow(10, y);
        bitrate = Math.min(targetBitrate, bitrate);
        publisherVideoQualityScore =
                (Math.log(bitrate / 30000) /
                        Math.log(targetBitrate / 30000)) * 4 + 1;

        statUpdate();
    }

    //Subscriber AudioStatsListener
    @Override
    public void onAudioStats(SubscriberKit subscriberKit, SubscriberKit.SubscriberAudioStats subscriberAudioStats) {

        if (lastSubscriberAudioStat == null) {
            lastSubscriberAudioStat = subscriberAudioStats;
            return;
        }

        if (subscriberAudioStats.timeStamp - lastSubscriberAudioStat.timeStamp < 15000) {
            return;
        }

        int totalAudioPackets =
                (subscriberAudioStats.audioPacketsLost - lastSubscriberAudioStat.audioPacketsLost) +
                        (subscriberAudioStats.audioPacketsReceived - lastSubscriberAudioStat.audioPacketsReceived);

        if (0 == totalAudioPackets) {
            lastSubscriberAudioStat = subscriberAudioStats;
            subscriberAudioQualityScore = 0.0;
            return;
        }

        double plr = (subscriberAudioStats.audioPacketsLost - lastSubscriberAudioStat.audioPacketsLost) /
                totalAudioPackets;

        double rtt = 1.0;
        double d = rtt + 20;
        double h = (d - 177.3) < 0 ? 0 : 1;
        double Id = 0.024 * d + 0.11 * (d - 177.3) * h;
        double Ie = 0 + 19.8 * Math.log(1 + 29.7 * plr);
        double R = 94.2 - Id - Ie;


        if (R < 0) {
            subscriberAudioQualityScore = 1;
        } else if (R > 100) {
            subscriberAudioQualityScore = 4.5;
        } else {
            subscriberAudioQualityScore = 1 + 0.035 * R + 7.10 / 1000000 * R * (R - 60) * (100 - R);
        }

        lastSubscriberAudioStat = subscriberAudioStats;
        statUpdate();
    }

    //Subscriber VideoStatsListener
    @Override
    public void onVideoStats(SubscriberKit subscriberKit, SubscriberKit.SubscriberVideoStats subscriberVideoStats) {

        if (lastSubscriberVideoStat == null) {
            lastSubscriberVideoStat = subscriberVideoStats;
            return;
        }

        if (subscriberVideoStats.timeStamp - lastSubscriberVideoStat.timeStamp < 15000) {
            return;
        }


        double interval = subscriberVideoStats.timeStamp - lastSubscriberVideoStat.timeStamp;
        double bytesIn = subscriberVideoStats.videoBytesReceived - lastSubscriberVideoStat.videoBytesReceived;
        double bitrate = (bytesIn * 8) / (interval / 1000);

        // Discard bitrates below a reasonable floor
        if (bitrate < 30000) {
            subscriberVideoQualityScore = 0.0;
        }
        // Cap the bitrate by a reasonable ceiling
        int pixelCount = subscriberKit.getStream().getVideoHeight() * subscriberKit.getStream().getVideoWidth();
        double y = 2.069924867 * Math.pow(Math.log10(pixelCount), 0.6250223771);
        double targetBitrate = Math.pow(10, y);
        bitrate = Math.min(targetBitrate, bitrate);
        subscriberVideoQualityScore =
                (Math.log(bitrate / 30000) /
                        Math.log(targetBitrate / 30000)) * 4 + 1;


        lastSubscriberVideoStat = subscriberVideoStats;
        statUpdate();

    }

    //SubscriberKit.VideoListener
    @Override
    public void onVideoDataReceived(SubscriberKit subscriberKit) {

    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriberKit, String reason) {

        switch (reason) {
            case SubscriberKit.VIDEO_REASON_QUALITY:
                if (isVideoCall()) {
                    isSubscriberQualityDegraded = true;
                    isSubscriberVideoMuted = true;
                    Log.d("TokBox", "subscribe video disabled because of quality changed ");
                    didVideoDisabledOrEnabledForSubscriberDueToQuality(false);
                }

                EventRecorder.recordCallUpdates("subscriber_video_disabled", "quality_changed");

                break;
            case SubscriberKit.VIDEO_REASON_PUBLISH_VIDEO:

                if (!isSubscriberVideoMuted) {
                    isSubscriberVideoMuted = true;

                    if (tokBoxUIInterface != null) {
                        tokBoxUIInterface.didSubscribeVideoDisabled();
                    }
                }

                EventRecorder.recordCallUpdates("subscriber_video_disabled", "publisher_property_changed");

                Log.d("TokBox", "subscribe video disabled because of publisher property");
                break;
            case SubscriberKit.VIDEO_REASON_SUBSCRIBE_TO_VIDEO:

                if (!isSubscriberVideoMuted) {
                    isSubscriberVideoMuted = true;

                    if (tokBoxUIInterface != null) {
                        tokBoxUIInterface.didSubscribeVideoDisabled();
                    }
                }

                EventRecorder.recordCallUpdates("subscriber_video_disabled", "subscriber_property_changed");

                Log.d("TokBox", "subscribe video disabled because of subscriber property");
                break;
        }
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String reason) {
        switch (reason) {
            case SubscriberKit.VIDEO_REASON_QUALITY:
                isSubscriberQualityDegraded = false;
                isSubscriberVideoMuted = false;

                Log.d("TokBox", "subscribe video enabled because of quality changed");

                didVideoDisabledOrEnabledForSubscriberDueToQuality(true);

                EventRecorder.recordCallUpdates("subscriber_video_enabled", "quality_changed");

                break;
            case SubscriberKit.VIDEO_REASON_PUBLISH_VIDEO:
                if (isSubscriberVideoMuted) {
                    isSubscriberVideoMuted = false;

                    if (tokBoxUIInterface != null) {
                        tokBoxUIInterface.didSubscribeVideoEnabled();
                    }
                }

                EventRecorder.recordCallUpdates("subscriber_video_enabled", "publisher_property_changed");

                Log.d("TokBox", "subscribe video enabled because of publisher property");

                break;
            case SubscriberKit.VIDEO_REASON_SUBSCRIBE_TO_VIDEO:
                if (isSubscriberVideoMuted) {
                    isSubscriberVideoMuted = false;

                    if (tokBoxUIInterface != null) {
                        tokBoxUIInterface.didSubscribeVideoEnabled();
                    }
                }

                EventRecorder.recordCallUpdates("subscriber_video_enabled", "subscriber_property_changed");

                Log.d("TokBox", "subscribe video enabled because of subscriber property");

                break;
        }
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {
        Log.d("TokBox", "onVideoDisableWarning ");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {
        Log.d("TokBox", "onVideoDisableWarningLifted ");
    }

    //Audio Interface
    @Override
    public void didChangeState(int state) {
        if (tokBoxUIInterface != null) {
            tokBoxUIInterface.didChangedAudioInput(state);
        }
    }

    @Override
    public void didPressedBluetoothButton() {
        if (TokBox.shared.getCallState() == OpenTokConstants.waitingForUserAction) {
            if (AudioDeviceManager.getAudioDevice() != null && AudioDeviceManager.getAudioDevice() instanceof CustomAudioDevice) {
                CustomAudioDevice customAudioDevice = (CustomAudioDevice) AudioDeviceManager.getAudioDevice();
                customAudioDevice.connectBluetooth();
            }

            if (tokBoxUIInterface != null) {
                tokBoxUIInterface.bluetoothMediaAction(false);
            }

        } else {
            if (tokBoxUIInterface != null) {
                tokBoxUIInterface.bluetoothMediaAction(true);
            }
        }
    }

    //AudioManager.OnAudioFocusChangeListener methods
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (outgoingPlayer != null && outgoingPlayer.isPlaying()) {
                    outgoingPlayer.start();
                } else if (incomingPlayer != null && incomingPlayer.isPlaying()) {
                    incomingPlayer.start();
                }

                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                if (outgoingPlayer != null && outgoingPlayer.isPlaying()) {
                    outgoingPlayer.stop();
                } else if (incomingPlayer != null && incomingPlayer.isPlaying()) {
                    incomingPlayer.stop();
                }

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (outgoingPlayer != null && outgoingPlayer.isPlaying()) {
                    outgoingPlayer.pause();
                } else if (incomingPlayer != null && incomingPlayer.isPlaying()) {
                    incomingPlayer.pause();
                }

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (outgoingPlayer != null && outgoingPlayer.isPlaying()) {
                    outgoingPlayer.setVolume(0.1f, 0.1f);
                } else if (incomingPlayer != null && incomingPlayer.isPlaying()) {
                    incomingPlayer.setVolume(0.1f, 0.1f);
                }
                break;

        }
    }

    /*
    //RecognitionListener methods
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("TokBox","Google search onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("TokBox","Google search onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.d("TokBox","onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("TokBox","Google search onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("TokBox","Google search onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        Log.e("TokBox","Google search Error in Google Speach "+message);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (matches.size() > 0) {
            currentTranscript += matches.get(0) + "\n";
        }
        //speech.cancel();
        //speech.startListening(recognizerIntent);
        Log.d("TokBox","Google search onResults : "+currentTranscript);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        ArrayList<String> unstableData = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");

        String mResult = "";
        if (data != null && data.size() > 0 && unstableData != null && unstableData.size() > 0) {
             mResult = data.get(0) + unstableData.get(0);
        } else if (data != null && data.size() > 0) {
            mResult = data.get(0);
        } else if (unstableData != null && unstableData.size() > 0) {
            mResult = unstableData.get(0);
        }

        currentTranscript += mResult + "\n";

        //speech.cancel();
        //speech.startListening(recognizerIntent);
        Log.d("TokBox","Google search onPartialResults : "+currentTranscript);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("TokBox","Google search onEvent event : "+eventType+" params :"+params.toString());
    }*/

    //OnDSListener methods
    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

    }

    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue) {

    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult) {
        Log.d("TokBox", "Google search onDroidSpeechLiveResult : " + liveSpeechResult);
    }

    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult) {
        currentTranscript += finalSpeechResult + "\n";
        Log.d("TokBox", "Google search onDroidSpeechFinalResult : " + finalSpeechResult);
    }

    @Override
    public void onDroidSpeechClosedByUser() {
        Log.d("TokBox", "Google search onDroidSpeechClosedByUser");
    }

    @Override
    public void onDroidSpeechError(String errorMsg) {
        Log.d("TokBox", "Google search onDroidSpeechError : " + errorMsg);
    }
}
