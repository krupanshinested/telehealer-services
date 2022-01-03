package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiViewModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import okhttp3.internal.Util;

public class NewDeviceDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private AppCompatTextView deviceDescription2, deviceDescription1, deviceDescriptionVital;
    private AppCompatEditText edtDeviceId;
    private AppCompatTextView txtSubmit;
    private LinearLayout linkLayout;
    private NewDeviceSetApiViewModel newDeviceSetApiViewModel;
    private MyDeviceListApiResponseModel.Data myDeviceDetail;
    private NewDeviceSetApiResponseModel.Data deviceDetail;
    private String healthCareId = "";
    private String title = "", description = "";
    private boolean deviceFlag = false;

    private void initObservers() {
        newDeviceSetApiViewModel = new ViewModelProvider(this).get(NewDeviceSetApiViewModel.class);
        newDeviceSetApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
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

        Gson gson = new Gson();
        if (getIntent().getExtras() != null) {
            String myDevice = getIntent().getStringExtra(ArgumentKeys.DEVICE_DETAILS);
            deviceFlag = getIntent().getBooleanExtra(ArgumentKeys.DEVICE_FLAG, false);

            if(!deviceFlag){
                deviceDetail = gson.fromJson(myDevice, NewDeviceSetApiResponseModel.Data.class);
                healthCareId = deviceDetail.getId();
                title = deviceDetail.getName();
                description = deviceDetail.getDescription();
            }else{
                myDeviceDetail = gson.fromJson(myDevice, MyDeviceListApiResponseModel.Data.class);
                healthCareId = myDeviceDetail.getHealthcare_device().getId();
                title = myDeviceDetail.getHealthcare_device().getName();
                description = myDeviceDetail.getHealthcare_device().getDescription();
            }
        }

        backIv = findViewById(R.id.back_iv);
        deviceDescriptionVital = findViewById(R.id.device_description_vital);
        linkLayout = findViewById(R.id.linkLayout);
        txtSubmit = findViewById(R.id.txtSubmit);
        edtDeviceId = findViewById(R.id.edt_device_id);
        toolbarTitle = findViewById(R.id.toolbar_title);
        deviceDescription2 = findViewById(R.id.device_description2);
        deviceDescription1 = findViewById(R.id.device_description1);
        backIv.setOnClickListener(this);
        toolbarTitle.setText(title);
        deviceDescription2.setText(description);

        if (deviceFlag) {
            edtDeviceId.setVisibility(View.GONE);
            txtSubmit.setVisibility(View.GONE);
            deviceDescription1.setVisibility(View.GONE);
            linkLayout.setVisibility(View.GONE);
            deviceDescriptionVital.setVisibility(View.GONE);
        }
    }

    private void setNewDevice() {
        newDeviceSetApiViewModel.setDevice(healthCareId, edtDeviceId.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.txtSubmit:
                if (edtDeviceId.getText().toString().isEmpty()) {
                    Utils.displayAlertMessage(this);
                } else
                    setNewDevice();
                break;
        }
    }
}
