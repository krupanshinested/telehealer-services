package com.thealer.telehealer.views.common.CustomDialogs;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.List;

/**
 * Created by Aswin on 15,March,2019
 */
class OptionSelectionAdapter extends RecyclerView.Adapter<OptionSelectionAdapter.ViewHolder> {
    private Context context;
    private List<String> optionList;
    private PickerListener pickerListener;
    private OptionSelectionDialog optionSelectionDialog;
    private int selectedPosition;

    public OptionSelectionAdapter(Context context, List<String> optionList, int selectedPosition, PickerListener pickerListener, OptionSelectionDialog optionSelectionDialog) {
        this.context = context;
        this.optionList = optionList;
        this.pickerListener = pickerListener;
        this.optionSelectionDialog = optionSelectionDialog;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_options_select, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.optionCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerListener.didSelectedItem(i);
                optionSelectionDialog.dismiss();
            }
        });

        viewHolder.optionTv.setText(optionList.get(i));
        if (optionList.get(i).equals(context.getString(R.string.Delete))) {
            viewHolder.optionTv.setTextColor(context.getColor(R.color.red));
        }

        if (selectedPosition == i){
            viewHolder.mSelectedIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout optionCl;
        private TextView optionTv;
        private View bottomView;
        private ImageView mSelectedIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            optionCl = (ConstraintLayout) itemView.findViewById(R.id.option_cl);
            optionTv = (TextView) itemView.findViewById(R.id.option_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
            mSelectedIv = (ImageView) itemView.findViewById(R.id.selected_iv);
        }
    }
}
