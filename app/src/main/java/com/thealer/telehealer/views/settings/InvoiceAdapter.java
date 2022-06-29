package com.thealer.telehealer.views.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.util.Constants;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.Payments.Transaction;
import com.thealer.telehealer.views.settings.Adapters.PaymentAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.Viewholder> {

    private Context context;
    private ArrayList<Transaction> transactions;

    public InvoiceAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dataView = LayoutInflater.from(context).inflate(R.layout.invoice_list, parent, false);
        return new Viewholder(dataView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        holder.invamount.setText("$" + transactions.get(position).getTotal_price());
        holder.invnumber.setText("" + transactions.get(position).getInvoice_number());
        holder.invstatus.setText("" + getStatus(transactions.get(position).getPayment_status()));

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date givenday = format.parse(""+transactions.get(position).getDate());
            DateFormat sdf = new SimpleDateFormat("MMMM yy");
            holder.invdate.setText(""+sdf.format(givenday));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.invdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(transactions.get(position).getInvoice_pdf_path()), "application/pdf");
                Intent chooser = Intent.createChooser(browserIntent, "");
                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooser);
            }
        });

    }

    private String getStatus(int status) {
        switch (status) {
            case 2:
                return "Failed";
            case 3:
                return "Success";
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void update(ArrayList<Transaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        this.notifyDataSetChanged();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView invnumber, invdate, invstatus, invamount;
        private Button invdownload;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            invnumber = (TextView) itemView.findViewById(R.id.tv_invnumber);
            invdate = (TextView) itemView.findViewById(R.id.tv_invdate);
            invstatus = (TextView) itemView.findViewById(R.id.tv_invstatus);
            invamount = (TextView) itemView.findViewById(R.id.tv_invamount);
            invdownload = (Button) itemView.findViewById(R.id.btn_invdownload);
        }
    }

}
