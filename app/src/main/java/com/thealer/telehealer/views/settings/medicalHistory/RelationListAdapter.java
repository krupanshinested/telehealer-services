package com.thealer.telehealer.views.settings.medicalHistory;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 23,January,2019
 */
class RelationListAdapter extends RecyclerView.Adapter<RelationListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<String> relationList = new ArrayList<>();
    private MedicalHistoryViewModel medicalHistoryViewModel;

    public RelationListAdapter(FragmentActivity activity) {
        this.activity = activity;
        relationList.clear();
        relationList.addAll(MedicalHistoryConstants.relationList);
        medicalHistoryViewModel = new ViewModelProvider(activity).get(MedicalHistoryViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_relation_selection, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (i == relationList.size() - 1) {
            viewHolder.bottomView.setVisibility(View.GONE);
        }

        viewHolder.itemCb.setText(relationList.get(i));

        viewHolder.itemCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    medicalHistoryViewModel.getSelectedRelationsLiveData().getValue().add(relationList.get(i));
                } else {
                    medicalHistoryViewModel.getSelectedRelationsLiveData().getValue().remove(relationList.get(i));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return relationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemCb;
        private View bottomView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCb = (CheckBox) itemView.findViewById(R.id.item_cb);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }
}
