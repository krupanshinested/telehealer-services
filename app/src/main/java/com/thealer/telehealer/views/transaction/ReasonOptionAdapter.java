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
                holder.etFees.setText(String.format(Locale.getDefault(), "%.2d", list.get(position).getFee()));
            else
                holder.etFees.setText(null);
        } else
            holder.etFees.setText(null);

        holder.chargeWatcher.setPosition(position);
/*
        holder.etFees.removeTextChangedListener(null);
        holder.etFees.addTextChangedListener(list.get(position).getTextWatcher());*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TransactionOptionVH extends RecyclerView.ViewHolder {

        CheckBox cbReason;
        EditText etFees;
        ChargeWatcher chargeWatcher = new ChargeWatcher();

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
            etFees.addTextChangedListener(chargeWatcher);
        }
    }

    interface OnOptionSelected {
        void onSelected(int pos);
    }

    class ChargeWatcher implements TextWatcher {

        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().isEmpty()) {
                try {
                    list.get(position).setFee(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                    list.get(position).setFee(0);
                }
            }
        }
    }
}
