package com.thealer.telehealer.views.settings;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeCard;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeClientToken;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeCustomer;
import com.thealer.telehealer.apilayer.models.Braintree.BrainTreeViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.HashMap;

/**
 * Created by rsekar on 1/22/19.
 */

public class CardInformationFragment extends BaseFragment implements View.OnClickListener {

    private TextView card_tv, expiration_tv;
    private ImageView card_iv;
    private ConstraintLayout main_container;
    private ConstraintLayout recyclerEmptyStateView;
    private ImageView emptyIv;
    private TextView emptyTitleTv;
    private TextView emptyMessageTv;

    private OnViewChangeInterface onViewChangeInterface;
    private BrainTreeViewModel brainTreeViewModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView addIv;

    private boolean isUserActionOccured = false;
    private boolean forCheckout = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_information, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.updateTitle(getString(R.string.card_information));
        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.hideOrShowClose(false);
        onViewChangeInterface.hideOrShowOtherOption(false);
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        addIv = (ImageView) view.findViewById(R.id.close_iv);
        card_tv = view.findViewById(R.id.card_tv);
        expiration_tv = view.findViewById(R.id.expiration_tv);
        card_iv = view.findViewById(R.id.card_iv);
        main_container = view.findViewById(R.id.main_container);
        recyclerEmptyStateView = (ConstraintLayout) view.findViewById(R.id.recycler_empty_state_view);
        emptyIv = (ImageView) view.findViewById(R.id.empty_iv);
        emptyTitleTv = (TextView) view.findViewById(R.id.empty_title_tv);
        emptyMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);

        recyclerEmptyStateView.setVisibility(View.GONE);
        main_container.setVisibility(View.GONE);
        brainTreeViewModel.getBrainTreeCustomer();

        addIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        nextTv.setText(getString(R.string.edit));

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.VISIBLE);
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OnCloseActionInterface) getActivity()).onClose(false);
                    }
                });
                toolbarTitle.setText(getString(R.string.card_information));
            }
        }

        nextTv.setVisibility(View.GONE);
        nextTv.setOnClickListener(this);
        addIv.setOnClickListener(this);
    }

    private void addObserver() {
        brainTreeViewModel = ViewModelProviders.of(getActivity()).get(BrainTreeViewModel.class);

        brainTreeViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                Log.v("CardInformation", "base api response model observer");

                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof BrainTreeClientToken) {
                        if (isUserActionOccured) {
                            DropInRequest dropInRequest = new DropInRequest().clientToken(((BrainTreeClientToken) baseApiResponseModel).getClient_token());
                            startActivityForResult(dropInRequest.getIntent(getActivity()), CallPlacingActivity.BRAIN_TREE_REQUEST);
                            isUserActionOccured = false;
                        }
                    } else if (baseApiResponseModel instanceof BrainTreeCustomer) {

                        BrainTreeCustomer brainTreeCustomer = (BrainTreeCustomer) baseApiResponseModel;

                        getArguments().putSerializable(ArgumentKeys.BRAIN_TREE_CUSTOMER, brainTreeCustomer);


                        if (brainTreeCustomer.getCreditCards().size() > 0) {
                            BrainTreeCard brainTreeCard = brainTreeCustomer.getCreditCards().get(0);

                            for (BrainTreeCard card : brainTreeCustomer.getCreditCards()) {
                                if (card.getDefault()) {
                                    brainTreeCard = card;
                                    break;
                                }
                            }

                            card_tv.setText(brainTreeCard.getFormattedCardNumber());
                            Utils.setImageWithGlide(getContext(), card_iv, brainTreeCard.getImageUrl(), getResources().getDrawable(R.drawable.placeholder_insurance), false, true);
                            expiration_tv.setText(getResources().getString(R.string.expiration_date) + " : " + brainTreeCard.getExpirationDate());
                            setTopButtonType(false);
                            main_container.setVisibility(View.VISIBLE);
                            recyclerEmptyStateView.setVisibility(View.GONE);
                        } else {
                            loadEmptyView();
                        }

                        nextTv.setVisibility(View.VISIBLE);

                    } else {
                        brainTreeViewModel.getBrainTreeCustomer();
                    }
                } else {
                    Log.v("CardInformation", "base api response model observer null");
                }
            }
        });


        brainTreeViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                Log.v("CardInformation", "error observer");
                if (forCheckout) {
                    brainTreeViewModel.getBrainTreeCustomer();
                    forCheckout = false;
                } else if (errorModel != null && errorModel.getName() != null) {
                    nextTv.setVisibility(View.VISIBLE);
                    loadEmptyView();
                }
            }
        });

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(brainTreeViewModel);
        }
    }

    private void loadEmptyView() {
        setTopButtonType(true);
        main_container.setVisibility(View.GONE);
        recyclerEmptyStateView.setVisibility(View.VISIBLE);

        String title = EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_CARDS);
        String message = EmptyStateUtil.getMessage(getActivity(), EmptyViewConstants.EMPTY_CARDS);
        int image = EmptyStateUtil.getImage(EmptyViewConstants.EMPTY_CARDS);

        emptyTitleTv.setText(title);
        emptyMessageTv.setText(message);
        emptyIv.setImageResource(image);
    }

    private void setTopButtonType(boolean isAdd) {
        if (isAdd) {
            nextTv.setVisibility(View.GONE);
            addIv.setVisibility(View.VISIBLE);
        } else {
            nextTv.setVisibility(View.VISIBLE);
            addIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CallPlacingActivity.BRAIN_TREE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("CardInformation", "brain tree ok");
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                    HashMap<String, Object> param = new HashMap<>();
                    if (result.getPaymentMethodNonce() != null) {
                        param.put("payment_method_nonce", result.getPaymentMethodNonce().getNonce());
                    }
                    param.put("amount", "0");
                    param.put("to", "");
                    param.put("savePayment", true);
                    forCheckout = true;
                    brainTreeViewModel.checkOutBrainTree(param);
                    Log.v("CardInformation", "brain tree called checkout");
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    break;
                } else {
                    break;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (getArguments() == null) {
            return;
        }

        BrainTreeCustomer customer = (BrainTreeCustomer) getArguments().getSerializable(ArgumentKeys.BRAIN_TREE_CUSTOMER);

        HashMap<String, String> param = new HashMap<>();
        if (customer != null && customer.getPaymentMethods() != null) {
            param.put("customerId", UserDetailPreferenceManager.getUser_guid());
            param.put("verifyCard", "true");
        }
        isUserActionOccured = true;
        brainTreeViewModel.getBrainTreeClientToken(param);
    }
}
