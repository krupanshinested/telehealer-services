package com.thealer.telehealer.views.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.emailValidation.EmailValidationApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 18,January,2019
 */
public class EmailVerificationActivity extends BaseActivity implements View.OnClickListener {
    private TextView emailMessage;
    private TextView resendTv;
    private CustomButton doneBtn;

    private boolean isButtonClicked;

    private WhoAmIApiViewModel whoAmIApiViewModel;
    private EmailValidationApiViewModel emailValidationApiViewModel;
    private TextView skipTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        initViewModels();
        initView();
    }

    private void initViewModels() {
        emailValidationApiViewModel = new ViewModelProvider(this).get(EmailValidationApiViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserver(whoAmIApiViewModel);
        attachObserver(emailValidationApiViewModel);
        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    if (whoAmIApiResponseModel.isEmail_verified()) {
                        UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                        startActivity(new Intent(EmailVerificationActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Utils.showAlertDialog(EmailVerificationActivity.this, getString(R.string.alert), String.format(getString(R.string.email_validation_message), whoAmIApiResponseModel.getEmail()), getString(R.string.ok), null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isButtonClicked = false;
                                        dialog.dismiss();
                                    }
                                }, null);
                    }
                }
            }
        });
        emailValidationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    showToast(baseApiResponseModel.getMessage());
                }
            }
        });

        emailValidationApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showToast(errorModel.getMessage());
                }
            }
        });
    }

    private void initView() {
        emailMessage = (TextView) findViewById(R.id.email_message);
        resendTv = (TextView) findViewById(R.id.resend_tv);
        doneBtn = (CustomButton) findViewById(R.id.done_btn);
        skipTv = (TextView) findViewById(R.id.skip_tv);

        String email = UserDetailPreferenceManager.getEmail();

        String htmlText = "<p>%s <font color=red> %s </font>%s</p>";

        htmlText = String.format(htmlText, getString(R.string.email_verification_check), email, getString(R.string.email_verification_link));

        emailMessage.setText(Utils.fromHtml(htmlText));

        String resendText = "<p>%s <font color= %s>%s</font></p>";

        resendText = String.format(resendText, getString(R.string.email_verification_resend_title),
                getString(R.string.app_gradient_start),
                getString(R.string.email_verification_new_email));

        resendTv.setText(Utils.fromHtml(resendText));

        doneBtn.setOnClickListener(this);
        resendTv.setOnClickListener(this);
        skipTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_btn:
                isButtonClicked = true;
                whoAmIApiViewModel.checkWhoAmI();
                break;
            case R.id.resend_tv:
                emailValidationApiViewModel.requestVerificationMain(true);
                break;
            case R.id.skip_tv:
                finish();
                break;
        }
    }
}
