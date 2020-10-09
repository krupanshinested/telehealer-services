package com.thealer.telehealer.apilayer.models.Braintree;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.stripe.OnSetupIntentResp;
import com.thealer.telehealer.stripe.SetUpIntentResp;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.io.IOException;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by rsekar on 1/22/19.
 */

public class BrainTreeViewModel extends BaseApiViewModel {

    public BrainTreeViewModel(@NonNull Application application) {
        super(application);
    }

    public void getBrainTreeCustomer() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getBrainTreeCustomer()
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BrainTreeCustomer>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BrainTreeCustomer customer) {
                                    getBaseApiResponseModelMutableLiveData().setValue(customer);
                                }
                            });
                }
            }
        });
    }

    public void getBrainTreeClientToken(HashMap<String, String> param) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getBrainTreeClientToken(param)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BrainTreeClientToken>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BrainTreeClientToken token) {
                                    getBaseApiResponseModelMutableLiveData().setValue(token);
                                }
                            });
                }
            }
        });
    }


    public void checkOutBrainTree(HashMap<String, Object> param) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().checkOutBrainTree(param)
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

    public void makeDefaultCard(String paymentMethodId, OnSetupIntentResp onSetupIntentResp) {
        HashMap<String, String> map = new HashMap<>();
        map.put("paymentMethodId", paymentMethodId);
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().makeDefault(map)
                            .compose(applySchedulers())
                            .subscribe(responseBody -> {
                                getSetupIntent(onSetupIntentResp);
                            });
                }
            }
        });
    }

    public void getSetupIntent(OnSetupIntentResp onSetupIntentResp) {
        fetchToken(status -> {
            if (status) {
                getAuthApiService().getSetupIntent()
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                            @Override
                            public void onSuccess(BaseApiResponseModel model) {
                                if (model instanceof SetUpIntentResp) {
                                    onSetupIntentResp.onSuccess(((SetUpIntentResp) model).getClientSecret());
                                }
                            }
                        });

            }
        });
    }


}
