package com.thealer.telehealer.views.Procedure;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Aswin on 22,July,2019
 */
public class SelectedChipListAdapter extends RecyclerView.Adapter<SelectedChipListAdapter.ViewHolder> {

    private FragmentActivity activity;
    private OnListItemSelectInterface listItemSelectInterface;

    private List<String> list;

    public SelectedChipListAdapter(FragmentActivity activity, OnListItemSelectInterface listItemSelectInterface) {
        this.activity = activity;
        this.listItemSelectInterface = listItemSelectInterface;
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_selected_icd_code_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemBtn.setText(list.get(position));
        holder.itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                listItemSelectInterface.onListItemSelected(position, null);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<String> selectedProcedureItems) {
        list = selectedProcedureItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomButton itemBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBtn = (CustomButton) itemView.findViewById(R.id.item_btn);
            itemBtn.getLayoutParams().width = -2;
        }
    }
}
