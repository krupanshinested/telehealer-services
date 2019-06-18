package com.thealer.telehealer.views.home.orders.prescription;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.GetPharmaciesApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.SendFaxRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.LocationTracker;
import com.thealer.telehealer.common.LocationTrackerInterface;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;

/**
 * Created by Aswin on 30,November,2018
 */
public class SelectPharmacyFragment extends OrdersBaseFragment implements View.OnClickListener, LocationTrackerInterface {
    private EditText searchEt;
    private CustomRecyclerView pharmacyCrv;
    private Button nextBtn;

    private PharmacyListAdapter pharmacyListAdapter;
    private GetPharmaciesApiResponseModel getPharmaciesApiResponseModel;
    private GetPharmaciesApiResponseModel.ResultsBean selectedPharmacy = null;
    private OrdersApiViewModel ordersApiViewModel;
    private CreatePrescriptionRequestModel createPrescriptionRequestModel;
    private OnCloseActionInterface onCloseActionInterface;

    private int nextPage = 1;
    private boolean isFromDetailView;
    private int referralId;
    private AppBarLayout appbarLayout;
    private ImageView backIv;
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private boolean isLocationRequested;
    private LocationTracker locationTracker;
    private boolean isProposerRequested, isSaveAndFax = false;
    private String userName, doctorGuid;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isProposerRequested = true;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);

        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof GetPharmaciesApiResponseModel) {
                        getPharmaciesApiResponseModel = (GetPharmaciesApiResponseModel) baseApiResponseModel;

                        pharmacyCrv.setNextPage(getPharmaciesApiResponseModel.getNext());

                        pharmacyListAdapter.setResults(getPharmaciesApiResponseModel.getResults(), nextPage);

                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmacy_search, container, false);
        setTitle(getString(R.string.choose_pharmacy));
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        pharmacyCrv = (CustomRecyclerView) view.findViewById(R.id.pharmacy_crv);
        nextBtn = (Button) view.findViewById(R.id.next_btn);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        searchEt.setHint(getString(R.string.search_pharmacy));
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nextPage = 1;
                getPharmacies(s.toString(), null, true);
                pharmacyListAdapter.removeSelected();
                selectedPharmacy = null;
                enableOrDisableNext();
            }
        });

        pharmacyCrv.getSwipeLayout().setEnabled(false);
        nextBtn.setOnClickListener(this);

        if (getArguments() != null) {

            isFromDetailView = getArguments().getBoolean(ArgumentKeys.IS_FROM_PRESCRIPTION_DETAIL);
            if (isFromDetailView) {
                toolbarTitle.setText(getString(R.string.choose_pharmacy));
                backIv.setOnClickListener(this);

                toolbar.setVisibility(View.VISIBLE);
            } else {
                toolbar.setVisibility(View.GONE);
            }

            if (isFromDetailView) {
                referralId = getArguments().getInt(ArgumentKeys.PRESCRIPTION_ID);
            } else {
                createPrescriptionRequestModel = (CreatePrescriptionRequestModel) getArguments().getSerializable(ArgumentKeys.PRESCRIPTION_DATA);
                userName = getArguments().getString(ArgumentKeys.USER_NAME);
                isSaveAndFax = true;
            }

            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
        }

        pharmacyCrv.setEmptyState(EmptyViewConstants.EMPTY_SEARCH);

        pharmacyListAdapter = new PharmacyListAdapter(getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                selectedPharmacy = (GetPharmaciesApiResponseModel.ResultsBean) bundle.getSerializable(ArgumentKeys.SELECTED_MENU_ITEM);
                enableOrDisableNext();
            }
        });

        pharmacyCrv.getRecyclerView().setAdapter(pharmacyListAdapter);

        pharmacyCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                nextPage = nextPage + 1;
                getPharmacies(searchEt.getText().toString(), null, false);
            }
        });

        enableOrDisableNext();

    }

    private void enableOrDisableNext() {
        boolean enable = false;

        if (selectedPharmacy != null) {
            enable = true;
        }

        nextBtn.setEnabled(enable);

        if (enable) {
            nextBtn.setAlpha(1);
        } else {
            nextBtn.setAlpha((float) 0.5);
        }
    }

    private void getPharmacies(String query, String city, boolean showProgress) {
        ordersApiViewModel.getPharmacy(query, city, nextPage, showProgress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                Utils.hideKeyboard(getActivity());
                showQuickLogin();
                break;
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.proposer_broadcastReceiver)));

        if (!isLocationRequested) {
            requestPharmacies();
        }
    }

    @Override
    public void onAuthenticated() {
        if (isSaveAndFax) {
            createPrescription(createPrescriptionRequestModel, userName, doctorGuid, true);
        } else {
            sendFax(referralId, false, true);
        }
    }

    private void requestPharmacies() {
        if (!isProposerRequested && PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_LOCATION)) {
            getLastKnownLocation();
        } else {
            getPharmacies(null, null, true);
        }
    }

    private void getLastKnownLocation() {
        isLocationRequested = true;
        locationTracker = new LocationTracker(getActivity(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationTracker != null) {
            locationTracker.stopLocationListener();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void sendFax(int referral_id, boolean isShowProgress, boolean isShowAuth) {
        super.sendFax(referral_id, isShowProgress, isShowAuth);
        ordersCreateApiViewModel.sendFax(new SendFaxRequestModel(selectedPharmacy.getFax(),
                String.valueOf(referral_id),
                new SendFaxRequestModel.DetailBean(selectedPharmacy)), doctorGuid, isShowProgress);
    }

    @Override
    public void onLocationUpdated(String city, String state) {
        if (city == null) {
            getLastKnownLocation();
        } else {
            getPharmacies(null, city, true);
        }
    }
}
