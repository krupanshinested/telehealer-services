package com.thealer.telehealer.views.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.signin.SigninActivity;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 10,October,2018
 */
public class SplashActivity extends BaseActivity {
    private boolean isSplashCreated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFullScreenMode();
        setContentView(R.layout.activity_splash);

        if (savedInstanceState != null) {
            isSplashCreated = savedInstanceState.getBoolean("isSplashCreated");
        }

        if (!isSplashCreated) {
            isSplashCreated = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!appPreference.getBoolean(PreferenceConstants.IS_USER_LOGGED_IN)) {
                        if (!appPreference.getBoolean(PreferenceConstants.IS_FIRST_TIME)) {
                            appPreference.setBoolean(PreferenceConstants.IS_FIRST_TIME, true);
                            startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                        }
                    } else {

                        TeleLogger.shared.initialLog();

                        if (appPreference.getInt(Constants.QUICK_LOGIN_TYPE) == -1) {
                            startActivity(new Intent(SplashActivity.this, QuickLoginActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }
                    }

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    String token = instanceIdResult.getToken();
                                    Log.d("TeleHealerApplication", "received token " + token);
                                    TelehealerFirebaseMessagingService.assignToken(token);
                                }
                            });

                    finish();
                }
            }, 3000);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSplashCreated", isSplashCreated);
    }
}
