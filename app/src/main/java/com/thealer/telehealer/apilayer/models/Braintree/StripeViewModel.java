package com.thealer.telehealer.apilayer.models.Braintree;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.BillingAddressFields;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.stripe.OnSetupIntentResp;
import com.thealer.telehealer.stripe.SetUpIntentResp;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by rsekar on 1/22/19.
 */

public class StripeViewModel extends BaseApiViewModel {

    public StripeViewModel(@NonNull Application application) {
        super(application);
    }

    private String paymentMethodId;

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

    public void makeDefaultCard(String paymentMethodId) {
        getSetupIntent(paymentMethodId);

    }

    public void getSetupIntent(String paymentMethodId) {
        fetchToken(status -> {
            if (status) {
                getAuthApiService().getSetupIntent()
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                            @Override
                            public void onSuccess(BaseApiResponseModel model) {
                                baseApiResponseModelMutableLiveData.setValue(model);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("paymentMethodId", paymentMethodId);
                                getAuthApiService().makeDefault(map)
                                        .compose(applySchedulers())
                                        .subscribe(responseBody -> {
                                            setPaymentMethodId(paymentMethodId);
                                            BaseApiResponseModel responseModel = new BaseApiResponseModel();
                                            responseModel.setMessage("SET_DEFAULT");
                                            responseModel.setSuccess(true);
                                            baseApiResponseModelMutableLiveData.setValue(responseModel);

                                        });
                            }
                        });

            }
        });
    }


    public void getDefaultCard() {
        fetchToken(status -> {
            if (status) {
                getAuthApiService().getDefaultCard()
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<DefaultCardResp>(Constants.SHOW_PROGRESS) {
                            @Override
                            public void onSuccess(DefaultCardResp model) {
                                setPaymentMethodId(model.getCardDetail().getCardId());
                                baseApiResponseModelMutableLiveData.setValue(model);
                            }
                        });

            }
        });
    }

    public void openPaymentScreen(Activity activity) {
        new PaymentMethodsActivityStarter(activity).startForResult(new PaymentMethodsActivityStarter.Args(getPaymentMethodId(), 0, false, Arrays.asList(PaymentMethod.Type.Card), null, null, BillingAddressFields.None, false, false, true));
    }


    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
