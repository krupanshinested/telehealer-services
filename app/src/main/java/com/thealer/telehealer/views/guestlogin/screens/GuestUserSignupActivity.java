package com.thealer.telehealer.views.guestlogin.screens;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.signup.SignUpActivity;


public class GuestUserSignupActivity extends BaseActivity implements View.OnClickListener {

    Button signup_btn;
    TextView tv_not_now;

    public GuestUserSignupActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guest_to_signup);
        initview();
    }

    private void initview() {
        signup_btn=findViewById(R.id.signup_btn);
        tv_not_now=findViewById(R.id.tv_not_now);

        signup_btn.setOnClickListener(this::onClick);
        tv_not_now.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_btn:
                Log.d("signup_btn","GuestUserSignupActivity");
                Intent i=new Intent(GuestUserSignupActivity.this, OnBoardingActivity.class);
                i.putExtra(ArgumentKeys.IS_DETAIL_PENDING,true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;

            case R.id.tv_not_now:
                Log.d("tv_not_now","GuestUserSignupActivity");
                LocalBroadcastManager.getInstance(GuestUserSignupActivity.this).sendBroadcast(new Intent(ArgumentKeys.NOT_NOW_GUEST_LOGIN));
                gotoOnBoarding();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoOnBoarding();
    }

    private void gotoOnBoarding() {
        Intent s=new Intent(GuestUserSignupActivity.this, OnBoardingActivity.class);
        s.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(s);
        finish();
    }
}