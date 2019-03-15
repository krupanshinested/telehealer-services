package com.thealer.telehealer.apilayer.models.createuser;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import okhttp3.MultipartBody;

/**
 * Created by Aswin on 17,October,2018
 */
public class CreateUserApiViewModel extends BaseApiViewModel {

    public CreateUserApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void createPatient(CreateUserRequestModel createUserRequestModel) {

        MultipartBody.Part user_avatar = getMultipartFile("user_avatar", createUserRequestModel.getUser_avatar_path());
        MultipartBody.Part insurance_front = getMultipartFile("insurance_front", createUserRequestModel.getInsurance_front_path());
        MultipartBody.Part insurance_back = getMultipartFile("insurance_back", createUserRequestModel.getInsurance_back_path());
        MultipartBody.Part secondary_insurance_front = getMultipartFile("secondary_insurance_front", createUserRequestModel.getSecondary_insurance_front_path());
        MultipartBody.Part secondary_insurance_back = getMultipartFile("secondary_insurance_back", createUserRequestModel.getSecondary_insurance_back_path());

        getPublicApiService().createPatient(createUserRequestModel.getUser_data(),
                user_avatar,
                insurance_front,
                insurance_back,
                secondary_insurance_front,
                secondary_insurance_back)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                    }
                });
    }

    public void createMedicalAssistant(CreateUserRequestModel createUserRequestModel) {
        MultipartBody.Part user_avatar = getMultipartFile("user_avatar", createUserRequestModel.getUser_avatar_path());
        MultipartBody.Part certification = getMultipartFile("certification", createUserRequestModel.getCertification_path());

        getPublicApiService().createMedicalAssistant(createUserRequestModel.getUser_data(),
                createUserRequestModel.getUser_detail().getData(),
                user_avatar,
                certification)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                    }
                });
    }

    public void createDoctor(CreateUserRequestModel createUserRequestModel) {
        MultipartBody.Part user_avatar = getMultipartFile("user_avatar", createUserRequestModel.getUser_avatar_path());
        MultipartBody.Part license = getMultipartFile("driver_license", createUserRequestModel.getDoctor_driving_license_path());
        MultipartBody.Part certification = getMultipartFile("diploma_certificate", createUserRequestModel.getDoctor_certificate_path());
        MultipartBody.Part signature = null;

        if (createUserRequestModel.getDoctor_signature_path() != null && !createUserRequestModel.getDoctor_signature_path().isEmpty())
            signature = getMultipartFile("signature", createUserRequestModel.getDoctor_signature_path());

        getPublicApiService().createDoctor(createUserRequestModel.getUser_data(),
                createUserRequestModel.getUser_detail().getData(),
                user_avatar,
                certification,
                license,
                signature)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                    }
                });
    }

    public void resetPassword(ResetPasswordRequestModel resetPasswordRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getPublicApiService().resetPassword(resetPasswordRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
