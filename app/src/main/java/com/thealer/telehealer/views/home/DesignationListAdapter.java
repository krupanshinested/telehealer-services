package com.thealer.telehealer.views.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.SpecialtiesBean;
import com.thealer.telehealer.common.OnItemEndListener;

import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 08,April,2021
 **/
public class DesignationListAdapter extends RecyclerView.Adapter<DesignationListAdapter.OnDesignationViewHolder> {
    private FragmentActivity fragmentActivity;
    List<String> adapterList;
    int currentSelected = -1;
    private OnItemEndListener itemEndListener;
    String designation;

    public DesignationListAdapter(FragmentActivity fragmentActivity, List<String> adapterList, OnItemEndListener itemEndListener) {
        this.fragmentActivity = fragmentActivity;
        this.adapterList = adapterList;
        this.itemEndListener = itemEndListener;
    }

    @NonNull
    @Override
    public OnDesignationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View designationView = LayoutInflater.from(parent.getContext()).inflate(R.layout.designation_raw_item, parent, false);
        return new DesignationListAdapter.OnDesignationViewHolder(designationView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnDesignationViewHolder holder, int position) {
        if (position == adapterList.size() - 1) {
            itemEndListener.itemEnd(position);
        }
        String designation = adapterList.get(position);
        if (this.designation != null && !designation.isEmpty()) {
            if (currentSelected == -1) {
                if (this.designation.equals(designation)) {
                    currentSelected = position;
                } else {
                    currentSelected = -1;
                }
            }
        } else {
            if (currentSelected == -1){
                currentSelected = 0;
            }

        }
        if (position == currentSelected)
            holder.rbDesignation.setChecked(true);
        else
            holder.rbDesignation.setChecked(false);

        holder.title.setText(designation);
        holder.rbDesignation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != currentSelected)
                    currentSelected = position;
                notifyDataSetChanged();
            }
        });
    }

    public void setCurrentSelected(int currentSelected) {
        this.currentSelected = currentSelected;
    }

    public int getCurrentSelected() {
        return currentSelected;
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public String getSpecialistInfo() {
        return adapterList.get(currentSelected);
    }

    public void setdesignation(String designation) {

        this.designation = designation;

    }

    public class OnDesignationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RadioButton rbDesignation;

        public OnDesignationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            rbDesignation = itemView.findViewById(R.id.rb_designation);
        }
    }
}
