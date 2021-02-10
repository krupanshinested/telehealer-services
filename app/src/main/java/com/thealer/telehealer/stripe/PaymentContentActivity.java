package com.thealer.telehealer.stripe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Braintree.DefaultCardResp;
import com.thealer.telehealer.apilayer.models.Braintree.StripeViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.ContentActivity;

import org.jetbrains.annotations.NotNull;

import static com.thealer.telehealer.TeleHealerApplication.application;

public class PaymentContentActivity extends ContentActivity {

    private boolean isHeadLess = false;
    private boolean canGoBack = false;

    private Stripe stripe = new Stripe(application, BuildConfig.STRIPE_KEY);

    private StripeViewModel stripeViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isHeadLess = getIntent().getBooleanExtra(ArgumentKeys.IS_HEAD_LESS, false);
        canGoBack = getIntent().getBooleanExtra(ArgumentKeys.IS_CLOSE_NEEDED, false);
        initStripe();
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initView() {
        if (!isHeadLess) {
            setTheme(R.style.AppTheme);
            super.initView();
        } else {
            performActionClick();
        }
    }

    @Override
    public void performActionClick() {
        stripeViewModel.getDefaultCard();
        CustomerSession.initCustomerSession(this, new AppEphemeralKeyProvider(stripeViewModel.getAuthApiService()));
    }


    private void initStripe() {
        stripeViewModel = new ViewModelProvider(this).get(StripeViewModel.class);

        stripeViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof SetUpIntentResp) {
                    String clientSecret = ((SetUpIntentResp) baseApiResponseModel).getClientSecret();
                    if (clientSecret != null) {
                        stripe.confirmSetupIntent(PaymentContentActivity.this, ConfirmSetupIntentParams.create(stripeViewModel.getPaymentMethodId(), clientSecret));
                    }
                } else if ("SET_DEFAULT".equals(baseApiResponseModel.getMessage())) {
                    setResult(RESULT_OK);
                    finish();
                } else if (baseApiResponseModel instanceof DefaultCardResp) {
                    openPayment();
                }


            }
        });
        stripeViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null)
                    openPayment();
            }
        });

        attachObserver(stripeViewModel);
    }

    private void openPayment() {
        stripeViewModel.openPaymentScreen(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                if (!UserType.isUserDoctor()) {
                    performSkipClick();
                } else {
                    if (isHeadLess)
                        performSkipClick();
                }
                return;
            }
            PaymentMethodsActivityStarter.Result result = PaymentMethodsActivityStarter.Result.fromIntent(data);
            if (result != null && result.paymentMethod != null) {
                if (result.paymentMethod.id.equals(stripeViewModel.getPaymentMethodId())) {
                    if (!UserType.isUserDoctor()) {
                        performSkipClick();
                    } else {
                        if (isHeadLess)
                            performSkipClick();
                    }
                    return;
                }

                setDefaultMethod(result.paymentMethod.id);
                stripeViewModel.setPaymentMethodId(result.paymentMethod.id);
                stripe.onSetupResult(requestCode, data, new ApiResultCallback<SetupIntentResult>() {
                    @Override
                    public void onSuccess(@NotNull SetupIntentResult setupIntentResult) {
                        Log.e("setupresult", "" + setupIntentResult.getIntent().getPaymentMethodId());
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                if (!UserType.isUserDoctor()) {
                    performSkipClick();
                } else {
                    if (isHeadLess)
                        performSkipClick();
                }
            }
        }
    }

    private void setDefaultMethod(String paymentMethodId) {
        stripeViewModel.makeDefaultCard(paymentMethodId);
    }

    @Override
    public void onBackPressed() {
        if (canGoBack)
            super.onBackPressed();
    }
}
