package com.thealer.telehealer.stripe;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserType;

public class AppOAuthUtils {

    public static boolean checkForOAuth(Activity activity, PaymentInfo paymentInfo) {
        if (UserType.isUserDoctor() && paymentInfo.isOathNotConnected()) {
            activity.startActivityForResult(getOAuthNotConnectedIntent(activity), 1);
            return false;
        }
        return true;
    }


    private static Intent getOAuthNotConnectedIntent(Activity activity) {
        Intent intent = new Intent(activity, PaymentContentActivity.class);
        String description;

        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.proceed));
        intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);
        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, false);
        intent.putExtra(ArgumentKeys.IS_FOR_OAUTH, true);
        intent.putExtra(ArgumentKeys.SKIP_TITLE, activity.getString(R.string.lbl_not_now));
        description = activity.getString(R.string.msg_provide_stripe_information);

        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        return intent;
    }
}
