package com.thealer.telehealer.views.base;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.TokenFetchModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.common.ContentActivity;

import java.util.logging.Logger;

import static android.app.Activity.RESULT_OK;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static com.thealer.telehealer.common.ArgumentKeys.CALL_INITIATE_MODEL;

/**
 * Created by rsekar on 1/4/19.
 */

public class CallPlacingFragment extends BaseFragment {

    public static final int RequestID = 230;

    @Nullable
    private CallInitiateModel callInitiateModel;

    public void openCallIfPossible(CallInitiateModel callInitiateModel) {
        if (UserType.isUserDoctor()) {
            fetchSessionId(callInitiateModel);
        } else if (UserType.isUserAssistant()) {
            fetchSessionId(callInitiateModel);
        }
    }

    private void fetchSessionId(CallInitiateModel callInitiateModel) {
        this.callInitiateModel = callInitiateModel;

        CallInitiateViewModel callInitiateViewModel = new CallInitiateViewModel(getActivity().getApplication());
        callInitiateViewModel.getTokenForSession("false",callInitiateModel.getDoctorGuid());

        callInitiateViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null && baseApiResponseModel instanceof TokenFetchModel) {

                    TokenFetchModel tokenFetchModel = (TokenFetchModel) baseApiResponseModel;
                    callInitiateModel.update(tokenFetchModel);

                    if (callInitiateModel.isForVideoCall()) {
                        openVideoCall();
                    } else {
                        openAudioCall();
                    }

                } else {
                    Log.d("CallPlacingFragment","failed to cast TokenFetchModel");
                    if (baseApiResponseModel != null)
                    Log.d("CallPlacingFragment",baseApiResponseModel.toString());
                }

            }
        });

        callInitiateViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {

                if (errorModel != null && errorModel.getStatusCode() == 400) {

                    openTrialContentScreen(UserType.isUserDoctor(),callInitiateModel.getDoctorName());

                } else {
                    String errorMessage = errorModel != null ? errorModel.getMessage() : getString(R.string.something_went_wrong_try_again);
                    showAlertDialog(getContext(),getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },null);
                }
            }
        });

    }

    private void openAudioCall() {
        if (PermissionChecker.with(getActivity().getApplication()).checkPermission(PermissionConstants.PERMISSION_MICROPHONE)) {
            if (TokBox.shared.isActivityPresent() || TokBox.shared.isActiveCallPreset()) {
                return;
            }

            Intent intent = new Intent(application, CallActivity.class);
            intent.putExtra(CALL_INITIATE_MODEL,callInitiateModel);
            application.startActivity(intent);

            didOpenCallKit();
            this.callInitiateModel = null;
        }
    }

    private void openVideoCall() {
        if (PermissionChecker.with(getActivity().getApplication()).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            if (TokBox.shared.isActivityPresent() || TokBox.shared.isActiveCallPreset()) {
                return;
            }

            Intent intent = new Intent(application, CallActivity.class);
            intent.putExtra(CALL_INITIATE_MODEL,callInitiateModel);
            application.startActivity(intent);

            didOpenCallKit();
            this.callInitiateModel = null;
        }
    }

    private void openTrialContentScreen(Boolean forDoctor,String doctorName) {
        didOpenTrialScreen();

        Intent intent = new Intent(getActivity(), ContentActivity.class);

        if  (forDoctor) {
            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE,getString(R.string.proceed));
            intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION,true);

            String description = getString(R.string.trial_period_expired_doc_sec_1);
            description += "<a>https://telehealer.com/product/doctors/#pricing</a>";
            description += getString(R.string.trial_period_expired_doc_sec_2);

            intent.putExtra(ArgumentKeys.DESCRIPTION,description);

        } else {

            intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE,getString(R.string.ok));
            intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION,false);

            String name = TextUtils.isEmpty(doctorName) ? getString(R.string.doctor) : doctorName;
            String description = getString(R.string.trial_period_expired_ma_sec_1,name);

            intent.putExtra(ArgumentKeys.DESCRIPTION,description);
        }

        intent.putExtra(ArgumentKeys.RESOURCE_ICON,R.drawable.app_icon);
        intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED,false);
        intent.putExtra(ArgumentKeys.TITLE,getString(R.string.payment_information_required));
        this.callInitiateModel = null;

        startActivityForResult(intent, CallPlacingFragment.RequestID);
    }



    void didOpenCallKit() {

    }

    void didOpenTrialScreen() {

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode) {
            case PermissionConstants.PERMISSION_CAM_MIC:
            case PermissionConstants.PERMISSION_MICROPHONE:
                if (resultCode == RESULT_OK && callInitiateModel != null) {
                   if (callInitiateModel.isForVideoCall()) {
                       openVideoCall();
                   } else {
                       openAudioCall();
                   }
                }
                break;
            case CallPlacingFragment.RequestID:
                if (requestCode == RESULT_OK) {


                } else  {


                }
                break;
        }
    }

}
