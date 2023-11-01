package com.thealer.telehealer.views.guestlogin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;

import java.util.ArrayList;
import java.util.List;

public class PaitentWaitingListRecyclerAdaper extends RecyclerView.Adapter<PaitentWaitingListRecyclerAdaper.MyViewHolder> {

    private Context context;
    private List<Patientinfo> itemList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Patientinfo data);

        void onAdmitClick(Patientinfo data);

        void onKikcoutClick(Patientinfo data);

        void onAskToAddCardClick(Patientinfo data);
    }

    public PaitentWaitingListRecyclerAdaper(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.itemList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paintent_waiting_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {

        Patientinfo patientinfo = itemList.get(position);

        viewHolder.tvName.setText(patientinfo.getDisplayName());
        viewHolder.tvPhn.setText(context.getString(R.string.phone) + ": " + patientinfo.getPhone());

        if (patientinfo.isAvailable()) {
            viewHolder.view_status.setBackground(ContextCompat.getDrawable(context, R.drawable.circular_green_indicator));
        } else {
            viewHolder.view_status.setBackground(ContextCompat.getDrawable(context, R.drawable.circular_orange_indicator));
        }


        viewHolder.btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(patientinfo);
            }
        });

        viewHolder.btn_Admit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdmitClick(patientinfo);
            }
        });

        viewHolder.im_kickout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onKikcoutClick(patientinfo);
            }
        });

        viewHolder.btn_ask_for_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAskToAddCardClick(patientinfo);
            }
        });

        if (patientinfo.isGuestUser()) {
            viewHolder.im_kickout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.im_kickout.setVisibility(View.VISIBLE);
            viewHolder.hasCardIV.setVisibility(View.VISIBLE);
            if (UserDetailPreferenceManager.getWhoAmIResponse().isCan_view_card_status()) {
                if (patientinfo.isHasValidCard()) {
                    viewHolder.btn_ask_for_card.setVisibility(View.GONE);
                    viewHolder.hasCardIV.setImageResource(R.drawable.ic_card_enabled);
                } else {
                    viewHolder.btn_ask_for_card.setVisibility(View.VISIBLE);
                    viewHolder.hasCardIV.setImageResource(R.drawable.ic_card_disabled);
                }
            } else {
                viewHolder.btn_ask_for_card.setVisibility(View.GONE);
                viewHolder.hasCardIV.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPhn;
        private TextView btn_message, btn_Admit, btn_ask_for_card;
        private ImageView im_kickout, hasCardIV;
        private View view_status;

        MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvPhn = view.findViewById(R.id.tv_phn);
            btn_Admit = view.findViewById(R.id.admit_btn);
            btn_message = view.findViewById(R.id.message_btn);
            im_kickout = view.findViewById(R.id.im_kickout);
            view_status = view.findViewById(R.id.view_status);
            btn_ask_for_card = view.findViewById(R.id.ask_for_card_btn);
            hasCardIV = view.findViewById(R.id.card_iv);
        }
    }

    public void updateItems(List<Patientinfo> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

}
