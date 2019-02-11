package com.thealer.telehealer.views.settings;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiViewModel;
import com.thealer.telehealer.apilayer.models.settings.AppointmentSlotUpdate;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.common.WebViewFragment;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.quickLogin.QuickLoginPinFragment;
import com.thealer.telehealer.views.settings.Interface.BundleReceiver;
import com.thealer.telehealer.views.settings.Interface.SettingClickListener;
import com.thealer.telehealer.views.settings.medicalAssistant.MedicalAssistantListFragment;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.CreatePasswordFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.OtpVerificationFragment;
import com.thealer.telehealer.views.signup.doctor.DoctorDetailFragment;
import com.thealer.telehealer.views.signup.medicalAssistant.MedicalAssistantDetailFragment;
import com.thealer.telehealer.views.signup.patient.PatientChoosePaymentFragment;
import com.thealer.telehealer.views.signup.patient.PatientRegistrationDetailFragment;
import com.thealer.telehealer.views.signup.patient.PatientUploadInsuranceFragment;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/15/18.
 */

public class ProfileSettingsActivity extends BaseActivity implements SettingClickListener,
        OnViewChangeInterface,
        OnActionCompleteInterface,
        SuccessViewInterface,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        ShowSubFragmentInterface,
        ChangeTitleInterface,
        OnCloseActionInterface,
        AttachObserverInterface {

    private FrameLayout mainContainer;
    private Toolbar toolbar;
    private AppBarLayout appbarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView toolbarTitle;
    private ImageView backIv, other_option_iv;
    private RelativeLayout collapseBackgroundRl;
    private TextView nextTv;
    private ImageView userProfileIv, genderIv;

    private String detailTitle = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initView();

        LocalBroadcastManager.getInstance(this).registerReceiver(profileListener, new IntentFilter(getString(R.string.profile_picture_updated)));

        if (savedInstanceState != null) {
            updateDetailTitle(savedInstanceState.getString(ArgumentKeys.CURRENT_TITLE));
            hideOrShowNext(savedInstanceState.getBoolean(ArgumentKeys.SHOW_NEXT));
            hideOrShowBackIv(savedInstanceState.getBoolean(ArgumentKeys.SHOW_BACK));
        } else {
            hideOrShowNext(false);
            updateDetailTitle(UserDetailPreferenceManager.getUserDisplayName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(profileListener);
    }

    /*
     *
     * Listener from Settings Fragment
     * */
    @Override
    public void didSelecteItem(int id) {
        switch (id) {
            case R.id.profile:
                Bundle profile = new Bundle();
                profile.putInt(ArgumentKeys.VIEW_TYPE, Constants.VIEW_MODE);
                switch (appPreference.getInt(Constants.USER_TYPE)) {
                    case Constants.TYPE_MEDICAL_ASSISTANT:
                        MedicalAssistantDetailFragment medicalAssistantDetailFragment = new MedicalAssistantDetailFragment();
                        medicalAssistantDetailFragment.setArguments(profile);
                        setFragment(medicalAssistantDetailFragment, false, true, true);
                        break;
                    case Constants.TYPE_DOCTOR:
                        DoctorDetailFragment doctorDetailFragment = new DoctorDetailFragment();
                        doctorDetailFragment.setArguments(profile);
                        setFragment(doctorDetailFragment, false, true, true);
                        break;
                    case Constants.TYPE_PATIENT:
                        PatientRegistrationDetailFragment patientRegistrationDetailFragment = new PatientRegistrationDetailFragment();
                        patientRegistrationDetailFragment.setArguments(profile);
                        setFragment(patientRegistrationDetailFragment, false, true, true);
                        break;
                }
                break;
            case R.id.medical_history:
                break;
            case R.id.settings:
                GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
                setFragment(generalSettingsFragment, false, true, true);
                break;
            case R.id.email_id:
                break;
            case R.id.phone_number:
                break;
            case R.id.change_password:

                ResetPasswordRequestModel resetPasswordRequestModel = ViewModelProviders.of(this).get(ResetPasswordRequestModel.class);
                resetPasswordRequestModel.setEmail(UserDetailPreferenceManager.getEmail());
                resetPasswordRequestModel.setApp_type(Utils.getAppType());

                CreateUserApiViewModel createUserApiViewModel = ViewModelProviders.of(this).get(CreateUserApiViewModel.class);

                updateDetailTitle(getString(R.string.change_password));
                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                setFragment(resetPasswordFragment, false, true, true);
                break;
            case R.id.appointment_slots:
                break;
            case R.id.feedback:
                updateDetailTitle(getString(R.string.feedback));
                WebViewFragment feedBackFragment = new WebViewFragment();
                feedBackFragment.urlToLoad = getString(R.string.feedback_url);
                feedBackFragment.title = getString(R.string.feedback);
                hideOrShowNext(false);
                setFragment(feedBackFragment, false, true, true);
                break;
            case R.id.terms_and_condition:
                updateDetailTitle(getString(R.string.terms_and_conditions));
                WebViewFragment termsFragment = new WebViewFragment();
                termsFragment.urlToLoad = getString(R.string.terms_and_conditions_url);
                termsFragment.title = getString(R.string.terms_and_conditions);
                hideOrShowNext(false);
                setFragment(termsFragment, false, true, true);
                break;
            case R.id.privacy_policy:
                updateDetailTitle(getString(R.string.privacy_policy));
                WebViewFragment privacyFragment = new WebViewFragment();
                privacyFragment.urlToLoad = getString(R.string.privacy_url);
                privacyFragment.title = getString(R.string.privacy_policy);
                hideOrShowNext(false);
                setFragment(privacyFragment, false, true, true);
                break;
            case R.id.signOut:
                EventRecorder.recordUserSession("logout");
                appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, false);
                PubnubUtil.shared.unsubscribe();
                startActivity(new Intent(this, SigninActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                break;
            case R.id.payments_billings:
                updateDetailTitle(getString(R.string.call_charges));
                PaymentsListingFragment paymentsListingFragment = new PaymentsListingFragment();
                hideOrShowNext(false);
                setFragment(paymentsListingFragment, false, true, true);
                break;
            case R.id.medical_assistant_ll:
                updateDetailTitle(getString(R.string.medical_assistant));
                showMedicalAssistantList();
                break;
        }
    }

    private void showMedicalAssistantList() {
        MedicalAssistantListFragment medicalAssistantListFragment = new MedicalAssistantListFragment();
        setFragment(medicalAssistantListFragment, false, true, true);
        hideOrShowNext(false);
    }

    @Override
    public void onBackPressed() {

        hideOrShowToolbarTile(false);
        if (getIntent() != null && getIntent().getExtras() != null &&
                getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == Constants.SCHEDULE_CREATION_MODE) {
            finish();
        } else {

            int maxCount = isSplitModeNeeded() ? 1 : 0;
            int currentBackStackCount = getSupportFragmentManager().getBackStackEntryCount();

            if (currentBackStackCount > maxCount) {
                getSupportFragmentManager().popBackStack();

                if ((currentBackStackCount - 1) <= maxCount) {
                    updateDetailTitle(UserDetailPreferenceManager.getUserDisplayName());
                    setExpandEnabled(true);
                    hideOrShowNext(false);
                }

            } else {
                finish();
            }
        }
    }


    /*
     *
     * Initializing the views
     * */
    private void initView() {
        mainContainer = findViewById(R.id.main_container);

        appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        userProfileIv = (ImageView) findViewById(R.id.user_profile_iv);
        genderIv = (ImageView) findViewById(R.id.gender_iv);
        collapseBackgroundRl = (RelativeLayout) findViewById(R.id.collapse_background_rl);
        backIv = findViewById(R.id.back_iv);
        other_option_iv = findViewById(R.id.other_option_iv);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));

        nextTv = findViewById(R.id.next_tv);
        toolbar.setTitle(getResources().getString(R.string.settings));
        setSupportActionBar(toolbar);

        updateProfile();

        //For appointment slot update
        AppointmentSlotUpdate appointmentSlotUpdate = ViewModelProviders.of(this).get(AppointmentSlotUpdate.class);

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (appbarLayout.isActivated()) {
                    if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                        // Collapsed
                        toolbarTitle.setVisibility(View.VISIBLE);
                        collapseBackgroundRl.setVisibility(View.INVISIBLE);

                    } else if (verticalOffset == 0) {
                        // Expanded
                        toolbarTitle.setVisibility(View.GONE);
                        collapseBackgroundRl.setVisibility(View.VISIBLE);
                    } else {
                        // Somewhere in between
                        toolbarTitle.setVisibility(View.GONE);
                        collapseBackgroundRl.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        backIv.setOnClickListener(this);
        nextTv.setOnClickListener(this);
        other_option_iv.setOnClickListener(this);

        setFragment(new ProfileSettingFragment(), true, false, false);

        WhoAmIApiViewModel whoAmIApiViewModel = ViewModelProviders.of(this).get(WhoAmIApiViewModel.class);
        attachObserver(whoAmIApiViewModel);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == Constants.SCHEDULE_CREATION_MODE) {
                PatientRegistrationDetailFragment patientRegistrationDetailFragment = new PatientRegistrationDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(ArgumentKeys.VIEW_TYPE, Constants.SCHEDULE_CREATION_MODE);
                patientRegistrationDetailFragment.setArguments(bundle);
                setFragment(patientRegistrationDetailFragment, false, true, true);
            }
        }
    }

    public void updateProfile() {
        if (UserType.isUserPatient()) {
            genderIv.setVisibility(View.VISIBLE);
            Utils.setGenderImage(ProfileSettingsActivity.this, genderIv, UserDetailPreferenceManager.getGender());
        } else {
            genderIv.setVisibility(View.GONE);
        }
        updateUserDetails();
        Utils.setImageWithGlide(ProfileSettingsActivity.this, userProfileIv, UserDetailPreferenceManager.getUser_avatar(), ProfileSettingsActivity.this.getDrawable(R.drawable.profile_placeholder), true);
    }

    public void updateUserDetails() {
        TextView userNameTv = (TextView) findViewById(R.id.user_name_tv);
        TextView userDobTv = (TextView) findViewById(R.id.user_dob_tv);

        toolbarTitle.setText(appPreference.getString(PreferenceConstants.USER_NAME));
        userNameTv.setText(UserDetailPreferenceManager.getUserDisplayName());

        switch (UserType.getUserType()) {
            case Constants.TYPE_DOCTOR:
                userDobTv.setText(UserDetailPreferenceManager.getSpeciality());
                break;
            case Constants.TYPE_PATIENT:
                userDobTv.setText(UserDetailPreferenceManager.getDob());
                break;
            case Constants.TYPE_MEDICAL_ASSISTANT:
                userDobTv.setText(UserDetailPreferenceManager.getTitle());
                break;
        }

    }

    private BroadcastReceiver profileListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProfile();
        }
    };

    private void updateDetailTitle(String detailTitle) {
        this.detailTitle = detailTitle;
        toolbarTitle.setText(detailTitle);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager()
                .findFragmentById(R.id.main_container);
    }

    private void setFragment(Fragment fragment, Boolean toBase, Boolean needToAddBackTrace, Boolean needToClearBackTrace) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (needToClearBackTrace) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        int containerId;
       /* if (toBase) {
            containerId = mainContainer.getId();
            findViewById(containerId).bringToFront();
        } else {
            //containerId = R.id.detail_container;
            //findViewById(R.id.detail_parent_container).bringToFront();
        }*/

        containerId = mainContainer.getId();
        findViewById(containerId).bringToFront();
        if (toBase) {
            setExpandEnabled(true);
        } else {
            setExpandEnabled(false);
        }

        if (needToAddBackTrace) {
            fragmentManager.beginTransaction().addToBackStack(fragment.getClass().getSimpleName()).replace(containerId, fragment).commit();
        } else {
            fragmentManager.beginTransaction().replace(containerId, fragment).commit();
        }

    }

    private void setExpandEnabled(boolean enabled) {
        appbarLayout.setExpanded(enabled, false);
        appbarLayout.setActivated(enabled);

        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

        if (enabled) {
            toolbarTitle.setVisibility(View.GONE);
            collapseBackgroundRl.setVisibility(View.VISIBLE);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        } else {
            toolbarTitle.setVisibility(View.VISIBLE);
            collapseBackgroundRl.setVisibility(View.GONE);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        }
        collapsingToolbarLayout.setLayoutParams(params);
    }


    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        switch (string) {
            case RequestID.REQ_PASSWORD_RESET_OTP:
                updateDetailTitle(getString(R.string.otp));
                OtpVerificationFragment otpVerificationFragment = new OtpVerificationFragment();

                Bundle otpBundle = bundle;

                if (otpBundle == null) {
                    otpBundle = new Bundle();
                }
                otpBundle.putInt(ArgumentKeys.OTP_TYPE, OtpVerificationFragment.reset_password);
                otpVerificationFragment.setArguments(otpBundle);
                setFragment(otpVerificationFragment, false, true, false);
                break;
            case RequestID.REQ_RESET_PASSWORD:
                updateDetailTitle(getString(R.string.password));
                CreatePasswordFragment createPasswordFragment = new CreatePasswordFragment();

                Bundle passwordBundle = bundle;

                if (passwordBundle == null) {
                    passwordBundle = new Bundle();
                }
                passwordBundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.reset_password);
                createPasswordFragment.setArguments(passwordBundle);
                setFragment(createPasswordFragment, false, true, false);

                break;
            case RequestID.RESET_PASSWORD_OTP_VALIDATED:
                updateDetailTitle(getString(R.string.reenter_password));
                CreatePasswordFragment reEnterPassword = new CreatePasswordFragment();

                Bundle repasswordBundle = bundle;

                if (repasswordBundle == null) {
                    repasswordBundle = new Bundle();
                }
                repasswordBundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.reset_password);
                reEnterPassword.setArguments(repasswordBundle);
                setFragment(reEnterPassword, false, true, false);

                break;
            case RequestID.REQ_QUICK_LOGIN_PIN:
                QuickLoginPinFragment quickLoginPinFragment = new QuickLoginPinFragment();
                setFragment(quickLoginPinFragment, false, true, false);
                break;
            case RequestID.QUICK_LOGIN_PIN_CREATED:
                didSelecteItem(R.id.settings);
                break;
            case RequestID.REQUEST_INSURANCE_CHANGE:
                PatientChoosePaymentFragment patientChoosePaymentFragment = new PatientChoosePaymentFragment();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putInt(ArgumentKeys.SCREEN_TYPE, Constants.forProfileUpdate);
                patientChoosePaymentFragment.setArguments(bundle);
                setFragment(patientChoosePaymentFragment, false, true, false);

                break;
            case RequestID.INSURANCE_REQUEST_IMAGE:
                getSupportFragmentManager().popBackStack();

                PatientUploadInsuranceFragment patientUploadInsuranceFragment = new PatientUploadInsuranceFragment();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putInt(ArgumentKeys.SCREEN_TYPE, Constants.forProfileUpdate);
                patientUploadInsuranceFragment.setArguments(bundle);
                setFragment(patientUploadInsuranceFragment, false, true, false);

                break;
            case RequestID.INSURANCE_CHANGE_RESULT:

                Bundle finalBundle = bundle;
                getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {

                        if (getCurrentFragment() instanceof BundleReceiver) {
                            ((BundleReceiver) getCurrentFragment()).didReceiveIntent(finalBundle, RequestID.INSURANCE_CHANGE_RESULT);
                            getSupportFragmentManager().removeOnBackStackChangedListener(this);
                        }

                    }
                });

                getSupportFragmentManager().popBackStack();

                break;
            case RequestID.CARD_INFORMATION_VIEW:
                updateDetailTitle(getString(R.string.card_information));
                CardInformationFragment cardInformationFragment = new CardInformationFragment();
                hideOrShowNext(false);
                setFragment(cardInformationFragment, false, true, false);

                break;

            case RequestID.TRANSACTION_DETAIL:
                updateDetailTitle("");
                PaymentDetailFragment paymentDetailFragment = new PaymentDetailFragment();
                paymentDetailFragment.setArguments(bundle);
                hideOrShowNext(false);
                setFragment(paymentDetailFragment, false, true, false);

                break;
            case RequestID.REQ_SHOW_DETAIL_VIEW:
                bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_CONNECTION);
                DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                doctorPatientDetailViewFragment.setArguments(bundle);
                onShowFragment(doctorPatientDetailViewFragment);
                hideOrShowToolbarTile(true);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getCurrentFragment().onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionConstants.GALLERY_REQUEST_CODE || requestCode == PermissionConstants.CAMERA_REQUEST_CODE) {
            String imagePath = CameraUtil.getImagePath(this, requestCode, resultCode, data);
            CameraInterface cameraInterface = (CameraInterface) getCurrentFragment();
            cameraInterface.onImageReceived(imagePath);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.next_tv:
                if (getCurrentFragment() instanceof DoCurrentTransactionInterface) {
                    ((DoCurrentTransactionInterface) getCurrentFragment()).doCurrentTransaction();
                }
                break;
            case R.id.other_option_iv:
                if (getCurrentFragment() instanceof DoCurrentTransactionInterface) {
                    ((DoCurrentTransactionInterface) getCurrentFragment()).doCurrentTransaction();
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ArgumentKeys.CURRENT_TITLE, this.detailTitle);

        outState.putBoolean(ArgumentKeys.SHOW_BACK, backIv.getVisibility() == View.VISIBLE);
        outState.putBoolean(ArgumentKeys.SHOW_NEXT, nextTv.getVisibility() == View.VISIBLE);
    }

    @Override
    public void enableNext(boolean enabled) {
        nextTv.setEnabled(enabled);
        if (enabled) {
            nextTv.setAlpha(1);
        } else {
            nextTv.setAlpha(0.5f);
        }
    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {
        if (hideOrShow) {
            other_option_iv.setVisibility(View.GONE);
            nextTv.setVisibility(View.VISIBLE);
        } else {
            nextTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {
        if (hideOrShow) {
            collapsingToolbarLayout.setVisibility(View.GONE);
        } else {
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {
        if (hideOrShow) {
            backIv.setVisibility(View.VISIBLE);
        } else {
            backIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateNextTitle(String nextTitle) {
        nextTv.setText(nextTitle);
    }

    @Override
    public void updateTitle(String title) {
        updateDetailTitle(title);
    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        updateDetailTitle(getString(R.string.settings));
        setFragment(new ProfileSettingFragment(), true, false, true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Intent intent = new Intent(Constants.DATE_PICKER_INTENT);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.YEAR, year);
        bundle.putInt(Constants.MONTH, month);
        bundle.putInt(Constants.DAY, dayOfMonth);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onTitleChange(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        setFragment(fragment, false, true, false);
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }
}
