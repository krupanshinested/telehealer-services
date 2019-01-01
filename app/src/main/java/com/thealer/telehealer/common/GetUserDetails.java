package com.thealer.telehealer.common;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aswin on 26,November,2018
 */
public class GetUserDetails {
    private static GetUserDetails getUserDetails;
    private GetUsersApiViewModel getUsersApiViewModel;
    public MutableLiveData<HashMap<String, CommonUserApiResponseModel>> hashMapMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<HashMap<String, CommonUserApiResponseModel>> getHashMapMutableLiveData() {
        return hashMapMutableLiveData;
    }

    public void setHashMapMutableLiveData(MutableLiveData<HashMap<String, CommonUserApiResponseModel>> hashMapMutableLiveData) {
        this.hashMapMutableLiveData = hashMapMutableLiveData;
    }

    public static GetUserDetails getInstance(FragmentActivity fragmentActivity) {

        if (getUserDetails == null) {
            getUserDetails = new GetUserDetails();
        }
        getUserDetails.getUsersApiViewModel = ViewModelProviders.of(fragmentActivity).get(GetUsersApiViewModel.class);
        getUserDetails.getUsersApiViewModel.baseApiArrayListMutableLiveData.observe(fragmentActivity, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {

                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModelArrayList = new ArrayList<>();

                    commonUserApiResponseModelArrayList = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;

                    onDataReceived(commonUserApiResponseModelArrayList);
                }
            }
        });
        return getUserDetails;
    }

    public GetUserDetails getDetails(Set<String> guidList) {
        if (getUsersApiViewModel != null && guidList.size() > 0) {
            Set<String> guids = new HashSet<>();
            if (getHashMapMutableLiveData().getValue() != null) {
                for (String guid : guidList) {
                    if (!getHashMapMutableLiveData().getValue().containsKey(guid)) {
                        guids.add(guid);
                    }
                }
            } else {
                guids.addAll(guidList);
            }
            
            if (guids.size() > 0) {
                getUsersApiViewModel.getUserByGuid(guids);
            }
        }
        return getUserDetails;
    }

    public static void onDataReceived(ArrayList<CommonUserApiResponseModel> commonUserApiResponseModelArrayList) {
        HashMap<String, CommonUserApiResponseModel> responseModelHashMap = new HashMap<>();
        for (CommonUserApiResponseModel model :
                commonUserApiResponseModelArrayList) {
            responseModelHashMap.put(model.getUser_guid(), model);
        }

        getUserDetails.hashMapMutableLiveData.setValue(responseModelHashMap);
    }
}
