package com.thealer.telehealer.views.guestlogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestLoginApiResponseModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestloginViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.guestlogin.screens.GuestLoginScreensActivity;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

public class GuestLoginActivity extends BaseActivity implements View.OnClickListener,SuccessViewInterface, TextWatcher {

    private GuestloginViewModel guestloginViewModel;
    private GuestLoginApiResponseModel guestLoginApiResponseModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private PatientInvite patientInvite;

    private EditText email_et,et_phn,et_invitecode,et_name;
    private Button btn_Enter;
    private ImageView im_Close;
    private CountryCodePicker countyCode;
    private AppPreference appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_login);

        appPreference = AppPreference.getInstance(this);
        guestloginViewModel = new ViewModelProvider(this).get(GuestloginViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserver(guestloginViewModel);
        attachObserver(whoAmIApiViewModel);

        guestloginViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof GuestLoginApiResponseModel) {

                        guestLoginApiResponseModel = (GuestLoginApiResponseModel) baseApiResponseModel;
                        if (guestLoginApiResponseModel.isSuccess()) {

                            Utils.updateLastLogin();
                            //user token
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, guestLoginApiResponseModel.getData().getToken());
                            whoAmIApiViewModel.checkWhoAmI();


                        } else {
                            showToast(guestLoginApiResponseModel.getMessage());
                        }

                    }
                }else {
                    Toast.makeText(GuestLoginActivity.this, getString(R.string.failure), Toast.LENGTH_SHORT).show();
                }
            }
        });

        guestloginViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        Log.d("ErrorModel","whoAmIApiViewModel");
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, false);
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(GuestLoginActivity.this).sendBroadcast(intent);
                    }
                });

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {
                    callSuccessDialogBroadcast();
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;

                    if (isValidUser(whoAmIApiResponseModel.getRole())) {

                        appPreference.setString(PreferenceConstants.USER_EMAIL, email_et.getText().toString());
                        appPreference.setBoolean(PreferenceConstants.IS_REMEMBER_EMAIL, false);

                        UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                        appPreference.setInt(PreferenceConstants.USER_TYPE, Utils.getUserTypeFromRole(whoAmIApiResponseModel.getRole()));

                        EventRecorder.recordUserRole(whoAmIApiResponseModel.getRole());
                        EventRecorder.updateUserId(whoAmIApiResponseModel.getUser_guid());
                        EventRecorder.recordUserStatus(whoAmIApiResponseModel.getUser_activated());

                        Patientinfo patientDetails = new Patientinfo(countyCode.getSelectedCountryCodeWithPlus()+""+et_phn.getText().toString(), email_et.getText().toString(), et_invitecode.getText().toString(), et_name.getText().toString(), guestLoginApiResponseModel.getData().getUser_guid(), guestLoginApiResponseModel.getApiKey(), guestLoginApiResponseModel.getSessionId(), guestLoginApiResponseModel.getToken(), true);
                        patientInvite = new PatientInvite();
                        patientInvite.setPatientinfo(patientDetails);
                        patientInvite.setDoctorDetails(guestLoginApiResponseModel.getDoctor_details());
                        patientInvite.setApiKey(guestLoginApiResponseModel.getApiKey());
                        patientInvite.setToken(guestLoginApiResponseModel.getToken());


                    } else {
                        Dialog dialog = new AlertDialog.Builder(GuestLoginActivity.this)
                                .setTitle(getString(R.string.app_name))
                                .setMessage(String.format(getString(R.string.user_not_allowed_error), getString(R.string.app_name), getString(R.string.opposite_app)))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserDetailPreferenceManager.deleteAllPreference();
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            }
        });

        whoAmIApiViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        Log.d("ErrorModel","whoAmIApiViewModel");
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, false);
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        LocalBroadcastManager.getInstance(GuestLoginActivity.this).sendBroadcast(intent);
                    }
                });

        initView();
    }

    private void callSuccessDialogBroadcast() {
        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, true);
        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
        intent.putExtra(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
        LocalBroadcastManager.getInstance(GuestLoginActivity.this).sendBroadcast(intent);
    }

    private void showSuccesDialog() {
        Bundle succesBundle = new Bundle();
        succesBundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
        succesBundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.validating_your_waiting_room));

        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setArguments(succesBundle);
        successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
    }

    private void initView() {
        et_name=findViewById(R.id.et_name);
        email_et =findViewById(R.id.et_email);
        et_phn=findViewById(R.id.et_phn);
        et_invitecode=findViewById(R.id.et_invitecode);
        btn_Enter=findViewById(R.id.btn_Enter);
        countyCode = (CountryCodePicker) findViewById(R.id.county_code);
        im_Close=findViewById(R.id.im_Close);
        btn_Enter.setOnClickListener(this);
        im_Close.setOnClickListener(this);

        et_name.addTextChangedListener(this);
        email_et.addTextChangedListener(this);
        et_phn.addTextChangedListener(this);
        et_invitecode.addTextChangedListener(this);

        String code = appPreference.getString(PreferenceConstants.GUEST_LOGIN_COUNTRY);
        if (code == null || code.isEmpty()) {
            code = appConfig.getLocaleCountry();
        }
        countyCode.setCountryForNameCode(code);

       /* if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_DEBUG)) {
            Log.d("GuestLoginActivity","setBuildType");
            UserDetailPreferenceManager.setInstallType("telehealer-india");
        }*/

        String email =appPreference.getString(PreferenceConstants.GUEST_LOGIN_EMAIL);
        String phone =appPreference.getString(PreferenceConstants.GUEST_LOGIN_PHONE);
        String name =appPreference.getString(PreferenceConstants.GUEST_LOGIN_NAME);

        if (email!=null && !email.equalsIgnoreCase(""))
            email_et.setText(email);
        if (phone!=null && !phone.equalsIgnoreCase(""))
            et_phn.setText(phone);
        if (name!=null && !name.equalsIgnoreCase(""))
            et_name.setText(name);
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void validateUserInputs() {
        if (email_et.getText().toString().isEmpty()) {
            email_et.requestFocus();
            email_et.setError(getString(R.string.enter_email_id));
        } else if (!Utils.isEmailValid(email_et.getText().toString())) {
            email_et.requestFocus();
            email_et.setError(getString(R.string.enter_valid_email));
        } else if (et_phn.getText().toString().isEmpty()) {
            et_phn.requestFocus();
            et_phn.setError(getString(R.string.enter_your_phone_number));
        }else if (et_invitecode.getText().toString().isEmpty()) {
            et_invitecode.requestFocus();
            et_invitecode.setError(getString(R.string.enter_code));
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        appPreference.setString(PreferenceConstants.GUEST_LOGIN_EMAIL,email_et.getText().toString());
        appPreference.setString(PreferenceConstants.GUEST_LOGIN_PHONE,et_phn.getText().toString());
        appPreference.setString(PreferenceConstants.GUEST_LOGIN_NAME,et_name.getText().toString());
        appPreference.setString(PreferenceConstants.GUEST_LOGIN_COUNTRY,countyCode.getSelectedCountryNameCode());

        showSuccesDialog();
        guestloginViewModel.guestLogin(email_et.getText().toString(), countyCode.getSelectedCountryCodeWithPlus()+""+et_phn.getText().toString(),et_name.getText().toString(),et_invitecode.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Enter:
                validateUserInputs();
                break;
            case R.id.im_Close:
               finish();
                break;
        }
    }

    private boolean isValidUser(String role) {
        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_DOCTOR)) {
            return !role.equals(Constants.ROLE_PATIENT);
        } else {
            return role.equals(Constants.ROLE_PATIENT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("CallActivity", "onActivityResult");

        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        switch (requestCode) {
            case PermissionConstants.PERMISSION_CAM_MIC:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.enter_waiting_room);
                    bundle.putString(ArgumentKeys.TITLE, getString(R.string.waiting_room));
                    bundle.putString(ArgumentKeys.DESCRIPTION, getDescription());
                    bundle.putBoolean(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
                    bundle.putBoolean(ArgumentKeys.IS_SKIP_NEEDED, false);
                    bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, true);
                    bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.enter));
                    bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
                    startActivityForResult(new Intent(GuestLoginActivity.this, ContentActivity.class).putExtras(bundle), RequestID.REQ_GUEST_LOGIN);
                }
                break;

            case RequestID.REQ_GUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    Intent i = new Intent(GuestLoginActivity.this, GuestLoginScreensActivity.class);
                    i.putExtra(ArgumentKeys.GUEST_INFO, patientInvite);
                    i.putExtra(ArgumentKeys.GUEST_SCREENTYPE, ArgumentKeys.WAITING_SCREEN);
                    startActivity(i);
                }
                break;
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success){
            goToWaitingScreen();
        }
    }

    private void goToWaitingScreen() {
        if (PermissionChecker.with(GuestLoginActivity.this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            Bundle bundle = new Bundle();
            bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.enter_waiting_room);
            bundle.putString(ArgumentKeys.TITLE, getString(R.string.waiting_room));
            bundle.putString(ArgumentKeys.DESCRIPTION, getDescription());
            bundle.putBoolean(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
            bundle.putBoolean(ArgumentKeys.IS_SKIP_NEEDED, false);
            bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, true);
            bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.enter));
            bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
            startActivityForResult(new Intent(GuestLoginActivity.this, ContentActivity.class).putExtras(bundle), RequestID.REQ_GUEST_LOGIN);
        }
    }

    String getDescription(){
        String description = getString(R.string.guest_login_bottom_title)+"\n\n\n "+getString(R.string.by_pressing_enter);
        String url1 = getString(R.string.terms_and_conditions_url);
        String url2 = getString(R.string.notice_to_consumers_url);
        description += " <a href=\""+url1+"\">"+getString(R.string.terms_and_conditions)+"</a> ";
        description += " and "+" <a href=\""+url2+"\">"+getString(R.string.notice_to_consumer)+"</a> ";

        return description;
    }

    void checkFieldsForEmptyValues(){
        String phn = et_phn.getText().toString();
        String email = email_et.getText().toString();
        String invite = et_invitecode.getText().toString();
        String name = et_name.getText().toString();

        if(phn.equals("")|| email.equals("") || invite.equals("") || name.equals("")){
            btn_Enter.setEnabled(false);
            btn_Enter.setBackgroundColor(getColor(R.color.bt_very_light_gray));
        } else {
            btn_Enter.setEnabled(true);
            btn_Enter.setBackgroundColor(getColor(R.color.color_blue));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkFieldsForEmptyValues();
    }
}