package com.thealer.telehealer.views.transaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.master.MasterApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.AddChargeViewModel;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldModel;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CallPlacingActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layoutChargeType;
    private LinearLayout layoutReason;
    private EditText etFees;

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;
    private RecyclerView rvSuppliers;
    private RecyclerView rvMedicines;
    private TextView tvChargeType;
    private TextView tvReason;
    private ImageView ivReason;
    private ImageView imgAddSupplier;
    private ImageView imgAddMedicine;

    private EditText etTextField;

    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;

    private ReasonOptionAdapter adapterReason;
    private TextFieldAdapter adapterSupplier;
    private TextFieldAdapter adapterMedicine;

    private DateRangeView viewBHI;
    private DateRangeView viewCCM;
    private DateRangeView viewConcierge;
    private DateRangeView viewRPM;
    private DateRangeView viewDateOfService;


    public static String EXTRA_REASON = "reason";
    public static String EXTRA_PATIENT_ID = "patientId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charge);
        initView();
        initViewModels();
        addChargeViewModel.setSelectedReason(getIntent().getIntExtra(EXTRA_REASON, -1));
        addChargeViewModel.setPatientId(getIntent().getIntExtra(EXTRA_PATIENT_ID, -1));
        masterApiViewModel.fetchMasters();
        updateUI();
        checkForVisitAndSetUI();
    }


    private void initView() {
        layoutChargeType = findViewById(R.id.layoutChargeType);
        layoutReason = findViewById(R.id.layoutReason);
        rvChargeType = findViewById(R.id.rvChargeType);
        rvReason = findViewById(R.id.rvReasonType);
        tvChargeType = findViewById(R.id.tvChargeType);
        tvReason = findViewById(R.id.tvReason);
        ivReason = findViewById(R.id.ivReasonArrow);

        viewBHI = findViewById(R.id.bhi);
        viewCCM = findViewById(R.id.ccm);
        viewRPM = findViewById(R.id.rpm);
        viewConcierge = findViewById(R.id.concierge);
        viewDateOfService = findViewById(R.id.dateOfService);


        etTextField = findViewById(R.id.etTextField);
        etFees = findViewById(R.id.etFees);

        rvSuppliers = findViewById(R.id.rvSuppliers);
        rvMedicines = findViewById(R.id.rvMedicines);

        imgAddMedicine = findViewById(R.id.imgAddMedicine);
        imgAddSupplier = findViewById(R.id.imgAddSupplier);

        imgAddMedicine.setOnClickListener(this);
        imgAddSupplier.setOnClickListener(this);

        layoutChargeType.setOnClickListener(this);

        rvReason.setNestedScrollingEnabled(false);

        etFees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    addChargeViewModel.setFees(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    addChargeViewModel.setFees(0);
                }

            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnPending).setOnClickListener(this);


    }

    private void initViewModels() {
        masterApiViewModel = new ViewModelProvider(this).get(MasterApiViewModel.class);
        addChargeViewModel = new ViewModelProvider(this).get(AddChargeViewModel.class);
        attachObserver(masterApiViewModel);
        attachObserver(addChargeViewModel);

        masterApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof MasterResp) {
                    MasterResp resp = (MasterResp) baseApiResponseModel;
                    addChargeViewModel.setUpChargeTypeFromMasters(resp);
                    MasterOptionAdapter adapterType = new MasterOptionAdapter(addChargeViewModel.getListChargeTypes(), pos -> {
                        rvChargeType.setVisibility(View.GONE);
                        tvChargeType.setText(addChargeViewModel.getListChargeTypes().get(pos).getName());
                        addChargeViewModel.setSelectedChargeTypeId(addChargeViewModel.getListChargeTypes().get(pos).getId());
                    });
                    rvChargeType.setAdapter(adapterType);
                }
            }
        });

        addChargeViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                finish();
            }
        });
        addChargeViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.error), errorMessage, getString(R.string.ok), null, (dialog, which) -> dialog.dismiss(), null);
            }
        });
    }

    private void updateUI() {
        for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
            showUIByReason(reasonOption);
        }
    }


    private void checkForVisitAndSetUI() {
        if (addChargeViewModel.isReasonSelected(Constants.ChargeReason.VISIT)) {
            addChargeViewModel.setOnlyVisit(true);
            layoutReason.setVisibility(View.VISIBLE);
            etFees.setVisibility(View.VISIBLE);
            viewDateOfService.setSingleSelection(getString(R.string.lbl_service_date));
            viewDateOfService.setVisibility(View.VISIBLE);
            tvReason.setText(getString(R.string.visit));
            rvReason.setVisibility(View.GONE);
            ivReason.setVisibility(View.GONE);
            layoutReason.setEnabled(false);
        } else {
            adapterReason = new ReasonOptionAdapter(addChargeViewModel.getReasonOptions(), pos -> {
                addChargeViewModel.getReasonOptions().get(pos).setSelected(!addChargeViewModel.getReasonOptions().get(pos).isSelected());
                adapterReason.notifyItemChanged(pos);
                showUIByReason(addChargeViewModel.getReasonOptions().get(pos));
            });
            rvReason.setAdapter(adapterReason);
            ivReason.setVisibility(View.VISIBLE);


            adapterSupplier = new TextFieldAdapter(addChargeViewModel.getSuppliers(), getString(R.string.lbl_supplier_name), new TextFieldAdapter.OnOptionSelected() {
                @Override
                public void onRemoveField(int pos) {
                    addChargeViewModel.getSuppliers().remove(pos);
                    adapterSupplier.notifyItemRemoved(pos);
                }
            });
            adapterMedicine = new TextFieldAdapter(addChargeViewModel.getMedicines(), getString(R.string.lbl_medicine_name), new TextFieldAdapter.OnOptionSelected() {
                @Override
                public void onRemoveField(int pos) {
                    addChargeViewModel.getMedicines().remove(pos);
                    adapterMedicine.notifyItemRemoved(pos);
                }
            });

            rvMedicines.setAdapter(adapterMedicine);
            rvSuppliers.setAdapter(adapterSupplier);

        }
    }

    private void showUIByReason(ReasonOption reasonOption) {
        switch (reasonOption.getValue()) {
            case Constants.ChargeReason.BHI: {
                viewBHI.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.CCM: {
                viewCCM.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.RPM: {
                viewRPM.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.CONCIERGE: {
                viewConcierge.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.MEDICINE: {
                if (reasonOption.isSelected()) {
                    if (addChargeViewModel.getMedicines().size() == 0) {
                        addChargeViewModel.getMedicines().add(new TextFieldModel());
                        adapterMedicine.notifyDataSetChanged();
                    }
                    rvMedicines.setVisibility(View.VISIBLE);
                    imgAddMedicine.setVisibility(View.VISIBLE);
                } else {
                    rvMedicines.setVisibility(View.GONE);
                    imgAddMedicine.setVisibility(View.GONE);
                    addChargeViewModel.getMedicines().clear();
                }
                break;
            }
            case Constants.ChargeReason.SUPPLIES: {
                if (reasonOption.isSelected()) {
                    if (addChargeViewModel.getSuppliers().size() == 0) {
                        addChargeViewModel.getSuppliers().add(new TextFieldModel());
                        adapterSupplier.notifyDataSetChanged();
                    }
                    rvSuppliers.setVisibility(View.VISIBLE);
                    imgAddSupplier.setVisibility(View.VISIBLE);
                } else {
                    rvSuppliers.setVisibility(View.GONE);
                    imgAddSupplier.setVisibility(View.GONE);
                    addChargeViewModel.getSuppliers().clear();
                }
                break;
            }
            case Constants.ChargeReason.VISIT: {
                if (reasonOption.isSelected()) {
                    viewDateOfService.setVisibility(View.VISIBLE);
                } else {
                    viewDateOfService.setVisibility(View.GONE);
                }
                viewDateOfService.setSingleSelection(getString(R.string.lbl_service_date));
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutChargeType:
                if (rvChargeType.getVisibility() == View.VISIBLE)
                    rvChargeType.setVisibility(View.GONE);
                else
                    rvChargeType.setVisibility(View.VISIBLE);
                break;
            case R.id.layoutReason:
                if (rvReason.getVisibility() == View.VISIBLE)
                    rvReason.setVisibility(View.GONE);
                else
                    rvReason.setVisibility(View.VISIBLE);
                break;
            case R.id.imgAddSupplier: {
                addChargeViewModel.getSuppliers().add(new TextFieldModel());
                adapterSupplier.notifyDataSetChanged();
                break;
            }
            case R.id.imgAddMedicine: {
                addChargeViewModel.getMedicines().add(new TextFieldModel());
                adapterMedicine.notifyDataSetChanged();
                break;
            }
            case R.id.btnSubmit: {
                if (isValid())
                    addChargeViewModel.addCharge(getReq());
                break;
            }
            case R.id.btnPending: {
                break;
            }
        }
    }

    private boolean isValid() {
        if (addChargeViewModel.getSelectedChargeTypeId() == -1) {
            showError(getString(R.string.msg_please_select_charge_type));
            return false;
        }
        if (addChargeViewModel.isOnlyVisit()) {
            if (viewDateOfService.getSelectedFromDate() == null) {
                showError(getString(R.string.msg_please_select_date_of_service));
                return false;
            }
            if (etFees.getText().length() == 0) {
                showError(getString(R.string.msg_please_enter_fees));
                return false;
            }
        } else {
            int selectedCount = 0;
            for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
                if (reasonOption.isSelected()) {
                    selectedCount++;
                    if (reasonOption.getFee() == 0) {
                        showError(getString(R.string.msg_please_enter_fees_for_any, reasonOption.getTitle()));
                        return false;
                    }
                    switch (reasonOption.getValue()) {
                        case Constants.ChargeReason.BHI: {
                            if (hasDateError(reasonOption, viewBHI.getSelectedFromDate(), viewBHI.getSelectedToDate())) {
                                return false;
                            }
                            break;
                        }
                        case Constants.ChargeReason.CCM: {
                            if (hasDateError(reasonOption, viewCCM.getSelectedFromDate(), viewCCM.getSelectedToDate())) {
                                return false;
                            }
                            break;
                        }
                        case Constants.ChargeReason.RPM: {
                            if (hasDateError(reasonOption, viewRPM.getSelectedFromDate(), viewRPM.getSelectedToDate())) {
                                return false;
                            }
                            break;
                        }
                        case Constants.ChargeReason.CONCIERGE: {
                            if (hasDateError(reasonOption, viewConcierge.getSelectedFromDate(), viewConcierge.getSelectedToDate())) {
                                return false;
                            }
                            break;
                        }
                        case Constants.ChargeReason.MEDICINE: {
                            if (addChargeViewModel.getMedicines().size() == 1) {
                                if (addChargeViewModel.getMedicines().get(0).getValue() == null || addChargeViewModel.getMedicines().get(0).getValue().isEmpty()) {
                                    showError(getString(R.string.msg_please_enter_medicine_name));
                                    return false;
                                }
                            } else {
                                for (TextFieldModel textFieldModel : addChargeViewModel.getMedicines()) {
                                    if (textFieldModel.getValue() == null || textFieldModel.getValue().isEmpty()) {
                                        showError(getString(R.string.msg_please_enter_medicine_name));
                                        return false;
                                    }
                                }

                            }
                            break;
                        }
                        case Constants.ChargeReason.SUPPLIES: {
                            if (addChargeViewModel.getSuppliers().size() == 1) {
                                if (addChargeViewModel.getSuppliers().get(0).getValue() == null || addChargeViewModel.getSuppliers().get(0).getValue().isEmpty()) {
                                    showError(getString(R.string.msg_please_enter_supplier_name));
                                    return false;
                                }
                            } else {
                                for (TextFieldModel textFieldModel : addChargeViewModel.getSuppliers()) {
                                    if (textFieldModel.getValue() == null || textFieldModel.getValue().isEmpty()) {
                                        showError(getString(R.string.msg_please_enter_supplier_name));
                                        return false;
                                    }
                                }
                            }
                            break;
                        }
                        case Constants.ChargeReason.VISIT: {
                            if (viewDateOfService.getSelectedFromDate() == null) {
                                showError(getString(R.string.msg_please_select_date_of_service));
                                return false;
                            }
                            break;
                        }
                    }
                }
            }
            if (selectedCount == 0) {
                showError(getString(R.string.msg_please_select_a_reason));
                return false;
            }
        }
        return true;
    }

    private boolean hasDateError(ReasonOption reasonOption, Calendar start, Calendar end) {
        if (start == null) {
            showError(getString(R.string.msg_please_select_start_date_for_any, reasonOption.getTitle()));
            return true;
        }
        if (end == null) {
            showError(getString(R.string.msg_please_select_end_date_for_any, reasonOption.getTitle()));
            return true;
        }
        if (start.after(end)) {
            showError(getString(R.string.msg_please_select_valid_date_range_for_any, reasonOption.getTitle()));
            return true;
        }
        return false;
    }

    private void showError(String message) {
        Utils.showAlertDialog(this, getString(R.string.error), message, getString(R.string.ok), null, null, null);
    }

    private AddChargeReq getReq() {
        AddChargeReq req = new AddChargeReq();
        req.setTypeOfCharge(addChargeViewModel.getSelectedChargeTypeId());
        req.setPatientId(addChargeViewModel.getPatientId());
        ArrayList<AddChargeReq.ChargeDataItem> chargeData = new ArrayList<>();
        if (addChargeViewModel.isOnlyVisit()) {
            AddChargeReq.ChargeDataItem chargeDataItem = new AddChargeReq.ChargeDataItem();
            chargeDataItem.setAmount(addChargeViewModel.getFees());
            chargeDataItem.setReason(Constants.ChargeReason.VISIT);
            AddChargeReq.Description description = new AddChargeReq.Description();
            description.setDateOfService(Utils.getUTCDateFromCalendar(viewDateOfService.getSelectedFromDate()));
            chargeDataItem.setDescription(description);
            chargeData.add(chargeDataItem);
        } else {
            for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
                if (reasonOption.isSelected()) {
                    AddChargeReq.ChargeDataItem chargeDataItem = new AddChargeReq.ChargeDataItem();
                    chargeDataItem.setAmount(reasonOption.getFee());
                    chargeDataItem.setReason(reasonOption.getValue());
                    chargeDataItem.setDescription(getDescriptionByReason(reasonOption));
                    chargeData.add(chargeDataItem);
                }
            }
        }
        req.setChargeData(chargeData);
        return req;
    }

    private AddChargeReq.Description getDescriptionByReason(ReasonOption reasonOption) {
        AddChargeReq.Description description = new AddChargeReq.Description();
        switch (reasonOption.getValue()) {
            case Constants.ChargeReason.VISIT: {
                description.setDateOfService(Utils.getUTCDateFromCalendar(viewDateOfService.getSelectedFromDate()));
                break;
            }
            case Constants.ChargeReason.BHI: {
                setDateInDescription(description, viewBHI);
                break;
            }
            case Constants.ChargeReason.CCM: {
                setDateInDescription(description, viewCCM);
                break;
            }
            case Constants.ChargeReason.RPM: {
                setDateInDescription(description, viewRPM);
                break;
            }
            case Constants.ChargeReason.CONCIERGE: {
                setDateInDescription(description, viewConcierge);
                break;
            }
            case Constants.ChargeReason.MEDICINE: {
                ArrayList<String> medicines = new ArrayList<>();
                for (TextFieldModel textFieldModel : addChargeViewModel.getMedicines()) {
                    medicines.add(textFieldModel.getValue());
                }
                description.setMedicines(medicines);
                break;
            }
            case Constants.ChargeReason.SUPPLIES: {
                ArrayList<String> supplier = new ArrayList<>();
                for (TextFieldModel textFieldModel : addChargeViewModel.getSuppliers()) {
                    supplier.add(textFieldModel.getValue());
                }
                description.setSuppliers(supplier);
                break;
            }
        }
        return description;
    }

    private void setDateInDescription(AddChargeReq.Description description, DateRangeView dateRangeView) {
        description.setStartDate(Utils.getUTCDateFromCalendar(dateRangeView.getSelectedFromDate()));
        description.setEndDate(Utils.getUTCDateFromCalendar(dateRangeView.getSelectedToDate()));
    }


}
