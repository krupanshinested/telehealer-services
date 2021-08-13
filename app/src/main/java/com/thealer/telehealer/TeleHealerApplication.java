package com.thealer.telehealer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.opentok.android.AudioDeviceManager;
import com.stripe.android.PaymentConfiguration;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.CallMinimizeService;
import com.thealer.telehealer.common.OpenTok.CallNotificationService;
import com.thealer.telehealer.common.OpenTok.CustomAudioDevice;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.guestlogin.WaitingRoomHearBeatService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import config.AppConfig;


/**
 * Created by Aswin on 25,November,2018
 */
public class TeleHealerApplication extends Application implements LifecycleObserver {

    public static AppPreference appPreference;
    public static TeleHealerApplication application;
    public static String notificationChannelId = "";
    public FirebaseAnalytics firebaseAnalytics;
    public static Set<Integer> popUpSchedulesId = new HashSet<>();
    public static boolean isVitalDeviceConnectionShown = false, isContentViewProceed = false, isInForeGround = false, isFromRegistration, isDestroyed = false;
    public static AppConfig appConfig;
    public static boolean stateChange = false;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        appConfig = new AppConfig(this);
        appPreference = AppPreference.getInstance(this);
        notificationChannelId = appConfig.getApnsChannel();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        CustomAudioDevice customAudioDevice = new CustomAudioDevice(application, null);
        AudioDeviceManager.setAudioDevice(customAudioDevice);

        createNotificationChannel();
        PaymentConfiguration.init(this, BuildConfig.STRIPE_KEY);
    }

    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId,
                    "General",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);

            NotificationChannel callNotification = new NotificationChannel(appConfig.getCallChannel(),
                    "Call",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(callNotification);

            NotificationChannel channel = new NotificationChannel(
                    appConfig.getVoipChannel(), "Call-voip",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setBypassDnd(true);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // app moved to foreground
        isInForeGround = true;
        Constants.DisplayQuickLogin = false;
        Intent i = new Intent(getString(R.string.APP_LIFECYCLE_STATUS));
        i.putExtra(ArgumentKeys.APP_LIFECYCLE_STATUS, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        Log.e("aswin", "onMoveToForeground: ");
        EventRecorder.recordLastUpdate("last_open_date");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (CallManager.shared.needToOpenCallScreenAutomatically() && !CallManager.shared.isActivityPresent() &&
                        !CallMinimizeService.isCallMinimizeActive) {
                    Log.d("TeleHealerApplication", "open call activity from Application");
                    Intent intent = CallActivity.getIntent(application, CallManager.shared.getActiveCallToShow().getCallRequest());
                    startActivity(intent);
                } else {
                    Log.d("TeleHealerApplication", "no active call present");
                }
            }
        });

        popUpSchedulesId.clear();
    }

    private boolean isRefreshTokenExpired() {
        try {

            JWT jwt = new JWT(appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
            Log.e("neem", "isRefreshTokenExpired: "+jwt.getExpiresAt() );
            Date date = new Date();
            return date.compareTo(jwt.getExpiresAt()) >= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        // app moved to background
        Log.e("aswin", "onMoveToBackground: ");
        isInForeGround = false;
        isFromRegistration = false;
        Constants.DisplayQuickLogin = true;
        if (isVitalDeviceConnectionShown) {
            isVitalDeviceConnectionShown = false;
        }

        if (CallManager.shared.isActiveCallPresent()) {
            OpenTok tokBox = CallManager.shared.getActiveCallToShow();
            if (!tokBox.getCallRequest().isCallForDirectWaitingRoom() && tokBox.getCallState() == OpenTokConstants.waitingForUserAction) {
                CallActivity.createNotificationBarCall(application, false, tokBox.getCallRequest().getDoctorName(), tokBox.getCallRequest());
            }
        } else {
            VitalsManager.getInstance().disconnectAll();
        }

        Intent i = new Intent(getString(R.string.APP_LIFECYCLE_STATUS));
        i.putExtra(ArgumentKeys.APP_LIFECYCLE_STATUS, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        // app moved to background
        Log.e("TeleHealerApplication", "appDestroyed");
        isInForeGround = false;
        Constants.DisplayQuickLogin = false;

        Intent i = new Intent(getString(R.string.APP_LIFECYCLE_STATUS));
        i.putExtra(ArgumentKeys.APP_LIFECYCLE_STATUS, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

    public void addShortCuts() {

    }

    public void removeShortCuts() {

    }
}
