package com.thealer.telehealer.views.transaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;

import java.util.Calendar;

/**
 * Created by Nimesh Patel
 * Created Date: 26,May,2021
 **/
public class SingleDateView extends LinearLayout {

    private Calendar selectedDate;
    private Calendar minDate;

    private TextView tvDate;

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public SingleDateView(Context context) {
        super(context);
        init(null);
    }

    public SingleDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SingleDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SingleDateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_single_date_view, this);

        tvDate = findViewById(R.id.tvFromDate);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(selectedDate, getMinDate(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        setSelectedDate(calendar);
                        if (onDateSetListener != null)
                            onDateSetListener.onDateSet(view, year, month, dayOfMonth);
                    }
                });
            }
        });
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


    public Calendar getMinDate() {
        return minDate;
    }

    public void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
        if (selectedDate == null)
            tvDate.setText(null);
        else
            updateDate(tvDate, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
    }

    private void updateDate(TextView dateTextView, int year, int month, int dayOfMonth) {
        dateTextView.setText(Utils.getFormatedDate(year, month, dayOfMonth));
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public void setHint(String hint) {
        tvDate.setHint(hint);
    }
}

