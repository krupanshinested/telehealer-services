package com.thealer.telehealer.views.transaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;

import java.util.List;
import java.util.Locale;

public class ReasonOptionAdapter extends RecyclerView.Adapter<ReasonOptionAdapter.TransactionOptionVH> {

    private List<ReasonOption> list;
    private OnOptionSelected onOptionSelected;

    public ReasonOptionAdapter(List<ReasonOption> list, OnOptionSelected onOptionSelected) {
        this.list = list;
        this.onOptionSelected = onOptionSelected;
    }


    @NonNull
    @Override
    public TransactionOptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fee_reason, parent, false);
        return new TransactionOptionVH(view, onOptionSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionOptionVH holder, int position) {
        holder.cbReason.setText(list.get(position).getTitle());
        holder.cbReason.setChecked(list.get(position).isSelected());
        holder.etFees.setEnabled(list.get(position).isSelected());
        if (list.get(position).isSelected()) {
            if (list.get(position).getFee() != 0)
                holder.etFees.setText(String.format(Locale.getDefault(), "%.2f", list.get(position).getFee()));
            else
                holder.etFees.setText(null);
        } else
            holder.etFees.setText(null);
        holder.etFees.removeTextChangedListener(list.get(position).getTextWatcher());
        holder.etFees.addTextChangedListener(list.get(position).getTextWatcher());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TransactionOptionVH extends RecyclerView.ViewHolder {

        CheckBox cbReason;
        EditText etFees;

        public TransactionOptionVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            cbReason = itemView.findViewById(R.id.item_cb);
            cbReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onSelected(getAdapterPosition());
                }
            });
            etFees = itemView.findViewById(R.id.etFees);
        }
    }

    interface OnOptionSelected {
        void onSelected(int pos);
    }
}
