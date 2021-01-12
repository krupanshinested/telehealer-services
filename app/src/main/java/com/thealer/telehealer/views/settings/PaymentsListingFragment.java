package com.thealer.telehealer.views.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.BillingAddressFields;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Braintree.StripeViewModel;
import com.thealer.telehealer.apilayer.models.Payments.Transaction;
import com.thealer.telehealer.apilayer.models.Payments.TransactionApiViewModel;
import com.thealer.telehealer.apilayer.models.Payments.TransactionResponse;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.stripe.AppEphemeralKeyProvider;
import com.thealer.telehealer.stripe.SetUpIntentResp;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.settings.Adapters.PaymentAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.thealer.telehealer.TeleHealerApplication.application;


/**
 * Created by rsekar on 1/22/19.
 */

public class PaymentsListingFragment extends BaseFragment implements DoCurrentTransactionInterface {

    private CustomRecyclerView recyclerContainer;

    private OnActionCompleteInterface onActionCompleteInterface;

    private TransactionApiViewModel transactionApiViewModel;

    Stripe stripe = new Stripe(application, BuildConfig.STRIPE_KEY);

    private StripeViewModel stripeViewModel;


    private PaymentAdapter paymentAdapter;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;
    private LinearLayout addCardButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_listing, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        addCardButton = (LinearLayout) view.findViewById(R.id.btnAddCard);
        recyclerContainer = view.findViewById(R.id.recyclerContainer);
        recyclerContainer.setScrollable(false);
        recyclerContainer.setEmptyState(EmptyViewConstants.EMPTY_PAYMENTS);
        recyclerContainer.hideEmptyState();

        toolbarTitle.setText(getString(R.string.lbl_payment));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnCloseActionInterface) getActivity()).onClose(false);
            }
        });
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCurrentTransaction();
            }
        });

        paymentAdapter = new PaymentAdapter(getActivity(), new ArrayList<>());
        recyclerContainer.getRecyclerView().setAdapter(paymentAdapter);

        paymentAdapter.clickListener = new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Transaction transaction = paymentAdapter.getTransactions().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.TRANSACTION, transaction);
                onActionCompleteInterface.onCompletionResult(RequestID.TRANSACTION_DETAIL, true, bundle);
            }
        };

        recyclerContainer.getSwipeLayout().setEnabled(false);
        if (getActivity().getIntent().getIntExtra(ArgumentKeys.VIEW_TYPE, 0) == ArgumentKeys.PAYMENT_INFO) {
            addCardButton.performClick();
        } else
            transactionApiViewModel.getTransactions();
    }

    private void addObserver() {
        transactionApiViewModel = new ViewModelProvider(this).get(TransactionApiViewModel.class);

        transactionApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof TransactionResponse) {
                        TransactionResponse transactionResponse = (TransactionResponse) baseApiResponseModel;
                        ArrayList<Transaction> transactions = transactionResponse.mergeTransactions();

                        if (transactions.size() == 0) {
                            loadEmptyView();
                        }

                        paymentAdapter.update(transactions);
                    } else {
                        loadEmptyView();
                    }
                } else {
                    loadEmptyView();
                }

            }
        });

        transactionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null)
                    loadEmptyView();
            }
        });


        stripeViewModel = new ViewModelProvider(this).get(StripeViewModel.class);

        stripeViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof SetUpIntentResp) {
                    String clientSecret = ((SetUpIntentResp) baseApiResponseModel).getClientSecret();
                    if (clientSecret != null && getActivity() != null)
                        stripe.confirmSetupIntent(getActivity(), ConfirmSetupIntentParams.create(stripeViewModel.getPaymentMethodId(), clientSecret));
                } else if ("SET_DEFAULT".equals(baseApiResponseModel.getMessage())) {
                    if (getActivity().getIntent().getIntExtra(ArgumentKeys.VIEW_TYPE, 0) == ArgumentKeys.PAYMENT_INFO) {
                        if (getActivity() instanceof ProfileSettingsActivity) {
                            getActivity().finish();
                        } else {
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                            getActivity().finishAffinity();
                        }
                    }
                }
            }
        });

        stripeViewModel.getDefaultCard();


        CustomerSession.initCustomerSession(getContext(), new AppEphemeralKeyProvider(stripeViewModel.getAuthApiService()));

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(transactionApiViewModel);
            ((BaseActivity) getActivity()).attachObserver(stripeViewModel);
        }
    }

    private void loadEmptyView() {
        recyclerContainer.setEmptyState(EmptyViewConstants.EMPTY_PAYMENTS);
        recyclerContainer.showEmptyState();
    }

    @Override
    public void doCurrentTransaction() {
        stripeViewModel.openPaymentScreen(getActivity());
//        onActionCompleteInterface.onCompletionResult(RequestID.CARD_INFORMATION_VIEW, true, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                if (getActivity().getIntent().getIntExtra(ArgumentKeys.VIEW_TYPE, 0) == ArgumentKeys.PAYMENT_INFO)
                    if (getActivity() instanceof ProfileSettingsActivity) {
                        getActivity().finish();
                    } else {
                        getActivity().finishAffinity();
                    }
                return;
            }
            PaymentMethodsActivityStarter.Result result = PaymentMethodsActivityStarter.Result.fromIntent(data);
            if (result != null && result.paymentMethod != null) {
                if (result.paymentMethod.id.equals(stripeViewModel.getPaymentMethodId())) {
                    if (getActivity().getIntent().getIntExtra(ArgumentKeys.VIEW_TYPE, 0) == ArgumentKeys.PAYMENT_INFO) {
                        if (getActivity() instanceof ProfileSettingsActivity) {
                            getActivity().finish();
                        } else {
                            getActivity().finishAffinity();
                        }
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
            }
        }
    }

    private void setDefaultMethod(String paymentMethodId) {
        stripeViewModel.makeDefaultCard(paymentMethodId);
    }
}
