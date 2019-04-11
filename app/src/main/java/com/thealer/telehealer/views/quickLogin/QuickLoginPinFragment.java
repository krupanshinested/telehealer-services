package com.thealer.telehealer.views.quickLogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.signin.SigninActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 31,October,2018
 */
public class QuickLoginPinFragment extends BaseFragment {
    private CircleImageView doctorCiv;
    private TextView doctorNameTv;
    private EditText pinEt;
    private ImageView circleIv1;
    private ImageView circleIv2;
    private ImageView circleIv3;
    private ImageView circleIv4;
    private TextView forgetPasswordTv;
    private ImageView[] imageViews;
    private TextView enterPinTv;
    private ImageView closeIv;

    private int quickLoginType;
    private boolean isNewUser = true;
    private boolean isCreatePin = true;
    private String pin;
    private final String IS_CREATE_PIN = "isCreatePin";
    private final String PIN = "pin";
    boolean isRefreshToken = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quicklogin_pin, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isCreatePin = savedInstanceState.getBoolean(IS_CREATE_PIN);
            pin = savedInstanceState.getString(PIN);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_CREATE_PIN, isCreatePin);
        outState.putString(PIN, pin);
    }

    private void initView(View view) {
        doctorCiv = (CircleImageView) view.findViewById(R.id.doctor_civ);
        doctorNameTv = (TextView) view.findViewById(R.id.doctor_name_tv);
        pinEt = (EditText) view.findViewById(R.id.pin_et);
        circleIv1 = (ImageView) view.findViewById(R.id.circle_iv1);
        circleIv2 = (ImageView) view.findViewById(R.id.circle_iv2);
        circleIv3 = (ImageView) view.findViewById(R.id.circle_iv3);
        circleIv4 = (ImageView) view.findViewById(R.id.circle_iv4);
        forgetPasswordTv = (TextView) view.findViewById(R.id.forget_password_tv);
        enterPinTv = (TextView) view.findViewById(R.id.enter_pin_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewUser) {
                    appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_NONE);
                    sendQuickLoginBroadCast(ArgumentKeys.QUICK_LOGIN_CREATED);
                } else {
                    if (isRefreshToken) {
                        getActivity().startActivity(new Intent(getActivity(), SigninActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    getActivity().finish();
                }
            }
        });

        imageViews = new ImageView[]{circleIv1, circleIv2, circleIv3, circleIv4};

        pinEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                updateCircleImage(text.toString().length());

                if (text.toString().length() == 4) {
                    if (isNewUser) {
                        if (isCreatePin) {
                            pin = text.toString();
                            showReEnterPin();
                        } else {
                            validatePin();
                        }
                    } else {
                        validatePin();
                    }
                }
            }
        });

        forgetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewUser || isRefreshToken) {
                    String email = UserDetailPreferenceManager.getEmail();
                    appPreference.deletePreference();
                    UserDetailPreferenceManager.setEmail(email);
                    getActivity().startActivity(new Intent(getActivity(), SigninActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                } else {
                    appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_PASSWORD);
                    getActivity().finish();
                }
            }
        });

        quickLoginType = appPreference.getInt(Constants.QUICK_LOGIN_TYPE);

        isNewUser = appPreference.getString(Constants.QUICK_LOGIN_PIN).isEmpty();

        if (getArguments() != null && getArguments().getBoolean(ArgumentKeys.IS_REFRESH_TOKEN)) {
            isRefreshToken = true;
        }

        if (!UserDetailPreferenceManager.getFirst_name().isEmpty()) {
            doctorNameTv.setText(UserDetailPreferenceManager.getFirst_name());
            Utils.setImageWithGlide(getActivity(), doctorCiv, UserDetailPreferenceManager.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true);
        }

        if (isNewUser) {
            forgetPasswordTv.setVisibility(View.GONE);
            if (!isCreatePin) {
                showReEnterPin();
            } else {
                showCreatePin();
            }
        } else {
            showValidatePin();
        }
    }

    private void validatePin() {
        if (isNewUser) {
            if (pin.equals(pinEt.getText().toString())) {
                appPreference.setString(Constants.QUICK_LOGIN_PIN, pin);
                appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_PIN);
                sendQuickLoginBroadCast(ArgumentKeys.QUICK_LOGIN_CREATED);
            } else {
                showErrorDialog(getString(R.string.passcode_not_match));
            }
        } else {
            pin = appPreference.getString(Constants.QUICK_LOGIN_PIN);

            if (pin.equals(pinEt.getText().toString())) {
                appPreference.setString(Constants.QUICK_LOGIN_PIN, pin);
                appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_PIN);
                sendQuickLoginBroadCast(ArgumentKeys.AUTH_SUCCESS);
            } else {
                sendQuickLoginBroadCast(ArgumentKeys.AUTH_FAILED);
            }
        }
    }

    private void sendQuickLoginBroadCast(int Authorized) {
        pinEt.clearFocus();
        Utils.hideKeyboard(getActivity());

        Intent intent = new Intent(getString(R.string.quick_login_broadcast_receiver));
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.QUICK_LOGIN_STATUS, Authorized);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void showErrorDialog(String pin) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage(pin);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showCreatePin();
                    }
                });
        alertDialog.show();
    }

    private void showValidatePin() {
        forgetPasswordTv.setVisibility(View.VISIBLE);
        pinEt.setText(null);
        enterPinTv.setText(getString(R.string.enter_your_pin));
    }

    private void showCreatePin() {
        pinEt.setText(null);
        enterPinTv.setText(getString(R.string.create_pin));
        isCreatePin = true;
    }

    private void showReEnterPin() {
        pinEt.setText(null);
        enterPinTv.setText(getString(R.string.reenter_pin));
        isCreatePin = false;
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
        pinEt.setShowSoftInputOnFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideKeyboard(getActivity());
    }
}
