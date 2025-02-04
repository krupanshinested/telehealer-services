package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.appupdate.AppUpdateResponse;
import com.thealer.telehealer.views.appupdate.AppUpdateViewModel;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.signin.SigninActivity;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Aswin on 10,October,2018
 */
public class SplashActivity extends BaseActivity {
    private boolean isSplashCreated;
    private ImageView splashIv;
    private TextView splashTv;

    private static int RC_APP_UPDATE = 1223;

    private AppUpdateViewModel appUpdateViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFullScreenMode();
        setContentView(R.layout.activity_splash);

        splashIv = (ImageView) findViewById(R.id.splash_iv);
        splashTv = (TextView) findViewById(R.id.splash_tv);

        if (!appConfig.isOtherThanTelehealer(this)) {
            Glide.with(this).load(R.raw.app_splash).apply(new RequestOptions().placeholder(getDrawable(R.drawable.app_icon))).into(splashIv);
        }

        appUpdateViewModel = new ViewModelProvider(this).get(AppUpdateViewModel.class);
        appUpdateViewModel.getBaseApiResponseModelMutableLiveData().observe(this, baseApiResponseModel -> {
            if (baseApiResponseModel instanceof AppUpdateResponse) {
                AppUpdateResponse.Data appUpdateResponse = ((AppUpdateResponse) baseApiResponseModel).getData();
                String code = appUpdateResponse.getVersionCode();
                if (code != null && !code.isEmpty()) {
                    int apiVerson = Integer.parseInt(code.replace(".", ""));
                    int currentVersion = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""));
                    if (currentVersion < apiVerson) {
                        startActivityForResult(new Intent(getApplication(), AppUpdateActivity.class)
                                .putExtra(AppUpdateActivity.EXTRA_IS_HARD_UPDATE, appUpdateResponse.isHardUpdate())
                                .putExtra(AppUpdateActivity.EXTRA_UPDATE_MESSAGE, appUpdateResponse.getUpdateDescription()), RC_APP_UPDATE);
                    } else {
                        checkAndMoveToNext(savedInstanceState, 3000);
                    }
                } else {
                    checkAndMoveToNext(savedInstanceState, 3000);
                }
            }
        });
        appUpdateViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                checkAndMoveToNext(savedInstanceState, 1000);
            }
        });
        appUpdateViewModel.checkForUpdate();
    }

    private void checkAndMoveToNext(Bundle savedInstanceState, long delay) {
        Log.e("splash", "checkandmovenext");
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
                        if(Utils.isRefreshTokenExpire()){
                            UserDetailPreferenceManager.invalidateUser();
                            startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                        }else {
                            Utils.validUserToLogin(SplashActivity.this);
                        }
                    }

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                TelehealerFirebaseMessagingService.assignToken(task.getResult());
                            }
                        }
                    });

                    finish();
                }
            }, delay);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSplashCreated", isSplashCreated);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE && resultCode == Activity.RESULT_CANCELED) {
            Log.e("splash", "cancel");
            checkAndMoveToNext(null, 1000);
        }
    }
}
