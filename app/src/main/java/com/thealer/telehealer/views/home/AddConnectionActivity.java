package com.thealer.telehealer.views.home;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.isContentViewProceed;

/**
 * Created by Aswin on 19,November,2018
 */
public class AddConnectionActivity extends BaseActivity implements OnCloseActionInterface,
        OnActionCompleteInterface, SuccessViewInterface, OnListItemSelectInterface, AttachObserverInterface, ShowSubFragmentInterface {

    private ImageView backIv;
    private TextView toolbarTitle;
    private EditText searchEt;
    private RecyclerView connectionListRv;
    private CustomRecyclerView connectionListCrv;
    private FrameLayout fragmentHolder;
    private ImageView searchClearIv;

    private ConnectionListApiViewModel connectionListApiViewModel;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private ConnectionListResponseModel connectionListResponseModel;
    private ConnectionListAdapter connectionListAdapter;
    private int page = 1, totalCount = 0;
    private int selectedPosition = -1, selectedId;
    private boolean isApiRequested = false;
    private boolean isMedicalAssistant = false;
    private List<CommonUserApiResponseModel> commonUserApiResponseModelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_connection);
        connectionListApiViewModel = ViewModelProviders.of(this).get(ConnectionListApiViewModel.class);
        addConnectionApiViewModel = ViewModelProviders.of(this).get(AddConnectionApiViewModel.class);

        attachObserver(connectionListApiViewModel);
        attachObserver(addConnectionApiViewModel);

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_STATUS, baseApiResponseModel.isSuccess());

                    if (baseApiResponseModel.isSuccess()) {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.add_connection_success), connectionListResponseModel.getResult().get(selectedPosition).getFirst_name()));

                        connectionListAdapter.setData(commonUserApiResponseModelList, selectedPosition);
                    } else {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.add_connection_failure), connectionListResponseModel.getResult().get(selectedPosition).getFirst_name()));
                    }

                    LocalBroadcastManager.getInstance(AddConnectionActivity.this).sendBroadcast(intent);
                }
            }
        });

        addConnectionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                    LocalBroadcastManager.getInstance(AddConnectionActivity.this).sendBroadcast(intent);
                }
            }
        });

        connectionListApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    connectionListResponseModel = (ConnectionListResponseModel) baseApiResponseModel;

                    if (connectionListAdapter != null) {

                        totalCount = connectionListResponseModel.getCount();

                        if (page == 1) {
                            commonUserApiResponseModelList = connectionListResponseModel.getResult();
                        } else {
                            commonUserApiResponseModelList.addAll(connectionListResponseModel.getResult());
                        }
                        connectionListAdapter.setData(commonUserApiResponseModelList, -1);

                        connectionListCrv.setTotalCount(totalCount);

                    }
                }
                isApiRequested = false;
                connectionListCrv.setScrollable(true);
                connectionListCrv.hideProgressBar();
                connectionListCrv.getSwipeLayout().setRefreshing(false);
            }
        });

        initView();
    }

    @Override
    public void onCompletionResult(String requestId, Boolean success, Bundle bundle) {
        if (success) {
            selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);
            String userGuid = bundle.getString(ArgumentKeys.USER_GUID);

            bundle = new Bundle();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(bundle);

            successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

            addConnectionApiViewModel.connectUser(userGuid, String.valueOf(selectedId));

        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        searchEt = (EditText) findViewById(R.id.search_et);
        fragmentHolder = (FrameLayout) findViewById(R.id.fragment_holder);
        connectionListCrv = (CustomRecyclerView) findViewById(R.id.connection_list_crv);
        searchClearIv = (ImageView) findViewById(R.id.search_clear_iv);

        if (getIntent() != null) {
            isMedicalAssistant = getIntent().getBooleanExtra(ArgumentKeys.IS_MEDICAL_ASSISTANT, false);
        }

        toolbarTitle.setText(getString(R.string.Add_connections));

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    connectionListCrv.setScrollable(false);
                    searchClearIv.setVisibility(View.VISIBLE);
                    showSearchResult(s.toString().toLowerCase());
                } else {
                    searchClearIv.setVisibility(View.GONE);
                    connectionListAdapter.setData(commonUserApiResponseModelList, -1);
                    connectionListCrv.setScrollable(true);
                    connectionListCrv.showOrhideEmptyState(false);
                }
            }
        });

        searchClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
            }
        });

        connectionListCrv.setEmptyState(EmptyViewConstants.EMPTY_SEARCH);

        connectionListCrv.showOrhideEmptyState(false);

        connectionListRv = connectionListCrv.getRecyclerView();

        connectionListAdapter = new ConnectionListAdapter(this);

        connectionListRv.setAdapter(connectionListAdapter);

        connectionListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                ++page;
                getApiData(false);
                isApiRequested = true;
                connectionListCrv.setScrollable(false);
            }
        });

        connectionListCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getApiData(false);
                isApiRequested = true;
                connectionListCrv.setScrollable(false);
            }
        });

        getApiData(true);
    }

    private void showSearchResult(String search) {
        List<CommonUserApiResponseModel> searchResult = new ArrayList<>();
        for (CommonUserApiResponseModel userModel : commonUserApiResponseModelList) {
            if (userModel.getFirst_name().toLowerCase().contains(search) ||
                    userModel.getLast_name().toLowerCase().contains(search) ||
                    userModel.getUserDisplay_name().toLowerCase().contains(search)) {
                searchResult.add(userModel);
            }
        }

        if (searchResult.size() > 0) {
            connectionListCrv.showOrhideEmptyState(false);
            connectionListAdapter.setData(searchResult, -1);
        } else {
            connectionListCrv.showOrhideEmptyState(true);
        }
    }

    private void getApiData(boolean showProgress) {
        if (!isApiRequested) {
            connectionListApiViewModel.getUnconnectedList(searchEt.getText().toString(), page, showProgress, isMedicalAssistant);
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (UserType.isUserPatient() &&
                !appPreference.getBoolean(PreferenceConstants.IS_KNOW_YOUR_NUMBER_SHOWN)) {

            appPreference.setBoolean(PreferenceConstants.IS_KNOW_YOUR_NUMBER_SHOWN, true);

            showKnowYourNumber();
        }
    }

    @Override
    public void onListItemSelected(int position, Bundle bundle) {
        this.selectedPosition = position;
        if (bundle != null)
            showDetailView(bundle);
    }

    private void showDetailView(Bundle bundle) {
        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_CONNECTION);

        doctorPatientDetailViewFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(doctorPatientDetailViewFragment.getClass().getSimpleName())
                .replace(fragmentHolder.getId(), doctorPatientDetailViewFragment)
                .commit();

        fragmentHolder.bringToFront();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

    @Override
    public void onShowFragment(Fragment fragment) {

    }

    private void showKnowYourNumber() {

        startActivityForResult(new Intent(this, ContentActivity.class)
                        .putExtra(ArgumentKeys.TITLE, getString(R.string.know_your_numbers))
                        .putExtra(ArgumentKeys.DESCRIPTION, getString(R.string.know_your_number_description))
                        .putExtra(ArgumentKeys.IS_SKIP_NEEDED, true)
                        .putExtra(ArgumentKeys.IS_BUTTON_NEEDED, true)
                        .putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed))
                        .putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_health_heart)
                , RequestID.REQ_CONTENT_VIEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestID.REQ_CONTENT_VIEW && resultCode == Activity.RESULT_OK) {
            isContentViewProceed = true;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.vital_devices_url))));
        }
    }
}
