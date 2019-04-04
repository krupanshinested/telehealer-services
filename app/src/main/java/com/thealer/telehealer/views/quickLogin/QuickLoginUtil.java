package com.thealer.telehealer.views.quickLogin;

import android.content.Context;

import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.biometric.BioMetricUtils;

/**
 * Created by rsekar on 11/22/18.
 */

public class QuickLoginUtil {

    public static int getAvailableQuickLoginType(Context context) {
        if (BioMetricUtils.isHardwareSupported(context) &&
                BioMetricUtils.isFingerprintAvailable(context)) {
            return Constants.QUICK_LOGIN_TYPE_TOUCH;
        } else {
            return Constants.QUICK_LOGIN_TYPE_PIN;
        }
    }

    public static Boolean isQuickLoginEnable(Context context) {
        int type = TeleHealerApplication.appPreference.getInt(Constants.QUICK_LOGIN_TYPE);
        return type == Constants.QUICK_LOGIN_TYPE_PIN || type == Constants.QUICK_LOGIN_TYPE_TOUCH;
    }
}
