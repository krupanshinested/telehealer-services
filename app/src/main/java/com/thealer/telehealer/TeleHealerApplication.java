package com.thealer.telehealer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.CallMinimizeService;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.views.call.CallActivity;

import java.util.HashSet;
import java.util.Set;

import config.AppConfig;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Aswin on 25,November,2018
 */
public class TeleHealerApplication extends Application implements LifecycleObserver {

    public static AppPreference appPreference;
    public static TeleHealerApplication application;
    public static final String notificationChannelId = "thealer";
    public FirebaseAnalytics firebaseAnalytics;
    public static Set<Integer> popUpSchedulesId = new HashSet<>();
    public static boolean isVitalDeviceConnectionShown = false, isContentViewProceed = false, isInForeGround = false;
    public static AppConfig appConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        appPreference = AppPreference.getInstance(this);
        appConfig = new AppConfig(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Fabric.with(this, new Crashlytics());

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId,
                    "General",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);

            NotificationChannel callNotification = new NotificationChannel("thealer-call",
                    "Call",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(callNotification);

        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // app moved to foreground
        isInForeGround = true;

        Log.e("aswin", "onMoveToForeground: ");
        EventRecorder.recordLastUpdate("last_open_date");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (TokBox.shared.isActiveCallPreset() && !TokBox.shared.isActivityPresent() && !CallMinimizeService.isCallMinimizeActive) {
                    Log.d("TeleHealerApplication", "open call activity from Application");
                    Intent intent = new Intent(application, CallActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.d("TeleHealerApplication", "no active call present");
                }
            }
        });

        popUpSchedulesId.clear();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        // app moved to background
        Log.e("aswin", "onMoveToBackground: ");
        isInForeGround = false;

        if (isVitalDeviceConnectionShown) {
            isVitalDeviceConnectionShown = false;
        }

        if (!TokBox.shared.isActiveCallPreset()) {
            VitalsManager.getInstance().disconnectAll();
        }
    }
}
