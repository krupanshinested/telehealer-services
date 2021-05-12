package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByEmailFragment extends InviteUserBaseFragment {
    private TextInputLayout emailTil;
    private EditText emailEt;
    private Button inviteBtn;

    private CommonUserApiResponseModel commonUserApiResponseModel = null;
    private String doctor_guid = null;
    private TextView infoTv;
    private Bundle bundle =  null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_by_email, container, false);
        changeTitleInterface.onTitleChange(getString(R.string.invite_by_email));
        initView(view);
        return view;
    }

    private void initView(View view) {
        emailTil = (TextInputLayout) view.findViewById(R.id.email_til);
        emailEt = (EditText) view.findViewById(R.id.email_et);
        inviteBtn = (Button) view.findViewById(R.id.invite_btn);
        infoTv = (TextView) view.findViewById(R.id.info_tv);

        infoTv.setText(String.format(getString(R.string.invite_by_email_info), getString(R.string.app_name)));

        if (getArguments() != null && getArguments().getSerializable(Constants.USER_DETAIL) != null) {
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (commonUserApiResponseModel != null) {
                doctor_guid = commonUserApiResponseModel.getUser_guid();
            }
        }

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuccessFragment();
                InviteByEmailPhoneRequestModel inviteByEmailPhoneRequestModel = new InviteByEmailPhoneRequestModel();
                bundle=getArguments();
                String role="";
                if(bundle!=null){
                    role=bundle.getString(ArgumentKeys.ROLE,"");
                    if(doctor_guid==null || doctor_guid.equals("")){
                        doctor_guid=bundle.getString(ArgumentKeys.USER_GUID,null);
                    }

                }
                inviteByEmailPhoneRequestModel.setRole(role);
                inviteByEmailPhoneRequestModel.getInvitations().add(new InviteByEmailPhoneRequestModel.InvitationsBean(emailEt.getText().toString(), null));
                inviteUserApiViewModel.inviteUserByEmailPhone(doctor_guid, inviteByEmailPhoneRequestModel, false);
            }
        });

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(s.toString());
            }
        });
    }

    private void validateEmail(String email) {
        if (email.isEmpty()) {
            enableOrDisableInvite(false);
        } else {
            if (Utils.isEmailValid(email)) {
                enableOrDisableInvite(true);
            } else {
                emailTil.setError(getString(R.string.enter_valid_email));
                enableOrDisableInvite(false);
            }
        }
    }

    private void enableOrDisableInvite(boolean enable) {
        inviteBtn.setEnabled(enable);
    }
}
