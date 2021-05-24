package com.thealer.telehealer.views.transaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import com.thealer.telehealer.apilayer.models.transaction.AskToAddCardViewModel;
import com.thealer.telehealer.apilayer.models.transaction.DateRangeReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.SingleDateReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldModel;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.TransactionListViewModel;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.AddChargeResp;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {


    private RecyclerView rvReason;

    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;
    private TransactionListViewModel transactionListViewModel;
    private AskToAddCardViewModel askToAddCardViewModel;


    private ReasonOptionAdapter adapterReason;


    public static String EXTRA_REASON = "reason";
    public static String EXTRA_PATIENT_ID = "patientId";
    public static String EXTRA_TRANSACTION_ITEM = "transactionItem";
    public static String EXTRA_ORDER_ID = "orderId";
    public static String EXTRA_DOCTOR_ID = "doctorId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charge);
        initView();
        initViewModels();
        addChargeViewModel.setSelectedReason(getIntent().getIntExtra(EXTRA_REASON, -1));
        addChargeViewModel.setPatientId(getIntent().getIntExtra(EXTRA_PATIENT_ID, -1));
        addChargeViewModel.setDoctorId(getIntent().getIntExtra(EXTRA_DOCTOR_ID, -1));
        addChargeViewModel.setOrderId(getIntent().getStringExtra(EXTRA_ORDER_ID));
        masterApiViewModel.fetchMasters();

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
        rvReason.setNestedScrollingEnabled(false);

        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnSubmitProcess).setOnClickListener(this);
    }

    private void setAdapters() {
        adapterReason = new ReasonOptionAdapter(addChargeViewModel.getReasonOptions(), true, pos -> {
            addChargeViewModel.getReasonOptions().get(pos).setSelected(!addChargeViewModel.getReasonOptions().get(pos).isSelected());
            adapterReason.notifyItemChanged(pos);
            if(pos>=2) {
                if (pos + 2 < addChargeViewModel.getReasonOptions().size() - 1)
                    rvReason.scrollToPosition(pos + 2);
                else if(pos==(addChargeViewModel.getReasonOptions().size()-1))
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvReason.scrollToPosition(addChargeViewModel.getReasonOptions().size() - 1);
                        }
                    }, 200);
                else
                    rvReason.scrollToPosition(addChargeViewModel.getReasonOptions().size() - 1);
            }else{
                rvReason.scrollToPosition(0);
            }

        });
        adapterReason.setTypeMasters(addChargeViewModel.getListChargeTypes());
        rvReason.setAdapter(adapterReason);

    }

    private void initViewModels() {
        masterApiViewModel = new ViewModelProvider(this).get(MasterApiViewModel.class);
        addChargeViewModel = new ViewModelProvider(this).get(AddChargeViewModel.class);
        transactionListViewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
        askToAddCardViewModel = new ViewModelProvider(this).get(AskToAddCardViewModel.class);
        attachObserver(masterApiViewModel);
        attachObserver(addChargeViewModel);
        attachObserver(transactionListViewModel);
        attachObserver(askToAddCardViewModel);

        masterApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof MasterResp) {
                    MasterResp resp = (MasterResp) baseApiResponseModel;
                    addChargeViewModel.setUpChargeTypeFromMasters(resp);
                    String json = getIntent().getStringExtra(EXTRA_TRANSACTION_ITEM);
                    if (json != null && json.length() > 0) {
                        prepareDataFromTransactionItem(new Gson().fromJson(json, TransactionItem.class));
                    }
                    setAdapters();
                    checkForVisitAndSetUI();
                }
            }
        });

        addChargeViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (addChargeViewModel.isSaveAndProcess()) {
                    if (baseApiResponseModel instanceof AddChargeResp) {
                        addChargeViewModel.setAddedTransaction(((AddChargeResp) baseApiResponseModel).getData());
                        transactionListViewModel.processPayment(addChargeViewModel.getAddedTransaction().getId(), Constants.PaymentMode.STRIPE);
                    }
                } else {
                    Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.success), baseApiResponseModel.getMessage(), getString(R.string.ok), null, (dialog, which) -> {
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }, null);

                }
            }
        });
        addChargeViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.error), errorMessage, getString(R.string.ok), null, (dialog, which) -> dialog.dismiss(), null);
            }
        });

        transactionListViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModels) {
                Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.success), baseApiResponseModels.getMessage(), getString(R.string.ok), null, (dialog, which) -> {
                    dialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }, null);
            }
        });
        transactionListViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                String json = errorModel.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    Runnable closeListener = new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_CANCELED);
                            confirmCharges();
                        }
                    };

                    Runnable proceedOfflineListener = new Runnable() {
                        @Override
                        public void run() {
                            transactionListViewModel.processPayment(addChargeViewModel.getAddedTransaction().getId(), Constants.PaymentMode.CASH);
                        }
                    };
                    Runnable paymentSettingListener = new Runnable() {
                        @Override
                        public void run() {
                            Constants.isRedirectProfileSetting = true;
                            Intent intent = new Intent(AddChargeActivity.this, ProfileSettingsActivity.class);
                            startActivity(intent);
                        }
                    };

                    if (jsonObject.has("display_button") && !errorModel.isDisplayButton()) {
                        String errMsg = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                        Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.app_name), errMsg,
                                getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, null);
                    }else if (!jsonObject.has("is_cc_captured") || AppPaymentCardUtils.hasValidPaymentCard(errorModel)) {
                        String message = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                        Utils.showAlertDialogWithClose(AddChargeActivity.this, getString(R.string.app_name), message,
                                getString(R.string.lbl_proceed_offline), getString(R.string.payment_settings),
                                proceedOfflineListener, paymentSettingListener, closeListener).getWindow().setBackgroundDrawableResource(R.drawable.border_red);
                    } else {
                        if (addChargeViewModel.getAddedTransaction() != null) {
                            showPatientCardErrorOptions();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        askToAddCardViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel responseModel) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        askToAddCardViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.error),
                        errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                        null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        });
            }
        });


    }

    private void confirmCharges() {
        Utils.showAlertDialog(AddChargeActivity.this, getString(R.string.success),
                getString(R.string.charge_successfully_added),
                null, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
    }

    private void showPatientCardErrorOptions() {
        ArrayList<String> options = new ArrayList<>();
        if (addChargeViewModel.getAddedTransaction().getDoctorId() != null)
            if (addChargeViewModel.getAddedTransaction().getDoctorId().isCan_view_card_status())
                options.add(getString(R.string.lbl_ask_to_add_credit_card));
        options.add(getString(R.string.lbl_proceed_offline));
        String message = getString(R.string.msg_invalid_credit_card_in_transaction_process, addChargeViewModel.getAddedTransaction().getPatientId().getDisplayName());

        if (options.size() == 1) {
            Utils.showAlertDialog(this, getString(R.string.error), message, getString(R.string.lbl_proceed_offline), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    transactionListViewModel.processPayment(addChargeViewModel.getAddedTransaction().getId(), Constants.PaymentMode.CASH);
                    dialog.dismiss();
                }
            }, (dialog, which) -> {
                setResult(RESULT_CANCELED);
                finish();
            }).getWindow().setBackgroundDrawableResource(R.drawable.border_red);
            return;
        }
        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(this, message, options, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {

                if (getString(R.string.lbl_ask_to_add_credit_card).equals(options.get(position))) {
                    askToAddCardViewModel.askToAddCard(addChargeViewModel.getAddedTransaction().getPatientId().getUser_guid(), addChargeViewModel.getAddedTransaction().getDoctorId().getUser_guid());
                } else if (getString(R.string.lbl_proceed_offline).equals(options.get(position))) {
                    transactionListViewModel.processPayment(addChargeViewModel.getAddedTransaction().getId(), Constants.PaymentMode.CASH);
                }

            }

            @Override
            public void didCancelled() {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        itemPickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_red);
        itemPickerDialog.setCancelable(false);
        itemPickerDialog.show();
    }

    public void prepareDataFromTransactionItem(TransactionItem transactionItem) {
        if (transactionItem != null) {
            addChargeViewModel.setChargeId(transactionItem.getId());
            addChargeViewModel.setPatientId(transactionItem.getPatientId().getUser_id());
            addChargeViewModel.setDoctorId(transactionItem.getDoctorId().getUser_id());
            addChargeViewModel.setOrderId(transactionItem.getOrderId());
            if (transactionItem.getChargeData() != null && transactionItem.getChargeData().size() > 0) {
                for (AddChargeReq.ChargeDataItem item : transactionItem.getChargeData()) {
                    setReasonDataFromTransactionItem(item, addChargeViewModel.getReasonByValue(item.getReason()));
                }
            }
        }
    }

    private void setReasonDataFromTransactionItem(AddChargeReq.ChargeDataItem item, ReasonOption reasonOption) {
        if (reasonOption != null && item != null) {
            reasonOption.setFee(item.getAmount());
            reasonOption.setSelected(true);
            reasonOption.setChargeTypeCode(item.getTypeOfChargeCode());
            reasonOption.setChargeTypeName(item.getTypeOfChargeName());
            if (item.getDescription() != null) {
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
    }


    private void checkForVisitAndSetUI() {
//        int selectedCount = addChargeViewModel.getSelectedReasonCount();
        /*if (addChargeViewModel.isReasonSelected(Constants.ChargeReason.VISIT)) {
            if (selectedCount == 1) {
                addChargeViewModel.setOnlyVisit(true);

                for (int i = addChargeViewModel.getReasonOptions().size() - 1; i > 0; i--) {
                    addChargeViewModel.getReasonOptions().remove(i);
                }
            }
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit: {
                if (isValid()) {
                    addChargeViewModel.addCharge(getReq(), getIntent().getStringExtra(EXTRA_TRANSACTION_ITEM) != null);
                }
                break;
            }
            case R.id.btnSubmitProcess: {
                if (isValid()) {
                    addChargeViewModel.setSaveAndProcess(true);
                    addChargeViewModel.addCharge(getReq(), getIntent().getStringExtra(EXTRA_TRANSACTION_ITEM) != null);
                }
            }
        }
    }

    private boolean isValid() {
        int selectedCount = 0;
        double totalFees = 0;
        for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
            if (reasonOption.isSelected()) {
                selectedCount++;
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
        req.setOrderId(addChargeViewModel.getOrderId());
        if (req.getDoctorId() != -1)
            req.setDoctorId(addChargeViewModel.getDoctorId());
        if (addChargeViewModel.getPatientId() > 0)
            req.setPatientId(addChargeViewModel.getPatientId());
        else {
            req.setPatientId(null);
        }
        ArrayList<AddChargeReq.ChargeDataItem> chargeData = new ArrayList<>();

        for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
            if (reasonOption.isSelected()) {
                AddChargeReq.ChargeDataItem chargeDataItem = new AddChargeReq.ChargeDataItem();
                chargeDataItem.setAmount(reasonOption.getFee());
                chargeDataItem.setReason(reasonOption.getValue());
                chargeDataItem.setTypeOfChargeCode(reasonOption.getChargeTypeCode());
                chargeDataItem.setTypeOfChargeName(reasonOption.getChargeTypeName());
                chargeDataItem.setDescription(getDescriptionByReason(reasonOption));
                chargeData.add(chargeDataItem);
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
