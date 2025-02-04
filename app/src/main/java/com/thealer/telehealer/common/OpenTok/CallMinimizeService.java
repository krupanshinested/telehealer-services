package com.thealer.telehealer.common.OpenTok;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.TokBoxUIInterface;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.call.CallActivity;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by rsekar on 2/5/19.
 */

public class CallMinimizeService extends Service implements FloatingViewListener, TokBoxUIInterface {

    private static final String TAG = "ChatHeadService";
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";
    private static final int NOTIFICATION_ID = 9083150;

    public static Boolean isCallMinimizeActive = false;
    private String currentCallQuality = OpenTokConstants.none;

    @Nullable
    private LinearLayout remoteView;
    private OpenTok currentCall;

    /**
     * FloatingViewManager
     */
    private FloatingViewManager mFloatingViewManager;

    @Override
    public void onCreate() {
        currentCall = CallManager.shared.getActiveCallToShow();
        startForeground(NOTIFICATION_ID, createNotification(this,currentCall.getCallType()));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(callScreenOpened, new IntentFilter(Constants.CALL_ACTIVITY_RESUMED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isCallMinimizeActive = true;

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final ConstraintLayout parentView = (ConstraintLayout) inflater.inflate(R.layout.view_call_minimized, null, false);

        currentCall = CallManager.shared.getActiveCallToShow();

        LinearLayout remoteView = parentView.findViewById(R.id.remoteView);
        this.remoteView = remoteView;
        remoteView.setClipToOutline(true);
        ImageView profile_iv = parentView.findViewById(R.id.profile_iv);

        if (currentCall.getCallType().equals(OpenTokConstants.audio)) {
            if (currentCall.getOtherPersonDetail() != null) {
                Utils.setImageWithGlide(getApplicationContext(), profile_iv, currentCall.getOtherPersonDetail().getUser_avatar(), getDrawable(R.drawable.profile_placeholder), true, true);
            } else {
                profile_iv.setImageResource(R.drawable.profile_placeholder);
            }

            profile_iv.setVisibility(View.VISIBLE);
            remoteView.setVisibility(View.GONE);
        } else {
            currentCall.setRemoteView(remoteView);
            profile_iv.setVisibility(View.GONE);
            remoteView.setVisibility(View.VISIBLE);
        }

        currentCall.setUIListener(this);

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCallScreen(getCallIntent());
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.overMargin = (int) (16 * metrics.density);

        if (currentCall.getCallType().equals(OpenTokConstants.audio)) {
            options.floatingViewWidth = (int) (120 * metrics.density);
            options.floatingViewHeight = (int) (120 * metrics.density);
        } else {
            options.floatingViewWidth = (int) (120 * metrics.density);
            options.floatingViewHeight = (int) (160 * metrics.density);
        }

        mFloatingViewManager.addViewToWindow(parentView, options);
        mFloatingViewManager.setTrashViewEnabled(false);

        return START_REDELIVER_INTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinishFloatingView() {
        stopSelf();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {

    }

    private void destroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(callScreenOpened);
        
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }

        currentCall.resetUIListener(this);
        currentCall.removeRemoteView(remoteView);

        stopForeground(true);

        isCallMinimizeActive = false;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(NOTIFICATION_ID);
    }

    private static Notification createNotification(Context context,String callType) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TeleHealerApplication.appConfig.getCallChannel());
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.app_icon);
        builder.setContentTitle(context.getString(R.string.app_name)+" Call");
        if (callType.equals(OpenTokConstants.audio)) {
            builder.setContentText("Audio call is going on");
        } else {
            builder.setContentText("Video call is going on");
        }
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }

    private Intent getCallIntent() {
        Intent intent = new Intent(application, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void openCallScreen(Intent intent) {
        getApplicationContext().startActivity(intent);
        Intent broadCastIntent = new Intent(Constants.CALL_SCREEN_MAXIMIZE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadCastIntent);
        destroy();
    }

    private BroadcastReceiver callScreenOpened = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            destroy();
        }
    };

    //TokBoxUIInterface method
    @Override
    public void startedCall() {

    }

    @Override
    public void didEndCall(String callRejectionReason) {
        CallActivity.openFeedBackIfNeeded(currentCall.getCallRequest(),currentCall.getConnectedDate(),callRejectionReason, application);
        destroy();
    }

    @Override
    public void updateCallInfo(String message) {
        //do nothing
    }

    @Override
    public void didUpdatedPatientLocation(String state) {
        //do nothing
    }

    @Override
    public void didReceivedOtherUserDetails(CommonUserApiResponseModel commonUserApiResponseModel) {
        //do nothing
    }

    @Override
    public void receivedRequestForVideoSwap() {
        Intent intent = getCallIntent();
        intent.putExtra(ArgumentKeys.CALL_REQUEST_ACTION, OpenTokConstants.receivedRequestForVideoSwap);
        openCallScreen(intent);
    }

    @Override
    public void receivedResponseForVideoSwap(Boolean isAccepted) {
        Intent intent = getCallIntent();
        intent.putExtra(ArgumentKeys.CALL_REQUEST_ACTION, OpenTokConstants.receivedResponseForVideoSwap);
        intent.putExtra(ArgumentKeys.CALL_REQUEST_DATA, isAccepted);
        openCallScreen(intent);
    }

    @Override
    public void updateCallQuality(String callQuality) {
        currentCallQuality = callQuality;
    }

    @Override
    public void updateVideoQuality(String qualityString, Boolean isMuted) {
        //do nothing
    }

    @Override
    public void didSubscribeVideoDisabled() {
        //do nothing
    }

    @Override
    public void didSubscribeAudioDisabled() {
        //do nothing
    }

    @Override
    public void didSubscribeVideoEnabled() {
        //do nothing
    }

    @Override
    public void didSubscribeAudioEnabled() {
        //do nothing
    }

    @Override
    public void didReceiveVitalData(String data, String type) {
        Intent intent = getCallIntent();
        intent.putExtra(ArgumentKeys.CALL_REQUEST_ACTION, OpenTokConstants.didReceiveVitalData);
        intent.putExtra(ArgumentKeys.CALL_REQUEST_DATA, data);
        intent.putExtra(ArgumentKeys.CALL_REQUEST_DATA_1, type);
        openCallScreen(intent);
    }

    @Override
    public void didPublishStarted() {

    }

    @Override
    public void didChangedAudioInput(int type) {
        //do nothing
    }

    @Override
    public void assignTokBoxApiViewModel(OpenTokViewModel openTokViewModel) {
        //do nothing
    }

    @Override
    public void bluetoothMediaAction(Boolean forEnd) {
        currentCall.endCall(OpenTokConstants.endCallPressed);
    }

    @Override
    public String getCurrentCallQuality() {
        return currentCallQuality;
    }

    @Override
    public void didChangedBattery() {

    }
}
