package com.thealer.telehealer.stripe;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.home.HomeActivity;

public class AppPaymentCardUtils {

    public static void handleCardCasesFromWhoAmI(Activity activity, WhoAmIApiResponseModel whoAmIApiResponseModel, @Nullable String doctorName) {
        if (!whoAmIApiResponseModel.getPayment_account_info().isCCCaptured()) {
            openCardNotAddedScreen(activity, doctorName);
        } else if (!whoAmIApiResponseModel.getPayment_account_info().isDefaultCardValid()) {
            openCardExpiredScreen(activity, whoAmIApiResponseModel.getPayment_account_info().getSavedCardsCount(), doctorName);
        }
    }

    /**
     * @param fragment   instance of fragment
     * @param errorModel instance of error model from api response
     */
    public static void handleCardCasesFromErrorModel(Fragment fragment, ErrorModel errorModel, @Nullable String doctorName) {
        if (!errorModel.isCCCaptured()) {
            openCardNotAddedScreen(fragment, doctorName);

        } else if (!errorModel.isDefaultCardValid()) {
            openCardExpiredScreen(fragment, errorModel.getSavedCardsCount(), doctorName);
        }
    }

    public static boolean hasValidPaymentCard(ErrorModel errorModel) {
        return errorModel.isCCCaptured() && errorModel.isDefaultCardValid();
    }

    public static boolean hasValidPaymentCard(WhoAmIApiResponseModel whoAmIApiResponseModel) {
        return whoAmIApiResponseModel.getPayment_account_info().isCCCaptured() && whoAmIApiResponseModel.getPayment_account_info().isDefaultCardValid();
    }


    private static void openCardNotAddedScreen(Activity activity, @Nullable String doctorName) {
        activity.startActivityForResult(getCardNotAddedIntent(activity, doctorName), RequestID.REQ_CARD_INFO);
    }

    public static void openCardExpiredScreen(Activity activity, int cardCount, @Nullable String doctorName) {
        activity.startActivityForResult(getCardExpiredIntent(activity, cardCount, doctorName), RequestID.REQ_CARD_EXPIRE);
    }


    private static void openCardNotAddedScreen(Fragment fragment, @Nullable String doctorName) {
        fragment.startActivityForResult(getCardNotAddedIntent(fragment.getActivity(), doctorName), RequestID.REQ_CARD_INFO);
    }

    private static void openCardExpiredScreen(Fragment fragment, int cardCount, @Nullable String doctorName) {
        fragment.startActivityForResult(getCardExpiredIntent(fragment.getActivity(), cardCount, doctorName), RequestID.REQ_CARD_EXPIRE);
    }


    private static Intent getCardNotAddedIntent(Activity activity, @Nullable String doctorName) {
        Intent intent = new Intent(activity, ContentActivity.class);
        String description;
        if (UserType.isUserDoctor()) {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.proceed));
            description = activity.getString(R.string.msg_payment_gateway_changed);
        } else if (UserType.isUserAssistant()) {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.ok));
            String name = TextUtils.isEmpty(doctorName) ? activity.getString(R.string.doctor) : doctorName;
            description = activity.getString(R.string.trial_period_expired_ma_sec_1, activity.getString(R.string.organization_name), name);
        } else {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.proceed));
            intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, true);
            intent.putExtra(ArgumentKeys.SKIP_TITLE, activity.getString(R.string.lbl_not_now));
            description = "One of your connected provider requires credit card to book appointment";
        }

        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        return intent;
    }

    private static Intent getCardExpiredIntent(Activity activity, int cardCount, @Nullable String doctorName) {
        boolean doesUserHasMultipleCards = cardCount > 1;
        Intent intent = new Intent(activity, ContentActivity.class);

        String description;
        if (UserType.isUserDoctor()) {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(doesUserHasMultipleCards ? R.string.lbl_manage_cards : R.string.lbl_add_card));
            description = activity.getString(doesUserHasMultipleCards ? R.string.msg_default_card_expired_multiple : R.string.msg_default_card_expired);
        } else {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, activity.getString(R.string.ok));
            String name = TextUtils.isEmpty(doctorName) ? activity.getString(R.string.doctor) : doctorName;
            description = activity.getString(R.string.trial_period_expired_ma_sec_1, activity.getString(R.string.organization_name), name);
        }

        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);
        intent.putExtra(ArgumentKeys.DESCRIPTION, description);
        return intent;
    }
}
