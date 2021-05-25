package com.thealer.telehealer.views.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.DetailAmountModel;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.RefundItem;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.fragmentcontainer.FragmentContainerActivity;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.PdfViewerFragment;

import java.util.ArrayList;
import java.util.List;

public class TransactionDetailsActivity extends BaseActivity {

    public static final String EXTRA_TRANSACTION = "transaction";

    private TextView tvStatus, tvDate, tvPatient, tvCharge, tvDoctor, tvRefund, tvFailureReason;
    private View doctorRow, patientRow, failureReasonRow;
    private RecyclerView rvReasonCharges, rvRefunds;
    private ImageView imgReceipt,ivPrintMenu;

    private TransactionItem transactionItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        rvReasonCharges = findViewById(R.id.rvReasonCharges);
        rvRefunds = findViewById(R.id.rvRefunds);

        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);
        imgReceipt = findViewById(R.id.imgReceipt);

        tvPatient = findViewById(R.id.tvPatient);

        tvCharge = findViewById(R.id.tvCharge);
        tvDoctor = findViewById(R.id.tvDoctor);
        tvRefund = findViewById(R.id.tvRefund);
        tvFailureReason = findViewById(R.id.tvFailureReason);

        doctorRow = findViewById(R.id.doctorRow);
        patientRow = findViewById(R.id.patientRow);
        failureReasonRow = findViewById(R.id.rowFailureReason);
        ivPrintMenu = findViewById(R.id.print_menu);

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        String json = getIntent().getStringExtra(EXTRA_TRANSACTION);
        if (json != null) {
            transactionItem = new Gson().fromJson(json, TransactionItem.class);
        }
        rvReasonCharges.setNestedScrollingEnabled(false);
        rvRefunds.setNestedScrollingEnabled(false);

        if (transactionItem.getChargeStatus() != Constants.ChargeStatus.CHARGE_PENDING) {
            prepareReasonList();
            prepareRefundList();
            if (transactionItem.getChargeStatus() == Constants.ChargeStatus.CHARGE_PROCESSED) {
                ivPrintMenu.setVisibility(transactionItem.getTransactionReceipt() != null && transactionItem.getTransactionReceipt().length() > 0 ? View.VISIBLE : View.GONE);
                ivPrintMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentKeys.PDF_URL, transactionItem.getTransactionReceipt());
                        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.lbl_receipt));
                        startActivity(new Intent(TransactionDetailsActivity.this, FragmentContainerActivity.class)
                                .putExtra(FragmentContainerActivity.EXTRA_FRAGMENT, PdfViewerFragment.class.getName())
                                .putExtra(FragmentContainerActivity.EXTRA_SHOW_TOOLBAR, false)
                                .putExtra(FragmentContainerActivity.EXTRA_BUNDLE, bundle));
                    }
                });
            }
        } else {
            findViewById(R.id.rowTotal).setVisibility(View.GONE);
            findViewById(R.id.rowRefunds).setVisibility(View.GONE);
        }

        updateUI();
    }

    private void prepareRefundList() {
        if (transactionItem.getRefunds() == null || transactionItem.getRefunds().size() == 0) {
            findViewById(R.id.rowRefunds).setVisibility(View.GONE);
            tvRefund.setVisibility(View.GONE);
            rvRefunds.setVisibility(View.GONE);
            return;
        }
        ArrayList<DetailAmountModel> refundAmounts = new ArrayList<>();
        for (RefundItem refundItem : transactionItem.getRefunds()) {
            DetailAmountModel amountModel = new DetailAmountModel();
            amountModel.setTitle(getString(R.string.lbl_refund));
            amountModel.setAmount(refundItem.getAmount());
            amountModel.setShowReceipt(refundItem.getInvoicePath() != null && refundItem.getInvoicePath().length() > 0);
            amountModel.setReceiptURL(refundItem.getInvoicePath());
            amountModel.setDetails(Utils.getFormatedDateTime(refundItem.getCreatedAt(), Utils.defaultDateFormat));
            refundAmounts.add(amountModel);
        }
        tvRefund.setText(Utils.getFormattedCurrency(transactionItem.getTotalRefund()));
        DetailAmountAdapter adapter = new DetailAmountAdapter(refundAmounts, pos -> {
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.PDF_URL, refundAmounts.get(pos).getReceiptURL());
            bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.lbl_receipt));
            startActivity(new Intent(this, FragmentContainerActivity.class)
                    .putExtra(FragmentContainerActivity.EXTRA_FRAGMENT, PdfViewerFragment.class.getName())
                    .putExtra(FragmentContainerActivity.EXTRA_SHOW_TOOLBAR, false)
                    .putExtra(FragmentContainerActivity.EXTRA_BUNDLE, bundle));
        });
        rvRefunds.setAdapter(adapter);
    }

    private void prepareReasonList() {
        if (transactionItem.getChargeData() == null || transactionItem.getChargeData().size() == 0) {
            findViewById(R.id.rowTotal).setVisibility(View.GONE);
            tvCharge.setVisibility(View.GONE);
            rvReasonCharges.setVisibility(View.GONE);
            return;
        }
        ArrayList<DetailAmountModel> reasonAmounts = new ArrayList<>();
        for (AddChargeReq.ChargeDataItem dataItem : transactionItem.getChargeData()) {
            DetailAmountModel amountModel = new DetailAmountModel();
            if (dataItem.getTypeOfChargeName() != null)
                amountModel.setTitle(String.format("%s (%s)", dataItem.getReasonString(this), dataItem.getTypeOfChargeName()));
            else
                amountModel.setTitle(dataItem.getReasonString(this));
            amountModel.setAmount(dataItem.getAmount());

            AddChargeReq.Description description = dataItem.getDescription();
            if (description != null) {
                List<String> medicines = description.getMedicines();
                if (medicines != null && medicines.size() > 0)
                    amountModel.setDetails(TextUtils.join(",", medicines));

                List<String> supplies = description.getSuppliers();
                if (supplies != null && supplies.size() > 0)
                    amountModel.setDetails(TextUtils.join(",", supplies));

                if (description.getStartDate() != null && description.getEndDate() != null) {
                    String subTitle = Utils.getFormatedDateTime(description.getStartDate(), Utils.defaultDateFormat) + " - " + Utils.getFormattedDateWithoutTimeZone(description.getEndDate(), Utils.defaultDateFormat);
                    amountModel.setDetails(subTitle);
                }
                if (description.getDateOfService() != null) {
                    String subTitle = Utils.getFormatedDateTime(description.getDateOfService(), Utils.defaultDateFormat);
                    amountModel.setDetails(subTitle);
                }
            }
            reasonAmounts.add(amountModel);
        }
        DetailAmountAdapter adapter = new DetailAmountAdapter(reasonAmounts, null);
        rvReasonCharges.setAdapter(adapter);
    }

    void updateUI() {
        tvStatus.setText(transactionItem.getStatusString());
        tvCharge.setText(transactionItem.getAmountString());
        tvDate.setText(Utils.getFormatedDateTime(transactionItem.getCreatedAt(), Utils.dd_mmm_yyyy_hh_mm_a));

        if (transactionItem.getErrorDescription() != null && transactionItem.getErrorDescription().length() > 0) {
            failureReasonRow.setVisibility(View.VISIBLE);
            tvFailureReason.setText(transactionItem.getErrorDescription());
        }
        if (UserType.isUserPatient()) {
            patientRow.setVisibility(View.GONE);

            doctorRow.setVisibility(View.VISIBLE);
            tvDoctor.setText(transactionItem.getDoctorId().getDisplayName());
        } else if (UserType.isUserAssistant()) {
            doctorRow.setVisibility(View.VISIBLE);
            tvDoctor.setText(transactionItem.getDoctorId().getDisplayName());

            patientRow.setVisibility(View.VISIBLE);
            tvPatient.setText(transactionItem.getPatientId().getDisplayName());
        } else {
            patientRow.setVisibility(View.VISIBLE);
            tvPatient.setText(transactionItem.getPatientId().getDisplayName());
            doctorRow.setVisibility(View.GONE);
        }
    }
}
