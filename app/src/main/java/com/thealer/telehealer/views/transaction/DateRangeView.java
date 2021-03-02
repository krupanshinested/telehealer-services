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


    private DateView fromDateView;
    private DateView toDateView;
    private TextView tvTitle;

    private OnDateSelectedListener onDateSelectedListener;

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
        fromDateView = findViewById(R.id.fromDateView);
        toDateView = findViewById(R.id.toDateView);
        tvTitle = findViewById(R.id.tv_title);
        fromDateView.setHint(getContext().getString(R.string.lbl_start_date));
        toDateView.setHint(getContext().getString(R.string.lbl_end_date));

        fromDateView.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (getSelectedFromDate() != null && getSelectedToDate() == null) {
                    Calendar afterMonth = Calendar.getInstance();
                    afterMonth.setTimeInMillis(fromDateView.getSelectedDate().getTimeInMillis());
                    afterMonth.add(Calendar.MONTH, 1);
                    setSelectedToDate(afterMonth);
                }
                onDateSelectedListener.onStartSelected(fromDateView.getSelectedDate());
                toDateView.setMinDate(fromDateView.getSelectedDate());
            }
        });

        toDateView.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                onDateSelectedListener.onEndSelected(toDateView.getSelectedDate());
            }
        });

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DateRangeView);
            String title = typedArray.getString(R.styleable.DateRangeView_date_title);
            setTitle(title);
        }
    }


    public Calendar getSelectedFromDate() {
        return fromDateView.getSelectedDate();
    }

    public void setSelectedFromDate(Calendar selectedFromDate) {
        fromDateView.setSelectedDate(selectedFromDate);
    }

    public Calendar getSelectedToDate() {
        return toDateView.getSelectedDate();
    }

    public void setSelectedToDate(Calendar selectedToDate) {
        toDateView.setSelectedDate(selectedToDate);
    }


    public void setTitle(String title) {
        if (title != null)
            tvTitle.setText(title);
        else
            tvTitle.setVisibility(GONE);
    }

    public void show(boolean isShow, boolean resetOld) {
        if (resetOld) {
            fromDateView.setSelectedDate(null);
            toDateView.setSelectedDate(null);
            if (isShow) {
                setSelectedFromDate(Calendar.getInstance());
                Calendar afterMonth = Calendar.getInstance();
                afterMonth.add(Calendar.MONTH, 1);
                setSelectedToDate(afterMonth);
            }
        }
        if (isShow) {
            setVisibility(VISIBLE);
        } else {

            setVisibility(GONE);
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    interface OnDateSelectedListener {
        void onStartSelected(Calendar calendar);

        void onEndSelected(Calendar calendar);
    }
}
