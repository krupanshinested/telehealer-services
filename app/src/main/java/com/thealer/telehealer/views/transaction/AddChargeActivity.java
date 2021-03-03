package com.thealer.telehealer.views.transaction;

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

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.master.MasterApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.AddChargeViewModel;
import com.thealer.telehealer.apilayer.models.transaction.DateRangeReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.SingleDateReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldModel;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {


    private RecyclerView rvReason;

    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;

    private ReasonOptionAdapter adapterReason;
    private DateView viewDateOfService;


    public static String EXTRA_REASON = "reason";
    public static String EXTRA_PATIENT_ID = "patientId";
    public static String EXTRA_TRANSACTION_ITEM = "transactionItem";
    public static String EXTRA_ORDER_ID = "orderId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charge);
        initView();
        initViewModels();
        addChargeViewModel.setSelectedReason(getIntent().getIntExtra(EXTRA_REASON, -1));
        addChargeViewModel.setPatientId(getIntent().getIntExtra(EXTRA_PATIENT_ID, -1));
        addChargeViewModel.setOrderId(getIntent().getStringExtra(EXTRA_ORDER_ID));
        String json = getIntent().getStringExtra(EXTRA_TRANSACTION_ITEM);
        if (json != null && json.length() > 0) {
            prepareDataFromTransactionItem(new Gson().fromJson(json, TransactionItem.class));
        }
        masterApiViewModel.fetchMasters();
        checkForVisitAndSetUI();

        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(R.string.lbl_transaction);
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {
        rvReason = findViewById(R.id.rvReasonType);

        viewDateOfService = findViewById(R.id.dateOfService);

        rvReason.setNestedScrollingEnabled(false);

        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnSubmitProcess).setOnClickListener(this);

    }

    private void setAdapters() {
        adapterReason = new ReasonOptionAdapter(addChargeViewModel.getReasonOptions(), true, pos -> {
            addChargeViewModel.getReasonOptions().get(pos).setSelected(!addChargeViewModel.getReasonOptions().get(pos).isSelected());
            adapterReason.notifyItemChanged(pos);
        });
        adapterReason.setTypeMasters(addChargeViewModel.getListChargeTypes());
        rvReason.setAdapter(adapterReason);

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
                    setAdapters();
                }
            }
        });

        addChargeViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                setResult(RESULT_OK);
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

    public void prepareDataFromTransactionItem(TransactionItem transactionItem) {
        if (transactionItem != null) {
            addChargeViewModel.setChargeId(transactionItem.getId());
            addChargeViewModel.setPatientId(transactionItem.getPatientId().getUser_id());
            addChargeViewModel.setOrderId(transactionItem.getOrderId());
            addChargeViewModel.setSelectedChargeTypeId(transactionItem.getTypeOfCharge().getId());
            if (transactionItem.getChargeData() != null && transactionItem.getChargeData().size() > 0) {
                for (AddChargeReq.ChargeDataItem item : transactionItem.getChargeData()) {
                    setReasonDataFromTransactionItem(item, addChargeViewModel.getReasonByValue(item.getReason()));
                }
            }
            adapterReason.notifyDataSetChanged();
        }
    }

    private void setReasonDataFromTransactionItem(AddChargeReq.ChargeDataItem item, ReasonOption reasonOption) {
        if (reasonOption != null) {
            reasonOption.setFee(item.getAmount());
            reasonOption.setSelected(true);
            if (reasonOption instanceof SingleDateReasonOption) {
                ((SingleDateReasonOption) reasonOption).setDate(Utils.getCalendar(item.getDescription().getDateOfService()));
            }
            if (reasonOption instanceof DateRangeReasonOption) {
                ((DateRangeReasonOption) reasonOption).setStartDate(Utils.getCalendar(item.getDescription().getStartDate()));
                ((DateRangeReasonOption) reasonOption).setEndDate(Utils.getCalendar(item.getDescription().getEndDate()));
            }
            if (reasonOption instanceof TextFieldReasonOption) {
                ((TextFieldReasonOption) reasonOption).setTextFieldValues(new ArrayList<>());
                List<String> fieldValues = null;
                if (reasonOption.getValue() == Constants.ChargeReason.MEDICINE) {
                    if (item.getDescription().getMedicines() != null && item.getDescription().getMedicines().size() != 0) {
                        fieldValues = item.getDescription().getMedicines();
                    }
                }
                if (reasonOption.getValue() == Constants.ChargeReason.SUPPLIES) {
                    if (item.getDescription().getSuppliers() != null && item.getDescription().getSuppliers().size() != 0) {
                        fieldValues = item.getDescription().getSuppliers();
                    }
                }
                if (fieldValues != null && fieldValues.size() > 0) {
                    for (String value : fieldValues) {
                        ((TextFieldReasonOption) reasonOption).getTextFieldValues().add(new TextFieldModel(value));
                    }
                }
            }
        }
    }


    private void checkForVisitAndSetUI() {
        int selectedCount = addChargeViewModel.getSelectedReasonCount();
        if (addChargeViewModel.isReasonSelected(Constants.ChargeReason.VISIT)) {
            if (selectedCount == 1) {
                addChargeViewModel.setOnlyVisit(true);
                rvReason.setVisibility(View.GONE);
                viewDateOfService.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutReason:
                if (rvReason.getVisibility() == View.VISIBLE)
                    rvReason.setVisibility(View.GONE);
                else
                    rvReason.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSubmit: {
                if (isValid()) {
                    addChargeViewModel.addCharge(getReq(), getIntent().getStringExtra(EXTRA_TRANSACTION_ITEM) != null);
                }
                break;
            }
            case R.id.btnSubmitProcess: {

            }
        }
    }

    private boolean isValid() {
        if (addChargeViewModel.getSelectedChargeTypeId() == -1) {
            showError(getString(R.string.msg_please_select_charge_type));
            return false;
        }
        if (addChargeViewModel.isOnlyVisit()) {
            if (viewDateOfService.getSelectedDate() == null) {
                showError(getString(R.string.msg_please_select_date_of_service));
                return false;
            }
            if (addChargeViewModel.getFees() <= Constants.STRIPE_MIN_AMOUNT) {
                showError(getString(R.string.msg_any_fees_should_be_greater_than_minimum, getString(R.string.visit), Constants.STRIPE_MIN_AMOUNT));
                return false;
            }
        } else {
            int selectedCount = 0;
            double totalFees = 0;
            for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
                if (reasonOption.isSelected()) {
                    selectedCount++;
                    if (reasonOption.getFee() <= Constants.STRIPE_MIN_AMOUNT) {
                        showError(getString(R.string.msg_any_fees_should_be_greater_than_minimum, reasonOption.getTitle(), Constants.STRIPE_MIN_AMOUNT));
                        return false;
                    }

                    if (reasonOption instanceof DateRangeReasonOption) {
                        if (hasDateError((DateRangeReasonOption) reasonOption)) {
                            return false;
                        }
                    }
                    if (reasonOption instanceof SingleDateReasonOption) {
                        if (((SingleDateReasonOption) reasonOption).getDate() == null) {
                            showError(getString(R.string.msg_please_select_date_of_service));
                            return false;
                        }

                    }
                    if (reasonOption instanceof TextFieldReasonOption) {
                        TextFieldReasonOption textFieldReasonOption = (TextFieldReasonOption) reasonOption;
                        for (TextFieldModel textFieldModel : textFieldReasonOption.getTextFieldValues()) {
                            if (textFieldModel.getValue() == null || textFieldModel.getValue().isEmpty()) {
                                if (reasonOption.getValue() == Constants.ChargeReason.MEDICINE)
                                    showError(getString(R.string.msg_please_enter_medicine_name));
                                else if (reasonOption.getValue() == Constants.ChargeReason.SUPPLIES)
                                    showError(getString(R.string.msg_please_enter_supplier_name));
                                return false;
                            }
                        }
                    }
                    totalFees += reasonOption.getFee();
                }
            }
            if (selectedCount == 0) {
                showError(getString(R.string.msg_please_select_a_reason));
                return false;
            }
            if (totalFees < 1) {
                showError(getString(R.string.msg_total_amount_should_be_greater_than_one));
                return false;
            }
        }
        return true;
    }

    private boolean hasDateError(DateRangeReasonOption reasonOption) {
        if (reasonOption.getStartDate() == null) {
            showError(getString(R.string.msg_please_select_start_date_for_any, reasonOption.getTitle()));
            return true;
        }
        if (reasonOption.getEndDate() == null) {
            showError(getString(R.string.msg_please_select_end_date_for_any, reasonOption.getTitle()));
            return true;
        }
        if (reasonOption.getStartDate().after(reasonOption.getEndDate())) {
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
        req.setOrderId(addChargeViewModel.getOrderId());
        if (addChargeViewModel.getPatientId() > 0)
            req.setPatientId(addChargeViewModel.getPatientId());
        else {
            req.setPatientId(null);
        }
        ArrayList<AddChargeReq.ChargeDataItem> chargeData = new ArrayList<>();
        if (addChargeViewModel.isOnlyVisit()) {
            AddChargeReq.ChargeDataItem chargeDataItem = new AddChargeReq.ChargeDataItem();
            chargeDataItem.setAmount(addChargeViewModel.getFees());
            chargeDataItem.setReason(Constants.ChargeReason.VISIT);
            AddChargeReq.Description description = new AddChargeReq.Description();
            description.setDateOfService(Utils.getUTCDateFromCalendar(viewDateOfService.getSelectedDate()));
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
        if (reasonOption instanceof SingleDateReasonOption) {
            description.setDateOfService(Utils.getUTCDateFromCalendar(((SingleDateReasonOption) reasonOption).getDate()));
        }
        if (reasonOption instanceof DateRangeReasonOption) {
            DateRangeReasonOption dateRangeReasonOption = ((DateRangeReasonOption) reasonOption);
            description.setStartDate(Utils.getUTCDateFromCalendar(dateRangeReasonOption.getStartDate()));
            description.setEndDate(Utils.getUTCDateFromCalendar(dateRangeReasonOption.getEndDate()));
        }
        if (reasonOption instanceof TextFieldReasonOption) {
            if (reasonOption.getValue() == Constants.ChargeReason.SUPPLIES) {
                description.setSuppliers(new ArrayList<>());
                for (TextFieldModel model : ((TextFieldReasonOption) reasonOption).getTextFieldValues()) {
                    description.getSuppliers().add(model.getValue());
                }
            }
            if (reasonOption.getValue() == Constants.ChargeReason.MEDICINE) {
                description.setMedicines(new ArrayList<>());
                for (TextFieldModel model : ((TextFieldReasonOption) reasonOption).getTextFieldValues()) {
                    description.getMedicines().add(model.getValue());
                }
            }
        }
        return description;
    }
}
