package com.thealer.telehealer.views.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.chat.UserKeysApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.Signal.SignalKeyManager;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.schedules.SchedulesListFragment;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.doctor.BAAFragment;
import com.thealer.telehealer.views.signup.doctor.CreateDoctorDetailFragment;
import com.thealer.telehealer.views.signup.doctor.DoctorCertificateFragment;
import com.thealer.telehealer.views.signup.doctor.DoctorDriverLicenseFragment;
import com.thealer.telehealer.views.signup.doctor.DoctorListFragment;
import com.thealer.telehealer.views.signup.doctor.DoctorRegistrationInfoFragment;
import com.thealer.telehealer.views.signup.doctor.DoctorSearchNameFragment;
import com.thealer.telehealer.views.signup.medicalAssistant.MedicalAssistantCertificatePreviewFragment;
import com.thealer.telehealer.views.signup.medicalAssistant.MedicalAssistantCertificateUploadFragment;
import com.thealer.telehealer.views.signup.medicalAssistant.MedicalAssistantDetailFragment;
import com.thealer.telehealer.views.signup.patient.PatientChoosePaymentFragment;
import com.thealer.telehealer.views.signup.patient.PatientRegistrationDetailFragment;
import com.thealer.telehealer.views.signup.patient.PatientUploadInsuranceFragment;

import java.util.Stack;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.common.UserType.isUserAssistant;
import static com.thealer.telehealer.common.UserType.isUserDoctor;
import static com.thealer.telehealer.common.UserType.isUserPatient;

/**
 * Created by Aswin on 11,October,2018
 */
