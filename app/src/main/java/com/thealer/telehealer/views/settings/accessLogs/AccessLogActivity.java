package com.thealer.telehealer.views.settings.accessLogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.accessLog.AccessLogApiResponseModel;
import com.thealer.telehealer.apilayer.models.accessLog.AccessLogApiViewModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 15,July,2019
 */
public class AccessLogActivity extends BaseActivity implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomRecyclerView logsCrv;

    private AccessLogApiViewModel accessLogApiViewModel;
    private AccessLogApiResponseModel accessLogApiResponseModel;
    private AccessLogsAdapter accessLogsAdapter;

    private int page = 1;
    private boolean isApiRequested = false;

    private void initObservers() {
        accessLogApiViewModel = ViewModelProviders.of(this).get(AccessLogApiViewModel.class);
        accessLogApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    accessLogApiResponseModel = (AccessLogApiResponseModel) baseApiResponseModel;
                    if (accessLogApiResponseModel.getCount() > 0) {
                        accessLogsAdapter.setData(accessLogApiResponseModel.getResult(), page);
                        logsCrv.showOrhideEmptyState(false);
                    } else {
                        logsCrv.showOrhideEmptyState(true);
                    }

                    logsCrv.setNextPage(accessLogApiResponseModel.getNext());
                    isApiRequested = false;
                    logsCrv.setScrollable(true);
                    logsCrv.hideProgressBar();
                    logsCrv.getSwipeLayout().setRefreshing(false);
                }
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_log);
        initObservers();
        initView();
    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        logsCrv = (CustomRecyclerView) findViewById(R.id.logs_crv);

        backIv.setOnClickListener(this);
        toolbarTitle.setText(getString(R.string.access_logs));

        toolbarTitle.setText(getString(R.string.access_logs));

        logsCrv.setEmptyState(EmptyViewConstants.EMPTY_CALL_LOGS);

        logsCrv.showOrhideEmptyState(false);

        logsCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                logsCrv.setScrollable(false);
                logsCrv.showProgressBar();
                getLogs(false);
            }
        });

        logsCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isApiRequested = false;
                getLogs(true);
            }
        });

        logsCrv.setErrorModel(this, accessLogApiViewModel.getErrorModelLiveData());

        logsCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getLogs(true);
            }
        });

        accessLogsAdapter = new AccessLogsAdapter(this);

        logsCrv.getRecyclerView().setAdapter(accessLogsAdapter);

        getLogs(true);
    }

    private void getLogs(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            accessLogApiViewModel.getAccessLog(page, isShowProgress);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
        }
    }
}
