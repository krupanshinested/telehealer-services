package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.AssociationAdapterListModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiViewModel;
import com.thealer.telehealer.apilayer.models.setDevice.SetDeviceResponseModel;
import com.thealer.telehealer.apilayer.models.unique.UniqueResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.home.DoctorPatientListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewDeviceDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private AppCompatTextView deviceDescription2, deviceDescription1, deviceDescriptionVital, deviceSmsPhysician;
    private AppCompatEditText edtDeviceId;
    private AppCompatTextView txtSubmit;
    private AppCompatImageView deviceTv;
    private LinearLayout linkLayout;
    AppCompatTextView deviceLink;
    private AssociationApiViewModel associationApiViewModel;
    private AssociationApiViewModel associationUniqueApiViewModel;
    private String uniqueUrl = "";
    private NewDeviceSetApiViewModel newDeviceSetApiViewModel;
    private MyDeviceListApiResponseModel.Devices myDeviceDetail;
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
        newDeviceSetApiViewModel.baseApiSetDeviceResponseModelMutableLiveData.observe(this, new Observer<SetDeviceResponseModel>() {
            @Override
            public void onChanged(SetDeviceResponseModel setDeviceResponseModel) {
                finish();
                txtSubmit.setClickable(true);
                Constants.NEW_DEVICE_SUPPORT_ACTIVITY.finishScreen();
            }
        });

        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        associationUniqueApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                doctorGroupedAssociations = new ArrayList(baseApiResponseModels);
                didReceivedResult();
            }
        });

        associationUniqueApiViewModel.baseUniqueApiResponseModelMutableLiveData.observe(this, new Observer<UniqueResponseModel>() {
            @Override
            public void onChanged(UniqueResponseModel uniqueResponseModel) {
                uniqueUrl = uniqueResponseModel.getData().getExternal_id();
                deviceLink.setText("" + uniqueUrl);
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

        activity = this;
        backIv = findViewById(R.id.back_iv);
        deviceDescriptionVital = findViewById(R.id.device_description_vital);
        deviceSmsPhysician = findViewById(R.id.device_sms_physician);
        linkLayout = findViewById(R.id.linkLayout);
        deviceTv = findViewById(R.id.deviceTv);
        deviceLink = findViewById(R.id.device_link);
        txtSubmit = findViewById(R.id.txtSubmit);
        edtDeviceId = findViewById(R.id.edt_device_id);
        toolbarTitle = findViewById(R.id.toolbar_title);
        deviceDescription2 = findViewById(R.id.device_description2);
        deviceDescription1 = findViewById(R.id.device_description1);
        backIv.setOnClickListener(this);

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
//                edtDeviceId.setText(deviceDetail.getId());
//                edtDeviceId.setEnabled(false);
//                edtDeviceId.setClickable(false);
            } else {
                myDeviceDetail = gson.fromJson(myDevice, MyDeviceListApiResponseModel.Devices.class);
                healthCareId = myDeviceDetail.getHealthcare_device().getId();
                title = myDeviceDetail.getHealthcare_device().getName();
                description = myDeviceDetail.getHealthcare_device().getDescription();
                image = myDeviceDetail.getHealthcare_device().getImage();
                edtDeviceId.setText(myDeviceDetail.getDevice_id());
                edtDeviceId.setEnabled(false);
                edtDeviceId.setClickable(false);
            }

            newDeviceCrv = findViewById(R.id.physician_crv);
            myPhysicianListAdapter = new MyPhysicianListAdapter(this, deviceFlag);
            newDeviceCrv.setLayoutManager(new LinearLayoutManager(activity));
            newDeviceCrv.setAdapter(myPhysicianListAdapter);
        }


        toolbarTitle.setText(title);
        deviceDescription2.setText(description);
        if (image != null) {
            Utils.setImageWithGlide(activity, deviceTv, image, activity.getDrawable(R.drawable.add_provider), true, true);
        }

        if (deviceFlag) {
            txtSubmit.setVisibility(View.GONE);
            deviceDescription1.setVisibility(View.GONE);
            deviceDescriptionVital.setVisibility(View.GONE);
        }

        getUniqueUrl();
        getAssociationsList(true);
    }

    private void setNewDevice() {
        txtSubmit.setClickable(false);
        ArrayList<String> smsList = new ArrayList<>();
        if (adapterListModels != null)
            for (AssociationAdapterListModel doctorGroupedAssociations : adapterListModels) {
                if (doctorGroupedAssociations.isSelectedFlag()) {
                    smsList.add(String.valueOf(doctorGroupedAssociations.getCommonUserApiResponseModel().getUser_id()));
                }
            }

        HashMap<String, Object> param = new HashMap<>();
        param.put(ApiInterface.HEALTHCARE_DEVICE_ID, healthCareId);
        param.put(ApiInterface.DEVICE_ID, edtDeviceId.getText().toString().trim());
        if (smsList.size() == 0)
            param.put(ApiInterface.SMS_ENABLED, false);
        else
            param.put(ApiInterface.SMS_ENABLED, true);
        param.put(ApiInterface.PHYSICIAN_NOTIFICATION, smsList);
        newDeviceSetApiViewModel.setDevice(param);
    }

    private void getAssociationsList(boolean isShowProgress) {
        if (UserType.isUserPatient()) {
            associationApiViewModel.getDoctorGroupedAssociations(isShowProgress);
        }
    }

    private void getUniqueUrl() {
        associationUniqueApiViewModel.getUniqueUrl();
    }

    List<AssociationAdapterListModel> adapterListModels;

    private void didReceivedResult() {
        adapterListModels = new ArrayList<>();
        for (DoctorGroupedAssociations associations : doctorGroupedAssociations) {
            if (associations.getGroup_name().equals("Others"))
                for (CommonUserApiResponseModel doctor : associations.getDoctors()) {
                    adapterListModels.add(new AssociationAdapterListModel(2, doctor));
                }
        }

        if (deviceFlag) {
            for (AssociationAdapterListModel associationAdapterListModel : adapterListModels) {
                for (MyDeviceListApiResponseModel.Devices.PhysicianNotification physicianNotification : myDeviceDetail.getPhysicianNotification()) {
                    if (physicianNotification.getUserId() == associationAdapterListModel.getCommonUserApiResponseModel().getUser_id()) {
                        associationAdapterListModel.setSelectedFlag(true);
                    }
                }
            }
        }
        myPhysicianListAdapter.setData(adapterListModels);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.copy_device_link:
                setClipboard(activity, uniqueUrl);
                break;
            case R.id.txtSubmit:
                if (edtDeviceId.getText().toString().isEmpty()) {
                    Utils.displayAlertMessage(this);
                } else
                    setNewDevice();
                break;
        }
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Url", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity, "Copied Url", Toast.LENGTH_SHORT).show();
        }
    }
}
