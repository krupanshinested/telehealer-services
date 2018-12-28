package com.thealer.telehealer.views.signup.doctor;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
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
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;

import static com.thealer.telehealer.common.Constants.TYPE_EXPIRATION;

/**
 * Created by Aswin on 25,October,2018
 */
public class DoctorLicenseBottomSheetFragment extends BaseBottomSheetDialogFragment {
    private TextView deleteTv;
    private TextView doneTv;
    private TextInputLayout stateTil;
    private EditText stateEt;
    private TextInputLayout numberTil;
    private EditText numberEt;
    private TextInputLayout expirationTil;
    private EditText expirationEt;
    private CreateUserRequestModel createUserRequestModel;
    private int licenseId;
    private boolean isNewLicense;
    private DateBroadcastReceiver dateBroadcastReceiver = new DateBroadcastReceiver() {
        @Override
        public void onDateReceived(java.lang.String formatedDate) {
            expirationEt.setText(formatedDate);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
        setData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_doctor_license, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        deleteTv = (TextView) view.findViewById(R.id.delete_tv);
        doneTv = (TextView) view.findViewById(R.id.done_tv);
        stateTil = (TextInputLayout) view.findViewById(R.id.state_til);
        stateEt = (EditText) view.findViewById(R.id.state_et);
        numberTil = (TextInputLayout) view.findViewById(R.id.number_til);
        numberEt = (EditText) view.findViewById(R.id.number_et);
        expirationTil = (TextInputLayout) view.findViewById(R.id.expiration_til);
        expirationEt = (EditText) view.findViewById(R.id.expiration_et);

        setBottomSheetHeight(view.findViewById(R.id.parent_view));
        if (getArguments() != null) {
            isNewLicense = false;
            licenseId = getArguments().getInt(Constants.LICENSE_ID);
        } else {
            isNewLicense = true;
        }

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserRequestModel.getUser_detail().getData().getLicenses().remove(licenseId);
                createUserRequestModel.getHasValidLicensesList().remove(licenseId);
                notifyParent();
                getDialog().dismiss();
            }
        });

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputFields()) {
                    if (isNewLicense) {
                        addNewLicense();
                    } else {
                        updateLicense();
                    }
                    notifyParent();
                    getDialog().dismiss();
                }
            }
        });

        expirationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DATE_PICKER_TYPE, TYPE_EXPIRATION);
                datePickerDialogFragment.setArguments(bundle);
                datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), DatePickerDialogFragment.class.getSimpleName());
            }
        });

        stateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Utils.isValidState(s.toString())) {
                    stateTil.setErrorEnabled(false);
                } else {
                    stateTil.setError(getString(R.string.enter_valid_state));
                }
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
            public void afterTextChanged(Editable text) {
                numberTil.setErrorEnabled(false);
                checkInputFields();
            }
        });

        expirationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                expirationTil.setErrorEnabled(false);
                checkInputFields();
            }
        });
    }

    private boolean checkInputFields() {
        boolean status = true;

        if (stateEt.getText().toString().isEmpty()) {
            stateTil.setError(getString(R.string.state_empty_error));
            status = false;
        }
        if (!Utils.isValidState(stateEt.getText().toString())) {
            status = false;
        }
        if (numberEt.getText().toString().isEmpty()) {
            numberTil.setError(getString(R.string.license_empty_error));
            status = false;
        }
        if (expirationEt.getText().toString().isEmpty()) {
            expirationTil.setError(getString(R.string.expiration_empty_error));
            status = false;
        }else if (!Utils.isDateExpired(expirationEt.getText().toString())) {
            expirationTil.setError(getString(R.string.expired_date_error));
            status = false;
        }
        return status;
    }

    private void notifyParent() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
    }

    private void updateLicense() {
        createUserRequestModel.getUser_detail().getData().getLicenses().get(licenseId).setState(stateEt.getText().toString());
        createUserRequestModel.getUser_detail().getData().getLicenses().get(licenseId).setNumber(numberEt.getText().toString());
        createUserRequestModel.getUser_detail().getData().getLicenses().get(licenseId).setEnd_date(expirationEt.getText().toString());
        createUserRequestModel.getHasValidLicensesList().set(licenseId, true);
    }

    private void addNewLicense() {
        createUserRequestModel.getUser_detail().getData().getLicenses().add(new LicensesBean(stateEt.getText().toString(),
                numberEt.getText().toString(),
                expirationEt.getText().toString()));
        createUserRequestModel.getHasValidLicensesList().add(true);
    }

    private void setData() {
        if (!isNewLicense) {
            deleteTv.setVisibility(View.VISIBLE);

            stateEt.setText(createUserRequestModel.getUser_detail().getData().getLicenses().get(licenseId).getState());
            numberEt.setText(createUserRequestModel.getUser_detail().getData().getLicenses().get(licenseId).getNumber());
            expirationEt.setText(createUserRequestModel.getUser_detail().getData().getLicenses().get(licenseId).getEnd_date());

            checkInputFields();
        } else {
            deleteTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
    }

}
