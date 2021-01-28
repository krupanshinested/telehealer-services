package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.app.NotificationManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.BillingAddressFields;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Braintree.StripeViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.OpenTok;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppEphemeralKeyProvider;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.stripe.SetUpIntentResp;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.home.HomeActivity;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by rsekar on 1/29/19.
 */

public class CallPlacingActivity extends BaseActivity {
    public static final int DOCTOR_PAYMENT_REQUEST = 230;
    public static final int MA_DOC_PAYMENT_REQUEST = 231;
    public static final int BRAIN_TREE_REQUEST = 233;
    public static final int VideoFeedRequestID = 235;
    public static final int OneWayCallRequestID = 237;

    @Nullable
    private CallRequest callRequest;

    private final OpenTokViewModel openTokViewModel = new OpenTokViewModel(application);

    Stripe stripe = new Stripe(application, BuildConfig.STRIPE_KEY);

    private StripeViewModel stripeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState);

        if (getIntent().getSerializableExtra(ArgumentKeys.CALL_INITIATE_MODEL) != null) {
            CallRequest callRequest = (CallRequest) getIntent().getSerializableExtra(ArgumentKeys.CALL_INITIATE_MODEL);
            openCallIfPossible(callRequest);
        }
    }

    public void openCallIfPossible(CallRequest callRequest) {
        if (callRequest.getScheduleId() != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.parseInt(callRequest.getScheduleId()));
        }

        if (UserType.isUserDoctor()) {
            fetchSessionId(callRequest);
        } else if (UserType.isUserAssistant()) {
            fetchSessionId(callRequest);
        }
    }

    private void fetchSessionId(CallRequest callRequest) {
        this.callRequest = callRequest;

        attachObserver(openTokViewModel);

        openTokViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null && baseApiResponseModel instanceof CallSettings) {

                    CallSettings tokenFetchModel = (CallSettings) baseApiResponseModel;
                    callRequest.update(tokenFetchModel);

                    if (!callRequest.isCallForDirectWaitingRoom() && ((!appPreference.getBoolean(PreferenceConstants.PATIENT_VIDEO_FEED) && callRequest.getCallType().equals(OpenTokConstants.video))
                            || (!appPreference.getBoolean(PreferenceConstants.ONE_WAY_CALL_INFO) && callRequest.getCallType().equals(OpenTokConstants.oneWay)))) {
                        Intent intent = new Intent(CallPlacingActivity.this, ContentActivity.class);
                        intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok));
                        intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);


                        int requestId = 0;
                        if (callRequest.getCallType().equals(OpenTokConstants.video)) {
                            intent.putExtra(ArgumentKeys.TITLE, getString(R.string.enable_patient_video_feed));
                            intent.putExtra(ArgumentKeys.DESCRIPTION, getString(R.string.patient_video_feed_description, getString(R.string.organization_name)));
                            intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.call_kit_education);
                            intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);

                            requestId = CallPlacingActivity.VideoFeedRequestID;

                        } else if (callRequest.getCallType().equals(OpenTokConstants.oneWay)) {
                            intent.putExtra(ArgumentKeys.TITLE, getString(R.string.one_way_call));
                            intent.putExtra(ArgumentKeys.DESCRIPTION, getString(R.string.one_way_call_description));
                            intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.one_way_call);
                            intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, true);
                            intent.putExtra(ArgumentKeys.SKIP_TITLE, getString(R.string.CANCEL));

                            requestId = CallPlacingActivity.OneWayCallRequestID;
                        }

                        intent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, true);
                        intent.putExtra(ArgumentKeys.CHECK_BOX_TITLE, getString(R.string.do_not_show_again));
                        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, false);

                        startActivityForResult(intent, requestId);
                    } else {
                        openCall();
                    }

                } else {
                    Log.d("CallPlacingFragment", "failed to cast TokenFetchModel");
                    if (baseApiResponseModel != null)
                        Log.d("CallPlacingFragment", baseApiResponseModel.toString());
                }

            }
        });

        openTokViewModel.getErrorModelLiveData().observe(this, errorModel -> {

            String errorMessage = null;
            if (errorModel != null) {
                Type type = new TypeToken<HashMap<String, Object>>() {
                }.getType();
                HashMap<String, Object> errorObject = new Gson().fromJson(errorModel.getResponse(), type);


                if (errorObject.get("is_cc_captured") != null) {
                    Boolean is_cc_captured = (Boolean) errorObject.get("is_cc_captured");
                    if (is_cc_captured != null) {
                        if (!is_cc_captured) {
                            openTrialContentScreen(UserType.isUserDoctor(), callRequest.getDoctorName());

                            if (UserType.isUserDoctor()) {
                                EventRecorder.recordTrialExpired("TRIAL_EXPIRED");
                            }
                            return;
                        } else {
                            if (errorObject.get("is_default_card_valid") != null) {
                                Boolean is_default_card_valid = (Boolean) errorObject.get("is_default_card_valid");

                                if (is_default_card_valid != null && !is_default_card_valid) {
                                    int counts = 0;
                                    if (errorObject.containsKey("saved_cards_count")) {
                                        if (errorObject.get("saved_cards_count") instanceof Integer) {
                                            counts = (Integer) errorObject.get("saved_cards_count");
                                        } else if (errorObject.get("saved_cards_count") instanceof Double)
                                            counts = ((Double) errorObject.get("saved_cards_count")).intValue();
                                    }
                                    if (UserType.isUserDoctor()) {
                                        initStripeVM();
                                        EventRecorder.recordTrialExpired("TRIAL_EXPIRED");
                                    }
                                    AppPaymentCardUtils.openCardExpiredScreen(CallPlacingActivity.this, counts, callRequest.getDoctorName());
                                    return;
                                }
                            }
                        }
                    }
                }
                errorMessage = errorModel.getMessage();
            } else {
                errorMessage = getString(R.string.something_went_wrong_try_again);
            }

            if (errorMessage != null) {
                Utils.showAlertDialog(CallPlacingActivity.this, getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
            }
        });


        String callType = callRequest.getCallType();

        if (callType.equals(OpenTokConstants.oneWay)) {
            callType = OpenTokConstants.video;
        }

        if (callRequest.isCallForDirectWaitingRoom()) {
            openTokViewModel.getTokenForSession(callRequest.getSessionId(), null);
        } else {
            openTokViewModel.postaVoipCall(callRequest.getDoctorGuid(), callRequest.getOtherUserGuid(), callRequest.getScheduleId(), callType);
        }
    }

    private void openAudioCall(CallRequest callRequest) {
        if (PermissionChecker.with(CallPlacingActivity.this).checkPermission(PermissionConstants.PERMISSION_MICROPHONE)) {
            EventRecorder.recordCallUpdates("TRYING_AUDIO", null);

            OpenTok tokBox = new OpenTok(callRequest);
            CallManager.shared.addCall(tokBox);

            Intent intent = CallActivity.getIntent(application, callRequest);
            application.startActivity(intent);

            didOpenCallKit();
            this.callRequest = null;
            finish();
        }
    }

    private void openVideoCall(CallRequest callRequest) {
        if (PermissionChecker.with(CallPlacingActivity.this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            EventRecorder.recordCallUpdates("TRYING_VIDEO", null);

            OpenTok tokBox = new OpenTok(callRequest);
            CallManager.shared.addCall(tokBox);

            Intent intent = CallActivity.getIntent(application, callRequest);
            application.startActivity(intent);

            didOpenCallKit();
            this.callRequest = null;
            finish();
        }
    }

    private void openWayCall(CallRequest callRequest) {
        if (PermissionChecker.with(CallPlacingActivity.this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            EventRecorder.recordCallUpdates("TRYING_ONE_WAY", null);

            OpenTok tokBox = new OpenTok(callRequest);
            CallManager.shared.addCall(tokBox);

            Intent intent = CallActivity.getIntent(application, callRequest);
            application.startActivity(intent);

            didOpenCallKit();
            this.callRequest = null;
            finish();
        }
    }

    private void openCall() {
        if (CallManager.shared.isActiveCallPresent()) {
            return;
        }

        if (callRequest == null) {
            return;
        }

        switch (callRequest.getCallType()) {
            case OpenTokConstants.audio:
                openAudioCall(callRequest);
                break;
            case OpenTokConstants.video:
                openVideoCall(callRequest);
                break;
            case OpenTokConstants.oneWay:
                openWayCall(callRequest);
                break;
        }
    }

    private void openTrialContentScreen(Boolean forDoctor, String doctorName) {
        didOpenTrialScreen();

        Intent intent = new Intent(CallPlacingActivity.this, ContentActivity.class);
        int requestId;

        if (forDoctor) {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed));
            intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);

            String description = String.format(getString(R.string.trial_period_expired_doc_sec_1), getString(R.string.app_name));
            String url = getString(R.string.pricing_url);
            description += " <a href=\"" + url + "\">" + url + "</a> ";
            description += getString(R.string.trial_period_expired_doc_sec_2);

            intent.putExtra(ArgumentKeys.DESCRIPTION, description);
            requestId = CallPlacingActivity.DOCTOR_PAYMENT_REQUEST;
            initStripeVM();
        } else {

            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok));
            intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);

            String name = TextUtils.isEmpty(doctorName) ? getString(R.string.doctor) : doctorName;
            String description = getString(R.string.trial_period_expired_ma_sec_1, getString(R.string.organization_name), name);

            intent.putExtra(ArgumentKeys.DESCRIPTION, description);
            requestId = CallPlacingActivity.MA_DOC_PAYMENT_REQUEST;
        }

        intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.emptystate_credit_card);
        intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);
        intent.putExtra(ArgumentKeys.TITLE, getString(R.string.payment_information_required));
        intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);
        intent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false);

        this.callRequest = null;

        startActivityForResult(intent, requestId);
    }

    private void initStripeVM() {
        if (stripeViewModel == null) {
            stripeViewModel = new ViewModelProvider(this).get(StripeViewModel.class);
            stripeViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
                @Override
                public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                    if (baseApiResponseModel instanceof SetUpIntentResp) {
                        String clientSecret = ((SetUpIntentResp) baseApiResponseModel).getClientSecret();
                        if (clientSecret != null)
                            stripe.confirmSetupIntent(CallPlacingActivity.this, ConfirmSetupIntentParams.create(stripeViewModel.getPaymentMethodId(), clientSecret));
                    } else if ("SET_DEFAULT".equals(baseApiResponseModel.getMessage())) {

                    }
                }
            });
            stripeViewModel.getDefaultCard();
            CustomerSession.initCustomerSession(this, new AppEphemeralKeyProvider(stripeViewModel.getAuthApiService()));
            attachObserver(stripeViewModel);
        }
    }

    void didOpenCallKit() {

    }

    void didOpenTrialScreen() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CallPlacingFragment", "onActivityResult " + requestCode + "_" + resultCode);
        switch (requestCode) {
            case PermissionConstants.PERMISSION_CAM_MIC:
            case PermissionConstants.PERMISSION_MICROPHONE:
                if (resultCode == RESULT_OK && callRequest != null) {
                    openCall();
                } else {
                    finish();
                }
                break;

            case CallPlacingActivity.DOCTOR_PAYMENT_REQUEST:
            case RequestID.REQ_CARD_EXPIRE: {
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    return;
                }
                stripeViewModel.openPaymentScreen(this);
                break;
            }

            case PaymentMethodsActivityStarter.REQUEST_CODE: {
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    return;
                }

                PaymentMethodsActivityStarter.Result result = PaymentMethodsActivityStarter.Result.fromIntent(data);
                if (result != null && result.paymentMethod != null) {
                    if (result.paymentMethod.id.equals(stripeViewModel.getPaymentMethodId())) {
                        finish();
                        return;
                    }

                    stripeViewModel.makeDefaultCard(result.paymentMethod.id);
                    stripeViewModel.setPaymentMethodId(result.paymentMethod.id);
                    stripe.onSetupResult(requestCode, data, new ApiResultCallback<SetupIntentResult>() {
                        @Override
                        public void onSuccess(@NotNull SetupIntentResult setupIntentResult) {
                            Log.e("setupresult", "" + setupIntentResult.getIntent().getPaymentMethodId());
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                    finish();
                }
                break;
            }

            case CallPlacingActivity.VideoFeedRequestID:

                appPreference.setBoolean(PreferenceConstants.PATIENT_VIDEO_FEED, data.getBooleanExtra(ArgumentKeys.IS_CHECK_BOX_CLICKED, false));

                if (callRequest != null) {
                    openCall();
                } else {
                    finish();
                }
                break;
            case CallPlacingActivity.OneWayCallRequestID:
                if (resultCode == RESULT_OK) {
                    appPreference.setBoolean(PreferenceConstants.ONE_WAY_CALL_INFO, data.getBooleanExtra(ArgumentKeys.IS_CHECK_BOX_CLICKED, false));
                    if (callRequest != null) {
                        openCall();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case MA_DOC_PAYMENT_REQUEST:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //disable back press
    }
}
