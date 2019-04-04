package com.thealer.telehealer.views.settings.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.Payments.Transaction;
import com.thealer.telehealer.common.ClickListener;

import java.util.ArrayList;


/**
 * Created by rsekar on 1/22/19.
 */

public class PaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Transaction> transactions;
    public ClickListener clickListener;

    public PaymentAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    public void update(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_payment_list, viewGroup, false);
        return new PaymentAdapter.DataHolder(dataView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PaymentAdapter.DataHolder dataHolder = (PaymentAdapter.DataHolder) viewHolder;

        Transaction transaction = transactions.get(i);
        dataHolder.date_tv.setText(transaction.getCreatedMonthYear());
        dataHolder.bill_tv.setText(context.getString(R.string.bill_no) + " " + transaction.getId());
        dataHolder.cash_tv.setText("$" + transaction.getAmount());

        dataHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView date_tv, bill_tv, cash_tv;
        private ConstraintLayout mainContainer;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            date_tv = (TextView) itemView.findViewById(R.id.date_tv);
            bill_tv = (TextView) itemView.findViewById(R.id.bill_tv);
            cash_tv = (TextView) itemView.findViewById(R.id.cash_tv);
            mainContainer = (ConstraintLayout) itemView.findViewById(R.id.main_container);
        }
    }

}
