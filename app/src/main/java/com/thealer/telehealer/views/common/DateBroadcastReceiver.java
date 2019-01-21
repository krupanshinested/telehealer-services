package com.thealer.telehealer.views.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

/**
 * Created by Aswin on 10,November,2018
 */
public class DateBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && !bundle.getBoolean(Constants.DATE_PICKER_CANCELLED)) {
                onDateReceived(Utils.getFormatedDate(bundle.getInt(Constants.YEAR), bundle.getInt(Constants.MONTH), bundle.getInt(Constants.DAY)));
            } else {
                onCancelled();
            }
        }
    }

    public void onDateReceived(String formatedDate) {
    }

    public void onCancelled() {
    }
}
