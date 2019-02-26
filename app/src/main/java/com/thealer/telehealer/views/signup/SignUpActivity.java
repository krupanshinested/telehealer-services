package com.thealer.telehealer.views.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.onboarding.OnBoardingActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
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

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.common.UserType.isUserAssistant;
import static com.thealer.telehealer.common.UserType.isUserDoctor;
import static com.thealer.telehealer.common.UserType.isUserPatient;

/**
 * Created by Aswin on 11,October,2018
 */
public class SignUpActivity extends BaseActivity implements View.OnClickListener, OnViewChangeInterface, OnActionCompleteInterface,
        SuccessViewInterface, AttachObserverInterface {

    private static final java.lang.String CURRENT_STEP = "current_step";
    private int currentStep = 1;
    private static Stack<java.lang.String> viewInfoStack = new Stack<>();
    public CreateUserRequestModel createUserRequestModel;

    public ImageView backIv;
    private TextView signupToolbarTitleTv;
    public TextView nextTv;
    public ImageView closeIv;
    private TextView viewInfoTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUserType();
        initView();

        boolean isVerifyOtp = false;
        if (getIntent().getExtras() != null) {
            isVerifyOtp = getIntent().getExtras().getBoolean(ArgumentKeys.IS_VERIFY_OTP, false);
        }

        if (isVerifyOtp) {
            showOtpVerification();
        } else {
            if (savedInstanceState != null) {
                currentStep = savedInstanceState.getInt(CURRENT_STEP);
                setCurrentStep();
            } else {
                setFragment(null);
            }
        }
    }

    private void showOtpVerification() {
        signupToolbarTitleTv.setVisibility(View.GONE);
        OtpVerificationFragment otpVerificationFragment = new OtpVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ArgumentKeys.IS_VERIFY_OTP, true);
        otpVerificationFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.signup_fragment_container, otpVerificationFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CURRENT_STEP, currentStep);
    }

    private void setUserType() {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            appPreference.setInt(Constants.USER_TYPE, Constants.TYPE_PATIENT);
        }
    }

    private void initView() {
        backIv = (ImageView) findViewById(R.id.back_iv);
        signupToolbarTitleTv = (TextView) findViewById(R.id.toolbar_title);
        nextTv = (TextView) findViewById(R.id.next_tv);
        closeIv = (ImageView) findViewById(R.id.close_iv);

        backIv.setOnClickListener(this);
        nextTv.setOnClickListener(this);
        closeIv.setOnClickListener(this);

        viewInfoTv = (TextView) findViewById(R.id.view_info_tv);

        createUserRequestModel = ViewModelProviders.of(this).get(CreateUserRequestModel.class);
        createUserRequestModel = new CreateUserRequestModel();

        setViewInfoText();
    }

    private Fragment getFrament() {
        closeIv.setVisibility(View.GONE);
        if (isUserPatient()) {
            switch (currentStep) {
                case 1:
                    viewInfoStack.push(getResources().getString(R.string.patient_info));
                    return new PatientRegistrationDetailFragment();
                case 2:
                    viewInfoStack.push(getResources().getString(R.string.email_info));
                    return new RegistrationEmailFragment();
                case 3:
                    viewInfoStack.push(getResources().getString(R.string.phone_info));
                    return new RegistrationMobileFragment();
                case 4:
                    viewInfoStack.push(getResources().getString(R.string.terms_and_conditions_info));
                    return new TermsAndConditionFragment();
                case 5:
                    viewInfoStack.push(getResources().getString(R.string.payment_info));
                    return new PatientChoosePaymentFragment();
                case 6:
                    viewInfoStack.push(getResources().getString(R.string.password_info));
                    return new CreatePasswordFragment();
                case 7:
                    viewInfoStack.push(getResources().getString(R.string.otp_info));
                    return new OtpVerificationFragment();
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
                            viewInfoStack.push(getResources().getString(R.string.assistant_info));
                            return new MedicalAssistantDetailFragment();
                        case 3:
                            viewInfoStack.push(getResources().getString(R.string.email_info));
                            return new RegistrationEmailFragment();
                        case 4:
                            viewInfoStack.push(getResources().getString(R.string.phone_info));
                            return new RegistrationMobileFragment();
                        case 5:
                            viewInfoStack.push(getResources().getString(R.string.terms_and_conditions_info));
                            return new TermsAndConditionFragment();
                        case 6:
                            viewInfoStack.push(getResources().getString(R.string.ma_certificate_info));
                            return new MedicalAssistantCertificateUploadFragment();
                        case 7:
                            return new MedicalAssistantCertificatePreviewFragment();
                        case 8:
                            viewInfoStack.push(getResources().getString(R.string.password_info));
                            return new CreatePasswordFragment();
                        case 9:
                            viewInfoStack.push(getResources().getString(R.string.otp_info));
                            return new OtpVerificationFragment();
                        default:
                            currentStep--;
                            return null;
                    }
                } else if (isUserDoctor()) {
                    switch (currentStep) {
                        case 2:
                            viewInfoStack.push(getString(R.string.what_is_your_name));
                            return new DoctorSearchNameFragment();
                        case 3:
                            viewInfoStack.push(getString(R.string.doctor_select_profile_info));
                            return new DoctorListFragment();
                        case 4:
                            viewInfoStack.push(getString(R.string.doctor_detail_info));
                            return new CreateDoctorDetailFragment();
                        case 5:
                            viewInfoStack.push(getString(R.string.doctor_driving_license_hint));
                            return new DoctorDriverLicenseFragment();
                        case 6:
                            viewInfoStack.push(getString(R.string.doctor_certificate_hint));
                            return new DoctorCertificateFragment();
                        case 7:
                            viewInfoStack.push(getString(R.string.email_info));
                            return new RegistrationEmailFragment();
                        case 8:
                            viewInfoStack.push(getResources().getString(R.string.phone_info));
                            return new RegistrationMobileFragment();
                        case 9:
                            viewInfoStack.push(getString(R.string.release_info));
                            return new DoctorRegistrationInfoFragment();
                        case 10:
                            viewInfoStack.push(getResources().getString(R.string.terms_and_conditions_info));
                            return new TermsAndConditionFragment();
                        case 11:
                            viewInfoStack.push(getResources().getString(R.string.password_info));
                            return new CreatePasswordFragment();
                        case 12:
                            viewInfoStack.push(getResources().getString(R.string.otp_info));
                            return new OtpVerificationFragment();
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
            signupToolbarTitleTv.setText(currentStep + " of 7");
        else {
            if (currentStep == 1)
                signupToolbarTitleTv.setVisibility(View.GONE);
            else {
                signupToolbarTitleTv.setVisibility(View.VISIBLE);
                if (isUserAssistant()) {
                    signupToolbarTitleTv.setText(currentStep - 1 + " of 8");
                }
                if (isUserDoctor()) {
                    signupToolbarTitleTv.setText(currentStep - 1 + " of 11");
                }
            }
        }
    }

    public void setFragment(Bundle bundle) {
        Fragment fragment = getFrament();

        if (fragment != null) {

            fragment.setArguments(bundle);
            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.signup_fragment_container, fragment)
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

        getSupportFragmentManager().beginTransaction().replace(R.id.signup_fragment_container, fragment)
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
                appPreference.deletePreference();
                startActivity(new Intent(this, OnBoardingActivity.class));
                finish();
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

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager() != null && getSupportFragmentManager().getBackStackEntryCount() > 1) {

            if (getSupportFragmentManager().getFragments().get(0) instanceof OtpVerificationFragment || getCurrentFragment() instanceof OtpVerificationFragment) {

                showAlertDialog();

            } else if (getSupportFragmentManager().getFragments().get(0) instanceof PatientUploadInsuranceFragment || getCurrentFragment() instanceof PatientUploadInsuranceFragment) {

                getSupportFragmentManager().popBackStack();

            } else {

                getSupportFragmentManager().popBackStack();

                --currentStep;
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
            nextTv.setVisibility(View.GONE);
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
            appPreference.setInt(Constants.QUICK_LOGIN_TYPE, -1);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ArgumentKeys.IS_FROM_SIGNUP, true);
            startActivity(new Intent(SignUpActivity.this, QuickLoginActivity.class).putExtras(bundle));
            finish();
        }
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
}

