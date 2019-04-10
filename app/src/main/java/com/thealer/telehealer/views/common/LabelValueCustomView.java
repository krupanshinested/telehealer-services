package com.thealer.telehealer.views.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;

import androidx.annotation.Nullable;

/**
 * Created by Aswin on 25,March,2019
 */
public class LabelValueCustomView extends ConstraintLayout {

    private TextView labelTv;
    private TextView valueTv;
    private View bottomView;
    private int labelTvColor = 0, valueTvColor = 0;

    boolean isLabelVisible = true, isValueVisible = true, isBottomViewVisible = true;
    String labelText, valueText;
    Context context;

    public LabelValueCustomView(Context context) {
        super(context);
    }

    public LabelValueCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        context = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.view_label_value, this, true);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelValueCustomView);

            isLabelVisible = typedArray.getBoolean(R.styleable.LabelValueCustomView_lbcv_label_visible, true);
            isValueVisible = typedArray.getBoolean(R.styleable.LabelValueCustomView_lbcv_value_visible, true);
            isBottomViewVisible = typedArray.getBoolean(R.styleable.LabelValueCustomView_lbcv_bottom_view_visible, true);
            labelText = typedArray.getString(R.styleable.LabelValueCustomView_lbcv_label_text);
            valueText = typedArray.getString(R.styleable.LabelValueCustomView_lbcv_value_text);
            labelTvColor = typedArray.getColor(R.styleable.LabelValueCustomView_lbcv_label_text_color, 0);
            valueTvColor = typedArray.getColor(R.styleable.LabelValueCustomView_lbcv_value_text_color, 0);

            labelTv = (TextView) view.findViewById(R.id.label_tv);
            valueTv = (TextView) view.findViewById(R.id.value_tv);
            bottomView = (View) view.findViewById(R.id.bottom_view);

            setLabelVisible(isLabelVisible);
            setValueVisible(isValueVisible);
            setBottomViewVisible(isBottomViewVisible);
            setLabelText(labelText);
            setValueText(valueText);

            if (labelTvColor != 0) {
                labelTv.setTextColor(labelTvColor);
            }
            if (valueTvColor != 0) {
                valueTv.setTextColor(valueTvColor);
            }

            typedArray.recycle();
        }
    }

    public void setLabelVisible(boolean labelVisible) {
        if (labelVisible)
            labelTv.setVisibility(VISIBLE);
        else
            labelTv.setVisibility(GONE);
    }

    public void setValueVisible(boolean valueVisible) {
        if (valueVisible)
            valueTv.setVisibility(VISIBLE);
        else
            valueTv.setVisibility(GONE);
    }

    public void setBottomViewVisible(boolean bottomViewVisible) {
        if (bottomViewVisible)
            bottomView.setVisibility(VISIBLE);
        else
            bottomView.setVisibility(GONE);
    }

    public void setLabelText(String labelText) {
        labelTv.setText(labelText);
    }

    public void setValueText(String valueText) {
        valueTv.setText(valueText);
    }
}
