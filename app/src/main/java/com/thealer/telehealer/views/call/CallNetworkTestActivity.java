package com.thealer.telehealer.views.call;

import androidx.lifecycle.Observer;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentok.android.AudioDeviceManager;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateViewModel;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.OpenTok.CustomAudioDevice;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.AudioInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static com.thealer.telehealer.TeleHealerApplication.application;

public class CallNetworkTestActivity extends BaseActivity implements
        Session.SessionListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener {

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private static final String LOGTAG = "quality-stats-demo";

    private String SESSION_ID = "";
    private String TOKEN = "";
    private String APIKEY = "";

    private static final int TEST_DURATION = 20; //test quality duration in seconds
    private static final int TIME_WINDOW = 3; //3 seconds
    private static final int TIME_VIDEO_TEST = 15; //time interval to check the video quality in seconds

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private double mVideoPLRatio = 0.0;
    private long mVideoBw = 0;

    private double mAudioPLRatio = 0.0;
    private long mAudioBw = 0;

    private long mPrevVideoPacketsLost = 0;
    private long mPrevVideoPacketsRcvd = 0;
    private double mPrevVideoTimestamp = 0;
    private long mPrevVideoBytes = 0;

    private long mPrevAudioPacketsLost = 0;
    private long mPrevAudioPacketsRcvd = 0;
    private double mPrevAudioTimestamp = 0;
    private long mPrevAudioBytes = 0;

    private long mStartTestTime = 0;

    private boolean audioOnly = false;

    private Handler mHandler = new Handler();

    private CircularProgressBar circular_progress;
    private TextView title_tv, sub_title_tv;
    private ImageView close_iv, success_iv;

    private final CallInitiateViewModel callInitiateViewModel = new CallInitiateViewModel(application);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_network_test);

        circular_progress = findViewById(R.id.circular_progress);
        title_tv = findViewById(R.id.title_tv);
        sub_title_tv = findViewById(R.id.sub_title_tv);
        close_iv = findViewById(R.id.close_iv);
        success_iv = findViewById(R.id.success_iv);

        title_tv.setText(getResources().getString(R.string.checking_network));
        sub_title_tv.setText(getResources().getString(R.string.calculating_video_audio_score));

        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        attachObserver(callInitiateViewModel);

        callInitiateViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null && baseApiResponseModel instanceof CallSettings) {
                    CallSettings tokenFetchModel = (CallSettings) baseApiResponseModel;

                    APIKEY = tokenFetchModel.getApiKey();
                    SESSION_ID = tokenFetchModel.getSessionId();
                    TOKEN = tokenFetchModel.getToken();

                    sessionConnect();

                }
            }
        });

        callInitiateViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showResult(getString(R.string.networkTestFailed), errorModel.getMessage(), false);
                } else {
                    showResult(getString(R.string.networkTestFailed), getString(R.string.error_on_contacting_server), false);
                }
            }
        });

        if (PermissionChecker.with(CallNetworkTestActivity.this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            callInitiateViewModel.getTokenForTestSession();
        } else {

            PermissionChecker checker = PermissionChecker.with(CallNetworkTestActivity.this);
            if (checker.isGranted(PermissionConstants.PERMISSION_CAM_MIC)) {
                title_tv.setText(getResources().getString(R.string.permission_denied));
                sub_title_tv.setText(getResources().getString(R.string.cam_mic_call_quality_permission));
            } else if (checker.isGranted(PermissionConstants.PERMISSION_CAMERA)) {
                title_tv.setText(getResources().getString(R.string.permission_denied));
                sub_title_tv.setText(getResources().getString(R.string.cam_call_quality_permission));
            } else {
                title_tv.setText(getResources().getString(R.string.permission_denied));
                sub_title_tv.setText(getResources().getString(R.string.mic_quality_permission));
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            switch (requestCode) {
                case PermissionConstants.PERMISSION_CAM_MIC:
                    callInitiateViewModel.getTokenForTestSession();
                    title_tv.setText(getResources().getString(R.string.checking_network));
                    sub_title_tv.setText(getResources().getString(R.string.calculating_video_audio_score));
                    break;
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mSession != null) {
            mSession.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void sessionConnect() {
        Log.i(LOGTAG, "Connecting session");
        if (mSession == null) {
            if (AudioDeviceManager.getAudioDevice() instanceof CustomAudioDevice) {
                ((CustomAudioDevice) AudioDeviceManager.getAudioDevice()).setRendererMute(true);
            } else {
                CustomAudioDevice customAudioDevice = new CustomAudioDevice(application, new AudioInterface() {
                    @Override
                    public void didChangeState(int state) {

                    }

                    @Override
                    public void didPressedBluetoothButton() {

                    }
                });
                customAudioDevice.setRendererMute(true);
                AudioDeviceManager.setAudioDevice(customAudioDevice);
            }

            mSession = new Session.Builder(this, APIKEY, SESSION_ID).build();
            mSession.setSessionListener(this);

            mSession.connect(TOKEN);
        }
    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOGTAG, "Session is connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);
        mPublisher.setAudioFallbackEnabled(false);
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOGTAG, "Session is disconnected");

        mPublisher = null;
        mSubscriber = null;
        mSession = null;
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOGTAG, "Session error: " + opentokError.getMessage());
        showResult(getString(R.string.app_name), getString(R.string.session_error) + opentokError.getMessage(), false);
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOGTAG, "Session onStreamDropped");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOGTAG, "Session onStreamReceived");
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOGTAG, "Publisher onStreamCreated");
        if (mSubscriber == null) {
            subscribeToStream(stream);
        }
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOGTAG, "Publisher onStreamDestroyed");
        if (mSubscriber == null) {
            unsubscribeFromStream(stream);
        }
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.i(LOGTAG, "Publisher error: " + opentokError.getMessage());
        showResult(getString(R.string.app_name), getString(R.string.publisher_error) + opentokError.getMessage(), false);
    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        Log.i(LOGTAG, "Subscriber onConnected");
        mHandler.postDelayed(statsRunnable, TEST_DURATION * 1000);
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.i(LOGTAG, "Subscriber onDisconnected");
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.i(LOGTAG, "Subscriber error: " + opentokError.getMessage());
        showResult(getString(R.string.app_name), getString(R.string.subscriber_error) + opentokError.getMessage(), false);
    }

    private void subscribeToStream(Stream stream) {

        mSubscriber = new Subscriber.Builder(CallNetworkTestActivity.this, stream).build();
        mSubscriber.setSubscriberListener(this);
        mSession.subscribe(mSubscriber);
        mSubscriber.setVideoStatsListener(new SubscriberKit.VideoStatsListener() {

            @Override
            public void onVideoStats(SubscriberKit subscriber,
                                     SubscriberKit.SubscriberVideoStats stats) {

                if (mStartTestTime == 0) {
                    mStartTestTime = System.currentTimeMillis() / 1000;
                }
                checkVideoStats(stats);

                //check quality of the video call after TIME_VIDEO_TEST seconds
                if (((System.currentTimeMillis() / 1000 - mStartTestTime) > TIME_VIDEO_TEST) && !audioOnly) {
                    checkVideoQuality();
                }
            }

        });

        mSubscriber.setAudioStatsListener(new SubscriberKit.AudioStatsListener() {
            @Override
            public void onAudioStats(SubscriberKit subscriber, SubscriberKit.SubscriberAudioStats stats) {

                checkAudioStats(stats);

            }
        });
    }

    private void unsubscribeFromStream(Stream stream) {
        if (mSubscriber.getStream().equals(stream)) {
            mSubscriber = null;
        }
    }

    private void checkVideoStats(SubscriberKit.SubscriberVideoStats stats) {
        double videoTimestamp = stats.timeStamp / 1000;

        //initialize values
        if (mPrevVideoTimestamp == 0) {
            mPrevVideoTimestamp = videoTimestamp;
            mPrevVideoBytes = stats.videoBytesReceived;
        }

        if (videoTimestamp - mPrevVideoTimestamp >= TIME_WINDOW) {
            //calculate video packets lost ratio
            if (mPrevVideoPacketsRcvd != 0) {
                long pl = stats.videoPacketsLost - mPrevVideoPacketsLost;
                long pr = stats.videoPacketsReceived - mPrevVideoPacketsRcvd;
                long pt = pl + pr;

                if (pt > 0) {
                    mVideoPLRatio = (double) pl / (double) pt;
                }
            }

            mPrevVideoPacketsLost = stats.videoPacketsLost;
            mPrevVideoPacketsRcvd = stats.videoPacketsReceived;

            //calculate video bandwidth
            mVideoBw = (long) ((8 * (stats.videoBytesReceived - mPrevVideoBytes)) / (videoTimestamp - mPrevVideoTimestamp));

            mPrevVideoTimestamp = videoTimestamp;
            mPrevVideoBytes = stats.videoBytesReceived;

            Log.i(LOGTAG, "Video bandwidth (bps): " + mVideoBw + " Video Bytes received: " + stats.videoBytesReceived + " Video packet lost: " + stats.videoPacketsLost + " Video packet loss ratio: " + mVideoPLRatio);

        }
    }

    private void checkAudioStats(SubscriberKit.SubscriberAudioStats stats) {
        double audioTimestamp = stats.timeStamp / 1000;

        //initialize values
        if (mPrevAudioTimestamp == 0) {
            mPrevAudioTimestamp = audioTimestamp;
            mPrevAudioBytes = stats.audioBytesReceived;
        }

        if (audioTimestamp - mPrevAudioTimestamp >= TIME_WINDOW) {
            //calculate audio packets lost ratio
            if (mPrevAudioPacketsRcvd != 0) {
                long pl = stats.audioPacketsLost - mPrevAudioPacketsLost;
                long pr = stats.audioPacketsReceived - mPrevAudioPacketsRcvd;
                long pt = pl + pr;

                if (pt > 0) {
                    mAudioPLRatio = (double) pl / (double) pt;
                }
            }
            mPrevAudioPacketsLost = stats.audioPacketsLost;
            mPrevAudioPacketsRcvd = stats.audioPacketsReceived;

            //calculate audio bandwidth
            mAudioBw = (long) ((8 * (stats.audioBytesReceived - mPrevAudioBytes)) / (audioTimestamp - mPrevAudioTimestamp));

            mPrevAudioTimestamp = audioTimestamp;
            mPrevAudioBytes = stats.audioBytesReceived;

            Log.i(LOGTAG, "Audio bandwidth (bps): " + mAudioBw + " Audio Bytes received: " + stats.audioBytesReceived + " Audio packet lost: " + stats.audioPacketsLost + " Audio packet loss ratio: " + mAudioPLRatio);

        }

    }

    private void checkVideoQuality() {
        if (mSession != null) {
            Log.i(LOGTAG, "Check video quality stats data");
            if (mVideoBw < 150000 || mVideoPLRatio > 0.03) {
                //go to audio call to check the quality with video disabled
                showResult(getString(R.string.network_test_succeeded), getString(R.string.audio_good_video_bad), true);
                mPublisher.setPublishVideo(false);
                mSubscriber.setSubscribeToVideo(false);
                mSubscriber.setVideoStatsListener(null);
                audioOnly = true;
            } else {
                //quality is good for video call
                mSession.disconnect();
                showResult(getString(R.string.network_test_succeeded), getString(R.string.audio_video_good), true);
            }
        }
    }

    private void checkAudioQuality() {
        if (mSession != null) {
            if (mAudioBw < 25000 || mAudioPLRatio > 0.05) {
                showResult(getString(R.string.network_test_succeeded), getString(R.string.poor_connection), true);
            } else {
                showResult(getString(R.string.network_test_succeeded), getString(R.string.audio_good_video_bad), true);
            }
        }
    }

    private void showResult(String title, String message, Boolean isSuccess) {
        title_tv.setText(title);
        sub_title_tv.setText(message);
        circular_progress.progressiveStop();
        circular_progress.setVisibility(View.INVISIBLE);

        if (isSuccess) {
            success_iv.setImageDrawable(getDrawable(R.drawable.success_animation_drawable));
        } else {
            success_iv.setImageDrawable(getDrawable(R.drawable.failure_animation_drawable));
        }

        ((Animatable) success_iv.getDrawable()).start();
    }

    private Runnable statsRunnable = new Runnable() {

        @Override
        public void run() {
            if (mSession != null) {
                checkAudioQuality();
                mSession.disconnect();
            }
        }
    };

}