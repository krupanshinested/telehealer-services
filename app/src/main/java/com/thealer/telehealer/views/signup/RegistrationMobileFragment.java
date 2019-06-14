package com.thealer.telehealer.views.signup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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

/**
 * Created by Aswin on 12,October,2018
 */
public class RegistrationMobileFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private CountryCodePicker countyCode;
    private EditText numberEt;
    private TextView pageHintTv;
    private TextView nextTv;
    private CheckUserEmailMobileApiViewModel checkUserEmailMobileApiViewModel;
    private TextInputLayout numberTil;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private String mobileNumber;
    private PhoneNumberUtil phoneNumberUtil;
    private PhoneNumberFormattingTextWatcher phoneNumberFormattingTextWatcher = null;
    private Phonenumber.PhoneNumber phoneNumber;
    private CreateUserRequestModel createUserRequestModel;

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

        checkUserEmailMobileApiViewModel = ViewModelProviders.of(this).get(CheckUserEmailMobileApiViewModel.class);
        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

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
        setInstallTypeAndCode();

        countyCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setInstallTypeAndCode();
                setHint();
            }
        });

        setHint();

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
        if (!appConfig.isStethio(getActivity())) {
            UserDetailPreferenceManager.setInstallType(appConfig.getInstallType(countyCode.getSelectedCountryEnglishName()));
            UserDetailPreferenceManager.setCountryCode(countyCode.getSelectedCountryNameCode());
        }
        mobileNumber = countyCode.getSelectedCountryCodeWithPlus() + "" + phoneNumber.getNationalNumber();
        createUserRequestModel.getUser_data().setPhone(mobileNumber);
        onActionCompleteInterface.onCompletionResult(null, true, null);
    }
}
