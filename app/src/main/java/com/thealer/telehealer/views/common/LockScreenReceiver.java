package com.thealer.telehealer.views.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thealer.telehealer.common.Constants;
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

                Log.e(TAG, "onReceive: Screen ON");

                // Screen is on but not unlocked (if any locking mechanism present)
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (!appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN).isEmpty()) {
                    Utils.storeLastActiveTime();
                }
                Log.e(TAG, "onReceive: Screen Lock");
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                if (!appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN).isEmpty()) {
                    Utils.storeLastActiveTime();
                    try {
                        if (!Constants.DisplayQuickLogin) {
                            Constants.DisplayQuickLogin = true;
                            context.startActivity(new Intent(context, QuickLoginActivity.class));
                        }
                    } catch (Exception e) {
                        context.startActivity(new Intent(context, QuickLoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
                Log.e(TAG, "onReceive: Screen Unlock - Present");

            }
        }
    }
}