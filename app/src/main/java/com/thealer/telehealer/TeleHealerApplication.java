package com.thealer.telehealer;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.views.call.CallActivity;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Aswin on 25,November,2018
 */
public class TeleHealerApplication extends Application implements LifecycleObserver {

    public static AppPreference appPreference;
    public static TeleHealerApplication application;
    public FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        appPreference = AppPreference.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Fabric.with(this, new Crashlytics());

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Log.d("TeleHealerApplication","received token "+token);
                        TelehealerFirebaseMessagingService.assignToken(token);
                    }
                });


        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // app moved to foreground

        EventRecorder.recordLastUpdate("last_open_date");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (TokBox.shared.isActiveCallPreset() && !TokBox.shared.isActivityPresent()) {
                    Log.d("TeleHealerApplication", "open call activity from Application");
                    Intent intent = new Intent(application, CallActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.d("TeleHealerApplication", "no active call present");
                }
            }
        });

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        // app moved to background

        if (VitalsManager.instance != null) {
            VitalsManager.instance.disconnectAll();
        }
    }
}
