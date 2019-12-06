package com.thealer.telehealer.views.signup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel.CheckUserEmailMobileApiViewModel;
import com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel.CheckUserEmailMobileResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.signin.ResetPasswordRequestModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signin.ForgotPassword;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 11,October,2018
 */
public class RegistrationEmailFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private TextInputLayout emailTil;
    private EditText emailEt;
    private TextView nextTv;
    private CheckUserEmailMobileApiViewModel checkUserEmailMobileApiViewModel;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private CheckBox rememberCb;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkUserEmailMobileApiViewModel = new ViewModelProvider(this).get(CheckUserEmailMobileApiViewModel.class);
        CreateUserRequestModel createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);

        onViewChangeInterface.attachObserver(checkUserEmailMobileApiViewModel);

        checkUserEmailMobileApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                emailEt.clearFocus();

                if (baseApiResponseModel != null) {
                    CheckUserEmailMobileResponseModel checkUserEmailMobileResponseModel = (CheckUserEmailMobileResponseModel) baseApiResponseModel;

                    if (getArguments() != null) {

                        String whereFrom = getArguments().getString(Constants.WHERE_FROM);

                        if (whereFrom.equals(ForgotPassword.class.getSimpleName())) {

                            if (!checkUserEmailMobileResponseModel.isUser_exists()) {
                                setEmailError(String.format(getString(R.string.user_not_allowed_error), getString(R.string.app_name), getString(R.string.app_name)));
                            } else {
                                ResetPasswordRequestModel resetPasswordRequestModel = new ViewModelProvider(getActivity()).get(ResetPasswordRequestModel.class);
                                resetPasswordRequestModel.setEmail(emailEt.getText().toString());

                                checkUserEmailMobileApiViewModel.getBaseApiResponseModelMutableLiveData().setValue(null);

                                onActionCompleteInterface.onCompletionResult(null, true, null);
                            }
                        }

                    } else {
                        if (checkUserEmailMobileResponseModel.isAvailable()) {

                            createUserRequestModel.getUser_data().setEmail(emailEt.getText().toString());

                            if (rememberCb.isChecked()) {
                                appPreference.setBoolean(PreferenceConstants.IS_REMEMBER_EMAIL, true);
                            }
                            appPreference.setString(PreferenceConstants.USER_EMAIL, emailEt.getText().toString());

                            checkUserEmailMobileApiViewModel.getBaseApiResponseModelMutableLiveData().setValue(null);

                            onActionCompleteInterface.onCompletionResult(null, true, null);

                        } else {
                            setEmailError(getString(R.string.email_already_taken));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_email, container, false);
        onViewChangeInterface.hideOrShowNext(true);
        initView(view);
        return view;
    }

    private void initView(View view) {

        onViewChangeInterface.enableNext(false);

        emailTil = (TextInputLayout) view.findViewById(R.id.email_til);
        emailEt = (EditText) view.findViewById(R.id.email_et);

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setEmailError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(s.toString());
            }
        });

        TextView pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);

        if (isDeviceXLarge() && isModeLandscape())
            pageHintTv.setVisibility(View.GONE);
        else{
            pageHintTv.setText(String.format(getString(R.string.email_info), getString(R.string.app_name)));
            pageHintTv.setVisibility(View.VISIBLE);
        }

        rememberCb = (CheckBox) view.findViewById(R.id.remember_cb);

        if (getArguments() != null) {
            String whereFrom = getArguments().getString(Constants.WHERE_FROM);
            if (whereFrom.equals(ForgotPassword.class.getSimpleName())) {
                rememberCb.setVisibility(View.GONE);
                pageHintTv.setVisibility(View.GONE);
            }
        }
    }


    private void validateEmail(String s) {
        if (!s.isEmpty()) {
            if (!Utils.isEmailValid(s)) {

                setEmailError(getString(R.string.enter_valid_email));
                onViewChangeInterface.enableNext(false);

            } else {
                onViewChangeInterface.enableNext(true);
            }
        } else
            onViewChangeInterface.enableNext(false);
    }

    @Override
    public void doCurrentTransaction() {
        makeApiCall();
    }

    public void makeApiCall() {
        checkUserEmailMobileApiViewModel.checkUserEmail(emailEt.getText().toString());
    }

    private void setEmailError(String error){
        emailTil.setError(null);
        emailTil.setErrorEnabled(false);
        if (error != null){
            emailTil.setError(error);
        }
    }

}
