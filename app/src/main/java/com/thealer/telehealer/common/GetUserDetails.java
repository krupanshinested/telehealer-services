package com.thealer.telehealer.common;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
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
    private AssociationApiViewModel associationApiViewModel;
    private AssociationApiResponseModel associationApiResponseModel;

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
        getUserDetails.getUsersApiViewModel = new ViewModelProvider(fragmentActivity).get(GetUsersApiViewModel.class);
        getUserDetails.getUsersApiViewModel.baseApiArrayListMutableLiveData.observe(fragmentActivity, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    onDataReceived(baseApiResponseModels);
                }
            }
        });

        getUserDetails.associationApiViewModel = new ViewModelProvider(fragmentActivity).get(AssociationApiViewModel.class);
        getUserDetails.associationApiViewModel.baseApiArrayListMutableLiveData.observe(fragmentActivity, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    onDataReceived(baseApiResponseModels);
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

    public static void onDataReceived(ArrayList<BaseApiResponseModel> baseApiResponseModels) {

        ArrayList<CommonUserApiResponseModel> commonUserApiResponseModelArrayList = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;

        HashMap<String, CommonUserApiResponseModel> responseModelHashMap = new HashMap<>();
        for (CommonUserApiResponseModel model :
                commonUserApiResponseModelArrayList) {
            responseModelHashMap.put(model.getUser_guid(), model);
        }

        getUserDetails.hashMapMutableLiveData.setValue(responseModelHashMap);
    }

    public GetUserDetails getAssociationDetail(Set<String> guidList, String doctorGuid) {
        associationApiViewModel.getAssociationUserDetails(guidList, doctorGuid, true);
        return getUserDetails;
    }
}
