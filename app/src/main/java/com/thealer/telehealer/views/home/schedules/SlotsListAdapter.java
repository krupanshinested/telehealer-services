package com.thealer.telehealer.views.home.schedules;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;

import java.util.List;

/**
 * Created by Aswin on 20,December,2018
 */
class SlotsListAdapter extends RecyclerView.Adapter<SlotsListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private CreateScheduleViewModel createScheduleViewModel;
    private List<String> slotsList;
    private SlotSelectionDialogFragment slotSelectionDialogFragment;

    public SlotsListAdapter(FragmentActivity activity) {
        this.activity = activity;
        createScheduleViewModel = ViewModelProviders.of(activity).get(CreateScheduleViewModel.class);
        slotsList = createScheduleViewModel.getTimeSlots().getValue();
        createScheduleViewModel.getTimeSlots().observe(activity, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {
                notifyDataSetChanged();
            }
        });
        slotSelectionDialogFragment = new SlotSelectionDialogFragment();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_slots_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.titleTv.setText(activity.getString(R.string.slot) + " " + (i + 1));
        viewHolder.slotTv.setText(Utils.getSlotTimeDate(slotsList.get(i)));
        viewHolder.slotItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.slotItem.setEnabled(false);
                Bundle bundle = new Bundle();
                bundle.putInt(ArgumentKeys.SELECTED_TIME_SLOT, i);
                slotSelectionDialogFragment.setArguments(bundle);
                slotSelectionDialogFragment.show(activity.getSupportFragmentManager(), slotSelectionDialogFragment.getClass().getSimpleName());
                viewHolder.slotItem.setEnabled(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slotsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private TextView slotTv;
        private ConstraintLayout slotItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slotItem = (ConstraintLayout) itemView.findViewById(R.id.slot_item);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            slotTv = (TextView) itemView.findViewById(R.id.slot_tv);
        }
    }
}
