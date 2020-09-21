package com.thealer.telehealer.apilayer.models.UpdateProfile;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserDetailBean;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by rsekar on 12/18/18.
 */

public class UpdateProfileModel extends BaseApiViewModel {

    public UpdateProfileModel(@NonNull Application application) {
        super(application);
    }

    public void updateMedicalAssistant(CreateUserRequestModel createUserRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    createUserRequestModel.getUser_data().setRole(null);
                    createUserRequestModel.getUser_data().setUser_avatar(null);
                    createUserRequestModel.getUser_detail().getData().setCertification(null);

                    MultipartBody.Part user_avatar = null;
                    if (createUserRequestModel.getUser_avatar_path() != null) {
                        user_avatar = getMultipartFile("user_avatar", createUserRequestModel.getUser_avatar_path());
                    }

                    MultipartBody.Part certification = null;
                    if (createUserRequestModel.getCertification_path() != null) {
                        certification = getMultipartFile("certification", createUserRequestModel.getCertification_path());
                    }

                    getAuthApiService().updateMedicalAssistant(createUserRequestModel.getUser_data(),
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
            }
        });
    }

    public void updateDoctor(CreateUserRequestModel createUserRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    createUserRequestModel.getUser_data().setRole(null);
                    createUserRequestModel.getUser_data().setUser_avatar(null);
                    createUserRequestModel.getUser_detail().getData().setDriver_license(null);
                    createUserRequestModel.getUser_detail().getData().setDiploma_certificate(null);

                    List<LicensesBean> licensesBeans = createUserRequestModel.getUser_detail().getData().getLicenses();
                    for (LicensesBean licensesBean : licensesBeans) {
                        Date endDate = Utils.getDateFromString(licensesBean.getEnd_date(),"dd MMM, yyyy");
                        if (endDate != null) {
                            licensesBean.setEnd_date(Utils.getStringFromDate(endDate,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                        }
                    }

                    MultipartBody.Part user_avatar = null;
                    if (createUserRequestModel.getUser_avatar_path() != null) {
                        user_avatar = getMultipartFile("user_avatar", createUserRequestModel.getUser_avatar_path());
                    }

                    MultipartBody.Part certification = null;
                    if (createUserRequestModel.getDoctor_certificate_path() != null) {
                        certification = getMultipartFile("diploma_certificate", createUserRequestModel.getDoctor_certificate_path());
                    }

                    MultipartBody.Part license = null;
                    if (createUserRequestModel.getDoctor_driving_license_path() != null) {
                        license = getMultipartFile("driver_license", createUserRequestModel.getDoctor_driving_license_path());
                    }

                    getAuthApiService().updateDoctor(createUserRequestModel.getUser_data(),
                            createUserRequestModel.getUser_detail().getData(),
                            user_avatar,
                            certification,
                            license)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void updatePatient(CreateUserRequestModel createUserRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    createUserRequestModel.getUser_data().setRole(null);
                    createUserRequestModel.getUser_data().setUser_avatar(null);
                    createUserRequestModel.getUser_detail().getData().setInsurance_back(null);
                    createUserRequestModel.getUser_detail().getData().setInsurance_front(null);

                    MultipartBody.Part user_avatar = null;
                    if (createUserRequestModel.getUser_avatar_path() != null) {
                        user_avatar = getMultipartFile("user_avatar", createUserRequestModel.getUser_avatar_path());
                    }

                    MultipartBody.Part insuranceFront = null;
                    if (createUserRequestModel.getInsurance_front_path() != null) {
                        insuranceFront = getMultipartFile("insurance_front", createUserRequestModel.getInsurance_front_path());
                    }

                    MultipartBody.Part insuranceBack = null;
                    if (createUserRequestModel.getInsurance_back_path() != null) {
                        insuranceBack = getMultipartFile("insurance_back", createUserRequestModel.getInsurance_back_path());
                    }

                    getAuthApiService().updatePatient(createUserRequestModel.getUser_data(),
                            user_avatar,
                            insuranceFront,
                            insuranceBack)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }


    public void deleteInsurance() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().deleteInsurance()
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

}