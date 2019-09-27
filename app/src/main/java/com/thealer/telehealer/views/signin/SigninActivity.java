package com.thealer.telehealer.views.signin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Signal.SignalKeyManager;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.monitoring.MonitoringFragment;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.signup.CreatePasswordFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.OtpVerificationFragment;
import com.thealer.telehealer.views.signup.SignUpActivity;

import org.whispersystems.libsignal.logging.Log;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static java.util.Arrays.asList;

/**
 * Created by Aswin on 31,October,2018
 */
public class SigninActivity extends BaseActivity implements View.OnClickListener, OnActionCompleteInterface, OnCloseActionInterface,
        OnViewChangeInterface, SuccessViewInterface {
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextInputLayout emailTil;
    private EditText emailEt;
    private TextInputLayout passwordTil;
    private EditText passwordEt;
    private CheckBox rememberCb;
    private CustomButton loginBtn;
    private TextView forgetPasswordTv;
    private LinearLayout quickLoginLl, subFragmentHolder;
    private ImageView quickLoginCiv;
    private TextView quickLoginTv;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

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

            if (authResponse == ArgumentKeys.AUTH_SUCCESS && appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN) != null) {
                makeRefreshTokenApiCall();
            }
        }
    };
    private CountryCodePicker countryPicker;
    private Snackbar snackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signinApiViewModel = new ViewModelProvider(this).get(SigninApiViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserver(signinApiViewModel);
        attachObserver(whoAmIApiViewModel);

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;

                    if (isValidUser(whoAmIApiResponseModel.getRole())) {

                        if (!appPreference.getString(PreferenceConstants.USER_EMAIL).equals(emailEt.getText().toString())) {
                            UserDetailPreferenceManager.deleteAllPreference();
                        }

                        setQuickLoginView();

                        if (rememberCb.isChecked()) {
                            appPreference.setString(PreferenceConstants.USER_EMAIL, emailEt.getText().toString());
                            appPreference.setBoolean(PreferenceConstants.IS_REMEMBER_EMAIL, true);
                        } else {
                            appPreference.setString(PreferenceConstants.USER_EMAIL, null);
                            appPreference.setBoolean(PreferenceConstants.IS_REMEMBER_EMAIL, false);
                        }

                        UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, authToken);
                        appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, refreshToken);
                        appPreference.setInt(PreferenceConstants.USER_TYPE, Utils.getUserTypeFromRole(whoAmIApiResponseModel.getRole()));

                        EventRecorder.recordUserRole(whoAmIApiResponseModel.getRole());
                        EventRecorder.updateUserId(whoAmIApiResponseModel.getUser_guid());
                        EventRecorder.recordUserStatus(whoAmIApiResponseModel.getUser_activated());

                        Log.d("SiginActivity","trigg checkSignalKeys whoami");
                        checkSignalKeys();

                    } else {
                        Dialog dialog = new AlertDialog.Builder(SigninActivity.this)
                                .setTitle(getString(R.string.error))
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

        signinApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof SigninApiResponseModel) {

                        SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                        if (signinApiResponseModel.isSuccess()) {

                            Utils.updateLastLogin();
                            authToken = signinApiResponseModel.getToken();
                            refreshToken = signinApiResponseModel.getRefresh_token();
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, authToken);

                            if (isQuickLogin) {
                                Log.e("SiginActivity","trigg checkSignalKeys isQuickLogin");
                                checkSignalKeys();
                            } else {
                                appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, refreshToken);
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
                    if (errorModel.isLocked()) {
                        UserDetailPreferenceManager.invalidateUser();
                        setQuickLoginView();
                        Utils.showAlertDialog(SigninActivity.this, getString(R.string.error),
                                getString(R.string.account_locked_info, String.valueOf(errorModel.getLockTimeInMins() / 60)),
                                getString(R.string.reset_password), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showResetPassword();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    } else {
                        Utils.showAlertDialog(SigninActivity.this, getString(R.string.error), getString(R.string.login_error_message),
                                getString(R.string.ok), null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, null);
                    }
                }
            }
        });

        initView();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                onNetworkAvailable(true);
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                onNetworkAvailable(false);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                onNetworkAvailable(false);
            }
        };

    }

    private void checkSignalKeys() {
        Log.e("SiginActivity","checkSignalKeys");
        SignalKeyManager
                .getInstance(SigninActivity.this, new SignalKeyManager.OnUserKeyReceivedListener() {
                    @Override
                    public void onKeyReceived(UserKeysApiResponseModel userKeysApiResponseModel) {
                        Log.e("SiginActivity","checkSignalKeys respo");
                        checkUserValidation();
                    }
                })
                .getUserKey(null, true, true, true);
    }

    private void checkUserValidation() {
        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        if (whoAmIApiResponseModel != null && whoAmIApiResponseModel.getUser_activated() != null &&
                whoAmIApiResponseModel.getUser_activated().equals(Constants.ACTIVATION_PENDING)) {
            showOTPContent();
        }  else if (UserDetailPreferenceManager.isProfileInComplete()){
           showProfileIncompleteContent();
        } else {
            proceedLoginSuccess();
        }
    }

    private void showProfileIncompleteContent() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        bundle.putString(ArgumentKeys.TITLE, getString(R.string.profile_incomplete_title));
        bundle.putString(ArgumentKeys.DESCRIPTION, getString(R.string.profile_incomplete_content));
        bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed));
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BOTTOM_TEXT_NEEDED, false);

        startActivityForResult(new Intent(this, ContentActivity.class).putExtras(bundle), RequestID.REQ_PROFILE_INCOMPLETE);
    }

    private void showOTPContent() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        bundle.putString(ArgumentKeys.TITLE, getString(R.string.otp_validation_title));
        bundle.putString(ArgumentKeys.DESCRIPTION, getString(R.string.otp_validation_content));
        bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed));
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BOTTOM_TEXT_NEEDED, false);

        startActivityForResult(new Intent(this, ContentActivity.class).putExtras(bundle), RequestID.REQ_OTP_VALIDATION);
    }

    private void proceedLoginSuccess() {
        if (isQuickLogin) {
           Utils.validUserToLogin(getApplicationContext());
        } else {
            Log.d("SignInActivity","proceedLoginSuccess");
            if (quickLoginType == -1) {
                startActivity(new Intent(SigninActivity.this, QuickLoginActivity.class));
            } else
                Utils.validUserToLogin(getApplicationContext());

            finish();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_OTP_VALIDATION && resultCode == Activity.RESULT_OK){
            proceedLoginSuccess();
        } else if (requestCode == RequestID.REQ_PROFILE_INCOMPLETE && resultCode == Activity.RESULT_OK){
            proceedLoginSuccess();
        }
    }

    private void showResetPassword() {
        ResetPasswordRequestModel resetPasswordRequestModel = new ViewModelProvider(this).get(ResetPasswordRequestModel.class);
        resetPasswordRequestModel.setEmail(emailEt.getText().toString());
        resetPasswordRequestModel.setApp_type(Utils.getAppType());

        OtpVerificationFragment otpVerificationFragment = new OtpVerificationFragment();

        Bundle otpBundle = new Bundle();
        otpBundle.putInt(ArgumentKeys.OTP_TYPE, OtpVerificationFragment.reset_password);
        otpBundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
        otpVerificationFragment.setArguments(otpBundle);

        showSubFragment(otpVerificationFragment);

    }

    private void showSubFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(subFragmentHolder.getId(), fragment).commit();
    }

    private boolean isValidUser(String role) {
        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_DOCTOR)) {
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
        loginBtn = (CustomButton) findViewById(R.id.login_btn);
        forgetPasswordTv = (TextView) findViewById(R.id.forget_password_tv);
        quickLoginLl = (LinearLayout) findViewById(R.id.quick_login_ll);
        quickLoginCiv = (ImageView) findViewById(R.id.quick_login_civ);
        quickLoginTv = (TextView) findViewById(R.id.quick_login_tv);
        countryPicker = (CountryCodePicker) findViewById(R.id.country_picker);

        if (!appConfig.isOtherThanTelehealer(this)) {
            countryPicker.setVisibility(View.VISIBLE);
            if (!appConfig.isLocaleIndia()) {
                countryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                    @Override
                    public void onCountrySelected() {
                        String installType = appConfig.getInstallType(countryPicker.getSelectedCountryEnglishName());
                        if (!UserDetailPreferenceManager.getInstallType().equals(installType)) {
                            UserDetailPreferenceManager.setInstallType(installType);
                            UserDetailPreferenceManager.setCountryCode(countryPicker.getSelectedCountryNameCode());
                            setQuickLoginView();
                        }
                    }
                });

                String countryCode = UserDetailPreferenceManager.getCountryCode();
                if (countryCode == null || countryCode.isEmpty()) {
                    countryCode = appConfig.getLocaleCountry();
                }
                countryPicker.setCountryForNameCode(countryCode);
            } else {
                countryPicker.setCcpClickable(false);
            }
        }
        subFragmentHolder = (LinearLayout) findViewById(R.id.sub_fragment_holder);

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

        if (appPreference.getBoolean(PreferenceConstants.IS_REMEMBER_EMAIL) &&
                !appPreference.getString(PreferenceConstants.USER_EMAIL).isEmpty()) {
            emailEt.setText(appPreference.getString(PreferenceConstants.USER_EMAIL));
            rememberCb.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        application.removeShortCuts();

        LocalBroadcastManager.getInstance(this).registerReceiver(quickLoginBroadcastReceiver, new IntentFilter(getString(R.string.quick_login_broadcast_receiver)));
        setQuickLoginView();


        connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);

        onNetworkAvailable(Utils.isInternetEnabled(this));
    }

    public void onNetworkAvailable(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        } else {
            if (snackbar == null) {
                snackbar = showSnack(findViewById(R.id.rootLayout), getString(R.string.no_network_connection), Snackbar.LENGTH_INDEFINITE);
            }
            snackbar.show();
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
        if (connectivityManager != null && networkCallback != null){
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
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
            quickLoginLl.setVisibility(View.VISIBLE);
            quickLoginTv.setText(text);
        } else {
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            startActivity(new Intent(this, OnBoardingActivity.class));
            finish();
        }
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        switch (string) {
            case RequestID.REQ_RESET_PASSWORD:
                CreatePasswordFragment createPasswordFragment = new CreatePasswordFragment();

                Bundle passwordBundle = bundle;

                if (passwordBundle == null) {
                    passwordBundle = new Bundle();
                }
                passwordBundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.reset_password);
                passwordBundle.putString(ArgumentKeys.TITLE, getString(R.string.password));
                passwordBundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                createPasswordFragment.setArguments(passwordBundle);

                showSubFragment(createPasswordFragment);

                break;
            case RequestID.RESET_PASSWORD_OTP_VALIDATED:

                CreatePasswordFragment reEnterPassword = new CreatePasswordFragment();

                Bundle repasswordBundle = bundle;

                if (repasswordBundle == null) {
                    repasswordBundle = new Bundle();
                }
                repasswordBundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.reset_password);
                repasswordBundle.putString(ArgumentKeys.TITLE, getString(R.string.reenter_password));
                repasswordBundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                reEnterPassword.setArguments(repasswordBundle);
                showSubFragment(reEnterPassword);

                break;
        }
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

    @Override
    public void enableNext(boolean enabled) {

    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {

    }

    @Override
    public void updateNextTitle(String nextTitle) {

    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }
    }
}
