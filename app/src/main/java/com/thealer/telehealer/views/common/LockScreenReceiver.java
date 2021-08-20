package com.thealer.telehealer.views.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class LockScreenReceiver extends BroadcastReceiver {
    private static final String TAG = "LockScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (!appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN).isEmpty()) {
                    Utils.storeLastActiveTime();
                    context.startActivity(new Intent(context, QuickLoginActivity.class));
                }
                Log.e(TAG, "onReceive: Unlock");

                // Screen is on but not unlocked (if any locking mechanism present)
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (!appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN).isEmpty()) {
                    Utils.storeLastActiveTime();
                }
                Log.e(TAG, "onReceive: Lock");
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.e(TAG, "onReceive: Present");
            }
        }
    }
}