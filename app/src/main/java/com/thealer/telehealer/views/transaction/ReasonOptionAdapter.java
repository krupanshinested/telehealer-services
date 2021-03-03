package com.thealer.telehealer.views.transaction;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.DateRangeReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.SingleDateReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldModel;
import com.thealer.telehealer.apilayer.models.transaction.TextFieldReasonOption;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReasonOptionAdapter extends RecyclerView.Adapter<ReasonOptionAdapter.TransactionOptionVH> {

    private List<ReasonOption> list;
    private OnOptionSelected onOptionSelected;
    private boolean showFees;
    private List<MasterResp.MasterItem> typeMasters;

    public ReasonOptionAdapter(List<ReasonOption> list, boolean showFees, OnOptionSelected onOptionSelected) {
        this.list = list;
        this.onOptionSelected = onOptionSelected;
        this.showFees = showFees;
    }


    @NonNull
    @Override
    public TransactionOptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(showFees ? R.layout.adapter_fee_reason : R.layout.adapter_filter_checkbox, parent, false);
        return new TransactionOptionVH(view, onOptionSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionOptionVH holder, int position) {
        holder.cbReason.setText(list.get(position).getTitle());
        holder.cbReason.setChecked(list.get(position).isSelected());
        if (showFees) {
            holder.etFees.setEnabled(list.get(position).isSelected());
            holder.llChargeType.setVisibility(View.GONE);
            holder.tvChargeType.setText(null);

            if (list.get(position).isSelected()) {
                if (list.get(position).getFee() != 0)
                    holder.etFees.setText(String.format(Locale.getDefault(), "%.2f", list.get(position).getFee()));
                else
                    holder.etFees.setText(null);
                holder.llChargeType.setVisibility(View.VISIBLE);
                holder.tvChargeType.setText(list.get(position).getChargeTypeName());
            } else {
                list.get(position).setFee(0);
                holder.etFees.setText(null);
            }

            holder.rvTextFields.setVisibility(View.GONE);
            holder.imgAddField.setVisibility(View.GONE);
            holder.dateRangeView.setVisibility(View.GONE);
            holder.singleDate.setVisibility(View.GONE);
            if (list.get(position) instanceof TextFieldReasonOption) {
                setTextFieldsUI(holder, (TextFieldReasonOption) list.get(position));
            }
            if (list.get(position) instanceof DateRangeReasonOption) {
                setDateRangeUI(holder, (DateRangeReasonOption) list.get(position));
            }
            if (list.get(position) instanceof SingleDateReasonOption) {
                setSingleDateUI(holder, (SingleDateReasonOption) list.get(position));
            }
        }
    }

    private void setSingleDateUI(TransactionOptionVH holder, SingleDateReasonOption reasonOption) {
        if (reasonOption.isSelected())
            holder.singleDate.setVisibility(View.VISIBLE);
        else {
            holder.singleDate.setVisibility(View.GONE);
            holder.singleDate.setSelectedDate(null);
        }

    }

    private void setDateRangeUI(TransactionOptionVH holder, DateRangeReasonOption reasonOption) {
        if (reasonOption.isSelected()) {
            holder.dateRangeView.setSelectedFromDate(reasonOption.getStartDate());
            holder.dateRangeView.setSelectedToDate(reasonOption.getEndDate());
            holder.dateRangeView.setVisibility(View.VISIBLE);
        } else {
            holder.dateRangeView.setSelectedFromDate(null);
            holder.dateRangeView.setSelectedToDate(null);
            holder.dateRangeView.setVisibility(View.GONE);
        }
    }

    private void setTextFieldsUI(@NonNull TransactionOptionVH holder, TextFieldReasonOption reasonOption) {
        if (reasonOption.isSelected()) {

            if (reasonOption.getTextFieldValues().size() == 0) {
                reasonOption.getTextFieldValues().add(new TextFieldModel());
            }
            holder.rvTextFields.setVisibility(View.VISIBLE);
            holder.imgAddField.setVisibility(View.VISIBLE);
        } else {
            holder.rvTextFields.setVisibility(View.GONE);
            holder.imgAddField.setVisibility(View.GONE);
            reasonOption.getTextFieldValues().clear();
        }
        holder.textFieldAdapter.setData(reasonOption.getTitle(), reasonOption.getTextFieldValues());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setTypeMasters(List<MasterResp.MasterItem> typeMasters) {
        this.typeMasters = typeMasters;
    }

    public class TransactionOptionVH extends RecyclerView.ViewHolder {

        CheckBox cbReason;
        EditText etFees;
        RecyclerView rvTextFields;
        TextView tvChargeType;

        ImageView imgAddField;

        TextFieldAdapter textFieldAdapter;

        DateRangeView dateRangeView;
        DateView singleDate;
        RecyclerView rvChargeType;

        LinearLayout llChargeType;

        public TransactionOptionVH(@NonNull View itemView, OnOptionSelected onOptionSelected) {
            super(itemView);
            cbReason = itemView.findViewById(R.id.item_cb);
            cbReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected.onSelected(getAdapterPosition());
                }
            });
            if (showFees) {
                etFees = itemView.findViewById(R.id.etFees);
                etFees.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            list.get(getAdapterPosition()).setFee(Double.parseDouble(s.toString().trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            list.get(getAdapterPosition()).setFee(0);
                        }
                    }
                });
                rvTextFields = itemView.findViewById(R.id.rvTextFields);
                textFieldAdapter = new TextFieldAdapter();
                rvTextFields.setAdapter(textFieldAdapter);

                imgAddField = itemView.findViewById(R.id.imgAddField);
                imgAddField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(getAdapterPosition()) instanceof TextFieldReasonOption) {
                            ((TextFieldReasonOption) list.get(getAdapterPosition())).getTextFieldValues().add(new TextFieldModel());
                            notifyDataSetChanged();
                        }
                    }
                });

                dateRangeView = itemView.findViewById(R.id.dateRange);
                dateRangeView.setOnDateSelectedListener(new DateRangeView.OnDateSelectedListener() {
                    @Override
                    public void onStartSelected(Calendar calendar) {
                        if (list.get(getAdapterPosition()) instanceof DateRangeReasonOption) {
                            ((DateRangeReasonOption) list.get(getAdapterPosition())).setStartDate(calendar);
                        }
                    }

                    @Override
                    public void onEndSelected(Calendar calendar) {
                        if (list.get(getAdapterPosition()) instanceof DateRangeReasonOption) {
                            ((DateRangeReasonOption) list.get(getAdapterPosition())).setEndDate(calendar);
                        }
                    }
                });

                singleDate = itemView.findViewById(R.id.singleDate);
                singleDate.setHint(itemView.getContext().getString(R.string.lbl_service_date));
                singleDate.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ((SingleDateReasonOption) list.get(getAdapterPosition())).setDate(singleDate.getSelectedDate());
                    }
                });

                llChargeType = itemView.findViewById(R.id.layoutChargeType);
                rvChargeType = itemView.findViewById(R.id.rvChargeType);
                tvChargeType = itemView.findViewById(R.id.tvChargeType);
                MasterOptionAdapter adapterType = new MasterOptionAdapter(typeMasters, pos -> {
                    rvChargeType.setVisibility(View.GONE);
                    tvChargeType.setText(typeMasters.get(pos).getName());
                    list.get(pos).setChargeTypeCode(typeMasters.get(pos).getCode());
                    list.get(pos).setChargeTypeName(typeMasters.get(pos).getName());
                });
                rvChargeType.setAdapter(adapterType);
                llChargeType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rvChargeType.getVisibility() == View.VISIBLE)
                            rvChargeType.setVisibility(View.GONE);
                        else
                            rvChargeType.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    interface OnOptionSelected {
        void onSelected(int pos);
    }
}
