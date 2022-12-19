package com.thealer.telehealer.apilayer.models.getUsers;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailFetcher;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aswin on 26,November,2018
 */
public class GetUsersApiViewModel extends BaseApiViewModel {
    public GetUsersApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserByGuid(Set<String> guidList) {
        List<String> stringList = new ArrayList<>(guidList);

        StringBuilder guid = new StringBuilder();
        for (int i = 0; i < guidList.size(); i++) {
            guid.append(stringList.get(i));
            if (i != guidList.size() - 1) {
                guid.append(",");
            }
        }

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUsersByGuid(guid.toString())
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<CommonUserApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(ArrayList<CommonUserApiResponseModel> o) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(o);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);

                                }
                            });
                }
            }
        });
    }


    public void getUserDetail(String userGuid, @Nullable UserDetailFetcher userDetailFetcher) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserDetail(userGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<CommonUserApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(CommonUserApiResponseModel commonUserApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(commonUserApiResponseModel);

                                    if (userDetailFetcher != null) {
                                        userDetailFetcher.didFetchedDetails(commonUserApiResponseModel);
                                    }

                                }
                            });
                }
            }
        });
    }

    public void getUserDetailforMA(String userGuid, Map<String, Object> body, @Nullable UserDetailFetcher userDetailFetcher) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserDetail(userGuid,body)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<CommonUserApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(CommonUserApiResponseModel commonUserApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(commonUserApiResponseModel);

                                    if (userDetailFetcher != null) {
                                        userDetailFetcher.didFetchedDetails(commonUserApiResponseModel);
                                    }

                                }
                            });
                }
            }
        });
    }


}
