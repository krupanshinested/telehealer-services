package com.thealer.telehealer.views.settings;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.apilayer.models.signout.SignoutApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.PaymentContentActivity;
import com.thealer.telehealer.views.EducationalVideo.EducationalListVideoFragment;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.call.CallNetworkTestActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.common.WebViewFragment;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.document.DocumentListFragment;
import com.thealer.telehealer.views.quickLogin.QuickLoginPinFragment;
import com.thealer.telehealer.views.settings.Interface.BundleReceiver;
import com.thealer.telehealer.views.settings.Interface.SettingClickListener;
import com.thealer.telehealer.views.settings.accessLogs.AccessLogActivity;
import com.thealer.telehealer.views.settings.medicalAssistant.MedicalAssistantListFragment;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryList;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryViewFragment;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.CreatePasswordFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.OtpVerificationFragment;
import com.thealer.telehealer.views.signup.doctor.CreateDoctorDetailFragment;
import com.thealer.telehealer.views.signup.medicalAssistant.MedicalAssistantDetailFragment;
import com.thealer.telehealer.views.signup.patient.PatientChoosePaymentFragment;
import com.thealer.telehealer.views.signup.patient.PatientRegistrationDetailFragment;
import com.thealer.telehealer.views.signup.patient.PatientUploadInsuranceFragment;
import com.thealer.telehealer.views.subscription.ActivePlanFragment;
import com.thealer.telehealer.views.transaction.TransactionListFragment;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.common.Constants.activatedPlan;

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

    private WhoAmIApiViewModel whoAmIApiViewModel;
    private boolean isBackDisabled = false;
    private SignoutApiViewModel signoutApiViewModel;
    private ImageView favoriteIv;
    private CircleImageView statusCiv;
    private boolean isSigningOutInProcess = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initObservers();

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

    private void initObservers() {
        signoutApiViewModel = new ViewModelProvider(this).get(SignoutApiViewModel.class);
        signoutApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                isSigningOutInProcess = false;
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, false);
                    startActivity(new Intent(ProfileSettingsActivity.this, SigninActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }
        });
        signoutApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showToast(errorModel.getMessage());
                }
                isSigningOutInProcess = false;
            }
        });


    }

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
        favoriteIv = (ImageView) findViewById(R.id.favorite_iv);
        statusCiv = (CircleImageView) findViewById(R.id.status_civ);

        statusCiv.setVisibility(View.GONE);
        favoriteIv.setVisibility(View.GONE);

        nextTv = findViewById(R.id.next_tv);

        updateProfile();

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

        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserver(whoAmIApiViewModel);

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                    showMedicalHistory();
                }
            }
        });

        if (getIntent() != null && getIntent().getExtras() != null) {
            isBackDisabled = getIntent().getExtras().getBoolean(ArgumentKeys.DISABLE_BACk, false);

            if (getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == Constants.SCHEDULE_CREATION_MODE) {
                PatientRegistrationDetailFragment patientRegistrationDetailFragment = new PatientRegistrationDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                bundle.putInt(ArgumentKeys.VIEW_TYPE, Constants.SCHEDULE_CREATION_MODE);
                patientRegistrationDetailFragment.setArguments(bundle);
                showSubFragment(patientRegistrationDetailFragment);

            } else if (getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == ArgumentKeys.HISTORY_UPDATE) {
                showMedicalHistory();
            } else if (getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == ArgumentKeys.LICENCE_UPDATE) {
                showUserProfile();
            } else if (getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == ArgumentKeys.PAYMENT_INFO) {
                didSelecteItem(R.id.add_card);
            }
        }
    }

    /*
     *
     * Listener from Settings Fragment
     * */
    @Override
    public void didSelecteItem(int id) {
        Bundle bundle;
        switch (id) {
            case R.id.profile:
                showUserProfile();
                break;
            case R.id.documents:
                DocumentListFragment documentListFragment = new DocumentListFragment();
                bundle = new Bundle();
                bundle.putBoolean(Constants.IS_FROM_HOME, true);
                bundle.putString(Constants.SELECTED_ITEM, OrderConstant.ORDER_DOCUMENTS);
                documentListFragment.setArguments(bundle);

                showSubFragment(documentListFragment);
                break;
            case R.id.medical_history:
                whoAmIApiViewModel.checkWhoAmI();
                break;
            case R.id.settings:
                GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
                showSubFragment(generalSettingsFragment);
                break;
            case R.id.email_id:
                break;
            case R.id.phone_number:
                break;
            case R.id.change_password:

                ResetPasswordRequestModel resetPasswordRequestModel = new ViewModelProvider(this).get(ResetPasswordRequestModel.class);
                resetPasswordRequestModel.setEmail(UserDetailPreferenceManager.getEmail());
                resetPasswordRequestModel.setApp_type(Utils.getAppType());

                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();

                showSubFragment(resetPasswordFragment);

                break;
            case R.id.appointment_slots:
                break;
            case R.id.educational_video:
                bundle = new Bundle();
                EducationalListVideoFragment educationalListVideoFragment = new EducationalListVideoFragment();
                showSubFragment(educationalListVideoFragment);
                break;
            case R.id.check_call_quality:
                Intent intent = new Intent(this, CallNetworkTestActivity.class);
                startActivity(intent);
                break;
            case R.id.logs:
                startActivity(new Intent(this, AccessLogActivity.class));
                break;
            case R.id.feedback:
                bundle = new Bundle();
                bundle.putString(ArgumentKeys.VIEW_TITLE, getString(R.string.feedback));
                bundle.putString(ArgumentKeys.WEB_VIEW_URL, getString(R.string.feedback_url));
                WebViewFragment feedBackFragment = new WebViewFragment();
                feedBackFragment.setArguments(bundle);

                showSubFragment(feedBackFragment);
                break;
            case R.id.terms_and_condition:
                bundle = new Bundle();
                bundle.putString(ArgumentKeys.VIEW_TITLE, getString(R.string.terms_and_conditions));
                bundle.putString(ArgumentKeys.WEB_VIEW_URL, getString(R.string.terms_and_conditions_url));

                WebViewFragment termsFragment = new WebViewFragment();
                termsFragment.setArguments(bundle);
                showSubFragment(termsFragment);
                break;
            case R.id.privacy_policy:
                bundle = new Bundle();
                bundle.putString(ArgumentKeys.VIEW_TITLE, getString(R.string.privacy_policy));
                bundle.putString(ArgumentKeys.WEB_VIEW_URL, getString(R.string.privacy_url));

                WebViewFragment privacyFragment = new WebViewFragment();
                privacyFragment.setArguments(bundle);
                showSubFragment(privacyFragment);
                break;
            case R.id.signOut:
                if (!isSigningOutInProcess && !CallManager.shared.isActiveCallPresent()) {
                    isSigningOutInProcess = true;
                    signoutApiViewModel.signOut();
                }
                break;
            case R.id.telehealer_billings:
                PaymentsListingFragment paymentsListingFragment = new PaymentsListingFragment();
                showSubFragment(paymentsListingFragment);
                break;
            case R.id.add_card:
                startActivity(new Intent(this, PaymentContentActivity.class).putExtra(ArgumentKeys.IS_HEAD_LESS, true));
                break;
            case R.id.subscription:
                activatedPlan = -1;
                ActivePlanFragment activePlanFragment = new ActivePlanFragment();
                showSubFragment(activePlanFragment);
                break;
            case R.id.medical_assistant_ll:
                showMedicalAssistantList();
                break;
            case R.id.patient_payments: {
                bundle = new Bundle();
                TransactionListFragment transactionListFragment = new TransactionListFragment();
                transactionListFragment.setArguments(bundle);
                showSubFragment(transactionListFragment);
            }
        }
    }

    private void showUserProfile() {
        Bundle profile = new Bundle();
        profile.putInt(ArgumentKeys.VIEW_TYPE, Constants.VIEW_MODE);
        profile.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
        switch (appPreference.getInt(Constants.USER_TYPE)) {
            case Constants.TYPE_MEDICAL_ASSISTANT:
                MedicalAssistantDetailFragment medicalAssistantDetailFragment = new MedicalAssistantDetailFragment();
                medicalAssistantDetailFragment.setArguments(profile);
                showSubFragment(medicalAssistantDetailFragment);
                break;
            case Constants.TYPE_DOCTOR:
                CreateDoctorDetailFragment doctorDetailFragment = new CreateDoctorDetailFragment();
                doctorDetailFragment.setArguments(profile);
                showSubFragment(doctorDetailFragment);
                break;
            case Constants.TYPE_PATIENT:
                PatientRegistrationDetailFragment patientRegistrationDetailFragment = new PatientRegistrationDetailFragment();
                patientRegistrationDetailFragment.setArguments(profile);
                showSubFragment(patientRegistrationDetailFragment);
                break;
        }
    }

    private void showMedicalAssistantList() {
        MedicalAssistantListFragment medicalAssistantListFragment = new MedicalAssistantListFragment();
        showSubFragment(medicalAssistantListFragment);
    }

    private void showMedicalHistory() {
        Fragment fragment;

        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        if (whoAmIApiResponseModel.getQuestionnaire() != null && whoAmIApiResponseModel.getQuestionnaire().isQuestionariesEmpty()) {
            fragment = new MedicalHistoryViewFragment();
        } else {
            fragment = new MedicalHistoryList();
        }
        showSubFragment(fragment);

    }

    @Override
    public void onBackPressed() {
        processBackPress();
    }

    private void processBackPress() {
        if (!isBackDisabled) {
            if (getIntent() != null && getIntent().getExtras() != null &&
                    getIntent().getExtras().getInt(ArgumentKeys.VIEW_TYPE) == Constants.SCHEDULE_CREATION_MODE) {
                finish();
            } else {

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        }
    }

    public void updateProfile() {
        if (UserType.isUserPatient()) {
            genderIv.setVisibility(View.VISIBLE);
            Utils.setGenderImage(getApplicationContext(), genderIv, UserDetailPreferenceManager.getGender());
        } else {
            genderIv.setVisibility(View.GONE);
        }
        updateUserDetails();
        Utils.setImageWithGlide(getApplicationContext(), userProfileIv, UserDetailPreferenceManager.getUser_avatar(), ProfileSettingsActivity.this.getDrawable(R.drawable.profile_placeholder), true, true);
    }

    public void updateUserDetails() {
        TextView userNameTv = (TextView) findViewById(R.id.user_name_tv);
        TextView userDobTv = (TextView) findViewById(R.id.user_dob_tv);

        toolbarTitle.setText(UserDetailPreferenceManager.getUserDisplayName());
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
                .findFragmentById(R.id.sub_fragment_holder);
    }

    private void setFragment(Fragment fragment, Boolean toBase, Boolean needToAddBackTrace, Boolean needToClearBackTrace) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (needToClearBackTrace) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        int containerId;

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
        appbarLayout.setExpanded(enabled, true);
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
                OtpVerificationFragment otpVerificationFragment = new OtpVerificationFragment();

                Bundle otpBundle = bundle;

                if (otpBundle == null) {
                    otpBundle = new Bundle();
                }
                otpBundle.putInt(ArgumentKeys.OTP_TYPE, OtpVerificationFragment.reset_password);
                otpBundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                otpVerificationFragment.setArguments(otpBundle);

                showSubFragment(otpVerificationFragment);
                break;
            case RequestID.REQ_RESET_PASSWORD:
                String title = getString(R.string.password);
                updateDetailTitle(title);
                CreatePasswordFragment createPasswordFragment = new CreatePasswordFragment();

                Bundle passwordBundle = bundle;

                if (passwordBundle == null) {
                    passwordBundle = new Bundle();
                }
                passwordBundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.reset_password);
                passwordBundle.putString(ArgumentKeys.TITLE, title);
                passwordBundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                createPasswordFragment.setArguments(passwordBundle);

                showSubFragment(createPasswordFragment);

                break;
            case RequestID.RESET_PASSWORD_OTP_VALIDATED:
                String re_title = getString(R.string.reenter_password);
                updateDetailTitle(re_title);
                CreatePasswordFragment reEnterPassword = new CreatePasswordFragment();

                Bundle repasswordBundle = bundle;

                if (repasswordBundle == null) {
                    repasswordBundle = new Bundle();
                }
                repasswordBundle.putInt(ArgumentKeys.PASSWORD_TYPE, CreatePasswordFragment.reset_password);
                repasswordBundle.putString(ArgumentKeys.TITLE, re_title);
                repasswordBundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                reEnterPassword.setArguments(repasswordBundle);
                showSubFragment(reEnterPassword);

                break;
            case RequestID.REQ_QUICK_LOGIN_PIN:
                QuickLoginPinFragment quickLoginPinFragment = new QuickLoginPinFragment();
                showSubFragment(quickLoginPinFragment);
                break;
            case RequestID.QUICK_LOGIN_PIN_CREATED:
                didSelecteItem(R.id.settings);
                break;
            case RequestID.REQUEST_INSURANCE_CHANGE:
                PatientChoosePaymentFragment patientChoosePaymentFragment = new PatientChoosePaymentFragment();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                bundle.putInt(ArgumentKeys.SCREEN_TYPE, Constants.forProfileUpdate);
                patientChoosePaymentFragment.setArguments(bundle);
                showSubFragment(patientChoosePaymentFragment);

                break;
            case RequestID.INSURANCE_REQUEST_IMAGE:
                getSupportFragmentManager().popBackStack();

                PatientUploadInsuranceFragment patientUploadInsuranceFragment = new PatientUploadInsuranceFragment();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                bundle.putInt(ArgumentKeys.SCREEN_TYPE, Constants.forProfileUpdate);
                patientUploadInsuranceFragment.setArguments(bundle);
                showSubFragment(patientUploadInsuranceFragment);

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
                //to open stripe payment method list


                /*CardInformationFragment cardInformationFragment = new CardInformationFragment();
                bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                cardInformationFragment.setArguments(bundle);
                showSubFragment(cardInformationFragment);*/
                break;

            case RequestID.TRANSACTION_DETAIL:
                PaymentDetailFragment paymentDetailFragment = new PaymentDetailFragment();
                paymentDetailFragment.setArguments(bundle);
                showSubFragment(paymentDetailFragment);
                break;
            case RequestID.REQ_SHOW_DETAIL_VIEW:
                bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
                DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                doctorPatientDetailViewFragment.setArguments(bundle);
                showSubFragment(doctorPatientDetailViewFragment);
                break;
        }

    }

    private void showSubFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(R.id.sub_fragment_holder, fragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getCurrentFragment() != null) {
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
            if (requestCode == PermissionConstants.GALLERY_REQUEST_CODE || requestCode == PermissionConstants.CAMERA_REQUEST_CODE) {
                String imagePath = CameraUtil.getImagePath(this, requestCode, resultCode, data);
                CameraInterface cameraInterface = (CameraInterface) getCurrentFragment();
                cameraInterface.onImageReceived(imagePath);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                processBackPress();
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
    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {
    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {
    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {
    }

    @Override
    public void updateNextTitle(String nextTitle) {
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
        updateDetailTitle(getString(R.string.preference));
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
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
        if (Constants.isRedirectProfileSetting) {
            GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
            showSubFragment(generalSettingsFragment);
        }
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        showSubFragment(fragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(profileListener);
    }

}
