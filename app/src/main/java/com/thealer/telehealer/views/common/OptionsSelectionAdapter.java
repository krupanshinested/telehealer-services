package com.thealer.telehealer.views.common;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;

import java.util.List;

/**
 * Created by Aswin on 28,February,2019
 */
public class OptionsSelectionAdapter extends RecyclerView.Adapter<OptionsSelectionAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<String> optionList;
    private PickerListener pickerListener;
    private AlertDialog alertDialog;

    public OptionsSelectionAdapter(FragmentActivity activity, List<String> optionList, PickerListener pickerListener, AlertDialog alertDialog) {
        this.activity = activity;
        this.optionList = optionList;
        this.pickerListener = pickerListener;
        this.alertDialog = alertDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_option_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (i == 0) {
            viewHolder.topView.setVisibility(View.GONE);
        }
        viewHolder.itemTv.setText(optionList.get(i));
        viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerListener.didSelectedItem(i);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout itemCv;
        private View topView;
        private TextView itemTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCv = (ConstraintLayout) itemView.findViewById(R.id.item_cv);
            topView = (View) itemView.findViewById(R.id.top_view);
            itemTv = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }
}
