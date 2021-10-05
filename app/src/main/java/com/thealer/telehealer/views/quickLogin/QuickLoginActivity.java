package com.thealer.telehealer.views.quickLogin;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.biometric.BioMetricAuth;
import com.thealer.telehealer.common.biometric.BioMetricUtils;
import com.thealer.telehealer.common.biometric.BiometricInterface;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.QuickLoginBroadcastReceiver;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 30,October,2018
 */
public class QuickLoginActivity extends BaseActivity implements BiometricInterface,
        OnActionCompleteInterface, SuccessViewInterface, AttachObserverInterface {

    private LinearLayout fragmentHolder;
    private boolean isViewShown = false;
    private static final java.lang.String IS_VIEW_SHOWN = "isViewShown";
    boolean isCreateQuickLogin = false;

    private QuickLoginBroadcastReceiver quickLoginBroadcastReceiver = new QuickLoginBroadcastReceiver() {
        @Override
        public void onQuickLogin(int status) {
            Constants.DisplayQuickLogin = false;
            Utils.hideKeyboard(QuickLoginActivity.this);
            if (status == ArgumentKeys.QUICK_LOGIN_CREATED) {
                int quickLoginType = appPreference.getInt(Constants.QUICK_LOGIN_TYPE);
                if (quickLoginType == Constants.QUICK_LOGIN_TYPE_NONE) {
                    goToMainActivity();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);

                    if (quickLoginType == Constants.QUICK_LOGIN_TYPE_TOUCH) {
                        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success_title_touchid));
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.success_message_touchid));
                    } else if (quickLoginType == Constants.QUICK_LOGIN_TYPE_PIN) {
                        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success_title_pin));
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.success_message_pin));
                    }

                    showSuccessViewDialog(bundle);
                }
            } else if (status == ArgumentKeys.AUTH_FAILED || status == ArgumentKeys.AUTH_CANCELLED) {
                invalidateUser();
            } else if (appPreference.getBoolean(PreferenceConstants.IS_AUTH_PENDING)) {
                Utils.storeLastActiveTime();
                Utils.validUserToLogin(QuickLoginActivity.this);
                appPreference.setBoolean(PreferenceConstants.IS_AUTH_PENDING, false);
            } else {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestFullScreenMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklogin);



        if (savedInstanceState != null) {
            isViewShown = savedInstanceState.getBoolean(IS_VIEW_SHOWN);
        }
        if (!isViewShown) {
            initView();
        }
        appPreference.setBoolean(PreferenceConstants.IS_AUTH_PENDING, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_VIEW_SHOWN, isViewShown);
    }

    private void initView() {
        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);
        int loginType = appPreference.getInt(Constants.QUICK_LOGIN_TYPE);
        boolean isFromSignup = false;
        if (getIntent() != null) {
            if (getIntent().getExtras() != null)
                isFromSignup = getIntent().getExtras().getBoolean(ArgumentKeys.IS_FROM_SIGNUP);

            isCreateQuickLogin = getIntent().getBooleanExtra(ArgumentKeys.IS_CREATE_PIN, false);
        }

        switch (loginType) {
            case Constants.QUICK_LOGIN_TYPE_TOUCH:
                if (BioMetricUtils.isSdkVersionSupported()
                        && BioMetricUtils.isHardwareSupported(this)
                        && BioMetricUtils.isFingerprintAvailable(this)) {
                    if (!isViewShown) {
                        BioMetricAuth.showBioMetricAuth(this, this);
                    }
                } else {
                    getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), new QuickLoginPasswordFragment()).commit();
                }
                break;
            case Constants.QUICK_LOGIN_TYPE_PIN:
                boolean isRefreshToken = false;
                if (getIntent() != null)
                    isRefreshToken = getIntent().getBooleanExtra(ArgumentKeys.IS_REFRESH_TOKEN, false);

                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.IS_REFRESH_TOKEN, isRefreshToken);

                QuickLoginPinFragment quickLoginPinFragment = new QuickLoginPinFragment();
                quickLoginPinFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), quickLoginPinFragment).commit();
                break;
            case Constants.QUICK_LOGIN_TYPE_NONE:
                if (isFromSignup) {
                    goToMainActivity();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), new QuickLoginPasswordFragment()).commit();
                }
                break;
            case Constants.QUICK_LOGIN_TYPE_PASSWORD:
                getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), new QuickLoginPasswordFragment()).commit();
                break;
            default:
                int availableQuickLogin = QuickLoginUtil.getAvailableQuickLoginType(this);
                if (availableQuickLogin == Constants.QUICK_LOGIN_TYPE_TOUCH) {
                    getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), new QuickLoginTouchFragment()).commit();
                } else if (availableQuickLogin == Constants.QUICK_LOGIN_TYPE_PIN) {
                    getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), new QuickLoginPinFragment())
                            .addToBackStack(QuickLoginPinFragment.class.getSimpleName())
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), new QuickLoginPasswordFragment()).commit();
                }
        }
        isViewShown = true;

    }

    @Override
    public void onBackPressed() {
        //back press disabled
    }


    @Override
    public void onBioMetricActionComplete(String status, int code) {
        Bundle bundle = new Bundle();
        int authStatus = ArgumentKeys.AUTH_NONE;
        switch (code) {
            case Constants.BIOMETRIC_CANCEL:
                //on user click cancel
                //send the user back to previous page
                authStatus = ArgumentKeys.AUTH_CANCELLED;
                invalidateUser();
                finish();
                break;
            case Constants.BIOMETRIC_ERROR:
                //user made too many attempts with invalid finger print
                //send the user back to previous page
                authStatus = ArgumentKeys.AUTH_FAILED;
                invalidateUser();
                finish();
                break;
            case Constants.BIOMETRIC_FAILED:
                authStatus = ArgumentKeys.AUTH_FAILED;
                showToast(status);
                break;
            case Constants.BIOMETRIC_SUCCESS:
                Utils.storeLastActiveTime();
                authStatus = ArgumentKeys.AUTH_SUCCESS;
                appPreference.setBoolean(PreferenceConstants.IS_AUTH_PENDING, false);
                break;
        }
        bundle.putInt(ArgumentKeys.QUICK_LOGIN_STATUS, authStatus);
        sendQuickLoginBroadCast(bundle);
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        appPreference.setBoolean(PreferenceConstants.IS_AUTH_PENDING, false);
        if (success) {
            showSuccessViewDialog(bundle);
        } else {
            sendQuickLoginBroadCast(bundle);
        }
    }

    private void showSuccessViewDialog(Bundle bundle) {
        try {
            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(bundle);
            successViewDialogFragment.show(getSupportFragmentManager(), SuccessViewDialogFragment.class.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success) {
            if (isCreateQuickLogin) {
                Utils.storeLastActiveTime();
                appPreference.setBoolean(PreferenceConstants.IS_AUTH_PENDING, false);
                setResult(Activity.RESULT_OK);
                finish();
            } else
                goToMainActivity();
        }
    }

    private void goToMainActivity() {
        Utils.validUserToLogin(getApplicationContext());
    }

    private void sendQuickLoginBroadCast(Bundle bundle) {
        Intent intent = new Intent(getString(R.string.quick_login_broadcast_receiver));
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(quickLoginBroadcastReceiver,
                new IntentFilter(getString(R.string.quick_login_broadcast_receiver)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.hideKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(quickLoginBroadcastReceiver);
    }
}
