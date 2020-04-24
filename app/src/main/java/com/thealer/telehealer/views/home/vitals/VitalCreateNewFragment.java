package com.thealer.telehealer.views.home.vitals;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.common.VitalCommon.VitalsConstant.getMaxRange;
import static com.thealer.telehealer.common.VitalCommon.VitalsConstant.getMinRange;

/**
 * Created by Aswin on 27,November,2018
 */
public class VitalCreateNewFragment extends VitalsSendBaseFragment implements View.OnClickListener {
    private OrdersCustomView dateOcv;
    private OrdersCustomView timeOcv;
    private TextInputLayout vital1Til;
    private EditText vital1Et;
    private TextInputLayout vital2Til;
    private EditText vital2Et;
    private Button submitBtn;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private boolean isInputValid;
    private AppBarLayout appbarLayout;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextInputLayout vital3Til;
    private EditText vital3Et;
    private OnCloseActionInterface onCloseActionInterface;
    private VitalsApiViewModel vitalsApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private ToolBarInterface toolBarInterface;
    private String selectedItem = SupportedMeasurementType.bp, inputType,secondInputType = VitalsConstant.INPUT_DIASTOLE, firstInputUnit, thirdInputUnit, hint1 = "";
    private boolean isFirstValid = false, isSecondValid = false, isThirdValid = false;

    private String secondInputHint = VitalsConstant.INPUT_DIASTOLE_HINT;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();
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
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        vital3Til = (TextInputLayout) view.findViewById(R.id.vital_3_til);
        vital3Et = (EditText) view.findViewById(R.id.vital_3_et);

        submitBtn.setOnClickListener(this);

        enableOrDisableSubmit(false);

        dateOcv.setTitleTv(Utils.getCurrentFomatedDate());
        timeOcv.setTitleTv(Utils.getCurrentFomatedTime());

        if (getArguments() != null) {
            selectedItem = getArguments().getString(ArgumentKeys.SELECTED_VITAL_TYPE);

            firstInputUnit = SupportedMeasurementType.getVitalUnit(selectedItem);

            switch (selectedItem) {
                case SupportedMeasurementType.gulcose:
                    inputType = VitalsConstant.INPUT_GLUCOSE;
                    hint1 = VitalsConstant.INPUT_GLUCOSE;
                    break;
                case SupportedMeasurementType.heartRate:
                    inputType = VitalsConstant.INPUT_PULSE;
                    hint1 = VitalsConstant.INPUT_PULSE;
                    break;
                case SupportedMeasurementType.pulseOximeter:
                    inputType = VitalsConstant.INPUT_SPO2;
                    hint1 = VitalsConstant.INPUT_SPO2;
                    break;
                case SupportedMeasurementType.temperature:
                    inputType = VitalsConstant.INPUT_TEMPERATURE;
                    hint1 = VitalsConstant.INPUT_TEMPERATURE;
                    break;
                case SupportedMeasurementType.weight:
                    inputType = VitalsConstant.INPUT_WEIGHT;
                    hint1 = VitalsConstant.INPUT_WEIGHT;
                    break;
                case SupportedMeasurementType.bp:
                    vital2Til.setVisibility(View.VISIBLE);
                    vital3Til.setVisibility(View.VISIBLE);

                    inputType = VitalsConstant.INPUT_SYSTOLE;
                    hint1 = VitalsConstant.INPUT_SYSTOLE_HINT;
                    vital2Til.setHint(secondInputHint);
                    vital3Til.setHint(VitalsConstant.INPUT_PULSE);

                    thirdInputUnit = SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.heartRate);
                    break;
                case SupportedMeasurementType.height:
                    vital2Til.setVisibility(View.VISIBLE);

                    firstInputUnit = " ";

                    inputType = VitalsConstant.INPUT_FEET;
                    hint1 = VitalsConstant.INPUT_FEET;
                    secondInputHint = " ";
                    vital2Til.setHint(VitalsConstant.INPUT_INCHES);
                    secondInputType = VitalsConstant.INPUT_INCHES;
                    break;
            }
            vital1Til.setHint(hint1);

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
                    validateFirstVital();

