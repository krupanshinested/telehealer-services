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

public class NewDeviceDetailActivity extends BaseActivity implements View.OnClickListener {
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

            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device_detail);
        initObservers();
        initView();
    }

    private void initView() {

        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        backIv.setOnClickListener(this);
        toolbarTitle.setText(getString(R.string.str_new_device_setup));
        getNewDeviceSetupDetail(true);
    }

    private void getNewDeviceSetupDetail(boolean isShowProgress) {
//        if (!isApiRequested) {
//            isApiRequested = true;
//            newDeviceApiViewModel.getAccessLog(page, isShowProgress);
//        }
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
