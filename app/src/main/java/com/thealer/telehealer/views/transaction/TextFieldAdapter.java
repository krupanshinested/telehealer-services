package com.thealer.telehealer.views.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldModel;

import java.util.List;
import java.util.Locale;

public class TextFieldAdapter extends RecyclerView.Adapter<TextFieldAdapter.TextFieldVH> {

    private List<TextFieldModel> list;
    private OnOptionSelected onOptionSelected;
    private String hint;

    public TextFieldAdapter(List<TextFieldModel> list, String hint, OnOptionSelected onOptionSelected) {
        this.list = list;
        this.hint = hint;
        this.onOptionSelected = onOptionSelected;
    }


    @NonNull
    @Override
    public TextFieldVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_textfield_list, parent, false);
        return new TextFieldVH(view, onOptionSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TextFieldVH holder, int position) {
        holder.etField.setHint(hint);
        holder.etField.setText(list.get(position).getValue());
        if (list.size() > 1 && position != 0)
            holder.imgRemove.setVisibility(View.VISIBLE);
        else
            holder.imgRemove.setVisibility(View.GONE);
        holder.etField.removeTextChangedListener(list.get(position).getTextWatcher());
        holder.etField.addTextChangedListener(list.get(position).getTextWatcher());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TextFieldVH extends RecyclerView.ViewHolder {

        EditText etField;
        ImageView imgRemove;

        public TextFieldVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onRemoveField(getAdapterPosition());
                }
            });
            etField = itemView.findViewById(R.id.etField);
        }
    }

    interface OnOptionSelected {
        void onRemoveField(int pos);
    }
}
