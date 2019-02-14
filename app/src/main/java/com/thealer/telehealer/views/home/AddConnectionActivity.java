package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.List;

/**
 * Created by Aswin on 19,November,2018
 */
public class AddConnectionActivity extends BaseActivity implements OnCloseActionInterface,
        OnActionCompleteInterface, SuccessViewInterface, OnListItemSelectInterface, AttachObserverInterface , ShowSubFragmentInterface {

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
    private int selectedPosition, selectedId;
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
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_success_prefix) + " " + connectionListResponseModel.getResult().get(selectedPosition).getFirst_name() + " " + getString(R.string.add_connection_success_suffix));

                        for (int i = 0; i < commonUserApiResponseModelList.size(); i++) {
                            if (commonUserApiResponseModelList.get(i).getUser_id() == selectedId) {
                                commonUserApiResponseModelList.get(i).setConnection_status(Constants.CONNECTION_STATUS_OPEN);
                                break;
                            }
                        }
                        connectionListAdapter.setData(commonUserApiResponseModelList);
                    } else {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_failure_prefix) + " " + connectionListResponseModel.getResult().get(selectedPosition).getFirst_name() + " " + getString(R.string.add_connection_failure_suffix));
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
                        connectionListAdapter.setData(commonUserApiResponseModelList);

                        connectionListCrv.setTotalCount(totalCount);

                    }
                }
                isApiRequested = false;
                connectionListCrv.setScrollable(true);
                connectionListCrv.hideProgressBar();
            }
        });

        initView();
    }

    @Override
    public void onCompletionResult(String requestId, Boolean success, Bundle bundle) {
        if (success) {
            CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) bundle.getSerializable(Constants.USER_DETAIL);

            selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);

            addConnectionApiViewModel.connectUser(commonUserApiResponseModel.getUser_guid(), String.valueOf(selectedId));

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));
            successViewDialogFragment.setArguments(bundle);

            successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

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
                    searchClearIv.setVisibility(View.VISIBLE);
                } else {
                    searchClearIv.setVisibility(View.GONE);
                }
                page = 1;
                getApiData(false);
            }
        });

        searchClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
            }
        });

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

        getApiData(true);
    }

    private void getApiData(boolean showProgress) {
        if (!isApiRequested) {
            connectionListApiViewModel.getUnconnectedList(searchEt.getText().toString(), page, showProgress, isMedicalAssistant);
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }

    @Override
    public void onListItemSelected(int position, Bundle bundle) {
        this.selectedPosition = position;
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
}
