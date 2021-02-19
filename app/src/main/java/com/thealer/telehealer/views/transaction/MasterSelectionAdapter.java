package com.thealer.telehealer.views.transaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;

import java.util.List;
import java.util.Locale;

public class MasterSelectionAdapter extends RecyclerView.Adapter<MasterSelectionAdapter.TransactionOptionVH> {

    private List<MasterResp.MasterItem> list;
    private OnOptionSelected onOptionSelected;

    public MasterSelectionAdapter(List<MasterResp.MasterItem> list, OnOptionSelected onOptionSelected) {
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
        holder.cbReason.setText(list.get(position).getName());
        holder.cbReason.setChecked(list.get(position).isSelected());
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

        public TransactionOptionVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            cbReason = itemView.findViewById(R.id.item_cb);
            itemView.findViewById(R.id.etFees).setVisibility(View.GONE);
            cbReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onSelected(getAdapterPosition());
                }
            });
        }
    }

    interface OnOptionSelected {
        void onSelected(int pos);
    }
}
