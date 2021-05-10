package com.thealer.telehealer.views.transaction;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OnItemEndListener;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;

import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionListVH> {

    private List<TransactionItem> list;
    private OnOptionSelected onOptionSelected;
    private OnItemEndListener onItemEndListener;

    public TransactionListAdapter(List<TransactionItem> list, OnOptionSelected onOptionSelected, OnItemEndListener onItemEndListener) {
        this.list = list;
        this.onOptionSelected = onOptionSelected;
        this.onItemEndListener = onItemEndListener;
    }


    @NonNull
    @Override
    public TransactionListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction_list, parent, false);
        return new TransactionListVH(view, onOptionSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListVH holder, int position) {
        if (position == list.size() - 1) {
            onItemEndListener.itemEnd(position);
        }
        holder.tvCharge.setText(list.get(position).getAmountString());
        holder.tvReason.setText(list.get(position).getCommaSeparatedReason(holder.itemView.getContext()));
        holder.tvDate.setText(Utils.getFormatedDateTime(list.get(position).getCreatedAt(), Utils.dd_mmm_yyyy_hh_mm_a));
        holder.failureReasonRow.setVisibility(View.GONE);
        if (list.get(position).getChargeStatus() == Constants.ChargeStatus.CHARGE_PROCESS_FAILED) {
            if (list.get(position).getErrorDescription() != null && list.get(position).getErrorDescription().length() > 0) {
                holder.failureReasonRow.setVisibility(View.VISIBLE);
                holder.tvFailureReason.setText(list.get(position).getErrorDescription());
            }
            holder.itemView.findViewById(R.id.container).setBackgroundResource(R.drawable.border_red);
        } else {
            holder.itemView.findViewById(R.id.container).setBackground(null);
        }

        String statusString = list.get(position).getStatusString();
        if (list.get(position).getChargeStatus() == Constants.ChargeStatus.CHARGE_PROCESSED) {
            int color = 0;
            if (list.get(position).getPaymentMode() == Constants.PaymentMode.STRIPE) {
                statusString += " - " + holder.itemView.getContext().getString(R.string.lbl_online);
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.bt_theme_blue);
            } else {
                statusString += " - " + holder.itemView.getContext().getString(R.string.lbl_offline);
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.bt_theme_green);
            }
            SpannableString formattedMessage = new SpannableString(statusString);
            formattedMessage.setSpan(new ForegroundColorSpan(color), statusString.indexOf("-") + 2, statusString.length(), 0);
            holder.tvStatus.setText(formattedMessage);
        } else {
            holder.tvStatus.setText(statusString);
        }

        if (list.get(position).getTotalRefund() > 0) {
            holder.tvTotalRefund.setText(Utils.getFormattedCurrency(list.get(position).getTotalRefund()));
            holder.refundRow.setVisibility(View.VISIBLE);
        } else
            holder.refundRow.setVisibility(View.GONE);
        if (UserType.isUserPatient()) {
            holder.patientRow.setVisibility(View.GONE);

            holder.doctorRow.setVisibility(View.VISIBLE);
            holder.tvDoctor.setText(list.get(position).getDoctorId().getDisplayName());
            if (list.get(position).getChargeStatus() == Constants.ChargeStatus.CHARGE_PROCESSED) {
                boolean hasReceipt = list.get(position).getTransactionReceipt() != null && list.get(position).getTransactionReceipt().length() > 0;
                holder.btnReceipt.setVisibility(hasReceipt ? View.VISIBLE : View.GONE);
                holder.actionRow.setVisibility(hasReceipt ? View.VISIBLE : View.GONE);
            } else {
                holder.actionRow.setVisibility(View.GONE);
            }

            holder.btnReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onReceiptClick(position);
                }
            });


        } else if (UserType.isUserAssistant()) {
            updateActionsForProvider(holder, position);
            holder.doctorRow.setVisibility(View.VISIBLE);
            holder.tvDoctor.setText(list.get(position).getDoctorId().getDisplayName());

            holder.patientRow.setVisibility(View.VISIBLE);
            holder.tvPatient.setText(list.get(position).getPatientId().getDisplayName());
        } else {
            updateActionsForProvider(holder, position);
            holder.patientRow.setVisibility(View.VISIBLE);
            holder.tvPatient.setText(list.get(position).getPatientId().getDisplayName());
            holder.doctorRow.setVisibility(View.GONE);
        }
    }

    private void updateActionsForProvider(@NonNull TransactionListVH holder, int position) {
        holder.actionRow.setVisibility(View.VISIBLE);
        switch (list.get(position).getChargeStatus()) {
            case Constants.ChargeStatus.CHARGE_ADDED: {
                holder.btnProcessPayment.setVisibility(View.VISIBLE);
                holder.btnRefundClick.setVisibility(View.GONE);
                holder.btnReceipt.setVisibility(View.VISIBLE);
                holder.btnReceipt.setText(R.string.update);
                ((LinearLayout.LayoutParams) holder.btnReceipt.getLayoutParams()).weight = 1;
                break;
            }
            case Constants.ChargeStatus.CHARGE_PENDING: {
                holder.btnRefundClick.setVisibility(View.VISIBLE);
                holder.btnRefundClick.setText(holder.itemView.getContext().getString(R.string.lbl_add_charge));
                holder.btnReceipt.setVisibility(View.GONE);
                holder.btnProcessPayment.setVisibility(View.GONE);
                break;
            }
            case Constants.ChargeStatus.CHARGE_PROCESS_INITIATED:
            case Constants.ChargeStatus.CHARGE_PROCESS_IN_STRIPE: {
                holder.actionRow.setVisibility(View.GONE);
                break;
            }
            case Constants.ChargeStatus.CHARGE_PROCESS_FAILED: {

                holder.btnProcessPayment.setVisibility(View.VISIBLE);
                holder.btnReceipt.setVisibility(View.GONE);
                holder.btnRefundClick.setVisibility(View.GONE);
                break;
            }
            case Constants.ChargeStatus.CHARGE_PROCESSED: {
                holder.btnProcessPayment.setVisibility(View.GONE);
                holder.btnReceipt.setVisibility(View.VISIBLE);
                holder.btnReceipt.setText(R.string.lbl_receipt);
                ((LinearLayout.LayoutParams) holder.btnReceipt.getLayoutParams()).weight = 0.5f;
                if (list.get(position).getPaymentMode() == Constants.PaymentMode.STRIPE) {
                    if (list.get(position).getTotalRefund() < list.get(position).getAmount())
                        holder.btnRefundClick.setVisibility(View.VISIBLE);
                    else
                        holder.btnRefundClick.setVisibility(View.GONE);
                } else
                    holder.btnRefundClick.setVisibility(View.GONE);
                break;
            }
        }

        holder.btnRefundClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getChargeStatus() == Constants.ChargeStatus.CHARGE_PENDING)
                    onOptionSelected.onAddChargeClick(position);
                else
                    onOptionSelected.onRefundClick(position);
            }
        });

        holder.btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getChargeStatus() == Constants.ChargeStatus.CHARGE_ADDED)
                    onOptionSelected.onUpdateChargeClick(position);
                else
                    onOptionSelected.onReceiptClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TransactionListVH extends RecyclerView.ViewHolder {

        Button btnReceipt;
        Button btnProcessPayment;
        Button btnRefundClick;
        TextView tvStatus, tvDate, tvDateOfService, tvPatient, tvReason, tvCharge, tvDoctor, tvFailureReason, tvTotalRefund;
        View doctorRow, patientRow, failureReasonRow, refundRow;
        View actionRow;

        public TransactionListVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            btnReceipt = itemView.findViewById(R.id.btnReciept);
            btnRefundClick = itemView.findViewById(R.id.btnRefund);
            btnProcessPayment = itemView.findViewById(R.id.btnProcessPayment);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDateOfService = itemView.findViewById(R.id.tvDateOfService);
            tvPatient = itemView.findViewById(R.id.tvPatient);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvCharge = itemView.findViewById(R.id.tvCharge);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvFailureReason = itemView.findViewById(R.id.tvFailureReason);
            tvTotalRefund = itemView.findViewById(R.id.tvTotalRefund);

            actionRow = itemView.findViewById(R.id.actionRow);
            doctorRow = itemView.findViewById(R.id.doctorRow);
            patientRow = itemView.findViewById(R.id.patientRow);
            failureReasonRow = itemView.findViewById(R.id.rowFailureReason);
            refundRow = itemView.findViewById(R.id.refundRow);

            btnProcessPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onProcessPaymentClick(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    interface OnOptionSelected {

        void onReceiptClick(int pos);

        void onProcessPaymentClick(int pos);

        void onRefundClick(int pos);

        void onAddChargeClick(int position);

        void onUpdateChargeClick(int position);

        void onItemClick(int position);

    }
}
