package com.thealer.telehealer.views.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.requestotp.OtpVerificationResponseModel;
import com.thealer.telehealer.apilayer.models.requestotp.RequestOtpApiViewModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 15,October,2018
 */
public class OtpVerificationFragment extends BaseFragment implements View.OnClickListener, DoCurrentTransactionInterface {

    public static final int signup = 0;
    public static final int forgot_password = 1;
    public static final int reset_password = 2;

    private TextView titleTv;
    private EditText otpEt;
    private ImageView circleIv1;
    private ImageView circleIv2;
    private ImageView circleIv3;
    private ImageView circleIv4;
    private ImageView circleIv5;
    private ImageView circleIv6;
    private TextView resendTv;
    private TextView timerTv;
    private ImageView[] imageViews;
    private RequestOtpApiViewModel requestOtpApiViewModel;
    private TextView pageHintTv;
    private ProgressBar timerPb;
    private boolean isApiRequested;
    private long remainingSeconds = 0;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private CountDownTimer countDownTimer;
    private boolean isRequestWithEmail;
    private static final int Max_timer = 30;
    private View view;
    private SuccessViewDialogFragment successViewDialogFragment;
    private ResetPasswordRequestModel resetPasswordRequestModel;
    private boolean isVerifyUser = false;

    private int otpType = signup;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private OtpVerificationResponseModel otpVerificationResponseModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        initView(view);
        this.view = view;
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isApiRequested = savedInstanceState.getBoolean(Constants.IS_API_REQUESTED);
            remainingSeconds = savedInstanceState.getLong(Constants.REMAINING_SECONDS);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_API_REQUESTED, isApiRequested);
        outState.putLong(Constants.REMAINING_SECONDS, remainingSeconds);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        initView(view);
    }

    private void sendSuccessBroadcast() {
        Bundle bundle = new Bundle();
        if (otpVerificationResponseModel != null) {
            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, otpVerificationResponseModel.isSuccess());
        } else {
            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
        }
        
        String description;

        if (otpType == signup) {
            description = getString(R.string.account_created);
        } else {
            description = getString(R.string.reset_password_success);
        }

        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, description);
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver)).putExtras(bundle));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        resetPasswordRequestModel = new ViewModelProvider(getActivity()).get(ResetPasswordRequestModel.class);

        requestOtpApiViewModel = new ViewModelProvider(getActivity()).get(RequestOtpApiViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(getActivity()).get(WhoAmIApiViewModel.class);

        onViewChangeInterface.attachObserver(requestOtpApiViewModel);
        onViewChangeInterface.attachObserver(whoAmIApiViewModel);

        requestOtpApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        showToast(errorModel.getMessage() + " " + errorModel.getData());
                        if (!isRequestWithEmail) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, errorModel.isSuccess());
                            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                            bundle.putString(Constants.SUCCESS_VIEW_TITLE, ((otpType == signup) ? getString(R.string.failure) : getString(R.string.password_reset_failed)));

                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver)).putExtras(bundle));
                        } else {
                            onActionCompleteInterface.onCompletionResult(null, false, null);
                        }
                    }
                }
            }
        });

        requestOtpApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof OtpVerificationResponseModel) {

                        EventRecorder.recordRegistration("OTP_VERIFIED", UserDetailPreferenceManager.getUser_guid());

                        if (getArguments() != null && getArguments().getString(ArgumentKeys.ROLE) != null) {
                            switch (getArguments().getString(ArgumentKeys.ROLE)) {
                                case Constants.ROLE_PATIENT:
                                    EventRecorder.recordRegistrationWithDate("REGISTRATION_COMPLETED");
                                    break;
                                case Constants.ROLE_DOCTOR:
                                    break;
                                case Constants.ROLE_ASSISTANT:
                                    EventRecorder.recordRegistrationWithDate("REGISTRATION_COMPLETED");
                                    break;
                            }
                        }

                        otpVerificationResponseModel = (OtpVerificationResponseModel) baseApiResponseModel;

                        String email = appPreference.getString(PreferenceConstants.USER_EMAIL);
                        int userType = appPreference.getInt(PreferenceConstants.USER_TYPE);

                        if (!isVerifyUser) {
                            UserDetailPreferenceManager.deleteAllPreference();
                        } else {
                            UserDetailPreferenceManager.setUser_activated(Constants.ACTIVATED);
                            WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                            whoAmIApiResponseModel.setUser_activated(Constants.ACTIVATED);
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                        }

                        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, otpVerificationResponseModel.getData().getToken());
                        appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, otpVerificationResponseModel.getData().getRefresh_token());
                        appPreference.setString(PreferenceConstants.USER_NAME, otpVerificationResponseModel.getData().getName());
                        appPreference.setBoolean(PreferenceConstants.IS_USER_ACTIVATED, false);
                        appPreference.setString(PreferenceConstants.USER_EMAIL, email);
                        appPreference.setInt(PreferenceConstants.USER_TYPE, userType);
                        UserDetailPreferenceManager.setUser_guid(otpVerificationResponseModel.getData().getUser_guid());
                        UserDetailPreferenceManager.setFirst_name(otpVerificationResponseModel.getData().getName());

                        EventRecorder.updateUserId(otpVerificationResponseModel.getData().getUser_guid());

                        UserDetailPreferenceManager.didUserLoggedIn();

                        Utils.updateLastLogin();

                        if (otpType == signup) {
                            whoAmIApiViewModel.checkWhoAmI();
                        } else {
                            sendSuccessBroadcast();
                        }

                    } else {
                        if (baseApiResponseModel.isSuccess()) {
                            if (!isApiRequested) {
                                showToast(baseApiResponseModel.getMessage());
                                isApiRequested = true;
                                startTimer(Max_timer);
                            }
                        }
                    }
                }
            }
        });

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                    sendSuccessBroadcast();
                }
            }
        });

    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        otpEt = (EditText) view.findViewById(R.id.otp_et);
        circleIv1 = (ImageView) view.findViewById(R.id.circle_iv1);
        circleIv2 = (ImageView) view.findViewById(R.id.circle_iv2);
        circleIv3 = (ImageView) view.findViewById(R.id.circle_iv3);
        circleIv4 = (ImageView) view.findViewById(R.id.circle_iv4);
        circleIv5 = (ImageView) view.findViewById(R.id.circle_iv5);
        circleIv6 = (ImageView) view.findViewById(R.id.circle_iv6);
        resendTv = (TextView) view.findViewById(R.id.resend_tv);
        timerTv = (TextView) view.findViewById(R.id.timer_tv);
        timerPb = view.findViewById(R.id.timer_pb);

        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);

        if (isDeviceXLarge() && isModeLandscape())
            pageHintTv.setVisibility(View.GONE);
        else
            pageHintTv.setVisibility(View.VISIBLE);


        imageViews = new ImageView[]{circleIv1, circleIv2, circleIv3, circleIv4, circleIv5, circleIv6};

        otpEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                updateCircleImage(text.toString().length());

                if (text.toString().length() == 6) {
                    requestOtpValidation();
                }
            }
        });

        resendTv.setOnClickListener(this);

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.VISIBLE);
                toolbarTitle.setText(getString(R.string.otp));
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
                nextTv.setVisibility(View.INVISIBLE);
            }

            int type = getArguments().getInt(ArgumentKeys.OTP_TYPE);
            isVerifyUser = getArguments().getBoolean(ArgumentKeys.IS_VERIFY_OTP);
            this.otpType = type;
            switch (type) {
                case OtpVerificationFragment.forgot_password:
                case OtpVerificationFragment.reset_password:
                    isRequestWithEmail = true;
                    if (UserDetailPreferenceManager.getWhoAmIResponse() != null)
                        resetPasswordRequestModel.setEmail(UserDetailPreferenceManager.getWhoAmIResponse().getEmail());

                    onViewChangeInterface.hideOrShowBackIv(true);
                    titleTv.setText(getString(R.string.enter_the_authorization_code_sent_to) + " " + getString(R.string.your_phone_number));
                    break;
            }

            if (type == OtpVerificationFragment.reset_password) {
                toolbarTitle.setText("");
            }
        }

        if (otpType == signup) {
            String phone;

            if (!isVerifyUser) {
                CreateUserRequestModel createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);
                phone = createUserRequestModel.getUser_data().getPhone();
            } else {
                phone = UserDetailPreferenceManager.getWhoAmIResponse().getPhone();
            }

            if (TextUtils.isEmpty(phone)) {
                phone = getString(R.string.your_phone_number);
            }

            titleTv.setText(getString(R.string.enter_the_authorization_code_sent_to) + " " + phone);
        }

        if (!isApiRequested)
            requestOtp();

        if (remainingSeconds != 0 && remainingSeconds < Max_timer) {
            timerPb.setMax(Max_timer);
            int barVal = (Max_timer) - ((int) (remainingSeconds / 60 * 100) + (int) (remainingSeconds % 60));
            timerPb.setProgress(barVal);

            startTimer(Integer.parseInt(String.valueOf(remainingSeconds)));
        } else {
            resendTv.setVisibility(View.VISIBLE);
            timerPb.setVisibility(View.GONE);
            timerTv.setVisibility(View.GONE);
        }

        otpEt.setShowSoftInputOnFocus(true);
        otpEt.requestFocus();
        showOrHideSoftInputWindow(true);

        Log.e(TAG, "initView: " + appPreference.getInt(Constants.QUICK_LOGIN_TYPE));
    }

    private void requestOtpValidation() {

        Utils.hideKeyboard(getActivity());

        if (isRequestWithEmail) {

            resetPasswordRequestModel.setOtp(otpEt.getText().toString());

            if (this.otpType == OtpVerificationFragment.forgot_password) {
                onActionCompleteInterface.onCompletionResult(RequestID.REQ_FORGOT_PASSWORD, true, null);
            } else {
                onActionCompleteInterface.onCompletionResult(RequestID.REQ_RESET_PASSWORD, true, null);
            }


        } else {
            successViewDialogFragment = new SuccessViewDialogFragment();

            successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
            successViewDialogFragment.show(getActivity().getSupportFragmentManager(), SuccessViewDialogFragment.class.getSimpleName());

            requestOtpApiViewModel.validateOtpUsingGuid(otpEt.getText().toString());

        }
        otpEt.setText(null);
    }

    private void requestOtp() {
        if (isRequestWithEmail) {
            requestOtpApiViewModel.requestOtpUsingEmail(resetPasswordRequestModel.getEmail());
        } else {
            requestOtpApiViewModel.requestOtpUsingGuid();
        }
    }

    private void startTimer(int seconds) {
        timerPb.setVisibility(View.VISIBLE);
        timerTv.setVisibility(View.VISIBLE);
        resendTv.setVisibility(View.GONE);


        countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long secondsLeft = leftTimeInMilliseconds / 1000;
                int barVal = (Max_timer) - ((int) (secondsLeft / 60 * 100) + (int) (secondsLeft % 60));
                timerPb.setProgress(barVal);
                timerTv.setText(String.format("%02d", secondsLeft % 60));
                remainingSeconds = secondsLeft;
            }

            @Override
            public void onFinish() {
                timerPb.setVisibility(View.GONE);
                timerTv.setVisibility(View.GONE);
                resendTv.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
    }

    private void updateCircleImage(int length) {
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setBackground(getResources().getDrawable(R.drawable.circular_unselected_indicator));
        }

        for (int i = 0; i < length; i++) {
            imageViews[i].setBackground(getResources().getDrawable(R.drawable.circular_selected_indicator));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.hideOrShowNext(false);
        onViewChangeInterface.hideOrShowClose(true);
        onViewChangeInterface.hideOrShowBackIv(false);
        onViewChangeInterface.updateNextTitle(getString(R.string.next));

        if (otpType != OtpVerificationFragment.reset_password) {
            onViewChangeInterface.updateTitle(getString(R.string.otp));
        }

        otpEt.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.hideOrShowClose(false);
        onViewChangeInterface.hideOrShowBackIv(true);
    }

    @Override
    public void onClick(View v) {
        EventRecorder.recordRegistration("RESEND_OTP", UserDetailPreferenceManager.getUser_guid());
        requestOtp();
    }

    @Override
    public void doCurrentTransaction() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW && resultCode != Activity.RESULT_OK) {
            otpEt.requestFocus();
            showOrHideSoftInputWindow(true);
        }
    }
}
