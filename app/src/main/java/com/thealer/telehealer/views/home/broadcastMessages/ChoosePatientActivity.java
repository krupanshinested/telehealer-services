package com.thealer.telehealer.views.home.broadcastMessages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.home.broadcastMessages.adapter.ChoosePatientAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.apilayer.api.ApiInterface.FILTER_USER_GUID_IN;

public class ChoosePatientActivity extends BaseActivity implements AttachObserverInterface {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private Button btnNext;
    private TextView toolbarTitle;
    private int page = 1;
    private boolean isApiRequested = false;
    private AssociationApiViewModel associationApiViewModel;
    private AssociationApiResponseModel associationApiResponseModel;
    private ChoosePatientAdapter choosePatientAdapter;
    private RecyclerView rvPatientList;
    private SearchCellView searchView;
    private CustomRecyclerView doctorPatientListCrv;
    private List<CommonUserApiResponseModel> lstPatient = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_patient);
        super.onCreate(savedInstanceState);
        initViewModel();
        initView();
    }

    private void initViewModel() {
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        attachObserver(associationApiViewModel);
        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof AssociationApiResponseModel) {
                        associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                        didReceivedResult();
                    }
                }
            }
        });
    }


    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        btnNext = (Button) findViewById(R.id.btnNext);
        doctorPatientListCrv = (CustomRecyclerView) findViewById(R.id.doctor_patient_list_crv);
        searchView = (SearchCellView) findViewById(R.id.search_view);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.choose_patient));
        if (UserType.isUserDoctor()) {
            doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_PATIENT_WITH_BTN);
        }
        rvPatientList = doctorPatientListCrv.getRecyclerView();
        // TODO- Manage Patient Adapter List
        choosePatientAdapter = new ChoosePatientAdapter(this);
        rvPatientList.setAdapter(choosePatientAdapter);

        searchView.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                page = 1;
                getAssociationsList(true);
            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosePatientAdapter != null) {
                    List<CommonUserApiResponseModel> selectedUserList = choosePatientAdapter.getSelectedUserList();
                    if (selectedUserList.size() > 0) {
                        startActivity(new Intent(ChoosePatientActivity.this, BroadcastMessagesActivity.class)
                                .putExtra(FILTER_USER_GUID_IN, (ArrayList<CommonUserApiResponseModel>) selectedUserList));
                    } else
                        Toast.makeText(ChoosePatientActivity.this, "Please Select User", Toast.LENGTH_SHORT).show();
                }
            }
        });

        doctorPatientListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                getAssociationsList(false);
                isApiRequested = true;
                doctorPatientListCrv.setScrollable(false);
            }
        });

        doctorPatientListCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doctorPatientListCrv.getSwipeLayout().setRefreshing(false);
                page = 1;
                getAssociationsList(false);
            }
        });

        doctorPatientListCrv.setErrorModel(this, associationApiViewModel.getErrorModelLiveData());

        getAssociationsList(true);

    }

    private void didReceivedResult() {
        boolean isItemsPresent = false;
        if (associationApiResponseModel != null) {
            isItemsPresent = associationApiResponseModel.getResult().size() != 0;
        }

        if (!isItemsPresent) {
            doctorPatientListCrv.showOrhideEmptyState(true);
            doctorPatientListCrv.showOrHideMessage(false);
        }

        if (choosePatientAdapter != null) {

            if (isItemsPresent) {
                doctorPatientListCrv.showOrhideEmptyState(false);
            }

            if (associationApiResponseModel != null) {
                doctorPatientListCrv.setNextPage(associationApiResponseModel.getNext());
                choosePatientAdapter.setData(associationApiResponseModel.getResult(), page);
            } else {
                doctorPatientListCrv.setNextPage(null);
            }

            associationApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

        }

        if (UserType.isUserAssistant() && associationApiResponseModel != null && !associationApiResponseModel.getResult().isEmpty()) {
            List<String> doctorGuidList = new ArrayList<>();
            for (int i = 0; i < associationApiResponseModel.getResult().size(); i++) {
                if (!doctorGuidList.contains(associationApiResponseModel.getResult().get(i).getUser_guid())) {
                    doctorGuidList.add(associationApiResponseModel.getResult().get(i).getUser_guid());
                }
            }
            String doctorGuids = doctorGuidList.toString().replace("[", "").replace("]", "").trim();
            appPreference.setString(PreferenceConstants.ASSOCIATION_GUID_LIST, doctorGuids);

        }
        isApiRequested = false;
        doctorPatientListCrv.setScrollable(true);
        doctorPatientListCrv.hideProgressBar();

    }

    private void getAssociationsList(boolean isShowProgress) {
        if (!isApiRequested) {
            doctorPatientListCrv.setScrollable(true);
            doctorPatientListCrv.showOrhideEmptyState(false);
            if (UserType.isUserDoctor()) {
                associationApiViewModel.getAssociationList(searchView.getCurrentSearchResult(), page, "", isShowProgress, false);
            }
        }

    }
}