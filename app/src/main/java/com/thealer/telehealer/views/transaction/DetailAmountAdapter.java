package com.thealer.telehealer.views.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.DetailAmountModel;
import com.thealer.telehealer.common.Utils;

import java.util.List;

public class DetailAmountAdapter extends RecyclerView.Adapter<DetailAmountAdapter.DetailAmountVH> {

    private List<DetailAmountModel> list;


    public DetailAmountAdapter(List<DetailAmountModel> list) {
        this.list = list;
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
        holder.amount.setText(Utils.getFormattedCurrency(list.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DetailAmountVH extends RecyclerView.ViewHolder {

        TextView title;
        TextView subTitle;
        TextView amount;


        public DetailAmountVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTv);
            subTitle = itemView.findViewById(R.id.subTitleTv);
            amount = itemView.findViewById(R.id.amount_tv);
        }
    }

    interface OnOptionSelected {
        void onSelected(int pos);
    }
}