public class SignUpActivity extends BaseActivity implements View.OnClickListener, OnViewChangeInterface, OnActionCompleteInterface,
        SuccessViewInterface, AttachObserverInterface, OnCloseActionInterface {

    private static final String CURRENT_STEP = "current_step";
    private int currentStep = 1;
    private static Stack<String> viewInfoStack = new Stack<>();
    public CreateUserRequestModel createUserRequestModel;

    public ImageView backIv;
    private TextView signupToolbarTitleTv;
    public TextView nextTv;
    public ImageView closeIv;
    private TextView viewInfoTv;
    private ImageView helpIv;
    boolean isVerifyOtp = false, isDetailPending = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUserType();
        initView();

        if (getIntent().getExtras() != null) {
            isVerifyOtp = getIntent().getExtras().getBoolean(ArgumentKeys.IS_VERIFY_OTP, false);
            isDetailPending = getIntent().getExtras().getBoolean(ArgumentKeys.IS_DETAIL_PENDING, false);
        }

        if (isVerifyOtp) {
            showOtpVerification();
        } if (isDetailPending){
            showUserDetail();
        } else {
            if (savedInstanceState != null) {
                currentStep = savedInstanceState.getInt(CURRENT_STEP);
                setCurrentStep();
            } else {
                setFragment(null);
            }
        }
    }

    private void showUserDetail() {

        signupToolbarTitleTv.setVisibility(View.GONE);
        if (UserType.isUserPatient()){
            currentStep = 6;
        }
        else if (UserType.isUserDoctor()){
            if (currentStep == 7 && appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_DOCTOR_SEARCH)) {
                currentStep = 9;
            }else {
                currentStep=7;
            }
        }
        else {
            currentStep = 7;
        }
        setFragment(null);
    }

    private void showOtpVerification() {
        if (UserType.isUserPatient()){
            currentStep =5;
        }
        else {
            currentStep =6;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CURRENT_STEP, currentStep);
    }

    private void setUserType() {
        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
            appPreference.setInt(Constants.USER_TYPE, Constants.TYPE_PATIENT);
        }
    }

    private void initView() {
        backIv = (ImageView) findViewById(R.id.back_iv);
        signupToolbarTitleTv = (TextView) findViewById(R.id.toolbar_title);
        nextTv = (TextView) findViewById(R.id.next_tv);
        closeIv = (ImageView) findViewById(R.id.close_iv);
        helpIv = (ImageView) findViewById(R.id.help_iv);
        viewInfoTv = (TextView) findViewById(R.id.view_info_tv);

        backIv.setOnClickListener(this);
        nextTv.setOnClickListener(this);
        closeIv.setOnClickListener(this);
        helpIv.setOnClickListener(this);
        helpIv.setVisibility(View.VISIBLE);

        createUserRequestModel = new ViewModelProvider(this).get(CreateUserRequestModel.class);
        createUserRequestModel = new CreateUserRequestModel();

        setViewInfoText();
    }

    private Fragment getFrament() {
        Bundle bundle = new Bundle();
        closeIv.setVisibility(View.GONE);
        if (isUserPatient()) {
            switch (currentStep) {
                case 1:
                    viewInfoStack.push(getString(R.string.email_info, getString(R.string.app_name)));
                    return new RegistrationEmailFragment();
                case 2:
                    viewInfoStack.push(getString(R.string.phone_info, getString(R.string.app_name)));
                    return new RegistrationMobileFragment();
                case 3:
                    viewInfoStack.push(getString(R.string.registration_email_phone_verify_info));
                    return new RegistrationEmailMobileVerificationFragment();
                case 4:
                    viewInfoStack.push(getResources().getString(R.string.password_info));
                    return new CreatePasswordFragment();
                case 5:
                    viewInfoStack.push(getResources().getString(R.string.otp_info));
                    return new OtpVerificationFragment();
                case 6:
                    viewInfoStack.push(getResources().getString(R.string.reg_patient_info));
                    return new PatientRegistrationDetailFragment();
                case 7:
                    viewInfoStack.push(getResources().getString(R.string.terms_and_conditions_info));
                    return new AgreementFragment();
                case 8:
                    viewInfoStack.push(getResources().getString(R.string.notice_to_consumer_info));
                    return new AgreementFragment();
                case 9:
                    viewInfoStack.push(getResources().getString(R.string.payment_info));
                    return new PatientChoosePaymentFragment();
                default:
                    currentStep--;
                    return null;
            }
        } else {
            if (currentStep == 1) {
                viewInfoStack.push(getResources().getString(R.string.role_info));
                return new RoleSelectionFragment();
            } else {
                if (isUserAssistant()) {
                    switch (currentStep) {
                        case 2:
                            viewInfoStack.push(getString(R.string.email_info, getString(R.string.app_name)));
                            return new RegistrationEmailFragment();
                        case 3:
                            viewInfoStack.push(getString(R.string.phone_info, getString(R.string.app_name)));
                            return new RegistrationMobileFragment();
                        case 4:
                            viewInfoStack.push(getString(R.string.registration_email_phone_verify_info));
                            return new RegistrationEmailMobileVerificationFragment();
                        case 5:
                            viewInfoStack.push(getResources().getString(R.string.password_info));
                            return new CreatePasswordFragment();
                        case 6:
                            viewInfoStack.push(getResources().getString(R.string.otp_info));
                            return new OtpVerificationFragment();
                        case 7:
                            viewInfoStack.push(getResources().getString(R.string.assistant_info));
                            return new MedicalAssistantDetailFragment();
                        case 8:
                            viewInfoStack.push(getResources().getString(R.string.terms_and_conditions_info));
                            return new AgreementFragment();
                        case 9:
                            viewInfoStack.push(getResources().getString(R.string.ma_certificate_info));
                            return new MedicalAssistantCertificateUploadFragment();
                        case 10:
                            return new MedicalAssistantCertificatePreviewFragment();
                        default:
                            currentStep--;
                            return null;
                    }
                } else if (isUserDoctor()) {
                    if (currentStep == 7 && appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_DOCTOR_SEARCH)) {
                        currentStep = 9;
                    }
                    switch (currentStep) {
                        case 2:
                            viewInfoStack.push(getString(R.string.email_info, getString(R.string.app_name)));
                            return new RegistrationEmailFragment();
                        case 3:
                            viewInfoStack.push(getString(R.string.phone_info, getString(R.string.app_name)));
                            return new RegistrationMobileFragment();
                        case 4:
                            viewInfoStack.push(getString(R.string.registration_email_phone_verify_info));
                            return new RegistrationEmailMobileVerificationFragment();
                        case 5:
                            viewInfoStack.push(getResources().getString(R.string.password_info));
                            return new CreatePasswordFragment();
                        case 6:
                            viewInfoStack.push(getResources().getString(R.string.otp_info));
                            return new OtpVerificationFragment();
                        case 7:
                            viewInfoStack.push(getString(R.string.what_is_your_name));
                            return new DoctorSearchNameFragment();
                        case 8:
                            viewInfoStack.push(getString(R.string.doctor_select_profile_info));
                            return new DoctorListFragment();
                        case 9:
                            viewInfoStack.push(getString(R.string.doctor_detail_info));
                            return new CreateDoctorDetailFragment();
                        case 10:
                            viewInfoStack.push(getString(R.string.doctor_driving_license_hint));
                            return new DoctorDriverLicenseFragment();
                        case 11:
                            viewInfoStack.push(getString(R.string.doctor_certificate_hint));
                            return new DoctorCertificateFragment();
                        case 12:
                            viewInfoStack.push(getString(R.string.release_info));
                            return new DoctorRegistrationInfoFragment();
                        case 13:
                            viewInfoStack.push(getResources().getString(R.string.terms_and_conditions_info));
                            return new AgreementFragment();
                        case 14:
                            viewInfoStack.push(getResources().getString(R.string.baa_info));
                            return new BAAFragment();
                        default:
                            currentStep--;
                            return null;
                    }
                }
                currentStep--;
                return null;
            }
        }
    }

    private void setCurrentStep() {
        if (isUserPatient())
            signupToolbarTitleTv.setText(getString(R.string.setup) + " " + currentStep + " of 8");
        else {
            if (currentStep == 1)
                signupToolbarTitleTv.setVisibility(View.GONE);
            else {
                signupToolbarTitleTv.setVisibility(View.VISIBLE);
                if (isUserAssistant()) {
                    signupToolbarTitleTv.setText(getString(R.string.setup) + " " + (currentStep - 1) + " of 9");
                }
                if (isUserDoctor()) {
                    int step;
                    if (currentStep < 5) {
                        step = currentStep - 1;
                    } else if (currentStep <= 7) {
                        step = 4;
                    } else {
                        step = currentStep - 3;
                    }
                    signupToolbarTitleTv.setText(getString(R.string.setup) + " " + step + " of 11");
                }
            }
        }
    }

    public void setFragment(Bundle bundle) {
        Fragment fragment = getFrament();

        if (fragment != null) {

            fragment.setArguments(bundle);
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                        .replace(R.id.signup_fragment_container, fragment)
                        .addToBackStack(fragment.getClass().getSimpleName())
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            setCurrentStep();
            setViewInfoText();

        } else
            showToast("Next page not available currently");
    }

    public void addChildFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)

                .replace(R.id.signup_fragment_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.next_tv:
                proceedNext();
                break;
            case R.id.close_iv:
                UserDetailPreferenceManager.deleteAllPreference();
                startActivity(new Intent(this, OnBoardingActivity.class));
                finish();
                break;
            case R.id.help_iv:
                Utils.sendHelpEmail(this);
                break;
        }
    }

    public void proceedNext() {
        enableNext(false);
        if (getSupportFragmentManager().getFragments().get(0) instanceof DoCurrentTransactionInterface) {
            DoCurrentTransactionInterface doCurrentTransactionInterface = (DoCurrentTransactionInterface) getSupportFragmentManager().getFragments().get(0);
            doCurrentTransactionInterface.doCurrentTransaction();
        } else {
            if (getCurrentFragment() instanceof DoCurrentTransactionInterface) {
                ((DoCurrentTransactionInterface) getCurrentFragment()).doCurrentTransaction();
            }
        }
    }

    private void changeFragment(Bundle bundle) {
        ++currentStep;
        setFragment(bundle);
    }

    private boolean isAllowedToGoPreviousStep() {
        Fragment currentFragment =  getSupportFragmentManager().getFragments().get(0);
        if (currentFragment instanceof OtpVerificationFragment || getCurrentFragment() instanceof OtpVerificationFragment) {
            return false;
        } else if (currentFragment instanceof PatientRegistrationDetailFragment || getCurrentFragment() instanceof PatientRegistrationDetailFragment) {
            return false;
        } else if (currentFragment instanceof MedicalAssistantDetailFragment || getCurrentFragment() instanceof MedicalAssistantDetailFragment) {
            return false;
        } else if (!appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_DOCTOR_SEARCH) && (currentFragment instanceof DoctorSearchNameFragment || getCurrentFragment() instanceof DoctorSearchNameFragment)) {
            return false;
        } else if (appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_DOCTOR_SEARCH) && (currentFragment instanceof CreateDoctorDetailFragment || getCurrentFragment() instanceof CreateDoctorDetailFragment)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager() != null && getSupportFragmentManager().getBackStackEntryCount() > 1) {

            if  (!isAllowedToGoPreviousStep()) {
                Utils.showAlertDialog(this, getString(R.string.alert), getString(R.string.do_you_want_to_close),
                        getString(R.string.ok), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(SignUpActivity.this, OnBoardingActivity.class));
                                finish();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

            } else if (getSupportFragmentManager().getFragments().get(0) instanceof PatientUploadInsuranceFragment || getCurrentFragment() instanceof PatientUploadInsuranceFragment) {

                getSupportFragmentManager().popBackStack();

            } else {
                getSupportFragmentManager().popBackStack();
                --currentStep;
                if (currentStep == 6 && UserType.isUserDoctor() && appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_DOCTOR_SEARCH)) {
                    currentStep = 4;
                }
                setCurrentStep();

                if (!viewInfoStack.isEmpty()) {
                    viewInfoStack.pop();
                    setViewInfoText();
                }
            }

        } else {
            startActivity(new Intent(this, OnBoardingActivity.class));
            finish();
        }
    }

    private void setViewInfoText() {
        if (viewInfoTv != null && !viewInfoStack.isEmpty())
            viewInfoTv.setText(viewInfoStack.peek());
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
        if (hideOrShow)
            nextTv.setVisibility(View.VISIBLE);
        else
            nextTv.setVisibility(View.INVISIBLE);
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
            signupToolbarTitleTv.setVisibility(View.VISIBLE);
        else
            signupToolbarTitleTv.setVisibility(View.GONE);

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {
        if (hideOrShow)
            backIv.setVisibility(View.VISIBLE);
        else
            backIv.setVisibility(View.GONE);
    }

    @Override
    public void updateNextTitle(String nextTitle) {
        nextTv.setText(nextTitle);
    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }


    @Override
    public void handleErrorResponse(ErrorModel errorModel) {
        super.handleErrorResponse(errorModel);
        showToast(errorModel.getMessage());
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (success) {
            changeFragment(bundle);
            enableNext(true);
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success) {
            if (UserDetailPreferenceManager.isProfileInComplete()){
                onCompletionResult(null, true, null);
                return;
            }

            checkSignalKeys();
        }
    }

    private void checkSignalKeys() {
        SignalKeyManager
                .getInstance(SignUpActivity.this, new SignalKeyManager.OnUserKeyReceivedListener() {
                    @Override
                    public void onKeyReceived(UserKeysApiResponseModel userKeysApiResponseModel) {
                        proceedLoginSuccess();
                    }
                })
                .getUserKey(null, true, true, true);
    }

    private void proceedLoginSuccess() {
        if (!isVerifyOtp) {
            appPreference.setInt(Constants.QUICK_LOGIN_TYPE, -1);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ArgumentKeys.IS_FROM_SIGNUP, true);
            startActivity(new Intent(SignUpActivity.this, QuickLoginActivity.class).putExtras(bundle));
        } else {
            Utils.validUserToLogin(SignUpActivity.this);
        }
        finish();
    }

    private Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.signup_fragment_container);
        return currentFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PermissionConstants.PERMISSION_CAM_PHOTOS) {
                CameraUtil.showImageSelectionAlert(this);
            } else if (requestCode == PermissionConstants.PERMISSION_CAMERA) {
                CameraUtil.openCamera(this);
            } else if (requestCode == PermissionConstants.PERMISSION_GALLERY) {
                CameraUtil.openGallery(this);
            } else if (requestCode == PermissionConstants.GALLERY_REQUEST_CODE || requestCode == PermissionConstants.CAMERA_REQUEST_CODE) {

                String imagePath = CameraUtil.getImagePath(this, requestCode, resultCode, data);

                if (getSupportFragmentManager().getFragments().get(0) instanceof CameraInterface) {
                    ((CameraInterface) getSupportFragmentManager().getFragments().get(0)).onImageReceived(imagePath);
                } else {
                    if (getCurrentFragment() instanceof CameraInterface) {
                        ((CameraInterface) getCurrentFragment()).onImageReceived(imagePath);
                    }
                }

            }
        }
    }

    @Override
    public void onClose(boolean isRefreshRequired) {

    }
}

