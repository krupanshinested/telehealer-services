package com.thealer.telehealer.views.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;

import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionListVH> {

    private List<TransactionItem> list;
    private OnOptionSelected onOptionSelected;

    public TransactionListAdapter(List<TransactionItem> list, OnOptionSelected onOptionSelected) {
        this.list = list;
        this.onOptionSelected = onOptionSelected;
    }


    @NonNull
    @Override
    public TransactionListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction_list, parent, false);
        return new TransactionListVH(view, onOptionSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListVH holder, int position) {
        holder.tvStatus.setText(list.get(position).getStatusString());
        holder.tvCharge.setText(String.format("($) %d", list.get(position).getAmount()));
        holder.tvReason.setText(list.get(position).getCommaSeparatedReason(holder.itemView.getContext()));
        holder.tvChargeType.setText(list.get(position).getTypeOfCharge().getName());
        holder.tvDate.setText(Utils.getFormatedDateTime(list.get(position).getCreatedAt(), Utils.dd_mmm_yyyy_hh_mm_a));
        if (UserType.isUserPatient()) {
            holder.patientRow.setVisibility(View.GONE);

            holder.doctorRow.setVisibility(View.VISIBLE);
            holder.tvDoctor.setText(list.get(position).getDoctorId().getDisplayName());
            if (list.get(position).getChargeStatus() == Constants.ChargeStatus.CHARGE_PROCESSED) {
                holder.btnReceipt.setVisibility(View.VISIBLE);
                holder.actionRow.setVisibility(View.VISIBLE);
            } else {
                holder.actionRow.setVisibility(View.GONE);
            }
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
                holder.btnRefundClick.setVisibility(View.VISIBLE);
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
        TextView tvStatus, tvDate, tvDateOfService, tvPatient, tvChargeType, tvReason, tvCharge, tvDoctor;
        View doctorRow, patientRow;
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
            tvChargeType = itemView.findViewById(R.id.tvChargeType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvCharge = itemView.findViewById(R.id.tvCharge);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);

            actionRow = itemView.findViewById(R.id.actionRow);
            doctorRow = itemView.findViewById(R.id.doctorRow);
            patientRow = itemView.findViewById(R.id.patientRow);

            btnProcessPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onProcessPaymentClick(getAdapterPosition());
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

    }
}
