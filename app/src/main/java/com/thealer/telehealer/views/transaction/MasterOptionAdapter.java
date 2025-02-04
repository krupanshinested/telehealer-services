package com.thealer.telehealer.views.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.master.MasterResp;

import java.util.List;

public class MasterOptionAdapter extends RecyclerView.Adapter<MasterOptionAdapter.TransactionOptionVH> {

    private List<MasterResp.MasterItem> list;
    private OnOptionSelected onOptionSelected;

    public MasterOptionAdapter(List<MasterResp.MasterItem> list, OnOptionSelected onOptionSelected) {
        this.list = list;
        this.onOptionSelected = onOptionSelected;
    }


    @NonNull
    @Override
    public TransactionOptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction_options, parent, false);
        return new TransactionOptionVH(view, onOptionSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionOptionVH holder, int position) {
        holder.title.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TransactionOptionVH extends RecyclerView.ViewHolder {

        TextView title;

        public TransactionOptionVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(new View.OnClickListener() {
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
