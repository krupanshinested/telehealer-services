package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiViewModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

public class NewDeviceSupportActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomRecyclerView newDeviceCrv;

    private NewDeviceApiViewModel newDeviceApiViewModel;
    private NewDeviceApiResponseModel newDeviceApiResponseModel;
    private NewDeviceSupportAdapter newDeviceSupportAdapter;

    private int page = 1;
    private boolean isApiRequested = false;

    private void initObservers() {
        newDeviceApiViewModel = new ViewModelProvider(this).get(NewDeviceApiViewModel.class);
        newDeviceApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    newDeviceApiResponseModel = (NewDeviceApiResponseModel) baseApiResponseModel;
                    if (newDeviceApiResponseModel.getCount() > 0) {
//                        newDeviceSupportAdapter.setData(newDeviceApiResponseModel.getResult(), page);
                        newDeviceCrv.showOrhideEmptyState(false);
                    } else {
                        newDeviceCrv.showOrhideEmptyState(true);
                    }

                    newDeviceCrv.setNextPage(newDeviceApiResponseModel.getNext());
                    isApiRequested = false;
                    newDeviceCrv.setScrollable(true);
                    newDeviceCrv.hideProgressBar();
                    newDeviceCrv.getSwipeLayout().setRefreshing(false);
                }
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        initObservers();
        initView();
    }

    private void initView() {

        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        newDeviceCrv = (CustomRecyclerView) findViewById(R.id.new_device_crv);

        backIv.setOnClickListener(this);
        toolbarTitle.setText(getString(R.string.str_new_device_setup));

        newDeviceCrv.setEmptyState(EmptyViewConstants.EMPTY_CALL_LOGS);

        newDeviceCrv.showOrhideEmptyState(false);

        newDeviceCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                newDeviceCrv.setScrollable(false);
                newDeviceCrv.showProgressBar();
                getNewDeviceSetup(false);
            }
        });

        newDeviceCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isApiRequested = false;
                getNewDeviceSetup(true);
            }
        });

        newDeviceCrv.setErrorModel(this, newDeviceApiViewModel.getErrorModelLiveData());

        newDeviceCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getNewDeviceSetup(true);
            }
        });

        newDeviceSupportAdapter = new NewDeviceSupportAdapter(this);

        newDeviceCrv.getRecyclerView().setAdapter(newDeviceSupportAdapter);

        getNewDeviceSetup(true);
    }

    private void getNewDeviceSetup(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            newDeviceApiViewModel.getAccessLog(page, isShowProgress);
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
