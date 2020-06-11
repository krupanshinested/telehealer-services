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

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeClientToken;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeCustomer;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeViewModel;
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
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.call.CallActivity;

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

    private BrainTreeViewModel brainTreeViewModel = null;

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

        openTokViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {

                String errorMessage = null;
                if (errorModel != null) {
                    Type type = new TypeToken<HashMap<String, Object>>() {
                    }.getType();
                    HashMap<String, Object> errorObject = new Gson().fromJson(errorModel.getResponse(), type);


                    if (errorObject.get("is_cc_captured") != null) {
                        Boolean is_cc_captured = (Boolean) errorObject.get("is_cc_captured");

                        if (is_cc_captured != null && !is_cc_captured) {
                            openTrialContentScreen(UserType.isUserDoctor(), callRequest.getDoctorName());

                            if (UserType.isUserDoctor()) {
                                EventRecorder.recordTrialExpired("TRIAL_EXPIRED");
                            }

                        } else {
                            errorMessage = errorModel.getMessage();
                        }
                    } else {
                        errorMessage = errorModel.getMessage();
                    }
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
            }
        });


        String callType = callRequest.getCallType();

        if (callType.equals(OpenTokConstants.oneWay)) {
            callType = OpenTokConstants.video;
        }

        if (callRequest.isCallForDirectWaitingRoom()) {
            openTokViewModel.getTokenForSession(callRequest.getSessionId(),null);
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

    private void addBrainTreeObserver() {
        brainTreeViewModel = new ViewModelProvider(CallPlacingActivity.this).get(BrainTreeViewModel.class);

        attachObserver(brainTreeViewModel);

        brainTreeViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof BrainTreeClientToken) {
                        DropInRequest dropInRequest = new DropInRequest().clientToken(((BrainTreeClientToken) baseApiResponseModel).getClient_token());
                        startActivityForResult(dropInRequest.getIntent(CallPlacingActivity.this), CallPlacingActivity.BRAIN_TREE_REQUEST);
                    } else if (baseApiResponseModel instanceof BrainTreeCustomer) {
                        BrainTreeCustomer customer = (BrainTreeCustomer) baseApiResponseModel;

                        HashMap<String, String> param = new HashMap<>();
                        if (customer.getPaymentMethods() != null) {
                            param.put("customerId", UserDetailPreferenceManager.getUser_guid());
                            param.put("verifyCard", "true");
                        }

                        brainTreeViewModel.getBrainTreeClientToken(param);
                    }
                }
            }
        });


        brainTreeViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null && errorModel.getName() != null) {
                    if (errorModel.getName().equals("notFoundError")) {
                        brainTreeViewModel.getBrainTreeClientToken(new HashMap<>());
                    }
                }
            }
        });
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
            description += " <a href=\""+url+"\">"+url+"</a> ";
            description += getString(R.string.trial_period_expired_doc_sec_2);

            intent.putExtra(ArgumentKeys.DESCRIPTION, description);
            requestId = CallPlacingActivity.DOCTOR_PAYMENT_REQUEST;

            if (brainTreeViewModel == null) {
                addBrainTreeObserver();
            }

        } else {

            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok));
            intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);

            String name = TextUtils.isEmpty(doctorName) ? getString(R.string.doctor) : doctorName;
            String description = getString(R.string.trial_period_expired_ma_sec_1, getString(R.string.organization_name),name);

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
                if (resultCode == RESULT_OK) {
                    brainTreeViewModel.getBrainTreeCustomer();
                } else {
                    finish();
                }
                break;
            case CallPlacingActivity.BRAIN_TREE_REQUEST:
                if (resultCode == RESULT_OK) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                    HashMap<String, Object> param = new HashMap<>();
                    if (result.getPaymentMethodNonce() != null) {
                        param.put("payment_method_nonce", result.getPaymentMethodNonce().getNonce());
                    }
                    param.put("amount", "0");
                    param.put("savePayment", true);

                    brainTreeViewModel.checkOutBrainTree(param);
                    finish();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d("CallPlacingFragment", "Braintree Cancelled");
                    finish();
                } else {
                    Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                    Log.d("CallPlacingFragment", error.getLocalizedMessage());
                    finish();
                }

                break;
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
