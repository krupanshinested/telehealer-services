package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewDeviceDetailActivity extends BaseActivity implements View.OnClickListener, SuccessViewInterface {
    private ImageView backIv;
    private TextView toolbarTitle;
    private AppCompatTextView deviceDescription2, deviceDescription1, deviceDescriptionVital, deviceSmsPhysician, devicestep;
    private AppCompatTextView deviceLink1, deviceLink2;
    private AppCompatEditText edtDeviceId;
    private AppCompatTextView txtSubmit;
    private AppCompatImageView deviceTv;
    private LinearLayout linkLayout;
    String productLink1 = "";
    String productLink2 = "";
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
    AlertDialog alertDialog = null;


    private void initObservers() {
        newDeviceSetApiViewModel = new ViewModelProvider(this).get(NewDeviceSetApiViewModel.class);
        newDeviceSetApiViewModel.baseApiSetDeviceResponseModelMutableLiveData.observe(this, new Observer<SetDeviceResponseModel>() {
            @Override
            public void onChanged(SetDeviceResponseModel setDeviceResponseModel) {

//                Utils.showAlertDialogWithFinish(activity, setDeviceResponseModel.getMessage(),getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                        txtSubmit.setClickable(true);
//                        Constants.NEW_DEVICE_SUPPORT_ACTIVITY.finishScreen();
//                    }
//                });
                SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, setDeviceResponseModel.getMessage());
                bundle.putBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON, false);
                successViewDialogFragment.setArguments(bundle);
                successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

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
        devicestep = findViewById(R.id.device_step);
        deviceLink1 = findViewById(R.id.device_link1);
        deviceLink2 = findViewById(R.id.device_link2);
        backIv.setOnClickListener(this);
        deviceLink1.setOnClickListener(this);
        deviceLink2.setOnClickListener(this);

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
                productLink1 = deviceDetail.getProduct_info_link();
                productLink2 = deviceDetail.getProduct_info_link_1();
//                edtDeviceId.setText(deviceDetail.getId());
//                edtDeviceId.setEnabled(false);
//                edtDeviceId.setClickable(false);
            } else {
                myDeviceDetail = gson.fromJson(myDevice, MyDeviceListApiResponseModel.Devices.class);
                healthCareId = myDeviceDetail.getHealthcare_device().getId();
                title = myDeviceDetail.getHealthcare_device().getName();
                description = myDeviceDetail.getHealthcare_device().getDescription();
                image = myDeviceDetail.getHealthcare_device().getImage();
                productLink1 = myDeviceDetail.getHealthcare_device().getProduct_info_link();
                productLink2 = myDeviceDetail.getHealthcare_device().getProduct_info_link_1();
                edtDeviceId.setText(myDeviceDetail.getDevice_id());
                edtDeviceId.setEnabled(false);
                edtDeviceId.setClickable(false);
            }

            newDeviceCrv = findViewById(R.id.physician_crv);
            myPhysicianListAdapter = new MyPhysicianListAdapter(this, deviceFlag);
            newDeviceCrv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            newDeviceCrv.setAdapter(myPhysicianListAdapter);
        }


        toolbarTitle.setText(title);
        deviceDescription2.setText(description);
        if (image != null) {
            Utils.setImageWithGlide(activity, deviceTv, image, activity.getDrawable(R.drawable.add_provider), true, true);
        }

        if (deviceFlag) {
            txtSubmit.setVisibility(View.GONE);
//            deviceDescription1.setVisibility(View.GONE);
//            deviceSmsPhysician.setText(getString(R.string.key_device_sms_enabled_view));
            deviceSmsPhysician.setText(getString(R.string.key_device_sms_enabled));
        } else
            deviceSmsPhysician.setText(getString(R.string.key_device_sms_enabled));


        getUniqueUrl();
        getAssociationsList(true);
        String clickHereText = getString(R.string.str_new_device_dummy_desc);
        SpannableString ss = new SpannableString(getString(R.string.str_new_device_dummy_desc));
        int currentIndex = clickHereText.indexOf(getString(R.string.key_to_find_number));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog, viewGroup, false);

                AppCompatImageView btnClose = dialogView.findViewById(R.id.btn_close);
                btnClose.setOnClickListener(view -> {
                    alertDialog.dismiss();
                });
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        };
        ss.setSpan(clickableSpan, currentIndex, clickHereText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        deviceDescription1.setText(ss);
        deviceDescription1.setMovementMethod(LinkMovementMethod.getInstance());

        devicestep.setMovementMethod(LinkMovementMethod.getInstance());
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
            case R.id.device_link1:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + productLink1), "text/html");
                startActivity(intent);
                break;
            case R.id.device_link2:
                Intent link2 = new Intent(Intent.ACTION_VIEW);
                link2.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + productLink2), "text/html");
                startActivity(link2);
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

    @Override
    public void onSuccessViewCompletion(boolean success) {
        finish();
        txtSubmit.setClickable(true);
        Constants.NEW_DEVICE_SUPPORT_ACTIVITY.finishScreen();
    }
}
