package com.thealer.telehealer.views.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

public class LockScreenReceiver extends BroadcastReceiver
{
    private static final String TAG = "LockScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent != null && intent.getAction() != null)
        {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Utils.storeLastActiveTime();
                context.startActivity(new Intent(context, QuickLoginActivity.class));
                Log.e(TAG, "onReceive: Unlock" );

                // Screen is on but not unlocked (if any locking mechanism present)
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                Utils.storeLastActiveTime();
                Log.e(TAG, "onReceive: Lock" );
            }
            else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT))
            {
                Log.e(TAG, "onReceive: Present");
            }
        }
    }
}