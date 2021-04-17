package com.thealer.telehealer.views.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListResponseModel;
import com.thealer.telehealer.apilayer.models.addConnection.DesignationResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.ArrayList;
import java.util.Arrays;
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
    private SearchCellView searchView;
    private RecyclerView connectionListRv;
    private CustomRecyclerView connectionListCrv;
    private FrameLayout fragmentHolder;
    private ImageView searchClearIv;

    private ConnectionListApiViewModel connectionListApiViewModel;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private ConnectionListResponseModel connectionListResponseModel;
    private DesignationResponseModel designationResponseModel;
    private ConnectionListAdapter connectionListAdapter;
    private int page = 1;
    private int selectedPosition = -1, selectedId;
    private boolean isApiRequested = false;
    private boolean isMedicalAssistant = false;
    private List<CommonUserApiResponseModel> commonUserApiResponseModelList;
    private Toolbar toolbar;
    private List<String> filterList;
    private String speciality;
    private int selectedFilterPosition = -1;

    @Nullable
    private TimerRunnable uiToggleTimer;
    private List<String> designationList=new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_connection);
        connectionListApiViewModel = new ViewModelProvider(this).get(ConnectionListApiViewModel.class);
        addConnectionApiViewModel = new ViewModelProvider(this).get(AddConnectionApiViewModel.class);

        attachObserver(connectionListApiViewModel);
        attachObserver(addConnectionApiViewModel);

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    if (selectedPosition < 0) {
                        return;
                    }

                    if (connectionListResponseModel.getResult().size() > selectedPosition) {

                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, baseApiResponseModel.isSuccess());

                        if (baseApiResponseModel.isSuccess()) {
                            intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                            intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.add_connection_success),
                                    connectionListResponseModel.getResult().get(selectedPosition).getFirst_name()));

                            connectionListAdapter.setData(commonUserApiResponseModelList, selectedPosition);
                        } else {
                            intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                            intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.add_connection_failure), connectionListResponseModel.getResult().get(selectedPosition).getFirst_name()));
                        }

                        LocalBroadcastManager.getInstance(AddConnectionActivity.this).sendBroadcast(intent);
                    }
                }
            }
        });

        addConnectionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
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
                    if (baseApiResponseModel instanceof ConnectionListResponseModel) {
                        connectionListResponseModel = (ConnectionListResponseModel) baseApiResponseModel;
                        if (connectionListAdapter != null) {

                            if (page == 1) {
                                commonUserApiResponseModelList = connectionListResponseModel.getResult();
                                toolbarTitle.setText(Utils.getPaginatedTitle(getString(R.string.Add_connections), connectionListResponseModel.getCount()));
                            } else {
                                commonUserApiResponseModelList.addAll(connectionListResponseModel.getResult());
                            }
                            connectionListAdapter.setData(commonUserApiResponseModelList, -1);

                            connectionListCrv.setNextPage(connectionListResponseModel.getNext());

                            if (commonUserApiResponseModelList.size() > 0) {
                                connectionListCrv.showOrhideEmptyState(false);
                            } else {
                                connectionListCrv.showOrhideEmptyState(true);
                            }
                        }
                        resetData();
                    } else if (baseApiResponseModel instanceof DesignationResponseModel) {
                        designationResponseModel = (DesignationResponseModel) baseApiResponseModel;
                        if (connectionListAdapter != null) {
                            if(designationResponseModel.getResult()!=null){
                                designationList=designationResponseModel.getResult();
                            }
                            connectionListAdapter.setDesignationData(designationResponseModel.getResult());

                        }
                    } else {
                        resetData();
                    }

                }

            }
        });

        initView();
    }

    private void resetData() {
        isApiRequested = false;
        connectionListCrv.setScrollable(true);
        connectionListCrv.hideProgressBar();
        connectionListCrv.getSwipeLayout().setRefreshing(false);
    }

    @Override
    public void onCompletionResult(String requestId, Boolean success, Bundle bundle) {
        if (success) {
            selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);
            String designation = bundle.getString(Constants.DESIGNATION);
            CommonUserApiResponseModel userModel = (CommonUserApiResponseModel) bundle.getSerializable(Constants.USER_DETAIL);
            String userGuid = null;

            if (userModel != null) {
                userGuid = userModel.getUser_guid();
            }

            bundle = new Bundle();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(bundle);

            successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

            addConnectionApiViewModel.connectUser(userGuid, null, String.valueOf(selectedId), designation);

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
        searchView = (SearchCellView) findViewById(R.id.search_view);
        fragmentHolder = (FrameLayout) findViewById(R.id.fragment_holder);
        connectionListCrv = (CustomRecyclerView) findViewById(R.id.connection_list_crv);
        searchClearIv = (ImageView) findViewById(R.id.search_clear_iv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (getIntent() != null) {
            isMedicalAssistant = getIntent().getBooleanExtra(ArgumentKeys.IS_MEDICAL_ASSISTANT, false);
        }

        toolbarTitle.setText(getString(R.string.Add_connections));
        setSupportActionBar(toolbar);

        searchView.setSearchEtHint(getString(R.string.search_ma));

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        connectionListCrv.setEmptyState(EmptyViewConstants.EMPTY_UNCONNECTED_USER);

        connectionListCrv.showOrhideEmptyState(false);

        connectionListRv = connectionListCrv.getRecyclerView();

        connectionListAdapter = new ConnectionListAdapter(this);

        connectionListRv.setAdapter(connectionListAdapter);

        connectionListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                ++page;
                getApiData(false);
            }
        });

        connectionListCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getApiData(false);
            }
        });

        filterList = new ArrayList<>();
        filterList.add(getString(R.string.all));
        filterList.addAll(Arrays.asList(getResources().getStringArray(R.array.doctor_speciality_list)));

        speciality = getString(R.string.all);

        getApiData(true);
        searchView.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                String search="";
                if(searchView.getCurrentSearchResult()!=null){
                    search=searchView.getCurrentSearchResult();
                }
                showSearchResult(search);
            }
        });
    }

    private void getApiData(boolean showProgress) {
        String search="";
        if(searchView.getCurrentSearchResult()!=null){
            search=searchView.getCurrentSearchResult();
        }
        if (!isApiRequested) {
            if (speciality != null && speciality.equals(getString(R.string.all))) {
                speciality = null;
            }
            connectionListApiViewModel.getUnconnectedList(search, speciality, page, showProgress, isMedicalAssistant);
            if(designationList==null || designationList.isEmpty())
                connectionListApiViewModel.getDesignationList();
        }

        isApiRequested = true;
        connectionListCrv.setScrollable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!UserType.isUserDoctor()) {
            getMenuInflater().inflate(R.menu.filter_menu, menu);
            MenuItem filterItem = menu.findItem(R.id.menu_filter);
            View view = filterItem.getActionView();
            ImageView filterIv = view.findViewById(R.id.filter_iv);
            ImageView filterIndicatorIv = view.findViewById(R.id.filter_indicatior_iv);

            filterIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showOptionsSelectionAlert(AddConnectionActivity.this, filterList, selectedFilterPosition, new PickerListener() {
                        @Override
                        public void didSelectedItem(int position) {
                            page = 1;
                            speciality = filterList.get(position);
                            if (position == 0) {
                                selectedFilterPosition = -1;
                                filterIndicatorIv.setVisibility(View.GONE);
                            } else {
                                selectedFilterPosition = position;
                                filterIndicatorIv.setVisibility(View.VISIBLE);
                            }
                            getApiData(true);
                        }

                        @Override
                        public void didCancelled() {

                        }
                    });
                }
            });
        } else if (UserType.isUserDoctor()) {
            getMenuInflater().inflate(R.menu.menu_connection, menu);
            MenuItem filterItem = menu.findItem(R.id.menu_add_support_staff);
            View view = filterItem.getActionView();
            LinearLayout llStaff = view.findViewById(R.id.btnAddPatient);
            llStaff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showInviteAlert(AddConnectionActivity.this, null);
                }
            });
        }
        return true;
    }

    private void showSearchResult(String search) {
        if (commonUserApiResponseModelList == null) {
            return;
        }

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
        if (bundle == null) {
            return;
        }

        if (bundle.getBoolean(ArgumentKeys.SHOW_CONNECTION_REQUEST_ALERT)) {
            Utils.showAlertDialog(AddConnectionActivity.this, "", getString(R.string.doctor_unavailable), getString(R.string.ok), null, null, null);
        } else {
            showDetailView(bundle);
        }
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
