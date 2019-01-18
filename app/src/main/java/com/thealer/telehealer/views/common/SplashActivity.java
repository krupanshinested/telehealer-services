package com.thealer.telehealer.views.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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
                        startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                    } else {

                        TeleLogger.shared.initialLog();

                        if (appPreference.getInt(Constants.QUICK_LOGIN_TYPE) == -1) {
                            startActivity(new Intent(SplashActivity.this, QuickLoginActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }
                    }
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
