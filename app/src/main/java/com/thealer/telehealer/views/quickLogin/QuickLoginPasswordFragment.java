package com.thealer.telehealer.views.quickLogin;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiViewModel;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 07,December,2018
 */
public class QuickLoginPasswordFragment extends BaseFragment implements View.OnClickListener {
    private ImageView closeIv;
    private CircleImageView userAvatarCiv;
    private TextView userNameTv;
    private TextView enterPasswordTv;
    private TextInputLayout passwordTil;
    private EditText passwordEt;
    private CustomButton validateBtn;
    private AppPreference appPreference;
    private SigninApiViewModel signinApiViewModel;
    private SigninApiResponseModel signinApiResponseModel;
    private AttachObserverInterface attachObserverInterface;
    int passCount = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        signinApiViewModel = new ViewModelProvider(this).get(SigninApiViewModel.class);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        attachObserverInterface.attachObserver(signinApiViewModel);

        signinApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        if (passCount < 2) {
                            passCount++;
                            String attemptRemains = (3 - passCount) + "";
                            showErrorDialog(getString(R.string.wrong_password, attemptRemains));
                            passwordEt.setText("");
                        } else {
                            passCount = 0;
                            appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, false);
                            sendQuickLoginBroadCast(ArgumentKeys.AUTH_FAILED);
                            getActivity().finish();
                        }
                    }
                }
            }
        });

        signinApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                    if (signinApiResponseModel.isSuccess()) {
                        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());
                        appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, signinApiResponseModel.getRefresh_token());
                        passCount = 0;
                        appPreference.setBoolean(PreferenceConstants.IS_AUTH_PENDING, false);
                        sendQuickLoginBroadCast(ArgumentKeys.AUTH_SUCCESS);
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private void sendQuickLoginBroadCast(int Authorized) {
        Intent intent = new Intent(getString(R.string.quick_login_broadcast_receiver));
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.QUICK_LOGIN_STATUS, Authorized);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quicklogin_password, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        userAvatarCiv = (CircleImageView) view.findViewById(R.id.user_avatar_civ);
        userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
        enterPasswordTv = (TextView) view.findViewById(R.id.enter_password_tv);
        passwordTil = (TextInputLayout) view.findViewById(R.id.password_til);
        passwordEt = (EditText) view.findViewById(R.id.password_et);
        validateBtn = (CustomButton) view.findViewById(R.id.validate_btn);
        appPreference = AppPreference.getInstance(getActivity());
        closeIv.setOnClickListener(this);
        validateBtn.setOnClickListener(this);

        boolean isNewUser = appPreference.getString(Constants.QUICK_LOGIN_PIN).isEmpty();
        closeIv.setVisibility(View.GONE);

        userNameTv.setText(UserDetailPreferenceManager.getUserDisplayName());
        Utils.setImageWithGlide(getActivity().getApplicationContext(), userAvatarCiv, UserDetailPreferenceManager.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true, true);

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordTil.isErrorEnabled())
                    passwordTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    passwordTil.setError(getString(R.string.password_empty_error));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                Constants.ErrorFlag = false;
                getActivity().finish();
                break;
            case R.id.validate_btn:
                if (!passwordEt.getText().toString().isEmpty()) {
                    Constants.ErrorFlag = false;
                    signinApiViewModel.loginUser(UserDetailPreferenceManager.getEmail(), passwordEt.getText().toString());
                } else {
                    passwordTil.setError(getString(R.string.password_empty_error));
                }
                break;
        }
    }

    private void showErrorDialog(String pin) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(pin);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
