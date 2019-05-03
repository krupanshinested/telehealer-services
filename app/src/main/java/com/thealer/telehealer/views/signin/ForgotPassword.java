package com.thealer.telehealer.views.signin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.signup.CreatePasswordFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.OtpVerificationFragment;
import com.thealer.telehealer.views.signup.RegistrationEmailFragment;

/**
 * Created by Aswin on 02,November,2018
 */
public class ForgotPassword extends BaseActivity implements OnViewChangeInterface, OnActionCompleteInterface,
        SuccessViewInterface, View.OnClickListener, OnCloseActionInterface {

    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;
    private LinearLayout fragmentHolder;
    private int currentStep = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ResetPasswordRequestModel resetPasswordRequestModel = ViewModelProviders.of(this).get(ResetPasswordRequestModel.class);

        initView();

        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt(Constants.CURRENT_STEP);
        } else {
            setFragment(null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.CURRENT_STEP, currentStep);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        nextTv = (TextView) findViewById(R.id.next_tv);
        closeIv = (ImageView) findViewById(R.id.close_iv);
        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);

        hideOrShowToolbarTile(false);

        backIv.setOnClickListener(this);
        closeIv.setOnClickListener(this);
        nextTv.setOnClickListener(this);

    }

    private void setFragment(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putString(Constants.WHERE_FROM, this.getClass().getSimpleName());

        Fragment fragment = getFragment(bundle);
        if (fragment != null) {

            getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    private Fragment getFragment(Bundle bundle) {
        switch (currentStep) {
            case 0:
                RegistrationEmailFragment registrationEmailFragment = new RegistrationEmailFragment();
                registrationEmailFragment.setArguments(bundle);
                return registrationEmailFragment;
            case 1:
                OtpVerificationFragment otpVerificationFragment = new OtpVerificationFragment();
                bundle.putInt(ArgumentKeys.OTP_TYPE, OtpVerificationFragment.forgot_password);
                otpVerificationFragment.setArguments(bundle);
                return otpVerificationFragment;
            case 2:
                CreatePasswordFragment createPasswordFragment = new CreatePasswordFragment();
                bundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.forgot_password);
                bundle.putBoolean(ArgumentKeys.IS_REENTER_PASSWORD, false);
                createPasswordFragment.setArguments(bundle);
                return createPasswordFragment;
            case 3:
                CreatePasswordFragment createPasswordFragment1 = new CreatePasswordFragment();
                bundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.forgot_password);
                bundle.putBoolean(ArgumentKeys.IS_REENTER_PASSWORD, true);
                createPasswordFragment1.setArguments(bundle);
                return createPasswordFragment1;
        }
        return null;
    }

    @Override
    public void enableNext(boolean enabled) {
        if (enabled) {
            nextTv.setEnabled(true);
            nextTv.setAlpha(1);
            nextTv.setTextColor(getResources().getColor(R.color.app_gradient_start));
        } else {
            nextTv.setEnabled(false);
            nextTv.setAlpha(0.5f);
            nextTv.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {
        if (hideOrShow) {
            nextTv.setVisibility(View.VISIBLE);
        } else {
            nextTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {
        if (hideOrShow)
            closeIv.setVisibility(View.VISIBLE);
        else
            closeIv.setVisibility(View.GONE);
    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {
        if (hideOrShow)
            toolbarTitle.setVisibility(View.VISIBLE);
        else
            toolbarTitle.setVisibility(View.GONE);

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {
        if (hideOrShow)
            backIv.setVisibility(View.VISIBLE);
        else
            backIv.setVisibility(View.GONE);
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (success) {
            ++currentStep;
            setFragment(bundle);
        } else {
            proceedBack();
        }
    }

    private void proceedBack() {
        --currentStep;
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                finish();
                break;
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.next_tv:
                proceedNext();
                break;
        }
    }

    public void proceedNext() {
        enableNext(false);
        DoCurrentTransactionInterface doCurrentTransactionInterface = (DoCurrentTransactionInterface) getSupportFragmentManager().getFragments().get(0);
        doCurrentTransactionInterface.doCurrentTransaction();
    }

    @Override
    public void onBackPressed() {
        if (currentStep > 0) {
            proceedBack();
        } else {
            finish();
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success) {
            finish();
        } else {
            startActivity(new Intent(this, ForgotPassword.class));
            finish();
        }
    }

    @Override
    public void attachObserver(BaseApiViewModel mViewModel) {
        super.attachObserver(mViewModel);
    }

    @Override
    public void updateNextTitle(String nextTitle) {

    }

    @Override
    public void updateTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }
}
