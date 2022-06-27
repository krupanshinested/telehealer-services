package com.thealer.telehealer.apilayer.models.subscription;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

/**
 * Created by Nimesh Patel
 * Created Date: 07,September,2021
 **/
public class SubscriptionViewModel extends BaseApiViewModel {

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchSubscriptionPlanList() {
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

    public void purchaseSubscriptionPlan(String planId, String billingCycle) {
        HashMap<String, String> param = new HashMap<>();
        param.put(ArgumentKeys.PlanID, planId);
        param.put(ArgumentKeys.BillingCycle, billingCycle);

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().purchasePlan(param)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel model) {
                                    getBaseApiResponseModelMutableLiveData().setValue(model);
                                }
                            });
                }
            }
        });
    }

    public void changeSubscriptionPlan(String planId) {
        HashMap<String, String> param = new HashMap<>();
        param.put(ArgumentKeys.PlanID, planId);

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().changePlan(param)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel model) {
                                    getBaseApiResponseModelMutableLiveData().setValue(model);
                                }
                            });
                }
            }
        });
    }

    public void unSubscriptionPlan(String reason) {
        HashMap<String, String> param = new HashMap<>();
        param.put(ArgumentKeys.Reason, reason);
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().unSubscribePlan(param)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel model) {
                                    getBaseApiResponseModelMutableLiveData().setValue(model);
                                }
                            });
                }
            }
        });
    }
}
