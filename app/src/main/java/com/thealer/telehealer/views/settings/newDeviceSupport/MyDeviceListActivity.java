package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.DeleteDeviceApiViewModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.OnDeviceItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyDeviceListActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomRecyclerView newDeviceCrv;
    private List<MyDeviceListApiResponseModel.Data> deviceList = new ArrayList<>();
    private NewDeviceApiViewModel newDeviceApiViewModel;
    private DeleteDeviceApiViewModel deleteDeviceApiViewModel;
    private MyDeviceListApiResponseModel myDeviceListApiResponseModel;
    private MyDeviceListAdapter myDeviceListAdapter;
    Activity activity;
    AppCompatTextView txtAddDevice;
    int currentPosition = -1;

    private void initObservers() {
        activity = this;
        newDeviceApiViewModel = new ViewModelProvider(this).get(NewDeviceApiViewModel.class);
        newDeviceApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    try {
                        myDeviceListApiResponseModel = (MyDeviceListApiResponseModel) baseApiResponseModel;

                        if (myDeviceListApiResponseModel.getData().size() > 0) {
                            deviceList = myDeviceListApiResponseModel.getData();
                            myDeviceListAdapter.setData(myDeviceListApiResponseModel.getData());
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

        deleteDeviceApiViewModel = new ViewModelProvider(this).get(DeleteDeviceApiViewModel.class);
        deleteDeviceApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    try {
                        Utils.showAlertDialog(activity, getString(R.string.alert), baseApiResponseModel.getMessage(),
                                getString(R.string.ok), null, (dialog, which) -> {
                                    dialog.dismiss();
                                    if (deviceList.size() > 0){
                                        deviceList.remove(currentPosition);

                                        if(deviceList.size() > 0){
                                            myDeviceListAdapter.setData(deviceList);
                                            newDeviceCrv.showOrhideEmptyState(false);
                                        }else{
                                            newDeviceCrv.setEmptyState(EmptyViewConstants.EMPTY_DEVICELIST);
                                            newDeviceCrv.showOrhideEmptyState(true);
                                        }
                                    }
                                }, (dialog, which) -> dialog.dismiss());
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
        setContentView(R.layout.activity_my_device_list);
        initObservers();
        initView();
    }

    private void initView() {

        backIv = findViewById(R.id.back_iv);
        toolbarTitle = findViewById(R.id.toolbar_title);
        newDeviceCrv = findViewById(R.id.new_device_crv);
        txtAddDevice = findViewById(R.id.txtAddDevice);

        backIv.setOnClickListener(this);
        txtAddDevice.setOnClickListener(this);
        toolbarTitle.setText(getString(R.string.key_my_devices));

        newDeviceCrv.setEmptyState(EmptyViewConstants.EMPTY_DEVICELIST);

        newDeviceCrv.showOrhideEmptyState(false);

        newDeviceCrv.setActionClickListener(v -> {
            getMyDeviceList();
        });

        newDeviceCrv.setErrorModel(this, newDeviceApiViewModel.getErrorModelLiveData());

        newDeviceCrv.getSwipeLayout().setOnRefreshListener(() -> getMyDeviceList());

        myDeviceListAdapter = new MyDeviceListAdapter(this, deviceList, new OnDeviceItemClickListener() {
            @Override
            public void onItemClick(int position, Bundle bundle) {
                Gson gson = new Gson();
                String json = gson.toJson(deviceList.get(position));
                startActivity(new Intent(activity, NewDeviceDetailActivity.class)
                        .putExtra(ArgumentKeys.DEVICE_DETAILS, json)
                        .putExtra(ArgumentKeys.DEVICE_FLAG, true));
            }

            @Override
            public void onItemDeleteClick(int position) {
                Utils.showAlertDialog(activity, getString(R.string.alert), getString(R.string.key_device_delete_confirmation),
                        getString(R.string.delete),
                        getString(R.string.cancel),
                        (dialog, which) -> {
                            dialog.dismiss();
                            deleteDevice(deviceList.get(position).getId(), position);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        newDeviceCrv.getRecyclerView().setAdapter(myDeviceListAdapter);

        getMyDeviceList();

    }

    private void deleteDevice(String id, int position){
        currentPosition = position;
        deleteDeviceApiViewModel.deleteDevice(id);
    }

    private void getMyDeviceList() {
        newDeviceApiViewModel.getMyDevicelist();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.txtAddDevice:
                startActivity(new Intent(activity, NewDeviceSupportActivity.class));
                break;
        }
    }
}
