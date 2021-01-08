package com.thealer.telehealer.stripe;

import android.app.Activity;
import android.content.Intent;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.home.HomeActivity;

public class AppPaymentCardUtils {

    public static void handleCardCasesFromWhoAmI(Activity activity, WhoAmIApiResponseModel whoAmIApiResponseModel) {
        if (!whoAmIApiResponseModel.getPayment_account_info().isCCCaptured()) {
            openCardNotAddedScreen(activity);
        } else if (!whoAmIApiResponseModel.getPayment_account_info().isDefaultCardValid()) {
            openCardExpiredScreen(activity, whoAmIApiResponseModel.getPayment_account_info().getSavedCardsCount());
        }
    }


    public static void openCardNotAddedScreen(Activity activity) {
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.proceed));
        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
        String description = activity.getString(R.string.msg_payment_gateway_changed);

        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        activity.startActivityForResult(intent, RequestID.REQ_CARD_INFO);
    }

    public static void openCardExpiredScreen(Activity activity, int cardCount) {
        boolean doesUserHasMultipleCards = cardCount > 1;
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(doesUserHasMultipleCards ? R.string.lbl_manage_cards : R.string.lbl_add_card));
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);
        String description = activity.getString(doesUserHasMultipleCards ? R.string.msg_default_card_expired_multiple : R.string.msg_default_card_expired);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        activity.startActivityForResult(intent, RequestID.REQ_CARD_EXPIRE);
    }
}
