package com.thealer.telehealer.views.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thealer.telehealer.common.ArgumentKeys;

/**
 * Created by Aswin on 03,December,2018
 */
public class QuickLoginBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("Quick login status", "Quick login status" + bundle.getInt(ArgumentKeys.QUICK_LOGIN_STATUS));
        if (bundle != null) {
            onQuickLogin(bundle.getInt(ArgumentKeys.QUICK_LOGIN_STATUS));
        }
    }

    public void onQuickLogin(int status) {
    }
}
