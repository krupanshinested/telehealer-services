package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.common.Constants;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByPhoneFragment extends InviteUserBaseFragment {
    private CountryCodePicker countyCode;
    private TextInputLayout numberTil;
    private EditText numberEt;
    private Button inviteBtn;

    private String doctor_guid = null;
    private CommonUserApiResponseModel commonUserApiResponseModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_by_phone, container, false);
        changeTitleInterface.onTitleChange(getString(R.string.invite_by_phone));
        initView(view);
        return view;
    }

    private void initView(View view) {
        countyCode = (CountryCodePicker) view.findViewById(R.id.county_code);
        numberTil = (TextInputLayout) view.findViewById(R.id.number_til);
        numberEt = (EditText) view.findViewById(R.id.number_et);
        inviteBtn = (Button) view.findViewById(R.id.invite_btn);

        if (getArguments() != null && getArguments().getSerializable(Constants.USER_DETAIL) != null) {
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (commonUserApiResponseModel != null) {
                doctor_guid = commonUserApiResponseModel.getUser_guid();
            }
        }

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = countyCode.getSelectedCountryCodeWithPlus() + "" + numberEt.getText().toString();
                showSuccessFragment();
                InviteByEmailPhoneRequestModel inviteByEmailPhoneRequestModel = new InviteByEmailPhoneRequestModel();
                inviteByEmailPhoneRequestModel.getInvitations().add(new InviteByEmailPhoneRequestModel.InvitationsBean(null, number));
                inviteUserApiViewModel.inviteUserByEmailPhone(doctor_guid, inviteByEmailPhoneRequestModel, false);

            }
        });

        numberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateNumber();
            }
        });
    }

    private void validateNumber() {
        if (!numberEt.getText().toString().isEmpty()) {

            try {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(getActivity());
                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(numberEt.getText().toString(), countyCode.getSelectedCountryNameCode()));

                if (isValid) {
                    enableOrDisableInvite(true);
                } else {
                    enableOrDisableInvite(false);
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
                enableOrDisableInvite(false);
            }
        } else {
            enableOrDisableInvite(false);
        }
    }

    private void enableOrDisableInvite(boolean enable) {
        inviteBtn.setEnabled(enable);
    }

}
