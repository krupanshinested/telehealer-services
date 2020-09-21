package com.thealer.telehealer.views.signup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

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
        CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

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

        countyCode = (CountryCodePicker) view.findViewById(R.id.county_code);
        numberEt = (EditText) view.findViewById(R.id.number_et);
        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);
        numberTil = (TextInputLayout) view.findViewById(R.id.number_til);

        if (isDeviceXLarge() && isModeLandscape())
            pageHintTv.setVisibility(View.GONE);
        else
            pageHintTv.setVisibility(View.VISIBLE);

        onViewChangeInterface.hideOrShowNext(true);

        validateNumber();

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

        numberEt.requestFocus();
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
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(getActivity());
                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(numberEt.getText().toString(), countyCode.getSelectedCountryNameCode()));

                if (isValid){
                    setNextEnable(true);
                }else {
                    setNextEnable(false);
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
                setNextEnable(false);
            }
        }else {
            setNextEnable(false);
        }
    }

    private void setNextEnable(boolean setEnable) {
        onViewChangeInterface.enableNext(setEnable);
    }

    private void makeApiCall() {
        mobileNumber = countyCode.getSelectedCountryCodeWithPlus() + "" + numberEt.getText().toString();
        checkUserEmailMobileApiViewModel.checkUserMobile(mobileNumber);
    }

    @Override
    public void doCurrentTransaction() {
        makeApiCall();
    }
}
