package com.thealer.telehealer.views.signup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileApiResponseModel;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseFragment;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;

public class SignupBaseFragment extends BaseFragment {

    private UpdateProfileModel updateProfileModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private WhoAmIApiResponseModel whoAmIApiResponseModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                android.util.Log.e(TAG, "whoAmIApiViewModel response");
                if (baseApiResponseModel != null){
                    whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                    sendSuccessViewBroadCast(getContext(), true, getString(R.string.success), getString(R.string.registration_success));
                }
            }
        });
        whoAmIApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                if (errorModel.geterrorCode() == null){
                    showToast(errorModel.getMessage());
                }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                    showToast(errorModel.getMessage());
                }
            }
        });
        updateProfileModel = new ViewModelProvider(this).get(UpdateProfileModel.class);
        updateProfileModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                android.util.Log.e(TAG, "updateProfileModel response");
                if (baseApiResponseModel != null){
                    UpdateProfileApiResponseModel updateProfileApiResponseModel = (UpdateProfileApiResponseModel) baseApiResponseModel;
                    appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, updateProfileApiResponseModel.getData().getToken());
                    appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, updateProfileApiResponseModel.getData().getRefresh_token());
                    appPreference.setString(PreferenceConstants.USER_NAME, updateProfileApiResponseModel.getData().getName());
                    appPreference.setBoolean(PreferenceConstants.IS_USER_ACTIVATED, true);

                    whoAmIApiViewModel.checkWhoAmI();
                }
            }
        });

        updateProfileModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                showToast(errorModel.getMessage());
                if (errorModel.geterrorCode() == null){
                    sendSuccessViewBroadCast(getContext(), false, getString(R.string.failure), getString(R.string.registration_error));
                }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                    sendSuccessViewBroadCast(getContext(), false, getString(R.string.failure), getString(R.string.registration_error));
                }
            }
        });
    }

    public void postMADetail(CreateUserRequestModel createUserRequestModel){
        showSuccessView();
        updateProfileModel.updateMedicalAssistant(createUserRequestModel, true);
    }

    public void postDoctorDetail(CreateUserRequestModel createUserRequestModel){
        showSuccessView();
        updateProfileModel.updateDoctor(createUserRequestModel, true);
    }

    public void postPatientDetail(CreateUserRequestModel createUserRequestModel){
        showSuccessView();
        application.isFromRegistration = true;
        updateProfileModel.updatePatient(createUserRequestModel, true);
    }

    public void showSuccessView(){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.registration_is_on_process));

        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, bundle);
    }

}
