package com.thealer.telehealer.views.home.orders;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersCustomView extends ConstraintLayout {

    private TextView labelTv;
    private TextView titleTv;
    private TextView subtitleTv;
    private View bottomView;
    private String label, title, subtitle;
    private boolean sub_title_visible;
    private boolean title_visible;
    private boolean label_visible;
    private boolean view_visible;
    private boolean arrow_visible;
    private ImageView arrowIv;

    public OrdersCustomView(Context context) {
        super(context);
    }

    public OrdersCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.order_custom_view, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OrdersCustomView);

        label = typedArray.getString(R.styleable.OrdersCustomView_view_label);
        title = typedArray.getString(R.styleable.OrdersCustomView_title_text);
        subtitle = typedArray.getString(R.styleable.OrdersCustomView_sub_title_text);
        sub_title_visible = typedArray.getBoolean(R.styleable.OrdersCustomView_sub_title_visible, true);
        title_visible = typedArray.getBoolean(R.styleable.OrdersCustomView_title_visible, true);
        label_visible = typedArray.getBoolean(R.styleable.OrdersCustomView_title_visible, true);
        view_visible = typedArray.getBoolean(R.styleable.OrdersCustomView_view_visible, true);
        arrow_visible = typedArray.getBoolean(R.styleable.OrdersCustomView_arrow_visible, false);

        typedArray.recycle();

        initView(view);
    }

    private void initView(View view) {

        labelTv = (TextView) view.findViewById(R.id.label_tv);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        subtitleTv = (TextView) view.findViewById(R.id.subtitle_tv);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        arrowIv = (ImageView) view.findViewById(R.id.arrow_iv);

        bottomView.setVisibility(view_visible ? VISIBLE : GONE);
        setSub_title_visible(sub_title_visible);
        setTitle_visible(title_visible);
        setArrow_visible(arrow_visible);

        setLabelTv(label);
        setTitleTv(title);
        setSubtitleTv(subtitle);
    }

    public void setLabelTv(String label) {
        labelTv.setText(label);
    }

    public void setTitleTv(String title) {
        titleTv.setText(title);
    }

    public void setSubtitleTv(String subtitle) {
        subtitleTv.setText(subtitle);
    }

    public void setSub_title_visible(boolean sub_title_visible) {
        subtitleTv.setVisibility(sub_title_visible ? VISIBLE : GONE);
    }

    public void setTitle_visible(boolean title_visible) {
        titleTv.setVisibility(title_visible ? VISIBLE : GONE);
    }

    public void setLabel_visible(boolean label_visible) {
        labelTv.setVisibility(label_visible ? VISIBLE : GONE);
    }

    public void setArrow_visible(boolean arrow_visible) {
        arrowIv.setVisibility(arrow_visible ? VISIBLE : GONE);
    }

    public String getTitleText() {
        return titleTv.getText().toString();
    }

    public String getSubTitleText() {
        return subtitleTv.getText().toString();
    }
}
