package com.thealer.telehealer.apilayer.models.pendingInvites;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.notification.NewNotificationListAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aswin on 04,March,2019
 */
public class PendingInvitesApiViewModel extends BaseApiViewModel {
    public PendingInvitesApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getPendingInvites(int page, boolean isRequestor, boolean isRequestee, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(ApiInterface.TYPE, NewNotificationListAdapter.REQUEST_TYPE_CONNECTION);
                    params.put(ApiInterface.STATUS, NewNotificationListAdapter.REQUEST_STATUS_OPEN);

                    if (isRequestor)
                        params.put("requestor", isRequestor);

                    if (isRequestee)
                        params.put("requestee", isRequestee);

                    getAuthApiService().getPendingInvites(params, true, page, Constants.PAGINATION_SIZE)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getNonRegisteredUserInvites(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getNonRegisteredUserInvites(true, page, Constants.PAGINATION_SIZE, false)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
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
