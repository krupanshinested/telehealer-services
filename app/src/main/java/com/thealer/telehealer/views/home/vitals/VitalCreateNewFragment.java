package com.thealer.telehealer.views.home.vitals;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsCreateApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.common.VitalCommon.VitalsConstant.getMaxRange;
import static com.thealer.telehealer.common.VitalCommon.VitalsConstant.getMinRange;

/**
 * Created by Aswin on 27,November,2018
 */
public class VitalCreateNewFragment extends BaseFragment implements View.OnClickListener {
    private OrdersCustomView dateOcv;
    private OrdersCustomView timeOcv;
    private TextInputLayout vital1Til;
    private EditText vital1Et;
    private TextInputLayout vital2Til;
    private EditText vital2Et;
    private Button submitBtn;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    private OnCloseActionInterface onCloseActionInterface;
    private String selectedItem = SupportedMeasurementType.bp, inputType, inputUnit;
    private boolean isInputValid;
    private VitalsApiViewModel vitalsApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private ToolBarInterface toolBarInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);
        attachObserverInterface.attachObserver(vitalsApiViewModel);

        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();

        vitalsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    VitalsCreateApiResponseModel createApiResponseModel = (VitalsCreateApiResponseModel) baseApiResponseModel;

                    if (createApiResponseModel.isSuccess()) {

                        if (UserType.isUserPatient() && createApiResponseModel.isAbnormal()) {
                            Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.abnormal_vital_alert_message),
                                    getString(R.string.ok), null,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            onCloseActionInterface.onClose(false);
                                        }
                                    }, null);
                        } else {
                            onCloseActionInterface.onClose(false);
                        }
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_create_new, container, false);
        initView(view);
        return view;
    }

    public void onResume() {
        super.onResume();

        if (getArguments() != null && getArguments().getBoolean(ArgumentKeys.USE_OWN_TOOLBAR)) {
            appBarLayout.setVisibility(View.VISIBLE);
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getString(SupportedMeasurementType.getTitle(selectedItem)));
            toolbar.findViewById(R.id.back_iv).setOnClickListener(this);
        } else {
            appBarLayout.setVisibility(View.GONE);

            toolBarInterface.updateTitle(getString(SupportedMeasurementType.getTitle(selectedItem)));
            onViewChangeInterface.hideOrShowClose(true);
            onViewChangeInterface.hideOrShowBackIv(true);
        }
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        dateOcv = (OrdersCustomView) view.findViewById(R.id.date_ocv);
        timeOcv = (OrdersCustomView) view.findViewById(R.id.time_ocv);
        vital1Til = (TextInputLayout) view.findViewById(R.id.vital_1_til);
        vital1Et = (EditText) view.findViewById(R.id.vital_1_et);
        vital2Til = (TextInputLayout) view.findViewById(R.id.vital_2_til);
        vital2Et = (EditText) view.findViewById(R.id.vital_2_et);
        submitBtn = (Button) view.findViewById(R.id.submit_btn);
        appBarLayout = view.findViewById(R.id.appbar);
        submitBtn.setOnClickListener(this);

        enableOrDisableSubmit(false);

        dateOcv.setTitleTv(Utils.getCurrentFomatedDate());
        timeOcv.setTitleTv(Utils.getCurrentFomatedTime());

        if (getArguments() != null) {
            selectedItem = getArguments().getString(ArgumentKeys.SELECTED_VITAL_TYPE);

            inputUnit = SupportedMeasurementType.getVitalUnit(selectedItem);

            vital1Et.setText("0 " + inputUnit);
            vital2Et.setText("0 " + inputUnit);

            switch (selectedItem) {
                case SupportedMeasurementType.bp:
                    vital2Til.setVisibility(View.VISIBLE);
                    inputType = VitalsConstant.INPUT_SYSTOLE;
                    vital1Til.setHint(inputType);
                    vital2Til.setHint(VitalsConstant.INPUT_DIASTOLE);
                    break;
                case SupportedMeasurementType.gulcose:
                    inputType = VitalsConstant.INPUT_GLUCOSE;
                    vital1Til.setHint(inputType);
                    break;
                case SupportedMeasurementType.heartRate:
                    inputType = VitalsConstant.INPUT_PULSE;
                    vital1Til.setHint(inputType);
                    break;
                case SupportedMeasurementType.pulseOximeter:
                    inputType = VitalsConstant.INPUT_SPO2;
                    vital1Til.setHint(inputType);
                    break;
                case SupportedMeasurementType.temperature:
                    inputType = VitalsConstant.INPUT_TEMPERATURE;
                    vital1Til.setHint(inputType);
                    break;
                case SupportedMeasurementType.weight:
                    inputType = VitalsConstant.INPUT_WEIGHT;
                    vital1Til.setHint(inputType);
                    break;
            }

            vital1Et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    vital1Til.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateFirstVital(s.toString().split(" ")[0]);
                }
            });

            vital2Et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    vital2Til.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateSecondVital(s.toString().split(" ")[0]);
                }
            });

            vital1Et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    updateEditText(vital1Et, hasFocus);
                }
            });

            vital2Et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    updateEditText(vital2Et, hasFocus);
                }
            });

        }
    }


    private void updateEditText(EditText editText, boolean hasFocus) {

        if (hasFocus) {
            String value = editText.getText().toString().replace(inputUnit, "").trim();
            if (value.equals("0")) {
                editText.setText(null);
            } else {
                editText.setText(value);
            }

            switch (selectedItem) {
                case SupportedMeasurementType.weight:
                case SupportedMeasurementType.temperature:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;
                default:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }

        } else {
            editText.setInputType(InputType.TYPE_NULL);

            if (editText.getText().toString().isEmpty()) {
                editText.setText("0 " + inputUnit);
            } else {
                String[] number = editText.getText().toString().split(" ");
                if (number[0].isEmpty()) {
                    number[0] = "0";
                }
                editText.setText(number[0] + " " + inputUnit);
            }
        }
    }

    private void validateFirstVital(String data) {

        if (!data.isEmpty() && !data.equals("0")) {
            isInputValid = false;
            if (!isValidInput(data, inputType)) {
                vital1Til.setError(VitalsConstant.getInputError(inputType));
                enableOrDisableSubmit(false);
            } else {
                isInputValid = true;
                enableOrDisableSubmit(true);
            }
        } else {
            isInputValid = false;
            enableOrDisableSubmit(false);
        }

        if (vital2Til.getVisibility() == View.VISIBLE && !data.split(" ")[0].equals("0")) {
            validateSecondVital(vital2Et.getText().toString().split(" ")[0]);
        }
    }

    private void validateSecondVital(String data) {

        if (data.isEmpty()) {
            enableOrDisableSubmit(false);
        } else {
            if (data.equals("0")) {
                enableOrDisableSubmit(false);
                if (vital2Et.isFocused()) {
                    vital2Til.setError(VitalsConstant.getInputError(VitalsConstant.INPUT_DIASTOLE));
                }
            } else {
                if (!isValidInput(data, VitalsConstant.INPUT_DIASTOLE)) {
                    vital2Til.setError(VitalsConstant.getInputError(VitalsConstant.INPUT_DIASTOLE));
                    enableOrDisableSubmit(false);
                } else {
                    enableOrDisableSubmit(true);
                }
            }
        }

        if (!isInputValid) {
            enableOrDisableSubmit(false);
        }
    }

    private boolean isValidInput(String data, String inputType) {
        if (data.length() == 1 && data.charAt(0) == '.') {
            return false;
        } else {
            if (data.charAt(0) == '.') {
                data = "0" + data;
            }
        }

        return Float.parseFloat(data) > getMinRange(inputType) && Float.parseFloat(data) <= getMaxRange(inputType);
    }

    private void enableOrDisableSubmit(boolean enable) {
        submitBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.submit_btn:
                Utils.hideKeyboard(getActivity());
                CreateVitalApiRequestModel vitalApiRequestModel = new CreateVitalApiRequestModel();

                vitalApiRequestModel.setType(selectedItem);

                if (getArguments() != null && getArguments().getSerializable(Constants.USER_DETAIL) != null) {

                    CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                    if (commonUserApiResponseModel != null) {
                        vitalApiRequestModel.setUser_guid(commonUserApiResponseModel.getUser_guid());
                    }
                }

                if (UserType.isUserDoctor()) {
                    vitalApiRequestModel.setMode(VitalsConstant.VITAL_MODE_DOCTOR);
                    vitalApiRequestModel.setDisplay_name(UserDetailPreferenceManager.getUserDisplayName());
                } else if (UserType.isUserPatient()) {
                    vitalApiRequestModel.setMode(VitalsConstant.VITAL_MODE_PATIENT);
                    vitalApiRequestModel.setDisplay_name(UserDetailPreferenceManager.getUserDisplayName());
                } else {
                    vitalApiRequestModel.setMode(VitalsConstant.VITAL_MODE_DEVICE);
                    vitalApiRequestModel.setDisplay_name(VitalsConstant.VITAL_MODE_DEVICE);
                }


                String value;
                double vital1Value = Double.parseDouble(vital1Et.getText().toString().split(" ")[0]);

                if (selectedItem.equals(SupportedMeasurementType.bp)) {
                    double vital2Value = Double.parseDouble(vital2Et.getText().toString().split(" ")[0]);

                    value = (int) vital1Value + "/" + (int) vital2Value;
                } else {
                    switch (selectedItem) {
                        case SupportedMeasurementType.temperature:
                        case SupportedMeasurementType.weight:
                            value = String.format("%.1f", vital1Value);
                            break;
                        default:
                            value = String.valueOf((int) vital1Value);
                    }
                }

                vitalApiRequestModel.setValue(value);

                vitalsApiViewModel.createVital(vitalApiRequestModel);
                break;
        }
    }
}
