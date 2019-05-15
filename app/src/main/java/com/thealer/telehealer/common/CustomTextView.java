package com.thealer.telehealer.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 15,May,2019
 */
public class CustomTextView extends AppCompatTextView {

    private int strokeColor = 0, backgroundColor;
    private int strokeWidth = 0;
    private float topLeft = 0, topRight = 0, bottomRight = 0, bottomLeft = 0, cornerRadius = 0;
    private GradientDrawable gradientDrawable;

    public CustomTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        strokeColor = typedArray.getColor(R.styleable.CustomTextView_ctv_stroke_color, context.getColor(R.color.colorWhite));
        backgroundColor = typedArray.getColor(R.styleable.CustomTextView_ctv_background_color, context.getColor(R.color.colorWhite));
        strokeWidth = typedArray.getInteger(R.styleable.CustomTextView_ctv_stroke_width, 0);
        topLeft = typedArray.getFloat(R.styleable.CustomTextView_ctv_top_left_corner, 0f);
        topRight = typedArray.getFloat(R.styleable.CustomTextView_ctv_top_right_corner, 0f);
        bottomLeft = typedArray.getFloat(R.styleable.CustomTextView_ctv_bottom_left_corner, 0f);
        bottomRight = typedArray.getFloat(R.styleable.CustomTextView_ctv_bottom_right_corner, 0f);
        cornerRadius = typedArray.getDimension(R.styleable.CustomTextView_ctv_corner_radius, 0);
        typedArray.recycle();

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor);

        setBackground(gradientDrawable);

        setCornerRadius();

        setStroke();

        setBackground(gradientDrawable);
    }

    private void setStroke() {
        if (strokeWidth > 0) {
            gradientDrawable.setStroke(strokeWidth, strokeColor);
        }
    }

    private void setCornerRadius() {

        if (cornerRadius > 0) {
            gradientDrawable.setCornerRadius(cornerRadius);
        } else {
            gradientDrawable.setCornerRadii(new float[]{
                    topLeft, topLeft,
                    topRight, topRight,
                    bottomRight, bottomRight,
                    bottomLeft, bottomLeft
            });
        }
    }
}
