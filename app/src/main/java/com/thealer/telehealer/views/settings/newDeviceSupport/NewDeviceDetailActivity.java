package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.skyfishjy.library.RippleBackground;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.AssociationAdapterListModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceSetApiViewModel;
import com.thealer.telehealer.apilayer.models.setDevice.SetDeviceResponseModel;
import com.thealer.telehealer.apilayer.models.unique.UniqueResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.signup.patient.PatientRegistrationDetailFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class NewDeviceDetailActivity extends BaseActivity implements View.OnClickListener, SuccessViewInterface {
    private ImageView backIv, previousPhysician, nextPhysician;
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
    private String uniqueUrl = "", termandconditions = "";
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
    private RippleBackground contentprevious;
    private RippleBackground contentpreviouss;
    private CardView vwprevious, vwnext;
    private AppCompatTextView tvtandc;
    private CheckBox checkboxsms, checkboxcall;
    private CheckBox checkboxsmscall1, checkboxsmscall2;
    private CountryCodePicker countyAltCode;
    private PhoneNumberFormattingTextWatcher altphoneNumberFormattingTextWatcher = null;
    private EditText numberAltEt;
    private PhoneNumberUtil phoneNumberUtil;
    private RelativeLayout numberaltrl;
    private WhoAmIApiViewModel whoAmIApiViewModel;

    private void initObservers() {
        newDeviceSetApiViewModel = new ViewModelProvider(this).get(NewDeviceSetApiViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);

        newDeviceSetApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                txtSubmit.setClickable(true);
                if (errorModel.geterrorCode() == null) {
                    SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                    bundle.putBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON, false);
                    successViewDialogFragment.setArguments(bundle);
                    successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
                }else {
                    if (!errorModel.geterrorCode().isEmpty() && errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
                        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        bundle.putBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON, false);
                        successViewDialogFragment.setArguments(bundle);
                        successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
                    }
                }
            }
        });

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

                try {
                    whoAmIApiViewModel.checkWhoAmI();
                } catch (Exception e) {
                    Log.d("TAG", "proceed: " + e.getMessage());
                }

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

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                        }
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
                termandconditions = uniqueResponseModel.getData().gettermAndConditionLink();
                deviceLink.setText("" + uniqueUrl);
                if (deviceFlag) {

                    if (myDeviceDetail.getcreated_at() == null || myDeviceDetail.getcreated_at().isEmpty()) {
                        tvtandc.setVisibility(View.GONE);
                    } else {
                        String linkedText = "On " + Utils.getNewDayMonthYear(myDeviceDetail.getcreated_at()) + ", you agreed to the " + String.format("<a href=\"%s\">Terms of Service</a>.", termandconditions);
                        tvtandc.setText(Html.fromHtml(linkedText));
                        tvtandc.setMovementMethod(new TextViewLinkHandler() {
                            @Override
                            public void onLinkClick(String url) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setDataAndType(Uri.parse(termandconditions), "application/pdf");
                                Intent chooser = Intent.createChooser(browserIntent, "");
                                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(chooser);
                            }
                        });
                    }
                } else {
                    String linkedText = "By click on submit, you agreed to the " + String.format("<a href=\"%s\">Terms of Service.</a>", termandconditions);
                    tvtandc.setText(Html.fromHtml(linkedText));
                    tvtandc.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String url) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setDataAndType(Uri.parse(termandconditions), "application/pdf");
                            Intent chooser = Intent.createChooser(browserIntent, "");
                            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(chooser);
                        }
                    });
                }
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
        vwprevious = findViewById(R.id.vw_previous);
        previousPhysician = findViewById(R.id.ibtn_previous);
        contentprevious = findViewById(R.id.contentprevious);
        contentprevious.startRippleAnimation();
        deviceSmsPhysician = findViewById(R.id.device_sms_physician);
        vwnext = findViewById(R.id.vw_next);
        nextPhysician = findViewById(R.id.ibtn_next);
        contentpreviouss = findViewById(R.id.contentpreviouss);
        contentpreviouss.startRippleAnimation();
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
        checkboxsms = findViewById(R.id.checkboxsms);
        checkboxcall = findViewById(R.id.checkboxcall);

        checkboxsmscall1 = findViewById(R.id.checkboxsmscall1);
        checkboxsmscall2 = findViewById(R.id.checkboxsmscall2);
        numberaltrl = findViewById(R.id.number_alt_rl);
        countyAltCode = findViewById(R.id.county_alt_code);
        numberAltEt = findViewById(R.id.number_alt_et);
        phoneNumberUtil = PhoneNumberUtil.createInstance(this);

        tvtandc = findViewById(R.id.tv_tandc);

        backIv.setOnClickListener(this);
        deviceLink1.setOnClickListener(this);
        deviceLink2.setOnClickListener(this);
        previousPhysician.setOnClickListener(this);
        nextPhysician.setOnClickListener(this);

        addTextWatcher(numberAltEt);
        setAltHint();
        countyAltCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setAltHint();
            }
        });

        if (checkboxsms.isChecked()) {
            checkboxsmscall1.setText(String.format(getString(R.string.sms_on), UserDetailPreferenceManager.getPhone()));
            checkboxsmscall2.setText(String.format(getString(R.string.sms_on), ""));
        } else {
            checkboxsmscall1.setText(String.format(getString(R.string.call_on), UserDetailPreferenceManager.getPhone()));
            checkboxsmscall2.setText(String.format(getString(R.string.call_on), ""));
        }

        if (UserDetailPreferenceManager.getWhoAmIResponse().getDefault_vital_response().equals(Constants.Defaultvital.primary)) {
            checkboxsmscall1.setChecked(true);
            checkboxsmscall2.setChecked(false);
        } else {
            checkboxsmscall1.setChecked(false);
            checkboxsmscall2.setChecked(true);
        }

