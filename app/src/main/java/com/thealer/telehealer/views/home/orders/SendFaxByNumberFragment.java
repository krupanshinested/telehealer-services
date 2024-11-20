package com.thealer.telehealer.views.home.orders;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.SendFaxRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 20,March,2019
 */
public class SendFaxByNumberFragment extends OrdersBaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView textView;
    private RelativeLayout numberRl;
    private CountryCodePicker countyCode;
    private TextInputLayout numberTil;
    private EditText numberEt;
    private Button sendBtn;

    private PhoneNumberFormattingTextWatcher phoneNumberFormattingTextWatcher = null;
    private PhoneNumberUtil phoneNumberUtil;
    private Phonenumber.PhoneNumber phoneNumber;
    private int refferralId;
    private boolean isSaveAndFax = false;

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private ChangeTitleInterface changeTitleInterface;
    private Object requestData;
    private String userName;
    private String doctorGuid;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_fax_by_number, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        textView = (TextView) view.findViewById(R.id.textView);
        numberRl = (RelativeLayout) view.findViewById(R.id.number_rl);
        countyCode = (CountryCodePicker) view.findViewById(R.id.county_code);
        numberTil = (TextInputLayout) view.findViewById(R.id.number_til);
        numberEt = (EditText) view.findViewById(R.id.number_et);
        sendBtn = (Button) view.findViewById(R.id.send_btn);

        toolbarTitle.setText(getString(R.string.enter_fax_number));

        phoneNumberUtil = PhoneNumberUtil.createInstance(getActivity());

        backIv.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        String Code = UserDetailPreferenceManager.getCountryCode();
        if (Code == null || Code.isEmpty()) {
            Code = appConfig.getLocaleCountry();
        }
        countyCode.setCountryForNameCode(Code);


        countyCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setHint();
            }
        });

        if (getArguments() != null) {
            refferralId = getArguments().getInt(ArgumentKeys.ORDER_ID);
            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);

            if (getArguments().getSerializable(ArgumentKeys.ORDER_DATA) != null) {
                toolbar.setVisibility(View.GONE);
                isSaveAndFax = true;
                requestData = getArguments().getSerializable(ArgumentKeys.ORDER_DATA);
                userName = getArguments().getString(ArgumentKeys.USER_NAME);
                changeTitleInterface.onTitleChange(getString(R.string.enter_fax_number));
            }
        }
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
                    enableOrDisableSend(true);
                } else {
                    enableOrDisableSend(false);
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
                enableOrDisableSend(false);
            }
        } else {
            enableOrDisableSend(false);
        }
    }

    private void enableOrDisableSend(boolean enable) {
        sendBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPress();
                break;
            case R.id.send_btn:
                Utils.hideKeyboard(getActivity());
                showQuickLogin();
                break;
        }
    }

    @Override
    public void onAuthenticated() {
        if (isSaveAndFax) {

            if (requestData instanceof AssignSpecialistRequestModel) {

                assignSpecialist(true,(AssignSpecialistRequestModel) requestData, userName, doctorGuid, true);

            } else if (requestData instanceof CreateRadiologyRequestModel) {

                createNewRadiologyOrder(true,(CreateRadiologyRequestModel) requestData, userName, doctorGuid, true);

            } else if (requestData instanceof CreateTestApiRequestModel) {

                createNewLabOrder(true, (CreateTestApiRequestModel) requestData, userName, doctorGuid, true);

            } else if (requestData instanceof CreatePrescriptionRequestModel) {
                createPrescription(true, (CreatePrescriptionRequestModel) requestData, userName, doctorGuid, true);
            }
        } else {
            sendFax(refferralId, false, true);
        }

    }

    private void onBackPress() {
        Utils.hideKeyboard(getActivity());
        onCloseActionInterface.onClose(false);
    }


    @Override
    public void sendFax(int referral_id, boolean isShowProgress, boolean isShowAuth) {
        super.sendFax(referral_id, isShowProgress, isShowAuth);
        SendFaxRequestModel sendFaxRequestModel = new SendFaxRequestModel();
        sendFaxRequestModel.setReferral_id(String.valueOf(referral_id));
        String number = countyCode.getSelectedCountryCodeWithPlus() + "" + phoneNumber.getNationalNumber();
        sendFaxRequestModel.setFax_number(number);

        ordersCreateApiViewModel.sendFax(sendFaxRequestModel, doctorGuid, isShowProgress);
    }
}
