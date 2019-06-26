package com.thealer.telehealer.apilayer.models.Braintree;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

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

    public void getBrainTreeClientToken(HashMap<String,String > param) {
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


    public void checkOutBrainTree(HashMap<String,Object > param) {
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
}