//        if (UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no() != null || !UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no().isEmpty()) {
//            try {
//                JSONArray jsonArray = new JSONArray(UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no());
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject altnumber = new JSONObject(jsonArray.getString(i));
////                    if (jsonArray.length() == i) {
//                        sb.append(altnumber.getString("code") + altnumber.getString("number"));
////                    } else if (i == 0) {
////                        sb.append(altnumber.getString("code") + altnumber.getString("number"));
////                    } else {
////                        sb.append(altnumber.getString("code") + altnumber.getString("number") + ", ");
////                    }
//                }
//                checkboxcallalt.setText(String.format(getString(R.string.call_on), sb));
//            } catch (Exception e) {
//                Log.d("TAG", "initView: " + e.getMessage());
//            }
//        }
        checkboxcall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkboxcall.setClickable(false);
                    checkboxsms.setChecked(false);
                    checkboxsms.setClickable(true);
                    checkboxsmscall1.setText(String.format(getString(R.string.call_on), UserDetailPreferenceManager.getPhone()));
                    checkboxsmscall2.setText(String.format(getString(R.string.call_on), ""));
//                    checkboxcallalt.setChecked(false);
//                    checkboxcallalt.setClickable(true);
                }
            }
        });

        checkboxsmscall1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkboxsmscall1.setClickable(false);
                    checkboxsmscall2.setClickable(true);
                    checkboxsmscall2.setChecked(false);
                }
            }
        });

        checkboxsmscall2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkboxsmscall2.setClickable(false);
                    checkboxsmscall1.setClickable(true);
                    checkboxsmscall1.setChecked(false);
                }
            }
        });

//        checkboxcallalt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    checkboxcallalt.setClickable(false);
//                    checkboxcall.setChecked(false);
//                    checkboxcall.setClickable(true);
//                    checkboxsms.setChecked(false);
//                    checkboxsms.setClickable(true);
//                }
//            }
//        });

        checkboxsms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkboxsms.setClickable(false);
                    checkboxcall.setChecked(false);
                    checkboxcall.setClickable(true);
                    checkboxsmscall1.setText(String.format(getString(R.string.sms_on), UserDetailPreferenceManager.getPhone()));
                    checkboxsmscall2.setText(String.format(getString(R.string.sms_on), ""));
//                    checkboxcallalt.setChecked(false);
//                    checkboxcallalt.setClickable(true);
                }
            }
        });

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
                if (myDeviceDetail.getsms_enabled().equals("true")) {
                    checkboxsms.setChecked(true);
                    checkboxcall.setChecked(false);
                    checkboxsms.setClickable(false);
                } else {
                    checkboxcall.setChecked(true);
                    checkboxsms.setChecked(false);
                    checkboxcall.setClickable(false);
                }
            }

            newDeviceCrv = findViewById(R.id.physician_crv);
            myPhysicianListAdapter = new MyPhysicianListAdapter(this, deviceFlag);
            newDeviceCrv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            newDeviceCrv.setAdapter(myPhysicianListAdapter);
        }

