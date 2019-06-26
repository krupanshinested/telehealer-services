package com.thealer.telehealer.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 13,October,2018
 */
public class CustomButton extends AppCompatButton {

    private int backgroundColor = 0;
    private Drawable backgroundDrawable = null;
    private int strokeColor = 0;
    private int strokeWidth = 0;
    private int textColor;
    private float topLeft = 0, topRight = 0, bottomRight = 0, bottomLeft = 0, cornerRadius = 0;
    private GradientDrawable gradientDrawable = new GradientDrawable();
    private boolean isAppGradient;

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);

        backgroundColor = typedArray.getColor(R.styleable.CustomButton_background_color, 0);
        strokeColor = typedArray.getColor(R.styleable.CustomButton_stroke_color, 0);
        strokeWidth = typedArray.getInteger(R.styleable.CustomButton_stroke_width, 0);
        textColor = typedArray.getColor(R.styleable.CustomButton_text_color, context.getColor(R.color.colorBlack));
        topLeft = typedArray.getFloat(R.styleable.CustomButton_top_left_corner, 1f);
        topRight = typedArray.getFloat(R.styleable.CustomButton_top_right_corner, 1f);
        bottomLeft = typedArray.getFloat(R.styleable.CustomButton_bottom_left_corner, 1f);
        bottomRight = typedArray.getFloat(R.styleable.CustomButton_bottom_right_corner, 1f);
        cornerRadius = typedArray.getDimension(R.styleable.CustomButton_corner_radius, 0);
        backgroundDrawable = typedArray.getDrawable(R.styleable.CustomButton_background_drawable);
        isAppGradient = typedArray.getBoolean(R.styleable.CustomButton_app_gradient, false);

        this.setBackground(gradientDrawable);
        this.setTextColor(textColor);

        if (backgroundColor != 0)
            setCustomBackground();
        else {
            if (backgroundDrawable != null) {
                this.setBackground(backgroundDrawable);
                gradientDrawable = (GradientDrawable) this.getBackground();
            }
        }

        if (isAppGradient) {
            setAppGradient();
        }

        setCustomBorder();

        setCornerRadius();

        typedArray.recycle();

        setStateListAnimator(null);

    }

    private void setAppGradient() {
        @SuppressLint("ResourceType")
        int[] colors = {Color.parseColor(getContext().getString(R.color.app_gradient_start)), Color.parseColor(getContext().getString(R.color.app_gradient_end))};
        gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        setBackground(gradientDrawable);
    }

    private void setCustomBorder() {
        gradientDrawable.setStroke(strokeWidth, strokeColor);
    }

    public void setStrokeColor(int color) {
        gradientDrawable.setStroke(strokeWidth, color);
    }

    private void setCustomBackground() {
        GradientDrawable drawable = (GradientDrawable) this.getBackground();
        drawable.setColor(backgroundColor);
    }

    private void setCornerRadius() {
        if (cornerRadius != 0) {
            gradientDrawable.setCornerRadius(cornerRadius);
        } else {
            gradientDrawable.setCornerRadii(new float[]{topLeft, topLeft,
                    topRight, topRight,
                    bottomRight, bottomRight,
                    bottomLeft, bottomLeft});
        }
    }

    public void assignBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        setCustomBackground();
    }
}
