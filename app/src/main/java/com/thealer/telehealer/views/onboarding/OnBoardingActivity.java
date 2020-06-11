package com.thealer.telehealer.views.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.guestlogin.screens.GuestUserSignupActivity;
import com.thealer.telehealer.views.signup.SignUpActivity;

/**
 * Created by Aswin on 10,October,2018
 */
public class OnBoardingActivity extends BaseActivity {
    private OnBoardingActionFragment onBoardingActionFragment;
    private OnBoardingViewPagerFragment onBoardingViewPagerFragment;
    boolean isDetailPending;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if (getIntent().getExtras() != null) {
            isDetailPending = getIntent().getExtras().getBoolean(ArgumentKeys.IS_DETAIL_PENDING, false);
        }

        initView();
    }

    /**
     * This method is used to initialize all the variables.
     */
    private void initView() {
        onBoardingActionFragment = (OnBoardingActionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_action_holder);
        onBoardingViewPagerFragment = (OnBoardingViewPagerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_viewpager_holder);

        //This will set the current text in OnBoardingActionFragment textview with list array position
        onBoardingActionFragment.setCurrentText(0);

        //This will listen to the change in view pager scroll in OnBoardingViewPagerFragment
        onBoardingViewPagerFragment.setInterface(new OnViewPageChangeListener() {
            @Override
            public void onPageChanged(int position) {
                onBoardingActionFragment.setCurrentText(position);
            }
        });

        if (isDetailPending)
        checkUserValidation();
    }

    private void checkUserValidation() {
        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        if (whoAmIApiResponseModel != null && whoAmIApiResponseModel.getUser_activated() != null &&
                whoAmIApiResponseModel.getUser_activated().equals(Constants.ACTIVATION_PENDING)) {
            Log.d("ACTIVATION_PENDING","OnBoarding");
            Intent i=new Intent(OnBoardingActivity.this, SignUpActivity.class);
            i.putExtra(ArgumentKeys.IS_VERIFY_OTP,true);
            startActivity(i);
        }  else if (UserDetailPreferenceManager.isProfileInComplete()){
            Log.d("IS_DETAIL_PENDING","OnBoarding");
            Intent i=new Intent(OnBoardingActivity.this, SignUpActivity.class);
            i.putExtra(ArgumentKeys.IS_DETAIL_PENDING,true);
            startActivity(i);
        }
    }
}