package com.thealer.telehealer.views.signin;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 31,October,2018
 */
public class SigninActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextInputLayout emailTil;
    private EditText emailEt;
    private TextInputLayout passwordTil;
    private EditText passwordEt;
    private CheckBox rememberCb;
    private Button loginBtn;
    private TextView forgetPasswordTv;
    private TextView orLoginTv;
    private LinearLayout quickLoginLl;
    private CircleImageView quickLoginCiv;
    private TextView quickLoginTv;


    private SigninApiViewModel signinApiViewModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private int quickLoginType;
    private String authToken, refreshToken;
    private int authResponse;
    private boolean isQuickLogin;

    private QuickLoginBroadcastReceiver quickLoginBroadcastReceiver = new QuickLoginBroadcastReceiver() {
        @Override
        public void onQuickLogin(int status) {
            if (status != ArgumentKeys.QUICK_LOGIN_CREATED) {
                authResponse = status;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signinApiViewModel = ViewModelProviders.of(this).get(SigninApiViewModel.class);
        whoAmIApiViewModel = ViewModelProviders.of(this).get(WhoAmIApiViewModel.class);
        attachObserver(signinApiViewModel);
        attachObserver(whoAmIApiViewModel);

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;

                    //TODO : need to remove when ma flow is done
                    if (whoAmIApiResponseModel.getRole().equals(Constants.ROLE_ASSISTANT)) {

                        Dialog dialog = new AlertDialog.Builder(SigninActivity.this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.ma_flow_not_available_currently))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        appPreference.deletePreference();
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();

                    } else if (isValidUser(whoAmIApiResponseModel.getRole())) {

                        if (!appPreference.getString(PreferenceConstants.USER_EMAIL).equals(emailEt.getText().toString())) {
                            appPreference.deletePreference();
                        }

                        setQuickLoginView();

                        if (rememberCb.isChecked()) {
                            appPreference.setString(PreferenceConstants.USER_EMAIL, emailEt.getText().toString());
                        } else {
                            appPreference.setString(PreferenceConstants.USER_EMAIL, null);
                        }

                        UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                        TelehealerFirebaseMessagingService.refresh();
                        appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, true);
                        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, authToken);
                        appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, refreshToken);
                        appPreference.setInt(PreferenceConstants.USER_TYPE, Utils.getUserTypeFromRole(whoAmIApiResponseModel.getRole()));

                        EventRecorder.recordUserRole(whoAmIApiResponseModel.getRole());
                        EventRecorder.updateUserId(whoAmIApiResponseModel.getUser_guid());
                        EventRecorder.recordUserStatus(whoAmIApiResponseModel.getUser_activated());

                        if (quickLoginType == -1) {
                            startActivity(new Intent(SigninActivity.this, QuickLoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            startActivity(new Intent(SigninActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }

                        finish();

                    } else {
                        Dialog dialog = new AlertDialog.Builder(SigninActivity.this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.user_not_allowed_error))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        appPreference.deletePreference();
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            }
        });

        signinApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof SigninApiResponseModel) {

                        SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                        if (signinApiResponseModel.isSuccess()) {

                            authToken = signinApiResponseModel.getToken();
                            refreshToken = signinApiResponseModel.getRefresh_token();
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, authToken);
                            appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, refreshToken);

                            if (isQuickLogin) {
                                appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, true);
                                goToMainActivity();
                            } else {
                                whoAmIApiViewModel.checkWhoAmI();
                            }

                        } else {
                            showToast(signinApiResponseModel.getMessage());
                        }

                    }
                }
            }
        });

        signinApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showToast(errorModel.getMessage());
                }
            }
        });

        initView();
    }

    private boolean isValidUser(String role) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_DOCTOR)) {
            return !role.equals(Constants.ROLE_PATIENT);
        } else {
            return role.equals(Constants.ROLE_PATIENT);
        }
    }

    private void initView() {
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        emailTil = (TextInputLayout) findViewById(R.id.email_til);
        emailEt = (EditText) findViewById(R.id.email_et);
        passwordTil = (TextInputLayout) findViewById(R.id.password_til);
        passwordEt = (EditText) findViewById(R.id.password_et);
        rememberCb = (CheckBox) findViewById(R.id.remember_cb);
        loginBtn = (Button) findViewById(R.id.login_btn);
        forgetPasswordTv = (TextView) findViewById(R.id.forget_password_tv);
        orLoginTv = (TextView) findViewById(R.id.or_login_tv);
        quickLoginLl = (LinearLayout) findViewById(R.id.quick_login_ll);
        quickLoginCiv = (CircleImageView) findViewById(R.id.quick_login_civ);
        quickLoginTv = (TextView) findViewById(R.id.quick_login_tv);

        toolbarTitle.setText(getString(R.string.login));

        backIv.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgetPasswordTv.setOnClickListener(this);
        quickLoginLl.setOnClickListener(this);

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (emailTil.isErrorEnabled())
                    emailTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordTil.isErrorEnabled())
                    passwordTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!appPreference.getString(PreferenceConstants.USER_EMAIL).isEmpty()) {
            emailEt.setText(appPreference.getString(PreferenceConstants.USER_EMAIL));
            rememberCb.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(quickLoginBroadcastReceiver, new IntentFilter(getString(R.string.quick_login_broadcast_receiver)));
        setQuickLoginView();
        if (authResponse == ArgumentKeys.AUTH_SUCCESS) {
            makeRefreshTokenApiCall();
        }
    }

    private void makeRefreshTokenApiCall() {
        isQuickLogin = true;
        signinApiViewModel.refreshToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(quickLoginBroadcastReceiver);
    }

    private void goToMainActivity() {
        appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, true);
        startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void setQuickLoginView() {
        quickLoginType = appPreference.getInt(Constants.QUICK_LOGIN_TYPE);

        switch (quickLoginType) {
            case Constants.QUICK_LOGIN_TYPE_PIN:
                quickLoginCiv.setImageDrawable(getDrawable(R.drawable.passcode));
                setQuickLoginText(true, getString(R.string.quick_login_with_pin));
                break;
            case Constants.QUICK_LOGIN_TYPE_TOUCH:
                quickLoginCiv.setImageDrawable(getDrawable(R.drawable.ic_fingerprint_48dp));
                setQuickLoginText(true, getString(R.string.touch_id));
                break;
            default:
                setQuickLoginText(false, null);
        }
    }

    private void setQuickLoginText(boolean visible, String text) {
        if (visible) {
            orLoginTv.setVisibility(View.VISIBLE);
            quickLoginLl.setVisibility(View.VISIBLE);
            quickLoginTv.setText(text);
        } else {
            orLoginTv.setVisibility(View.GONE);
            quickLoginLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.login_btn:
                validateUserInputs();
                break;
            case R.id.forget_password_tv:
                startActivity(new Intent(SigninActivity.this, ForgotPassword.class));
                break;
            case R.id.quick_login_ll:
                startActivity(new Intent(SigninActivity.this, QuickLoginActivity.class));
                break;
        }
    }

    private void validateUserInputs() {
        if (emailEt.getText().toString().isEmpty()) {
            emailEt.requestFocus();
            emailTil.setError(getString(R.string.enter_email_id));
        } else if (!Utils.isEmailValid(emailEt.getText().toString())) {
            emailEt.requestFocus();
            emailTil.setError(getString(R.string.enter_valid_email));
        } else if (passwordEt.getText().toString().isEmpty()) {
            passwordEt.requestFocus();
            passwordTil.setError(getString(R.string.enter_password));
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        signinApiViewModel.loginUser(emailEt.getText().toString(), passwordEt.getText().toString());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, OnBoardingActivity.class));
        finish();
    }
}
