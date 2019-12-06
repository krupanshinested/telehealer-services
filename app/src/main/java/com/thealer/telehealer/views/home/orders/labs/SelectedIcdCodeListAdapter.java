package com.thealer.telehealer.views.home.orders.labs;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class SelectedIcdCodeListAdapter extends RecyclerView.Adapter<SelectedIcdCodeListAdapter.ViewHolder> {

    private FragmentActivity fragmentActivity;
    private IcdCodesDataViewModel icdCodesDataViewModel;
    private List<String> selectedIcdCodesList = new ArrayList<>();

    public SelectedIcdCodeListAdapter(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        icdCodesDataViewModel = new ViewModelProvider(fragmentActivity).get(IcdCodesDataViewModel.class);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        icdCodesDataViewModel.getSelectedIcdCodeList().observe(fragmentActivity,
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> list) {
                        if (list != null) {
                            selectedIcdCodesList = list;
                            notifyDataSetChanged();
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_selected_icd_code_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemBtn.setText(selectedIcdCodesList.get(i));
        viewHolder.itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIcdCodesList.remove(i);
                icdCodesDataViewModel.getSelectedIcdCodeList().setValue(selectedIcdCodesList);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedIcdCodesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomButton itemBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBtn = (CustomButton) itemView.findViewById(R.id.item_btn);
        }
    }
}
