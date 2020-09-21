package com.thealer.telehealer.views.common.CustomDialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.ArrayList;

/**
 * Created by rsekar on 12/11/18.
 */

public class ItemPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> items;
    public int selectedPosition = 0;

    public ItemPickerAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_item_picker, viewGroup, false);
        return new ItemPickerAdapter.DataHolder(dataView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemPickerAdapter.DataHolder dataHolder = (ItemPickerAdapter.DataHolder)viewHolder;

        dataHolder.titleTv.setText(items.get(i));

        if (selectedPosition == i) {
            dataHolder.radioButton.setChecked(true);
        } else {
            dataHolder.radioButton.setChecked(false);
        }

        RecyclerView.Adapter adapter = this;
        dataHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = i;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView titleTv;
        private RadioButton radioButton;
        private ConstraintLayout mainContainer;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            radioButton =  itemView.findViewById(R.id.radio);
            mainContainer = (ConstraintLayout) itemView.findViewById(R.id.main_container);
        }
    }

}
