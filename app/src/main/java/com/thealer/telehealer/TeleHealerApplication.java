package com.thealer.telehealer;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Bundle;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.firebase.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;


/**
 * Created by Aswin on 25,November,2018
 */
public class TeleHealerApplication extends Application implements LifecycleObserver {

    public static AppPreference appPreference;
    public static TeleHealerApplication application;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        appPreference = AppPreference.getInstance(this);

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
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        // app moved to background

        if (VitalsManager.instance != null) {
            VitalsManager.instance.disconnectAll();
        }
    }
}
