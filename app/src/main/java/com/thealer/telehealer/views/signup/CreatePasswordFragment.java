package com.thealer.telehealer.views.signup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserApiViewModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PasswordValidator;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 15,October,2018
 */
public class CreatePasswordFragment extends BaseFragment implements DoCurrentTransactionInterface {

    public static final int signup = 0;
    public static final int reset_password = 1;
    public static final int forgot_password = 2;

    private TextView titleTv;
    private EditText passwordEt;
    private TextView passwordRequirementTv;
    private TextInputLayout passwordTil;
    private PasswordValidator passwordValidator;
    private CreateUserApiViewModel createUserApiViewModel;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private boolean isReEnterPassword;
    private ResetPasswordRequestModel resetPasswordRequestModel;
    private int type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_password, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createUserApiViewModel = ViewModelProviders.of(this).get(CreateUserApiViewModel.class);

        onViewChangeInterface.attachObserver(createUserApiViewModel);

        createUserApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    passwordEt.clearFocus();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());

                    showSuccessView(bundle);


                    if (errorModel.getMessage() != null) {
                        EventRecorder.recordRegistration("SIGNUP_ERROR_"+errorModel.getMessage(),null);
                    } else {
                        EventRecorder.recordRegistration("SIGNUP_ERROR_UNKNOWN",null);
                    }
                }
            }
        });

        createUserApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    passwordEt.clearFocus();

                    if (baseApiResponseModel instanceof CreateUserApiResponseModel) {
                        CreateUserApiResponseModel createUserApiResponseModel = (CreateUserApiResponseModel) baseApiResponseModel;

                        if (createUserApiResponseModel.isSuccess()) {
                            appPreference.setString(PreferenceConstants.USER_GUID, createUserApiResponseModel.getData().getUser_guid());
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, createUserApiResponseModel.getData().getToken());
                            appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, createUserApiResponseModel.getData().getRefresh_token());
                            appPreference.setString(PreferenceConstants.USER_NAME, createUserApiResponseModel.getData().getName());

                            createUserApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

                            onActionCompleteInterface.onCompletionResult(null, true, null);

                            if (getActivity() != null) {
                                CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

                                switch (createUserRequestModel.getUser_data().getRole()) {
                                    case Constants.ROLE_PATIENT:
                                        EventRecorder.recordRegistration("SIGNUP_PATIENT_NOT_VERIFIED", createUserApiResponseModel.getData().getUser_guid());
                                        break;
                                    case Constants.ROLE_DOCTOR:
                                        EventRecorder.recordRegistration("SIGNUP_DOCTOR_NOT_VERIFIED", createUserApiResponseModel.getData().getUser_guid());
                                        break;
                                    default:
                                        EventRecorder.recordRegistration("SIGNUP_MA_NOT_VERIFIED", createUserApiResponseModel.getData().getUser_guid());
                                        break;
                                }
                            }
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                        bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.success_reset_password));

                        showSuccessView(bundle);

                    }
                }
            }
        });

    }

    private void showSuccessView(Bundle bundle) {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setArguments(bundle);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        resetPasswordRequestModel = ViewModelProviders.of(getActivity()).get(ResetPasswordRequestModel.class);
    }


    private void initView(View view) {

        passwordValidator = new PasswordValidator();

        passwordEt = (EditText) view.findViewById(R.id.password_et);
        passwordRequirementTv = (TextView) view.findViewById(R.id.password_requirement_tv);
        passwordTil = (TextInputLayout) view.findViewById(R.id.password_til);
        titleTv = (TextView) view.findViewById(R.id.title_tv);

        passwordRequirementTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().startActivity(new Intent(getActivity(), PasswordRequirementActivity.class)
                        .putExtra("password", passwordEt.getText().toString()));
            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordTil.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
            }
        });

        if (getArguments() != null) {
            type = getArguments().getInt(ArgumentKeys.PASSWORD_TYPE);
            isReEnterPassword = getArguments().getBoolean(ArgumentKeys.IS_REENTER_PASSWORD);

            if (!isReEnterPassword) {
                titleTv.setText(getString(R.string.enter_your_password));
            } else {
                titleTv.setText(getString(R.string.reenter_password));
            }
            //only to change the title
            switch (type) {
                case reset_password:
                    titleTv.setVisibility(View.GONE);
                    break;
                case forgot_password:
                default:
                    onViewChangeInterface.hideOrShowClose(false);
                    onViewChangeInterface.hideOrShowBackIv(true);
                    onViewChangeInterface.updateNextTitle(getString(R.string.next));
                    break;
            }
        }
    }


    private void createUser() {
        CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

        createUserRequestModel.getUser_data().setPassword(passwordEt.getText().toString());

        if (UserType.isUserPatient()) {

            createUserRequestModel.getUser_data().setRole(Constants.ROLE_PATIENT);

            createUserApiViewModel.createPatient(createUserRequestModel);

        } else if (UserType.isUserAssistant()) {

            createUserRequestModel.getUser_data().setRole(Constants.ROLE_ASSISTANT);
            createUserRequestModel.getUser_data().setUser_name(Constants.BUILD_MEDICAL);

            createUserApiViewModel.createMedicalAssistant(createUserRequestModel);

        } else if (UserType.isUserDoctor()) {

            createUserRequestModel.getUser_data().setRole(Constants.ROLE_DOCTOR);
            createUserRequestModel.getUser_data().setUser_name(Constants.BUILD_MEDICAL);

            createUserApiViewModel.createDoctor(createUserRequestModel);
        }
    }

    private void validatePassword() {
        String password = passwordEt.getText().toString();
        if (!password.isEmpty()) {
            onViewChangeInterface.enableNext(false);
            String error = passwordValidator.isValidPassword(password);

            if (error != null) {
                passwordTil.setError(error);
            } else {
                onViewChangeInterface.enableNext(true);
            }

        } else {
            onViewChangeInterface.enableNext(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (type != reset_password) {
            onViewChangeInterface.hideOrShowClose(false);
            onViewChangeInterface.hideOrShowBackIv(true);
            onViewChangeInterface.updateNextTitle(getString(R.string.next));
            onViewChangeInterface.updateTitle(getString(R.string.password));
        } else {
            if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(ArgumentKeys.TITLE))) {
                onViewChangeInterface.updateTitle(getArguments().getString(ArgumentKeys.TITLE));
            } else {
                onViewChangeInterface.updateTitle(getString(R.string.reenter_password));
            }
        }

        onViewChangeInterface.hideOrShowNext(true);

        validatePassword();
    }

    @Override
    public void doCurrentTransaction() {

        switch (type) {
            case reset_password:

                if (getArguments() != null) {
                    if (getArguments().getBoolean(ArgumentKeys.IS_PASSWORD_ENTERED)) {
                        resetPasswordRequestModel.setConfirm_password(passwordEt.getText().toString());
                        makeResetPasswordApiCall();
                    } else {
                        Bundle bundle = new Bundle();
                        resetPasswordRequestModel.setPassword(passwordEt.getText().toString());
                        bundle.putBoolean(ArgumentKeys.IS_PASSWORD_ENTERED,true);
                        onActionCompleteInterface.onCompletionResult(RequestID.RESET_PASSWORD_OTP_VALIDATED, true, bundle);
                    }
                }
                break;
            case forgot_password:
                if (!isReEnterPassword) {
                    resetPasswordRequestModel.setPassword(passwordEt.getText().toString());
                    onActionCompleteInterface.onCompletionResult(null, true, null);
                } else {
                    resetPasswordRequestModel.setConfirm_password(passwordEt.getText().toString());
                    makeResetPasswordApiCall();
                }
                break;
            default:
                createUser();
        }
    }

    private void makeResetPasswordApiCall() {
        createUserApiViewModel.resetPassword(resetPasswordRequestModel);
    }
}
