package com.thealer.telehealer.views.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.PasswordValidator;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 16,October,2018
 */
public class PasswordRequirementActivity extends BaseActivity implements View.OnClickListener {
    private TextView requirement1Tv;
    private TextView requirement2Tv;
    private TextView requirement3Tv;
    private TextView requirement4Tv;
    private TextView okBtn;
    private String password;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_password_requirement);

        initView();

        if (getIntent() != null) {
            password = getIntent().getStringExtra("password");
            validatePassword();
        }
    }


    private void validatePassword() {
        if (password == null || password.isEmpty()) {
            disableAll();
        } else {
            validateWithRegex();
        }
    }

    private void validateWithRegex() {
        PasswordValidator passwordValidator = new PasswordValidator();

        if (passwordValidator.isLengthMatch(password)) {
            enableOrDisable(requirement1Tv, true);
        } else
            enableOrDisable(requirement1Tv, false);

        if (passwordValidator.isContainUpperCase(password) &&
                passwordValidator.isContainLowerCase(password) &&
                passwordValidator.isContainNumber(password))
            enableOrDisable(requirement2Tv, true);
        else
            enableOrDisable(requirement2Tv, false);


        if (passwordValidator.isContainRepeatedCharacter(password))
            enableOrDisable(requirement3Tv, false);
        else
            enableOrDisable(requirement3Tv, true);


        if (passwordValidator.isContainSymbol(password))
            enableOrDisable(requirement4Tv, true);
        else
            enableOrDisable(requirement4Tv, false);

    }

    private void disableAll() {
        enableOrDisable(requirement1Tv, false);
        enableOrDisable(requirement2Tv, false);
        enableOrDisable(requirement3Tv, false);
        enableOrDisable(requirement4Tv, false);
    }

    private void enableAll() {
        enableOrDisable(requirement1Tv, true);
        enableOrDisable(requirement2Tv, true);
        enableOrDisable(requirement3Tv, true);
        enableOrDisable(requirement4Tv, true);
    }

    private void enableOrDisable(TextView textView, boolean b) {
        textView.setEnabled(b);
    }


    private void initView() {
        requirement1Tv = (TextView) findViewById(R.id.requirement1_tv);
        requirement2Tv = (TextView) findViewById(R.id.requirement2_tv);
        requirement3Tv = (TextView) findViewById(R.id.requirement3_tv);
        requirement4Tv = (TextView) findViewById(R.id.requirement4_tv);
        okBtn = (TextView) findViewById(R.id.ok_btn);

        okBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        finish();
    }
}
