package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 12,April,2019
 */
public class RecentsSelectionActivity extends BaseActivity {
    private CustomRecyclerView recentsCrv;

    private String userGuid, doctorGuid;
    private int page = 1;
    private boolean isApiRequested;
    private RecentsSelectionAdapter recentsSelectionAdapter;

    private RecentsApiViewModel recentsApiViewModel;
    private RecentsApiResponseModel recentsApiResponseModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_selection);
        initViewModels();
        initView();
    }

    private void initViewModels() {
        recentsApiViewModel = ViewModelProviders.of(this).get(RecentsApiViewModel.class);
        attachObserver(recentsApiViewModel);
        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    recentsApiResponseModel = (RecentsApiResponseModel) baseApiResponseModel;

                    if (recentsApiResponseModel.getCount() > 0) {
                        recentsCrv.showOrhideEmptyState(false);
                        if (page == 1) {
                            recentsCrv.setNextPage(recentsApiResponseModel.getNext());
                        }
                        recentsSelectionAdapter.setData(recentsApiResponseModel.getResult(), page);
                        recentsCrv.showOrhideEmptyState(false);
                    } else {
                        recentsCrv.showOrhideEmptyState(true);
                    }

                    isApiRequested = false;
                    recentsCrv.setScrollable(true);
                    recentsCrv.getSwipeLayout().setRefreshing(false);
                }
            }
        });

    }

    private void initView() {
        recentsCrv = (CustomRecyclerView) findViewById(R.id.recents_crv);
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getString(R.string.recents));

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recentsCrv.setEmptyState(EmptyViewConstants.EMPTY_CALLS);

        recentsCrv.showOrhideEmptyState(false);

        userGuid = getIntent().getStringExtra(ArgumentKeys.USER_GUID);
        doctorGuid = getIntent().getStringExtra(ArgumentKeys.DOCTOR_GUID);

        int selectedTranscriptionId = getIntent().getIntExtra(ArgumentKeys.SELECTED_TRANSCRIPTION_ID, -1);

        recentsSelectionAdapter = new RecentsSelectionAdapter(this, selectedTranscriptionId, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                setResult(Activity.RESULT_OK, new Intent().putExtras(bundle));
                finish();
            }
        });

        recentsCrv.getRecyclerView().setAdapter(recentsSelectionAdapter);

        recentsCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                getUserRecentsList(false);
            }
        });

        recentsCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getUserRecentsList(false);
            }
        });

        recentsCrv.setErrorModel(this, recentsApiViewModel.getErrorModelLiveData());

        getUserRecentsList(true);
    }

    private void getUserRecentsList(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            recentsCrv.setScrollable(false);
            recentsApiViewModel.getUserCorrespondentList(userGuid, doctorGuid, null, page, true, isShowProgress);
        }
    }
}
