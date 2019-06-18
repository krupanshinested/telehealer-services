package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.base.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 05,December,2018
 */
public class DoctorOnBoardingActivity extends BaseActivity {

    private TextView userNameTv;
    private CircleImageView userAvatarCiv;

    private WhoAmIApiViewModel whoAmIApiViewModel;
    private SigninApiViewModel signinApiViewModel;
    private CircularProgressBar circularProgress;
    private TextView onboardingTv;
    private TextView descriptionTv;
    private TextView videoLinkTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_onboarding);

        whoAmIApiViewModel = ViewModelProviders.of(this).get(WhoAmIApiViewModel.class);
        signinApiViewModel = ViewModelProviders.of(this).get(SigninApiViewModel.class);

        attachObserver(signinApiViewModel);
        attachObserver(whoAmIApiViewModel);

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                            setUserData();

                            if (appPreference.getBoolean(PreferenceConstants.IS_USER_ACTIVATED)) {
                                signinApiViewModel.refreshToken();
                                EventRecorder.recordRegistrationWithDate("REGISTRATION_COMPLETED");
                            }
                        }
                    }
                });

        signinApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                    if (signinApiResponseModel.isSuccess()) {
                        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());
                        TelehealerFirebaseMessagingService.refresh();
                        startActivity(new Intent(DoctorOnBoardingActivity.this, HomeActivity.class));
                        finish();
                        TelehealerFirebaseMessagingService.refresh();
                    }
                }
            }
        });
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestWhoAmI();
    }

    private void requestWhoAmI() {
        whoAmIApiViewModel.checkWhoAmI();
    }

    private void initView() {
        userNameTv = (TextView) findViewById(R.id.user_name_tv);
        userAvatarCiv = (CircleImageView) findViewById(R.id.user_avatar_civ);
        circularProgress = (CircularProgressBar) findViewById(R.id.circular_progress);
        onboardingTv = (TextView) findViewById(R.id.onboarding_tv);
        descriptionTv = (TextView) findViewById(R.id.description_tv);
        videoLinkTv = (TextView) findViewById(R.id.video_link_tv);

        setUserData();

        videoLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://vimeo.com/278934065";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void setUserData() {
        userNameTv.setText("Hi " + UserDetailPreferenceManager.getFirst_name() + "!");
        Log.e(TAG, "setUserData: " + UserDetailPreferenceManager.getUser_avatar());
        Utils.setImageWithGlide(getApplicationContext(), userAvatarCiv, UserDetailPreferenceManager.getUser_avatar(), getDrawable(R.drawable.profile_placeholder), true, true);

    }

    @Override
    public void onBackPressed() {

    }
}