//        if (getOnlyAltNumber().isEmpty()) {
//            numberaltrl.setVisibility(View.GONE);
//            checkboxsmscall2.setVisibility(View.GONE);
//        } else {
            if (deviceFlag) {
                numberaltrl.setVisibility(View.GONE);
                checkboxsmscall2.setVisibility(View.VISIBLE);
                if (checkboxsms.isChecked()) {
                    checkboxsmscall2.setText(String.format(getString(R.string.sms_on), getAltNumber(checkboxsmscall2)));
                } else {
                    checkboxsmscall2.setText(String.format(getString(R.string.call_on), getAltNumber(checkboxsmscall2)));
                }
            } else {
                numberaltrl.setVisibility(View.VISIBLE);
                checkboxsmscall2.setVisibility(View.VISIBLE);
            }
//        }
        toolbarTitle.setText(title);
        deviceDescription2.setText(description);
        if (image != null) {
            Utils.setImageWithGlide(activity, deviceTv, image, activity.getDrawable(R.drawable.add_provider), true, true);
        }

        if (deviceFlag) {
            checkboxsms.setClickable(false);
//            checkboxsms.setEnabled(false);
            checkboxcall.setClickable(false);
//            checkboxcall.setEnabled(false);
            checkboxsmscall1.setClickable(false);
//            checkboxsmscall1.setEnabled(false);
            checkboxsmscall2.setClickable(false);
//            checkboxsmscall2.setEnabled(false);
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

    private String getOnlyAltNumber() {

        if (UserDetailPreferenceManager.getRole().equals(Constants.ROLE_PATIENT)) {
            try {
                JSONArray jsonArray = new JSONArray(UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject altnumber = new JSONObject(jsonArray.getString(i));

                    countyAltCode.setCountryForPhoneCode(Integer.parseInt(altnumber.getString("code").replace("+", "")));
                    if (jsonArray.length() == i) {
                        sb.append(altnumber.getString("number"));
                    } else if (i == 0) {
                        sb.append(altnumber.getString("number"));
                    } else {
                        sb.append(altnumber.getString("number") + ", ");
                    }
                }
                numberAltEt.setText(sb);
                return sb.toString();
            } catch (Exception e) {
                Log.d("TAG", "updateUI: " + e.getMessage());
                return "";
            }
        }

        return "";
    }

    private String getAltNumber(CheckBox checkboxcallalt) {

        if (UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no() != null && !UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no().isEmpty()) {
            checkboxcallalt.setVisibility(View.VISIBLE);
            try {
                JSONArray jsonArray = new JSONArray(UserDetailPreferenceManager.getWhoAmIResponse().getAlt_rpm_response_no());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject altnumber = new JSONObject(jsonArray.getString(i));
//                    if (jsonArray.length() == i) {

                    if (altnumber.getString("number").isEmpty()) {
                        checkboxcallalt.setVisibility(View.GONE);
                    } else {
                        checkboxcallalt.setVisibility(View.VISIBLE);
                        sb.append(altnumber.getString("code") + altnumber.getString("number"));
                    }
                }
                return sb.toString();
            } catch (Exception e) {
                Log.d("TAG", "initView: " + e.getMessage());
                return "";
            }
        } else {
            checkboxcallalt.setVisibility(View.GONE);
        }
        return "";
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

        if (smsList.size() < 1) {
            Toast.makeText(activity, "Please select physician", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> param = new HashMap<>();
        param.put(ApiInterface.HEALTHCARE_DEVICE_ID, healthCareId);
        param.put(ApiInterface.DEVICE_ID, edtDeviceId.getText().toString().trim());
        param.put(ApiInterface.TERMCONDITION, true);
//        if (smsList.size() == 0)
//            param.put(ApiInterface.SMS_ENABLED, false);
//        else
//            param.put(ApiInterface.SMS_ENABLED, true);

        if (checkboxsms.isChecked()) {
            param.put(ApiInterface.SMS_ENABLED, true);
            param.put(ApiInterface.CALL_ENABLED, false);
            param.put(ApiInterface.PHYSICIAN_NOTIFICATION_SMS, smsList);
        } else {
            param.put(ApiInterface.SMS_ENABLED, false);
            param.put(ApiInterface.CALL_ENABLED, true);
            param.put(ApiInterface.PHYSICIAN_NOTIFICATION_CALL, smsList);

//            if (checkboxcall.isChecked()) {
//                param.put(ApiInterface.PHONE, UserDetailPreferenceManager.getPhone());
//            }

//            if (checkboxcallalt.isChecked()){
//                param.put(ApiInterface.PHONE, getAltNumber());
//            }
        }
        Log.d("TAG", "setNewDevice: " + param);
        if (validateNumber()) {
            try {
                param.put(ApiInterface.PHONE, checkboxsmscall1.isChecked() ? Constants.Defaultvital.primary : Constants.Defaultvital.alternate);
                param.put(ApiInterface.default_vital, UserDetailPreferenceManager.getWhoAmIResponse().getDefault_vital_response());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", countyAltCode.getSelectedCountryCodeWithPlus());
                jsonObject.put("number", numberAltEt.getText().toString());
                JSONArray array = new JSONArray();
                array.put(jsonObject);
                param.put(ApiInterface.alt_no, array.toString());
            } catch (Exception e) {
                Log.d("TAG", "proceed: " + e.getMessage());
                txtSubmit.setClickable(true);
                Toast.makeText(this, "Some technical error. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            txtSubmit.setClickable(true);
            Toast.makeText(this, "Please update proper Alternate RPM response number", Toast.LENGTH_SHORT).show();
            return;
        }
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
            if (!associations.getGroup_name().equals("Medical Assistants"))
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

        if (adapterListModels.size() <= 1) {
            vwnext.setVisibility(View.GONE);
            vwprevious.setVisibility(View.GONE);
        } else {
            vwnext.setVisibility(View.VISIBLE);
            vwprevious.setVisibility(View.VISIBLE);
        }

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
            case R.id.ibtn_previous:
                int CurrentPos = ((LinearLayoutManager) newDeviceCrv.getLayoutManager()).findFirstVisibleItemPosition();
                if (CurrentPos > 0)
                    newDeviceCrv.scrollToPosition(CurrentPos - 1);
                break;
            case R.id.ibtn_next:
                int CurrentPosition = ((LinearLayoutManager) newDeviceCrv.getLayoutManager()).findFirstVisibleItemPosition();
                if (CurrentPosition < newDeviceCrv.getAdapter().getItemCount() - 1)
                    newDeviceCrv.scrollToPosition(CurrentPosition + 1);
                break;
        }
    }

//    public void showDialog(Activity activity){
//        final Dialog dialog = new Dialog(activity);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.custom_add_alternate_no);
//
//        countyAltCode = (CountryCodePicker) dialog.findViewById(R.id.county_alt_code);
//        numberAltEt = (EditText) dialog.findViewById(R.id.number_alt_et);
//        phoneNumberUtil = PhoneNumberUtil.createInstance(activity);
//        addTextWatcher(numberAltEt);
//        setAltHint();
//        countyAltCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
//            @Override
//            public void onCountrySelected() {
//                setAltHint();
//            }
//        });
//
//        Button cancel = (Button) dialog.findViewById(R.id.cancel);
//        Button numberadd = (Button) dialog.findViewById(R.id.number_add);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        numberadd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (validateNumber()){
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        dialog.show();
//
//    }

    private void setAltHint() {

        if (altphoneNumberFormattingTextWatcher != null) {
            numberAltEt.removeTextChangedListener(altphoneNumberFormattingTextWatcher);
        }

        altphoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher(countyAltCode.getSelectedCountryNameCode()) {
            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
            }
        };

        numberAltEt.addTextChangedListener(altphoneNumberFormattingTextWatcher);

        try {
            String countryNameCode = countyAltCode.getSelectedCountryNameCode();
            String hintNumber = String.valueOf(phoneNumberUtil.getExampleNumber(countryNameCode).getNationalNumber());
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(hintNumber, countyAltCode.getSelectedCountryNameCode());
            hintNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replace(countyAltCode.getSelectedCountryCodeWithPlus(), "").trim();
            numberAltEt.setHint(hintNumber);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
    }

    private boolean validateNumber() {
        if (!numberAltEt.getText().toString().isEmpty()) {
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(numberAltEt.getText().toString(), countyAltCode.getSelectedCountryNameCode());
                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);

                if (isValid) {
                    return true;
                } else {
                    return false;
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    private void addTextWatcher(EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        txtSubmit.setClickable(true);
        if (success) {
            finish();
            Constants.NEW_DEVICE_SUPPORT_ACTIVITY.finishScreen();
        }
    }
}

abstract class TextViewLinkHandler extends LinkMovementMethod {

    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP)
            return super.onTouchEvent(widget, buffer, event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
        if (link.length != 0) {
            onLinkClick(link[0].getURL());
        }
        return true;
    }

    abstract public void onLinkClick(String url);
}