                    if (selectedItem.equals(SupportedMeasurementType.bp)) {
                        checkAllInput();
                    }
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
                    validateSecondVital();

                    if (selectedItem.equals(SupportedMeasurementType.bp)) {
                        checkAllInput();
                    }
                }
            });

            vital3Et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    vital3Til.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateThirdVital();

                    if (selectedItem.equals(SupportedMeasurementType.bp)) {
                        checkAllInput();
                    }
                }
            });

            vital1Et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && vital1Et.getText().toString().isEmpty()) {
                        vital1Til.setHint(hint1);
                    } else {
                        vital1Til.setHint(hint1.toUpperCase());
                    }
                    if (!vital1Et.getText().toString().isEmpty())
                        updateEditText(vital1Et, hasFocus, firstInputUnit);
                    else {
                        setInputType(vital1Et);
                    }

                }
            });

            vital2Et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus && vital2Et.getText().toString().isEmpty()) {
                        vital2Til.setHint(secondInputHint);
                    } else {
                        vital2Til.setHint(secondInputHint.toUpperCase());
                    }
                    if (!vital2Et.getText().toString().isEmpty())
                        updateEditText(vital2Et, hasFocus, firstInputUnit);

                }
            });

            vital3Et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && vital3Et.getText().toString().isEmpty()) {
                        vital3Til.setHint(VitalsConstant.INPUT_PULSE);
                    } else {
                        vital3Til.setHint(VitalsConstant.INPUT_PULSE.toUpperCase());
                    }

                    if (!vital3Et.getText().toString().isEmpty())
                        updateEditText(vital3Et, hasFocus, thirdInputUnit);

                }
            });

        }

    }

    private void setInputType(EditText editText) {
        switch (selectedItem) {
            case SupportedMeasurementType.weight:
            case SupportedMeasurementType.temperature:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            default:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        editText.requestFocus();
        showOrHideSoftInputWindow(true);
    }

    private void checkAllInput() {
        if ((isFirstValid && isSecondValid && isThirdValid) ||
                (isFirstValid && isSecondValid && !isThirdValid) ||
                (!isFirstValid && !isSecondValid && isThirdValid)) {
            enableOrDisableSubmit(true);
        } else {
            enableOrDisableSubmit(false);
        }
    }

    private void updateEditText(EditText editText, boolean hasFocus, String unit) {

        if (hasFocus) {
            String value = editText.getText().toString().replace(unit, "").trim();
            if (!value.isEmpty()) {
                if (value.equals("0")) {
                    editText.setText("");
                } else {
                    editText.setText(value);
                }
            }

            setInputType(editText);

        } else {
            editText.setInputType(InputType.TYPE_NULL);

            if (!editText.getText().toString().isEmpty()) {
                String[] number = editText.getText().toString().split(" ");
                editText.setText(number[0] + " " + unit);
            }
        }
    }

    private void validateFirstVital() {
        String data = vital1Et.getText().toString().split(" ")[0];

        isFirstValid = false;
        if (!data.isEmpty()) {
            isInputValid = false;
            if (!isValidInput(data, inputType)) {
                vital1Til.setError(VitalsConstant.getInputError(getActivity(), inputType));
                enableOrDisableSubmit(false);
            } else {
                isInputValid = true;
                isFirstValid = true;
                enableOrDisableSubmit(true);
            }
        } else {
            isInputValid = false;
            enableOrDisableSubmit(false);
        }
    }

    private void validateSecondVital() {
        String data = vital2Et.getText().toString().split(" ")[0];
        isSecondValid = false;

        if (!data.isEmpty()) {
            if (!isValidInput(data, secondInputType)) {
                vital2Til.setError(VitalsConstant.getInputError(getActivity(), secondInputType));
                enableOrDisableSubmit(false);
            } else {
                isSecondValid = true;
                enableOrDisableSubmit(true);
            }

        } else {
            enableOrDisableSubmit(false);
        }


        if (!isInputValid) {
            enableOrDisableSubmit(false);
        }
    }

    private void validateThirdVital() {
        String data = vital3Et.getText().toString().split(" ")[0];
        isThirdValid = false;

        if (data.isEmpty()) {
            enableOrDisableSubmit(false);
        } else {
            if (data.equals("0")) {
                enableOrDisableSubmit(false);
                if (vital3Et.isFocused()) {
                    vital3Til.setError(VitalsConstant.getInputError(getActivity(), VitalsConstant.INPUT_PULSE));
                }
            } else {
                if (!isValidInput(data, VitalsConstant.INPUT_PULSE)) {
                    vital3Til.setError(VitalsConstant.getInputError(getActivity(), VitalsConstant.INPUT_PULSE));
                    enableOrDisableSubmit(false);
                } else {
                    isThirdValid = true;
                    enableOrDisableSubmit(true);
                }
            }
        }
    }

    private boolean isValidInput(String data, String inputType) {
        if (data.isEmpty()) {
            return false;
        }

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
    public void didFinishPost() {
        onCloseActionInterface.onClose(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                Utils.hideKeyboard(getActivity());
                onCloseActionInterface.onClose(false);
                break;
            case R.id.submit_btn:
                Utils.hideKeyboard(getActivity());
                CreateVitalApiRequestModel vitalApiRequestModel = new CreateVitalApiRequestModel();

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

                @Nullable
                CreateVitalApiRequestModel secondary = null;
                double vital1Value = 0;
                if (!vital1Et.getText().toString().isEmpty())
                    vital1Value = Double.parseDouble(vital1Et.getText().toString().split(" ")[0]);

                if (selectedItem.equals(SupportedMeasurementType.bp)) {

                    if (isFirstValid && isSecondValid && !isThirdValid) {
                        double vital2Value = Double.parseDouble(vital2Et.getText().toString().split(" ")[0]);
                        String value = (int) vital1Value + "/" + (int) vital2Value;
                        vitalApiRequestModel.setType(SupportedMeasurementType.bp);
                        vitalApiRequestModel.setValue(value);
                    } else if (isThirdValid && !(isFirstValid && isSecondValid)) {
                        double vital3Value = Double.parseDouble(vital3Et.getText().toString().split(" ")[0]);
                        vitalApiRequestModel.setType(SupportedMeasurementType.heartRate);
                        vitalApiRequestModel.setValue(String.valueOf((int) vital3Value));
                    } else if (isFirstValid && isSecondValid && isThirdValid) {
                        double vital2Value = Double.parseDouble(vital2Et.getText().toString().split(" ")[0]);
                        String value = (int) vital1Value + "/" + (int) vital2Value;
                        vitalApiRequestModel.setType(SupportedMeasurementType.bp);
                        vitalApiRequestModel.setValue(value);

                        try {
                            double vital3Value = Double.parseDouble(vital3Et.getText().toString().split(" ")[0]);
                            secondary = (CreateVitalApiRequestModel) vitalApiRequestModel.clone();
                            secondary.setValue(String.valueOf((int) vital3Value));
                            secondary.setType(SupportedMeasurementType.heartRate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    String value;
                    switch (selectedItem) {
                        case SupportedMeasurementType.temperature:
                        case SupportedMeasurementType.weight:
                            value = String.format("%.1f", vital1Value);
                            break;
                        case SupportedMeasurementType.height:
                            int feet = ((int) vital1Value) * 12;
                            int inches = 0;
                            try {
                                inches = Integer.parseInt(vital2Et.getText().toString().split(" ")[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            value = String.valueOf(feet + inches);
                            break;
                        default:
                            value = String.valueOf((int) vital1Value);
                    }
                    vitalApiRequestModel.setType(selectedItem);
                    vitalApiRequestModel.setValue(value);
                }

                String doctorGuid = null;
                CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                if (doctorModel != null) {
                    doctorGuid = doctorModel.getUser_guid();
                }

                sendVitals(vitalApiRequestModel, secondary, doctorGuid);

                break;
        }
    }

}
