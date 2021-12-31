package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
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
    private AppCompatEditText edtDeviceId;
    private NewDeviceSetApiViewModel newDeviceSetApiViewModel;
    private NewDeviceSetApiResponseModel newDeviceSetApiResponseModel;

    private String healthCareId = "";
    private String title = "";
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

        if(getIntent().getExtras() != null){
            healthCareId = getIntent().getStringExtra(ArgumentKeys.HEALTH_CARE_ID);
            title = getIntent().getStringExtra(ArgumentKeys.DEVICE_TITLE);
        }
        backIv = findViewById(R.id.back_iv);
        edtDeviceId = findViewById(R.id.edt_device_id);
        toolbarTitle = findViewById(R.id.toolbar_title);
        backIv.setOnClickListener(this);
        toolbarTitle.setText(title);
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
                }
                else
                    setNewDevice();
                break;
        }
    }
}
