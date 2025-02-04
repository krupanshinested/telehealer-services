package com.thealer.telehealer.views.transaction;

import android.text.Editable;
import android.text.TextWatcher;
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
    private String hint;

    public void setData(String hint, List<TextFieldModel> list) {
        this.list = list;
        this.hint = hint;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TextFieldVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_textfield_list, parent, false);
        return new TextFieldVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextFieldVH holder, int position) {
        holder.etField.setHint(hint);
        holder.etField.setText(list.get(position).getValue());
        if (list.size() > 1 && position != 0)
            holder.imgRemove.setVisibility(View.VISIBLE);
        else
            holder.imgRemove.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class TextFieldVH extends RecyclerView.ViewHolder {

        EditText etField;
        ImageView imgRemove;

        public TextFieldVH(@NonNull View itemView) {
            super(itemView);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            etField = itemView.findViewById(R.id.etField);
            etField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    list.get(getAdapterPosition()).setValue(s.toString());
                }
            });
        }
    }
}