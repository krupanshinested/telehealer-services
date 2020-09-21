package com.thealer.telehealer.views.home.orders.radiology;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 11,December,2018
 */
class RadiologySelectedListAdapter extends RecyclerView.Adapter<RadiologySelectedListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private RadiologyListViewModel radiologyListViewModel;
    private List<RadiologyListModel> selectedRadiologyModelList = new ArrayList<>();
    private List<String> selectedIdList = new ArrayList<>();

    public RadiologySelectedListAdapter(FragmentActivity activity) {
        this.activity = activity;
        radiologyListViewModel = ViewModelProviders.of(activity).get(RadiologyListViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_radiology_selected_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.itemBtn.setText(selectedRadiologyModelList.get(i).getDisplayText());

        viewHolder.itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIdList.remove(selectedRadiologyModelList.get(i).getId());
                selectedRadiologyModelList.remove(i);
                radiologyListViewModel.getSelectedRadiologyListLiveData().setValue(selectedRadiologyModelList);
                radiologyListViewModel.getSelectedIdList().setValue(selectedIdList);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedRadiologyModelList.size();
    }

    public void setData(List<RadiologyListModel> radiologyModelList, List<String> selectedIdList) {
        this.selectedRadiologyModelList = radiologyModelList;
        this.selectedIdList = selectedIdList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomButton itemBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBtn = (CustomButton) itemView.findViewById(R.id.item_btn);
        }
    }
}
