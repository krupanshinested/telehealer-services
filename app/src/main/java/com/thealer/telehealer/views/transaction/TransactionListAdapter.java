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
import com.thealer.telehealer.apilayer.models.transaction.TransactionItem;
import com.thealer.telehealer.common.Constants;

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
        switch (list.get(position).getStatus()) {
            case Constants.ChargeStatus.CHARGE_ADDED: {
                holder.btnProcessPayment.setVisibility(View.VISIBLE);
                holder.btnRefundClick.setVisibility(View.GONE);
                holder.btnReceipt.setVisibility(View.VISIBLE);
                holder.btnReceipt.setText(R.string.update);
                ((LinearLayout.LayoutParams) holder.btnReceipt.getLayoutParams()).weight = 1;
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
                ((LinearLayout.LayoutParams) holder.btnReceipt.getLayoutParams()).weight = 0.5f;
                holder.btnRefundClick.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TransactionListVH extends RecyclerView.ViewHolder {

        Button btnReceipt;
        Button btnProcessPayment;
        Button btnRefundClick;
        TextView tvStatus;

        public TransactionListVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            btnReceipt = itemView.findViewById(R.id.btnReciept);
            btnRefundClick = itemView.findViewById(R.id.btnRefund);
            btnProcessPayment = itemView.findViewById(R.id.btnProcessPayment);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            btnRefundClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onRefundClick(getAdapterPosition());
                }
            });

            btnReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onReceiptClick(getAdapterPosition());
                }
            });
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

    }
}
