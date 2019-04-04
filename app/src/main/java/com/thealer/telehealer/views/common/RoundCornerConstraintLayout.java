package com.thealer.telehealer.views.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.thealer.telehealer.R;

/**
 * Created by rsekar on 12/25/18.
 */

public class RoundCornerConstraintLayout extends ConstraintLayout {


    private int backgroundColor, strokeColor;

    private float topLeft, topRight, bottomRight, bottomLeft, cornerRadius;

    private GradientDrawable roundCorner = new GradientDrawable();


    public RoundCornerConstraintLayout(Context context) {
        super(context);

        initView();
    }

    public RoundCornerConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerConstraintLayout);

        backgroundColor = a.getColor(R.styleable.RoundCornerConstraintLayout_lay_background_color, getResources().getColor(R.color.app_gradient_start));
        strokeColor = a.getColor(R.styleable.RoundCornerConstraintLayout_lay_stroke_color, backgroundColor);
        topLeft = a.getDimensionPixelSize(R.styleable.RoundCornerConstraintLayout_lay_top_left_corner, 0);
        topRight = a.getDimensionPixelSize(R.styleable.RoundCornerConstraintLayout_lay_top_right_corner, 0);
        bottomRight = a.getDimensionPixelSize(R.styleable.RoundCornerConstraintLayout_lay_bottom_right_corner, 0);
        bottomLeft = a.getDimensionPixelSize(R.styleable.RoundCornerConstraintLayout_lay_bottom_left_corner, 0);
        cornerRadius = a.getDimensionPixelSize(R.styleable.RoundCornerConstraintLayout_lay_corner_radius, -1);

        a.recycle();

        initView();
    }

    private void initView() {

        //roundCorner.setColor(Color.parseColor("#de545f"));
        setCorners();
        //roundCorner.setColor(getResources().getColor(R.color.white));
        this.setBackground(roundCorner);

        setStrokeColor();

        if (backgroundColor != 0) {
            setBackgroundColor();
        }
    }

    private void setCorners() {
        if (cornerRadius > 0) {
            roundCorner.setCornerRadii(new float[]{
                    cornerRadius, cornerRadius,
                    cornerRadius, cornerRadius,
                    cornerRadius, cornerRadius,
                    cornerRadius, cornerRadius
            });
        } else {
            roundCorner.setCornerRadii(new float[]{
                    topLeft, topLeft,
                    topRight, topRight,
                    bottomRight, bottomRight,
                    bottomLeft, bottomLeft
            });
        }
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public void setCorners(float cornerRadii) {
        this.topLeft = cornerRadii;
        this.topRight = cornerRadii;
        this.bottomLeft = cornerRadii;
        this.bottomRight = cornerRadii;
        setCorners();
    }

    public void assignBackgroundColor(int color) {
        this.backgroundColor = getResources().getColor(color);
        setBackgroundColor();
    }

    public void assignStrokeColor(int color) {
        this.strokeColor = getResources().getColor(color);
        setStrokeColor();
    }

    public void setStrokeColor() {
        roundCorner.setStroke(2, strokeColor);
    }

    public void setBackgroundColor() {
        GradientDrawable drawable = (GradientDrawable) this.getBackground();

        drawable.setColor(backgroundColor);
    }

    public void setTopCornerRadius(int dimenId) {
        cornerRadius = -1;

        topLeft = getResources().getDimensionPixelOffset(dimenId);
        topRight = getResources().getDimensionPixelOffset(dimenId);

        setCorners();
    }

    public void setBottomCornerRadius(int dimenId) {
        cornerRadius = -1;

        bottomRight = getResources().getDimensionPixelOffset(dimenId);
        bottomLeft = getResources().getDimensionPixelOffset(dimenId);

        setCorners();
    }

    public void setLeftCornerRadius(int dimenId) {
        cornerRadius = -1;
        topLeft = getResources().getDimensionPixelOffset(dimenId);
        bottomLeft = getResources().getDimensionPixelOffset(dimenId);

        setCorners();
    }

    public void setRightCornerRadius(int dimenId) {
        cornerRadius = -1;
        bottomRight = getResources().getDimensionPixelOffset(dimenId);
        topRight = getResources().getDimensionPixelOffset(dimenId);

        setCorners();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setStrokeColor(int color) {
        roundCorner.setStroke(2, color);
    }
}

