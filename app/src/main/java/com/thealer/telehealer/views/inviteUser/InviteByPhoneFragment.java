package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

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
    private PhoneNumberFormattingTextWatcher phoneNumberFormattingTextWatcher = null;
    private PhoneNumberUtil phoneNumberUtil;
    private Phonenumber.PhoneNumber phoneNumber;
    private RelativeLayout numberRl;
    private TextView infoTv;
    private Bundle bundle = null;

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
        phoneNumberUtil = PhoneNumberUtil.createInstance(getActivity());
        infoTv = (TextView) view.findViewById(R.id.info_tv);
        infoTv.setText(getString(R.string.invite_by_phone_info, getString(R.string.app_name)));

        if (getArguments() != null && getArguments().getSerializable(Constants.USER_DETAIL) != null) {
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (commonUserApiResponseModel != null) {
                doctor_guid = commonUserApiResponseModel.getUser_guid();
            }
        }

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = countyCode.getSelectedCountryCodeWithPlus() + "" + phoneNumber.getNationalNumber();
                showSuccessFragment();
                InviteByEmailPhoneRequestModel inviteByEmailPhoneRequestModel = new InviteByEmailPhoneRequestModel();
                bundle=getArguments();
                String role="";
                if(bundle != null){
                    role=bundle.getString(ArgumentKeys.ROLE,"");
                    if(doctor_guid==null || doctor_guid.equals("")){
                        doctor_guid=bundle.getString(ArgumentKeys.USER_GUID,null);
                    }
                }
                inviteByEmailPhoneRequestModel.setRole(role);
                inviteByEmailPhoneRequestModel.getInvitations().add(new InviteByEmailPhoneRequestModel.InvitationsBean(null, number));
                inviteUserApiViewModel.inviteUserByEmailPhone(doctor_guid, inviteByEmailPhoneRequestModel, false);

            }
        });
        countyCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setHint();
            }
        });

        setHint();

        validateNumber();

        numberEt.requestFocus();
    }

    private void setHint() {

        if (phoneNumberFormattingTextWatcher != null) {
            numberEt.removeTextChangedListener(phoneNumberFormattingTextWatcher);
        }

        phoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher(countyCode.getSelectedCountryNameCode()) {

            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                validateNumber();
            }


        };

        numberEt.addTextChangedListener(phoneNumberFormattingTextWatcher);

        try {

            String countryNameCode = countyCode.getSelectedCountryNameCode();
            String hintNumber = String.valueOf(phoneNumberUtil.getExampleNumber(countryNameCode).getNationalNumber());
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(hintNumber, countyCode.getSelectedCountryNameCode());
            hintNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replace(countyCode.getSelectedCountryCodeWithPlus(), "").trim();

            numberEt.setHint(hintNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
        }
    }

    private void validateNumber() {
        if (!numberEt.getText().toString().isEmpty()) {

            try {
                phoneNumber = phoneNumberUtil.parse(numberEt.getText().toString(), countyCode.getSelectedCountryNameCode());
                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);

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
