package com.thealer.telehealer.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.List;

/**
 * Created by Aswin on 30,November,2018
 */
public class CustomSpinnerView extends ConstraintLayout {
    private TextView labelTv;
    private Spinner spinner;
    private View bottomView;

    private String labelText;
    private boolean isLablelVisible;

    public CustomSpinnerView(Context context) {
        super(context);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSpinnerView);
        labelText = typedArray.getString(R.styleable.CustomSpinnerView_spinner_label_text);
        isLablelVisible = typedArray.getBoolean(R.styleable.CustomSpinnerView_spinner_label_visible, true);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_custom_spinner_view, this, true);

        initView(view);

        typedArray.recycle();
    }

    private void initView(View view) {
        labelTv = (TextView) view.findViewById(R.id.label_tv);
        spinner = (Spinner) view.findViewById(R.id.custom_spinner);
        bottomView = (View) view.findViewById(R.id.bottom_view);

        setLabelText(labelText);

        labelTv.setVisibility(isLablelVisible ? VISIBLE : GONE);
    }

    public void setLabelText(String text) {
        labelTv.setText(text);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinnerAdapter(SpinnerAdapter spinnerAdapter) {
        spinner.setAdapter(spinnerAdapter);
    }

    public void setSpinnerData(Context context, List<String> spinnerList, AdapterView.OnItemSelectedListener itemSelectedListener) {
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.custom_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

}
