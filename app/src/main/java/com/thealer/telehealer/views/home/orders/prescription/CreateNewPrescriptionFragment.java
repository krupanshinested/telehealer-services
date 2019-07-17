package com.thealer.telehealer.views.home.orders.prescription;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomSpinnerView;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.SendFaxByNumberFragment;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 30,November,2018
 */
public class CreateNewPrescriptionFragment extends OrdersBaseFragment implements View.OnClickListener {

    private OrdersCustomView patientOcv;
    private TextInputLayout drugTil;
    private EditText drugEt;
    private CustomSpinnerView formCsv;
    private TextInputLayout strengthTil;
    private EditText strengthEt;
    private CustomSpinnerView metricCsv;
    private TextInputLayout directionTil;
    private EditText directionEt;
    private CustomSpinnerView direction1Csv;
    private CustomSpinnerView direction2Csv;
    private TextInputLayout dispenseTil;
    private EditText dispenseEt;
    private TextView refillCountTv;
    private ImageButton addIb;
    private ImageButton subIb;
    private CheckBox doNotDisturbCb;
    private CheckBox labelCb;
    private Button saveBtn;
    private Button saveFaxBtn;

    private ShowSubFragmentInterface showSubFragmentInterface;

    private CommonUserApiResponseModel commonUserApiResponseModel;
    private CommonUserApiResponseModel selectedUserModel;
    private OnCloseActionInterface onCloseActionInterface;
    private boolean isFromHome;
    private int refillCount = 0;
    private String doctorGuid;
    private OrdersCustomView visitOcv;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prescription_create_new, container, false);
        setTitle(getString(R.string.new_prescription_order));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        drugTil = (TextInputLayout) view.findViewById(R.id.drug_til);
        drugEt = (EditText) view.findViewById(R.id.drug_et);
        formCsv = (CustomSpinnerView) view.findViewById(R.id.form_csv);
        strengthTil = (TextInputLayout) view.findViewById(R.id.strength_til);
        strengthEt = (EditText) view.findViewById(R.id.strength_et);
        metricCsv = (CustomSpinnerView) view.findViewById(R.id.metric_csv);
        directionTil = (TextInputLayout) view.findViewById(R.id.direction_til);
        directionEt = (EditText) view.findViewById(R.id.direction_et);
        direction1Csv = (CustomSpinnerView) view.findViewById(R.id.direction_1_csv);
        direction2Csv = (CustomSpinnerView) view.findViewById(R.id.direction_2_csv);
        dispenseTil = (TextInputLayout) view.findViewById(R.id.dispense_til);
        dispenseEt = (EditText) view.findViewById(R.id.dispense_et);
        refillCountTv = (TextView) view.findViewById(R.id.refill_count_tv);
        addIb = (ImageButton) view.findViewById(R.id.add_ib);
        subIb = (ImageButton) view.findViewById(R.id.sub_ib);
        doNotDisturbCb = (CheckBox) view.findViewById(R.id.do_not_disturb_cb);
        labelCb = (CheckBox) view.findViewById(R.id.label_cb);
        saveBtn = (Button) view.findViewById(R.id.save_btn);
        saveFaxBtn = (Button) view.findViewById(R.id.save_fax_btn);
        visitOcv = (OrdersCustomView) view.findViewById(R.id.visit_ocv);

        loadForms();
        loadMetrics();
        loadDirectionOne();
        loadDirectionTwo();

        addIb.setOnClickListener(this);
        subIb.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveFaxBtn.setOnClickListener(this);
        patientOcv.setOnClickListener(this);

        addTextWatcher(drugEt);
        addTextWatcher(strengthEt);
        addTextWatcher(directionEt);
        addTextWatcher(dispenseEt);

        if (getArguments() != null) {
            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {

                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                if (commonUserApiResponseModel != null) {
                    patientOcv.setArrow_visible(false);
                    patientOcv.setClickable(false);
                }

                CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                if (doctorModel != null) {
                    doctorGuid = doctorModel.getUser_guid();
                }
            }
        }

        if (selectedUserModel != null) {
            commonUserApiResponseModel = selectedUserModel;
        }
        setUserData();

        enableOrDisableBtn();
    }

    private void setUserData() {
        if (commonUserApiResponseModel != null) {
            patientOcv.setTitleTv(commonUserApiResponseModel.getUserDisplay_name());
            patientOcv.setSubtitleTv(commonUserApiResponseModel.getDob());
            patientOcv.setSub_title_visible(true);
            setVisitsView(visitOcv, commonUserApiResponseModel.getUser_guid(), doctorGuid);
            getPatientsRecentsList(commonUserApiResponseModel.getUser_guid(), doctorGuid);
        }
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeError(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableOrDisableBtn();
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateFocusedData((EditText) v, hasFocus);
            }
        });
    }

    private void updateFocusedData(EditText editText, boolean hasFocus) {
        String suffix = formCsv.getSpinner().getSelectedItem().toString();
        switch (editText.getId()) {
            case R.id.direction_et:
            case R.id.dispense_et:
                if (!hasFocus) {
                    editText.setInputType(InputType.TYPE_NULL);
                    String[] number = editText.getText().toString().split(" ");
                    if (number[0].isEmpty()) {
                        number[0] = "0";
                    }
                    editText.setText(number[0] + " " + suffix);
                } else {
                    if (!editText.getText().toString().isEmpty()) {
                        if (editText.getText().toString().charAt(0) == '0') {
                            editText.setText(null);
                        } else {
                            editText.setText(editText.getText().toString().replace(suffix, " ").trim());
                        }
                    } else {
                        setError(editText);
                    }
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                break;
        }
    }

    private void removeError(EditText editText) {
        switch (editText.getId()) {
            case R.id.drug_et:
                drugTil.setErrorEnabled(false);
                break;
            case R.id.direction_et:
                directionTil.setErrorEnabled(false);
                break;
            case R.id.dispense_et:
                dispenseTil.setErrorEnabled(false);
                break;
            case R.id.strength_et:
                strengthTil.setErrorEnabled(false);
                break;
        }
    }

    private void setError(EditText editText) {
        switch (editText.getId()) {
            case R.id.drug_et:
                drugTil.setError(getString(R.string.drug_error_nsg));
                break;
            case R.id.direction_et:
                directionTil.setError(getString(R.string.direction_error_msg));
                break;
            case R.id.dispense_et:
                dispenseTil.setError(getString(R.string.dispense_error_msg));
                break;
            case R.id.strength_et:
                strengthTil.setError(getString(R.string.strength_error_msg));
                break;
        }
    }

    public void enableOrDisableBtn() {
        boolean enable = false;

        if (commonUserApiResponseModel != null &&
                !drugEt.getText().toString().isEmpty() &&
                !strengthEt.getText().toString().isEmpty() &&
                !directionEt.getText().toString().isEmpty() &&
                !directionEt.getText().toString().split(" ")[0].trim().equals("0") &&
                !dispenseEt.getText().toString().isEmpty() &&
                !dispenseEt.getText().toString().split(" ")[0].trim().equals("0")) {

            enable = true;

        }

        saveBtn.setEnabled(enable);
        saveFaxBtn.setEnabled(enable);

        if (enable) {
            saveBtn.setAlpha(1);
            saveFaxBtn.setAlpha(1);
        } else {
            saveBtn.setAlpha((float) 0.5);
            saveFaxBtn.setAlpha((float) 0.5);
        }

    }

    private void loadDirectionTwo() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, PrescriptionConstans.prescription_direction_two);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        direction2Csv.setSpinnerAdapter(arrayAdapter);
        direction2Csv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadDirectionOne() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, PrescriptionConstans.prescription_direction_one);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        direction1Csv.setSpinnerAdapter(arrayAdapter);
        direction1Csv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadMetrics() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, PrescriptionConstans.prescription_metrics);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metricCsv.setSpinnerAdapter(arrayAdapter);
        metricCsv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadForms() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, PrescriptionConstans.prescription_forms);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formCsv.setSpinnerAdapter(arrayAdapter);
        formCsv.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFocusedData(directionEt, false);
                updateFocusedData(dispenseEt, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_ib:
                refillCount = refillCount + 1;
                setRefillCount(refillCount);
                break;
            case R.id.sub_ib:
                if (refillCount != 0) {
                    refillCount = refillCount - 1;
                    setRefillCount(refillCount);
                }
                break;
            case R.id.patient_ocv:
                SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.SEARCH_TYPE, ArgumentKeys.SEARCH_ASSOCIATION);
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                selectAssociationFragment.setArguments(bundle);
                selectAssociationFragment.setTargetFragment(this, RequestID.REQ_SELECT_ASSOCIATION);
                showSubFragmentInterface.onShowFragment(selectAssociationFragment);
                break;
            case R.id.save_btn:
                showQuickLogin();
                break;
            case R.id.save_fax_btn:
                if (appConfig.isIndianUser(getActivity()))
                    sendFaxByNumber();
                else
                    saveAndFaxPrescription();
                break;
        }
    }

    private void sendFaxByNumber() {
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.USER_NAME, commonUserApiResponseModel.getUserDisplay_name());
        bundle.putSerializable(ArgumentKeys.ORDER_DATA, getPrescriptionModel());
        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

        SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
        sendFaxByNumberFragment.setTargetFragment(this, RequestID.REQ_SEND_FAX);
        sendFaxByNumberFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(sendFaxByNumberFragment);
    }

    private void saveAndFaxPrescription() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ArgumentKeys.PRESCRIPTION_DATA, getPrescriptionModel());
        bundle.putString(ArgumentKeys.USER_NAME, commonUserApiResponseModel.getUserDisplay_name());
        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
        SelectPharmacyFragment selectPharmacyFragment = new SelectPharmacyFragment();
        selectPharmacyFragment.setArguments(bundle);

        showSubFragmentInterface.onShowFragment(selectPharmacyFragment);
    }

    @Override
    public void onAuthenticated() {
        createPrescription(getPrescriptionModel(), commonUserApiResponseModel.getUserDisplay_name(), doctorGuid, false);
    }

    private CreatePrescriptionRequestModel getPrescriptionModel() {
        return new CreatePrescriptionRequestModel(commonUserApiResponseModel.getUser_guid(), commonUserApiResponseModel.getUserDisplay_name(),
                getVistOrderId(),
                new CreatePrescriptionRequestModel.DetailBean(drugEt.getText().toString(),
                        strengthEt.getText().toString(),
                        metricCsv.getSpinner().getSelectedItem().toString(),
                        formCsv.getSpinner().getSelectedItem().toString(),
                        directionEt.getText().toString().replace(formCsv.getSpinner().getSelectedItem().toString(), "").trim(),
                        direction1Csv.getSpinner().getSelectedItem().toString(),
                        direction2Csv.getSpinner().getSelectedItem().toString(),
                        dispenseEt.getText().toString().replace(formCsv.getSpinner().getSelectedItem().toString(), "").trim(),
                        String.valueOf(refillCount),
                        doNotDisturbCb.isChecked(),
                        labelCb.isChecked()));
    }

    private void setRefillCount(int refillCount) {
        refillCountTv.setText(refillCount + " " + getString(R.string.times));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestID.REQ_SELECT_ASSOCIATION:
                    if (data != null && data.getExtras() != null) {
                        selectedUserModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                    }
                    break;
            }
        }
    }
}
