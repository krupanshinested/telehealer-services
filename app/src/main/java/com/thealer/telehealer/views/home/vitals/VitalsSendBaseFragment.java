package com.thealer.telehealer.views.home.vitals;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsCreateApiModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsCreateApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;

public class VitalsSendBaseFragment extends BaseFragment {

    private VitalsCreateApiModel vitalsApiViewModel;
    protected boolean needToAssignIHealthListener = false;

    @Nullable
    private String doctor_guid;
    @Nullable
    private String nextPostMeasurementType = null,nextPostValue = null;

    @Nullable
    private CreateVitalApiRequestModel nextPostRequest = null;

    @Nullable
    private VitalsCreateApiResponseModel previousResponse;

    private String captureSuccessMessage = "";

    private String currentPostingMeasurementType = SupportedMeasurementType.bp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vitalsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                Log.v("VitalsSendBaseFragment","vitalsApiViewModel success");
                if(!TextUtils.isEmpty(nextPostMeasurementType) && !TextUtils.isEmpty(nextPostValue)) {
                    previousResponse = (VitalsCreateApiResponseModel) baseApiResponseModel;

                    postVitals(nextPostMeasurementType,nextPostValue);
                    nextPostValue = null;
                    nextPostMeasurementType = null;
                    Log.v("VitalsSendBaseFragment","posting next value");
                } else if (nextPostRequest != null) {
                    previousResponse = (VitalsCreateApiResponseModel) baseApiResponseModel;
                    vitalsApiViewModel.createVital(nextPostRequest, doctor_guid);
                    nextPostRequest = null;
                    Log.v("VitalsSendBaseFragment","posting next request");
                } else if (!isPresentedInsideCallActivity()) {

                    VitalsCreateApiResponseModel vitalsCreateApiResponseModel = (VitalsCreateApiResponseModel) baseApiResponseModel;
                    String message = "";

                    boolean isAbnormal = false;

                    if (UserType.isUserPatient()) {
                        if (previousResponse != null) {

                            if (previousResponse.getDescription() != null) {
                                message += previousResponse.getDescription() + "\n";
                            }

                            if (previousResponse.isAbnormal()) {
                                isAbnormal = true;
                            }
                        }

                        if (vitalsCreateApiResponseModel != null) {
                            if (vitalsCreateApiResponseModel.getDescription() != null) {
                                message += vitalsCreateApiResponseModel.getDescription();
                            }

                            if (vitalsCreateApiResponseModel.isAbnormal()) {
                                isAbnormal = true;
                            }

                        }
                    }

                    if (TextUtils.isEmpty(message)) {
                        message = getString(R.string.vitals_has_been_posted);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, captureSuccessMessage);

                    if (UserType.isUserPatient()) {
                        bundle.putInt(Constants.SUCCESS_VIEW_SUCCESS_IMAGE, SupportedMeasurementType.getDrawable(currentPostingMeasurementType));
                        bundle.putInt(Constants.SUCCESS_VIEW_SUCCESS_IMAGE_TINT,isAbnormal ? R.color.red : R.color.vital_good);
                    }

                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, message);
                    LocalBroadcastManager
                            .getInstance(getActivity())
                            .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                    .putExtras(bundle));

                    Log.v("VitalsSendBaseFragment","not present in call kit");
                } else {
                    Log.v("VitalsSendBaseFragment","present in call kit");
                    didFinishPost();
                }
            }
        });

        vitalsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                Log.v("VitalsSendBaseFragment","vitalsApiViewModel error");
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));

                if (errorModel != null && !TextUtils.isEmpty(errorModel.getMessage())) {
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                } else {
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.something_went_wrong_try_again));
                }

                LocalBroadcastManager
                        .getInstance(getActivity())
                        .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                .putExtras(bundle));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsCreateApiModel.class);

        if (getActivity() instanceof  AttachObserverInterface && needToAddViewModelObserver()) {
            ((AttachObserverInterface) getActivity()).attachObserver(vitalsApiViewModel);
        }
    }

    public boolean needToAddViewModelObserver() {
        return !isPresentedInsideCallActivity();
    }

    public void sendVitals(String measurementType_1,@Nullable String value_1,String measurementType_2,@Nullable String value_2) {
        showSuccessState();
        String measurementType,value;

        if (!TextUtils.isEmpty(value_1)) {
            measurementType = measurementType_1;
            value = value_1;

            nextPostMeasurementType = measurementType_2;
            nextPostValue = value_2;
        } else  {
            measurementType = measurementType_2;
            value = value_2;

            nextPostMeasurementType = null;
            nextPostValue = null;
        }

        currentPostingMeasurementType = measurementType;

        if (!TextUtils.isEmpty(value_1) && !TextUtils.isEmpty(value_2)) {
            captureSuccessMessage = getString(SupportedMeasurementType.getTitle(measurementType_1)) + " " + getString(R.string.and) + " " + getString(SupportedMeasurementType.getTitle(measurementType_2)) + " " + getString(R.string.captured);
        } else if (!TextUtils.isEmpty(value_1)) {
            captureSuccessMessage = getString(SupportedMeasurementType.getTitle(measurementType_1)) + " " + getString(R.string.captured);
        } else  {
            captureSuccessMessage = getString(SupportedMeasurementType.getTitle(measurementType_2)) + " " + getString(R.string.captured);
        }

        postVitals(measurementType,value);
    }

    public Boolean isPresentedInsideCallActivity() {
        return getActivity() instanceof CallActivity;
    }

    private void postVitals(String measurementType,String value) {
        CreateVitalApiRequestModel vitalApiRequestModel = new CreateVitalApiRequestModel();
        vitalApiRequestModel.setType(measurementType);
        vitalApiRequestModel.setMode(VitalsConstant.VITAL_MODE_DEVICE);
        vitalApiRequestModel.setValue(value);

        vitalsApiViewModel.createVital(vitalApiRequestModel, null);
    }

    public void sendVitals(CreateVitalApiRequestModel vitalApiRequestModel_1, CreateVitalApiRequestModel vitalApiRequestModel_2, String doctor_guid) {
        showSuccessState();
        this.doctor_guid = doctor_guid;

        if (vitalApiRequestModel_1 != null) {
            vitalsApiViewModel.createVital(vitalApiRequestModel_1, doctor_guid);
            nextPostRequest = vitalApiRequestModel_2;

            currentPostingMeasurementType = vitalApiRequestModel_1.getType();
        } else  {
            vitalsApiViewModel.createVital(vitalApiRequestModel_2, doctor_guid);
            currentPostingMeasurementType = vitalApiRequestModel_2.getType();
        }

        if (vitalApiRequestModel_1 != null && vitalApiRequestModel_2 != null) {
            captureSuccessMessage = getString(SupportedMeasurementType.getTitle(vitalApiRequestModel_1.getType())) + " " + getString(R.string.and) + " " + getString(SupportedMeasurementType.getTitle(vitalApiRequestModel_2.getType())) + " " + getString(R.string.captured);
        } else if (vitalApiRequestModel_1 != null) {
            captureSuccessMessage = getString(SupportedMeasurementType.getTitle(vitalApiRequestModel_1.getType())) + " " + getString(R.string.captured);
        } else   {
            captureSuccessMessage = getString(SupportedMeasurementType.getTitle(vitalApiRequestModel_2.getType())) + " " + getString(R.string.captured);
        }

    }

    public void didFinishPost() {
        Log.d("VitalSendBaseFragment","didFinishPost");
    }

    private void showSuccessState() {
        if (!isPresentedInsideCallActivity()) {
            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
            successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    didFinishPost();
                }
                break;
        }
    }
}
