package com.thealer.telehealer.views.common;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;

/**
 * Created by rsekar on 1/11/19.
 */

public class CCMItemView extends ConstraintLayout {

    private TextView title_tv;
    private ImageView selection_iv, info_iv;

    public CCMItemView(Context context) {
        super(context);

        initMyView();
    }

    public CCMItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initMyView();
    }

    private void initMyView() {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_ccm_item_view, this, true);

        title_tv = view.findViewById(R.id.title_tv);
        selection_iv = view.findViewById(R.id.selection_iv);
        info_iv = view.findViewById(R.id.info_iv);
    }

    public void update(String title, Boolean isSelected) {
        title_tv.setText(title);
        update(isSelected);
    }

    public void update(Boolean isSelected) {
        if (isSelected) {
            selection_iv.setVisibility(VISIBLE);
        } else {
            selection_iv.setVisibility(INVISIBLE);
        }
    }

    public void setListenerForInfo(OnClickListener onClickListener) {
        info_iv.setOnClickListener(onClickListener);
    }

    public Boolean getIsSelected() {
        return selection_iv.getVisibility() == VISIBLE;
    }

}

