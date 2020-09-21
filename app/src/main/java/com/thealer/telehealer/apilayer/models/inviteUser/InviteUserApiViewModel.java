package com.thealer.telehealer.apilayer.models.inviteUser;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteUserApiViewModel extends BaseApiViewModel {
    public InviteUserApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void inviteUserByEmailPhone(String doctor_user_guid, InviteByEmailPhoneRequestModel emailPhoneRequestModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().inviteUserByEmailPhone(doctor_user_guid, emailPhoneRequestModel)
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                            @Override
                            public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                            }
                        });
            }
        });
    }

    public void inviteUserByDemographic(InviteByDemographicRequestModel demographicRequestModel, String doctor_guid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().inviteUserByDemographic(demographicRequestModel, doctor_guid)
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                            @Override
                            public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                            }
                        });
            }
        });
    }
}
