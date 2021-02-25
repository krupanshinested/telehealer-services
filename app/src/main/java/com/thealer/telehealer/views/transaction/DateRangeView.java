package com.thealer.telehealer.views.transaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.util.Calendar;

public class DateRangeView extends LinearLayout {


    private LinearLayout layoutFromDate;
    private LinearLayout layoutToDate;
    private TextView tvFromDate;
    private TextView tvToDate;
    private TextView tvTitle;

    private Calendar selectedFromDate = null;
    private Calendar selectedToDate = null;

    private boolean isSingleSelection;

    private OnClickListener onLayoutClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layoutFromDate:
                    showDatePickerDialog(selectedFromDate, isSingleSelection ? null : Calendar.getInstance(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            selectedFromDate = calendar;
                            updateDate(tvFromDate, year, month, dayOfMonth);
                        }
                    });
                    break;
                case R.id.layoutTomDate:
                    showDatePickerDialog(selectedToDate, selectedFromDate, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            selectedToDate = calendar;
                            updateDate(tvToDate, year, month, dayOfMonth);
                        }
                    });
                    break;
            }
        }
    };


    public DateRangeView(Context context) {
        super(context);
        init(null);
    }

    public DateRangeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DateRangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DateRangeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_add_charge_date, this);
        layoutFromDate = findViewById(R.id.layoutFromDate);
        layoutToDate = findViewById(R.id.layoutTomDate);
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        tvTitle = findViewById(R.id.tv_title);

        layoutFromDate.setOnClickListener(onLayoutClick);
        layoutToDate.setOnClickListener(onLayoutClick);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DateRangeView);
            String title = typedArray.getString(R.styleable.DateRangeView_date_title);
            setTitle(title);
        }
    }


    private void updateDate(TextView dateTextView, int year, int month, int dayOfMonth) {
        dateTextView.setText(Utils.getFormatedDate(year, month, dayOfMonth));
    }

    public Calendar getSelectedFromDate() {
        return selectedFromDate;
    }

    public void setSelectedFromDate(Calendar selectedFromDate) {
        this.selectedFromDate = selectedFromDate;
        updateDate(tvFromDate, selectedFromDate.get(Calendar.YEAR), selectedFromDate.get(Calendar.MONTH), selectedFromDate.get(Calendar.DAY_OF_MONTH));

    }

    public Calendar getSelectedToDate() {
        return selectedToDate;
    }

    public void setSelectedToDate(Calendar selectedToDate) {
        this.selectedToDate = selectedToDate;
        updateDate(tvToDate, selectedToDate.get(Calendar.YEAR), selectedToDate.get(Calendar.MONTH), selectedToDate.get(Calendar.DAY_OF_MONTH));
    }


    public void setTitle(String title) {
        if (title != null)
            tvTitle.setText(title);
        else
            tvTitle.setVisibility(GONE);
    }

    public void show(boolean isShow, boolean resetOld) {
        if (resetOld) {
            selectedFromDate = null;
            selectedToDate = null;
            tvFromDate.setText(null);
            tvToDate.setText(null);
        }
        if (isShow) {
            setVisibility(VISIBLE);
        } else {

            setVisibility(GONE);
        }
    }

    public void setSingleSelection(String hintText) {
        isSingleSelection = true;
        tvFromDate.setHint(hintText);
        layoutToDate.setVisibility(View.GONE);
        tvFromDate.setTextSize(14);
        selectedToDate = null;
        TextView txt = findViewById(R.id.lbStartDate);
        txt.setText(hintText);
        findViewById(R.id.lbEndDate).setVisibility(GONE);
    }


    private void showDatePickerDialog(Calendar selectedDate, Calendar minDate, DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = selectedDate != null ? selectedDate : Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener onDateSetListener = dateSetListener;

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, year, month, day);

        if (minDate != null) {
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }


        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        datePickerDialog.show();
    }
}
