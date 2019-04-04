package com.thealer.telehealer.views.quickLogin;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.thealer.telehealer.common.ArgumentKeys;
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

    private SigninApiViewModel signinApiViewModel;
    private SigninApiResponseModel signinApiResponseModel;
    private AttachObserverInterface attachObserverInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        signinApiViewModel = ViewModelProviders.of(this).get(SigninApiViewModel.class);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        attachObserverInterface.attachObserver(signinApiViewModel);

        signinApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendQuickLoginBroadCast(ArgumentKeys.AUTH_FAILED);
                    getActivity().finish();
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

        closeIv.setOnClickListener(this);
        validateBtn.setOnClickListener(this);

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
                getActivity().finish();
                break;
            case R.id.validate_btn:
                if (!passwordEt.getText().toString().isEmpty()) {
                    signinApiViewModel.loginUser(UserDetailPreferenceManager.getEmail(), passwordEt.getText().toString());
                } else {
                    passwordTil.setError(getString(R.string.password_empty_error));
                }
                break;
        }
    }
}
