package com.thealer.telehealer.apilayer.models.userPermission;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nimesh Patel
 * Created Date: 07,April,2021
 **/
public class UserPermissionApiViewModel extends BaseApiViewModel {
    public UserPermissionApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateUserPermission(PermissionRequestModel requestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().updateUserPermission(requestModel)
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

    public void checkSupportStaffPermission(String moduleCode,String doctorGuid){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if(status){
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE,moduleCode);
                    }
                    getAuthApiService().checkSupportStaffPermission(headers,doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModel.setPermissionCode(moduleCode);
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
