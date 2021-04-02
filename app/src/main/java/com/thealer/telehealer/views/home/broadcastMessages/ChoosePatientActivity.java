package com.thealer.telehealer.views.home.broadcastMessages;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.home.broadcastMessages.adapter.ChoosePatientAdapter;

import java.util.List;

public class ChoosePatientActivity extends BaseActivity implements AttachObserverInterface {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private int page = 1;
    private AssociationApiViewModel associationApiViewModel;
    private AssociationApiResponseModel associationApiResponseModel;
    private ChoosePatientAdapter choosePatientAdapter;
    private RecyclerView rvPatientList;
    private SearchCellView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_patient);
        initView();
    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        rvPatientList = (RecyclerView) findViewById(R.id.rv_patient_list);
        searchView = (SearchCellView) findViewById(R.id.search_view);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        searchView.setSearchHint(getString(R.string.lbl_search_patient));
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        attachObserver(associationApiViewModel);
        toolbarTitle.setText(getString(R.string.choose_patient));
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

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    if (baseApiResponseModel instanceof AssociationApiResponseModel) {
                        associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                        handlePatientList();
                    }
                }
            }
        });
    }

    private void handlePatientList() {
        List<CommonUserApiResponseModel> lstPatient = associationApiResponseModel.getResult();
        if (lstPatient.size() > 0 && choosePatientAdapter == null) {
            choosePatientAdapter = new ChoosePatientAdapter(this, lstPatient);
            rvPatientList.setLayoutManager(new LinearLayoutManager(this));
            rvPatientList.setAdapter(choosePatientAdapter);
            enableItemView(true);
        } else if (lstPatient.size() > 0 && choosePatientAdapter != null) {
            int refresh = (page - 1) * Constants.PAGINATION_SIZE;
            if (refresh > 0) {
                refresh = refresh - 1;
            }
            choosePatientAdapter.notifyItemChanged(refresh);
            enableItemView(true);
        } else {
            enableItemView(false);
        }
    }

    private void enableItemView(boolean isItemVisible) {
        if (isItemVisible) {
            rvPatientList.setVisibility(View.VISIBLE);
        } else {
            rvPatientList.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAssociationsList(true);
    }

    private void getAssociationsList(boolean isShowProgress) {

        if (UserType.isUserDoctor()) {
            associationApiViewModel.getAssociationList(searchView.getCurrentSearchResult(), page, "", isShowProgress, false);
        }
    }
}