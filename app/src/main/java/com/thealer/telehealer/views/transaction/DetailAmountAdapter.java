package com.thealer.telehealer.views.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.DetailAmountModel;
import com.thealer.telehealer.common.Utils;

import java.util.List;

public class DetailAmountAdapter extends RecyclerView.Adapter<DetailAmountAdapter.DetailAmountVH> {

    private List<DetailAmountModel> list;
    private OnOptionSelected onOptionSelected;


    public DetailAmountAdapter(List<DetailAmountModel> list, OnOptionSelected onOptionSelected) {
        this.list = list;
        this.onOptionSelected = onOptionSelected;
    }


    @NonNull
    @Override
    public DetailAmountVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_detail_amount, parent, false);
        return new DetailAmountVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAmountVH holder, int position) {
        if (list.get(position).getTitle() == null)
            holder.title.setVisibility(View.GONE);
        else
            holder.title.setText(list.get(position).getTitle());
        if (list.get(position).getDetails() == null)
            holder.title.setVisibility(View.GONE);
        else
            holder.subTitle.setText(list.get(position).getDetails());
        if (list.get(position).isShowReceipt())
            holder.imgReceipt.setVisibility(View.VISIBLE);
        else
            holder.imgReceipt.setVisibility(View.GONE);
        holder.amount.setText(Utils.getFormattedCurrency(list.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DetailAmountVH extends RecyclerView.ViewHolder {

        TextView title;
        TextView subTitle;
        TextView amount;
        ImageView imgReceipt;


        public DetailAmountVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTv);
            subTitle = itemView.findViewById(R.id.subTitleTv);
            amount = itemView.findViewById(R.id.amount_tv);
            imgReceipt = itemView.findViewById(R.id.imgReceipt);
            imgReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onOptionSelected != null)
                        onOptionSelected.onReceiptClick(getAdapterPosition());
                }
            });
        }
    }

    interface OnOptionSelected {
        void onReceiptClick(int pos);
    }
}
