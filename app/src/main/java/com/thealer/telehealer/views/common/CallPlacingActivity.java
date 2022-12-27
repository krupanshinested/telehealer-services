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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.Braintree.StripeViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
                dismissProgressDialog();
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

                String json = errorModel.getResponse();
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (!jsonObject.has("is_cc_captured") || AppPaymentCardUtils.hasValidPaymentCard(errorModel)) {
                            errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                        } else {
                            AppPaymentCardUtils.handleCardCasesFromErrorModel(this, errorModel, callRequest.getDoctorName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    errorMessage = getString(R.string.something_went_wrong_try_again);
                }
            } else {
                errorMessage = getString(R.string.something_went_wrong_try_again);
            }

            if (errorMessage != null) {
                Utils.showAlertDialog(CallPlacingActivity.this, getString(R.string.app_name), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dismissProgressDialog();
                        finish();
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
            String currentUserGuid="";
            if(UserType.isUserAssistant()){
                currentUserGuid=callRequest.getOtherUserGuid()!=null?callRequest.getOtherUserGuid():"";
            }
            openTokViewModel.postaVoipCall(currentUserGuid,callRequest.getDoctorGuid(), callRequest.getOtherUserGuid(), callRequest.getScheduleId(), callType);
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

    void didOpenCallKit() {

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
            case RequestID.REQ_CARD_EXPIRE:
            case RequestID.REQ_CARD_INFO: {
                finish();
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
