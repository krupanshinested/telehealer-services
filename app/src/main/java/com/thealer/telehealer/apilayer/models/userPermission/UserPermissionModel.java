package com.thealer.telehealer.apilayer.models.userPermission;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

/**
 * Created by Nimesh Patel
 * Created Date: 07,April,2021
 **/
public class UserPermissionModel extends BaseApiViewModel {
    public UserPermissionModel(@NonNull Application application) {
        super(application);
    }

    public void getUserPermission() {

        String temp_json_string = "{\"success\":true,\"data\":[{\"permission_name\":\"call\",\"permission_state\":true,\"sub_permission\":[{\"permission_name\":\"call sub permission 1\",\"permission_state\":true},{\"permission_name\":\"call sub_permission 1\",\"permission_state\":true}]},{\"permission_name\":\"permission 2\",\"permission_state\":false,\"sub_permission\":[]},{\"permission_name\":\"permission 3\",\"permission_state\":true,\"sub_permission\":[{\"permission_name\":\"call sub permission 1\",\"permission_state\":false},{\"permission_name\":\"call sub_permission 1\",\"permission_state\":true}]}]}";

        UserPermissionApiResponseModel userPermissionApiResponseModel = new Gson().fromJson(temp_json_string, UserPermissionApiResponseModel.class);
        baseApiResponseModelMutableLiveData.setValue(userPermissionApiResponseModel);

    }
}
