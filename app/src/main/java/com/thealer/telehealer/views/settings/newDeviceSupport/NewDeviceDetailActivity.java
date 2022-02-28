package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiViewModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;

import java.util.ArrayList;

import okhttp3.internal.Util;

public class NewDeviceDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private AppCompatTextView deviceDescription2, deviceDescription1, deviceDescriptionVital;
    private AppCompatEditText edtDeviceId;
    private AppCompatTextView txtSubmit;
    private AppCompatImageView deviceTv;
    private LinearLayout linkLayout;
    AppCompatTextView deviceLink;
    private AssociationApiViewModel associationApiViewModel;

    private NewDeviceSetApiViewModel newDeviceSetApiViewModel;
    private MyDeviceListApiResponseModel.Data myDeviceDetail;
    private NewDeviceSetApiResponseModel.Data deviceDetail;
    private String healthCareId = "";
    private String title = "", description = "", image;
    private boolean deviceFlag = false;
    private Activity activity;
    private ArrayList<DoctorGroupedAssociations> doctorGroupedAssociations = new ArrayList<>();
    private MyPhysicianListAdapter myPhysicianListAdapter;
    private RecyclerView newDeviceCrv;


    private void initObservers() {
        newDeviceSetApiViewModel = new ViewModelProvider(this).get(NewDeviceSetApiViewModel.class);
        newDeviceSetApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                finish();
            }
        });

        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                doctorGroupedAssociations = new ArrayList(baseApiResponseModels);

                didReceivedResult();
            }
        });

        newDeviceCrv = findViewById(R.id.physician_crv);
        myPhysicianListAdapter = new MyPhysicianListAdapter(this);
        newDeviceCrv.setLayoutManager(new LinearLayoutManager(activity));
        newDeviceCrv.setAdapter(myPhysicianListAdapter);

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

            if (!deviceFlag) {
                deviceDetail = gson.fromJson(myDevice, NewDeviceSetApiResponseModel.Data.class);
                healthCareId = deviceDetail.getId();
                title = deviceDetail.getName();
                description = deviceDetail.getDescription();
                image = deviceDetail.getImage();
            } else {
                myDeviceDetail = gson.fromJson(myDevice, MyDeviceListApiResponseModel.Data.class);
                healthCareId = myDeviceDetail.getHealthcare_device().getId();
                title = myDeviceDetail.getHealthcare_device().getName();
                description = myDeviceDetail.getHealthcare_device().getDescription();
                image = myDeviceDetail.getHealthcare_device().getImage();

            }
        }

        activity = this;
        backIv = findViewById(R.id.back_iv);
        deviceDescriptionVital = findViewById(R.id.device_description_vital);
        linkLayout = findViewById(R.id.linkLayout);
        deviceTv = findViewById(R.id.deviceTv);
        deviceLink = findViewById(R.id.device_link);
        txtSubmit = findViewById(R.id.txtSubmit);
        edtDeviceId = findViewById(R.id.edt_device_id);
        toolbarTitle = findViewById(R.id.toolbar_title);
        deviceDescription2 = findViewById(R.id.device_description2);
        deviceDescription1 = findViewById(R.id.device_description1);
        backIv.setOnClickListener(this);
        toolbarTitle.setText(title);
        deviceDescription2.setText(description);
        if (image != null) {
            Utils.setImageWithGlide(activity, deviceTv, image, activity.getDrawable(R.drawable.add_provider), true, true);
        }

        if (deviceFlag) {
            edtDeviceId.setVisibility(View.GONE);
            txtSubmit.setVisibility(View.GONE);
            deviceDescription1.setVisibility(View.GONE);
            linkLayout.setVisibility(View.GONE);
            deviceDescriptionVital.setVisibility(View.GONE);
        }
        getAssociationsList(true);
    }

    private void setNewDevice() {
        newDeviceSetApiViewModel.setDevice(healthCareId, edtDeviceId.getText().toString().trim());
    }

    private void getAssociationsList(boolean isShowProgress) {
        if (UserType.isUserPatient()) {
            associationApiViewModel.getDoctorGroupedAssociations(isShowProgress);
        }
    }

    private void didReceivedResult() {
        myPhysicianListAdapter.setData(doctorGroupedAssociations);
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
