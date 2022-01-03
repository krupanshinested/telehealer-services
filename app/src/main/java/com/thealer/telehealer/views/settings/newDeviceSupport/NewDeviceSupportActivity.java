package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewDeviceSupportActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomRecyclerView newDeviceCrv;
    private List<NewDeviceApiResponseModel.Data> deviceList = new ArrayList<>();
    private NewDeviceApiViewModel newDeviceApiViewModel;
    private NewDeviceApiResponseModel newDeviceApiResponseModel;
    private NewDeviceSupportAdapter newDeviceSupportAdapter;
    Activity activity;

    private void initObservers() {
        activity = this;
        newDeviceApiViewModel = new ViewModelProvider(this).get(NewDeviceApiViewModel.class);
        newDeviceApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    try {
                        newDeviceApiResponseModel = (NewDeviceApiResponseModel) baseApiResponseModel;

                        if (newDeviceApiResponseModel.getData().size() > 0) {
                            newDeviceSupportAdapter.setData(newDeviceApiResponseModel.getData());
                            deviceList = newDeviceApiResponseModel.getData();
                            newDeviceCrv.showOrhideEmptyState(false);
                        } else {
                            newDeviceCrv.setEmptyState(EmptyViewConstants.EMPTY_DEVICELIST);
                            newDeviceCrv.showOrhideEmptyState(true);
                        }
                        newDeviceCrv.hideProgressBar();
                        newDeviceCrv.getSwipeLayout().setRefreshing(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

        backIv = findViewById(R.id.back_iv);
        toolbarTitle = findViewById(R.id.toolbar_title);
        newDeviceCrv = findViewById(R.id.new_device_crv);

        backIv.setOnClickListener(this);
        toolbarTitle.setText(getString(R.string.str_new_device_setup));

        newDeviceCrv.setEmptyState(EmptyViewConstants.EMPTY_DEVICELIST);

        newDeviceCrv.showOrhideEmptyState(false);

        newDeviceCrv.setActionClickListener(v -> {
            getNewDeviceSetup();
        });

        newDeviceCrv.setErrorModel(this, newDeviceApiViewModel.getErrorModelLiveData());

        newDeviceCrv.getSwipeLayout().setOnRefreshListener(() -> getNewDeviceSetup());

        newDeviceSupportAdapter = new NewDeviceSupportAdapter(this, deviceList, (position, bundle) -> {
            Gson gson = new Gson();
            String json = gson.toJson(deviceList.get(position));
            startActivity(new Intent(activity, NewDeviceDetailActivity.class)
                    .putExtra(ArgumentKeys.DEVICE_DETAILS, json));
        });
        newDeviceCrv.getRecyclerView().setAdapter(newDeviceSupportAdapter);

        getNewDeviceSetup();
    }

    private void getNewDeviceSetup() {
        newDeviceApiViewModel.getDevicelist();
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
