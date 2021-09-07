package com.thealer.telehealer.apilayer.models.subscription;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Nimesh Patel
 * Created Date: 07,September,2021
 **/
public class SubscriptionViewModel  extends BaseApiViewModel {

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
    }

    public  void fetchSubscriptionPlanList(){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().fetchSubscriptionList()
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<PlanInfoBean>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(PlanInfoBean baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });

                }
            }
        });
    }
}
