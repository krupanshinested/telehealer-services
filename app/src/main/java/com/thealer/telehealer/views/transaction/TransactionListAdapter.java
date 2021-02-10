package com.thealer.telehealer.views.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.TransactionItem;

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TransactionListVH extends RecyclerView.ViewHolder {

        Button btnReceipt;
        Button btnProcessPayment;
        Button btnRefundClick;

        public TransactionListVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            btnReceipt = itemView.findViewById(R.id.btnReciept);
            btnRefundClick = itemView.findViewById(R.id.btnRefund);
            btnProcessPayment = itemView.findViewById(R.id.btnProcessPayment);

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
