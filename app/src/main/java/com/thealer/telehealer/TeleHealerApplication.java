package com.thealer.telehealer;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.CallMinimizeService;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.home.HomeActivity;

import java.util.HashSet;
import java.util.List;
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
    public static boolean isVitalDeviceConnectionShown = false, isContentViewProceed = false, isInForeGround = false, isFromRegistration;
    public static AppConfig appConfig;
    public static boolean stateChange=false;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        appConfig = new AppConfig(this);
        appPreference = AppPreference.getInstance(this);
        notificationChannelId = appConfig.getApnsChannel();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        isFromRegistration = false;
        if (isVitalDeviceConnectionShown) {
            isVitalDeviceConnectionShown = false;
        }

        if (!TokBox.shared.isActiveCallPreset()) {
            VitalsManager.getInstance().disconnectAll();
        }
    }

    public void addShortCuts() {

    }

    public void removeShortCuts() {

    }

    public ComponentName getCurrentActivity() {
        ActivityManager am = (ActivityManager) this .getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> taskInfo = am.getAppTasks();
        ComponentName componentInfo = taskInfo.get(0).getTaskInfo().baseActivity;
        Log.d( "CURRENT Activity ::" ,""+componentInfo+"   Package Name :  "+componentInfo.getPackageName());
        return componentInfo;
    }
}
