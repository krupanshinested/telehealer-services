package com.thealer.telehealer.stripe;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
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

    /**
     * @param fragment   instance of fragment
     * @param errorModel instance of error model from api response
     */
    public static void handleCardCasesFromErrorModel(Fragment fragment, ErrorModel errorModel) {
        if (!errorModel.isCCCaptured()) {
            openCardNotAddedScreen(fragment);

        } else if (!errorModel.isDefaultCardValid()) {
            openCardExpiredScreen(fragment, errorModel.getSavedCardsCount());
        }
    }

    public static boolean hasValidPaymentCard(ErrorModel errorModel) {
        return errorModel.isCCCaptured() && errorModel.isDefaultCardValid();
    }


    public static void openCardNotAddedScreen(Activity activity) {
        activity.startActivityForResult(getCardNotAddedIntent(activity), RequestID.REQ_CARD_INFO);
    }

    public static void openCardExpiredScreen(Activity activity, int cardCount) {
        activity.startActivityForResult(getCardExpiredIntent(activity, cardCount), RequestID.REQ_CARD_EXPIRE);
    }


    public static void openCardNotAddedScreen(Fragment fragment) {
        fragment.startActivityForResult(getCardNotAddedIntent(fragment.getActivity()), RequestID.REQ_CARD_INFO);
    }

    public static void openCardExpiredScreen(Fragment fragment, int cardCount) {
        fragment.startActivityForResult(getCardExpiredIntent(fragment.getActivity(), cardCount), RequestID.REQ_CARD_EXPIRE);
    }


    private static Intent getCardNotAddedIntent(Activity activity) {
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.proceed));
        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
        String description = activity.getString(R.string.msg_payment_gateway_changed);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        return intent;
    }

    private static Intent getCardExpiredIntent(Activity activity, int cardCount) {
        boolean doesUserHasMultipleCards = cardCount > 1;
        Intent intent = new Intent(activity, ContentActivity.class);
        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(doesUserHasMultipleCards ? R.string.lbl_manage_cards : R.string.lbl_add_card));
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);
        String description = activity.getString(doesUserHasMultipleCards ? R.string.msg_default_card_expired_multiple : R.string.msg_default_card_expired);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        return intent;
    }
}
