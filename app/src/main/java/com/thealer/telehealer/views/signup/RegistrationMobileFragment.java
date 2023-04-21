package com.thealer.telehealer.views.signup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel.CheckUserEmailMobileApiViewModel;
import com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel.CheckUserEmailMobileResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.common.UserType.isUserPatient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Aswin on 12,October,2018
 */
public class RegistrationMobileFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private CountryCodePicker countyCode, countyAltCode;
    private EditText numberEt, numberAltEt;
    private TextView pageHintTv, pageHintAltTv;
    private TextView nextTv;
    private CheckUserEmailMobileApiViewModel checkUserEmailMobileApiViewModel;
    private TextInputLayout numberTil, numberAltTil;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private String mobileNumber;
    private PhoneNumberUtil phoneNumberUtil;
    private PhoneNumberFormattingTextWatcher phoneNumberFormattingTextWatcher = null;
    private PhoneNumberFormattingTextWatcher altphoneNumberFormattingTextWatcher = null;
    private Phonenumber.PhoneNumber phoneNumber;
    private CreateUserRequestModel createUserRequestModel;
    private ConstraintLayout altLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_mobile, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkUserEmailMobileApiViewModel = new ViewModelProvider(this).get(CheckUserEmailMobileApiViewModel.class);
        createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);

        onViewChangeInterface.attachObserver(checkUserEmailMobileApiViewModel);

        checkUserEmailMobileApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    numberEt.clearFocus();
                    CheckUserEmailMobileResponseModel checkUserEmailMobileResponseModel = (CheckUserEmailMobileResponseModel) baseApiResponseModel;

                    if (checkUserEmailMobileResponseModel.isAvailable()) {
                        createUserRequestModel.getUser_data().setPhone(mobileNumber);

                        checkUserEmailMobileApiViewModel.getBaseApiResponseModelMutableLiveData().setValue(null);
                        onActionCompleteInterface.onCompletionResult(null, true, null);
                    } else {
                        numberTil.setError(getString(R.string.number_already_taken));
                    }
                }
            }
        });

    }

    private void initView(View view) {
        phoneNumberUtil = PhoneNumberUtil.createInstance(getActivity());

        countyCode = (CountryCodePicker) view.findViewById(R.id.county_code);
        numberEt = (EditText) view.findViewById(R.id.number_et);
        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);
        numberTil = (TextInputLayout) view.findViewById(R.id.number_til);

        altLayout = (ConstraintLayout) view.findViewById(R.id.alt_cl);
        countyAltCode = (CountryCodePicker) view.findViewById(R.id.county_alt_code);
        numberAltEt = (EditText) view.findViewById(R.id.number_alt_et);
        pageHintAltTv = (TextView) view.findViewById(R.id.page_hint_alt_tv);
        numberAltTil = (TextInputLayout) view.findViewById(R.id.number_alt_til);

        if (!isUserPatient()) {
            altLayout.setVisibility(View.GONE);
        }

        if (isDeviceXLarge() && isModeLandscape())
            pageHintTv.setVisibility(View.GONE);
        else
            pageHintTv.setVisibility(View.VISIBLE);

        onViewChangeInterface.hideOrShowNext(true);

        String Code = UserDetailPreferenceManager.getCountryCode();
        if (Code == null || Code.isEmpty()) {
            Code = appConfig.getLocaleCountry();
        }
        countyCode.setCountryForNameCode(Code);
        countyAltCode.setCountryForNameCode(Code);
        setInstallTypeAndCode();

        countyCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setInstallTypeAndCode();
                setHint();
            }
        });

        countyAltCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
//                setInstallTypeAndCode();
                setAltHint();
            }
        });

        setHint();
        setAltHint();
        validateNumber();

        numberEt.requestFocus();
    }

    private void setInstallTypeAndCode() {
        String installType = appConfig.getInstallType(countyCode.getSelectedCountryEnglishName());
        if (!UserDetailPreferenceManager.getInstallType().equals(installType)) {
            UserDetailPreferenceManager.setInstallType(installType);
            UserDetailPreferenceManager.setCountryCode(countyCode.getSelectedCountryNameCode());
        }
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

    private void setAltHint() {

        if (altphoneNumberFormattingTextWatcher != null) {
            numberAltEt.removeTextChangedListener(altphoneNumberFormattingTextWatcher);
        }

        altphoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher(countyAltCode.getSelectedCountryNameCode()) {

            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
//                validateNumber();
            }


        };

        numberAltEt.addTextChangedListener(altphoneNumberFormattingTextWatcher);

        try {

            String countryNameCode = countyAltCode.getSelectedCountryNameCode();
            String hintNumber = String.valueOf(phoneNumberUtil.getExampleNumber(countryNameCode).getNationalNumber());
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(hintNumber, countyAltCode.getSelectedCountryNameCode());
            hintNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replace(countyAltCode.getSelectedCountryCodeWithPlus(), "").trim();

            numberAltEt.setHint(hintNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    private void validateNumber() {
        if (!numberEt.getText().toString().isEmpty()) {

            try {

                phoneNumber = phoneNumberUtil.parse(numberEt.getText().toString(), countyCode.getSelectedCountryNameCode());
                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);

                if (isValid) {
                    setNextEnable(true);
                } else {
                    setNextEnable(false);
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
                setNextEnable(false);
            }
        } else {
            setNextEnable(false);
        }
    }

    private void setNextEnable(boolean setEnable) {
        onViewChangeInterface.enableNext(setEnable);
    }

    private void makeApiCall() {
        mobileNumber = countyCode.getSelectedCountryCodeWithPlus() + "" + phoneNumber.getNationalNumber();
        checkUserEmailMobileApiViewModel.checkUserMobile(mobileNumber);
    }

    @Override
    public void doCurrentTransaction() {
//        makeApiCall();
        proceed();
    }

    private void proceed() {
        if (!appConfig.isOtherThanTelehealer(getActivity())) {
            UserDetailPreferenceManager.setInstallType(appConfig.getInstallType(countyCode.getSelectedCountryEnglishName()));
            UserDetailPreferenceManager.setCountryCode(countyCode.getSelectedCountryNameCode());
        }
        mobileNumber = countyCode.getSelectedCountryCodeWithPlus() + "" + phoneNumber.getNationalNumber();

//        if (isUserPatient()) {
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("code", countyAltCode.getSelectedCountryCodeWithPlus());
//                jsonObject.put("number", numberAltEt.getText().toString());
//
//                JSONArray array = new JSONArray();
//                array.put(jsonObject);
//                createUserRequestModel.getUser_data().setAlt_rpm_response_no(array.toString());
//            } catch (Exception e) {
//                Log.d("TAG", "proceed: " + e.getMessage());
//            }
//        }
        createUserRequestModel.getUser_data().setPhone(mobileNumber);

        onActionCompleteInterface.onCompletionResult(null, true, null);
    }
}
