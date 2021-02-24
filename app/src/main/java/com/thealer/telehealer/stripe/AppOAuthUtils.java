package com.thealer.telehealer.stripe;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;

public class AppOAuthUtils {

    public static boolean checkForOAuth(Fragment fragment, PaymentInfo paymentInfo) {
        if (UserType.isUserDoctor()) {
            if (Constants.OAuthStatus.CONNECTED.equals(paymentInfo.getOauthStatus()))
                return true;
            String message = null;
            if (Constants.OAuthStatus.PAYOUT_DISABLED.equals(paymentInfo.getOauthStatus())) {
                message = fragment.getString(R.string.msg_missing_info_in_stripe, TextUtils.join(", ", paymentInfo.getCurrentlyDue()));
            }
            fragment.startActivityForResult(getOAuthIntent(fragment.getActivity(), message), RequestID.REQ_OAUTH);
            return false;
        }
        return true;
    }


    private static Intent getOAuthIntent(Activity activity, String message) {
        Intent intent = new Intent(activity, PaymentContentActivity.class);
        String description;

        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.proceed));
        intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, true);
        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);
        intent.putExtra(ArgumentKeys.IS_FOR_OAUTH, true);
        intent.putExtra(ArgumentKeys.SKIP_TITLE, activity.getString(R.string.lbl_not_now));
        if (message != null)
            description = message;
        else
            description = activity.getString(R.string.msg_provide_stripe_information);

        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        return intent;
    }
}
