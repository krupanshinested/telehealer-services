package com.thealer.telehealer.views.settings.cellView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.thealer.telehealer.R;


/**
 * Created by rsekar on 11/15/18.
 */

public class ProfileCellView extends ConstraintLayout {

    private TextView titleTextView, valueTextView;
    private ImageView iconImageView, rightArrowImageView;
    private Spinner spinner;
    private View splitter;

    public ProfileCellView(Context context) {
        super(context);

        initMyView();
    }

    public ProfileCellView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initMyView();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProfileCellView);
            String title = a.getString(R.styleable.ProfileCellView_title);
            Drawable iconDrawable = a.getDrawable(R.styleable.ProfileCellView_icon);

            Boolean isSpinnerType = a.getBoolean(R.styleable.ProfileCellView_isSpinnerType, false);
            Boolean hideSplitter = a.getBoolean(R.styleable.ProfileCellView_hideSpiltter, false);

            update(title, "", iconDrawable, isSpinnerType, hideSplitter);
        }

    }

    private void initMyView() {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_profile_cell_view, this, true);

        titleTextView = view.findViewById(R.id.title);
        valueTextView = view.findViewById(R.id.value);
        iconImageView = view.findViewById(R.id.icon);
        rightArrowImageView = view.findViewById(R.id.right_arrow);
        spinner = view.findViewById(R.id.spinner);
        splitter = view.findViewById(R.id.splitter);

        updateUI(false);
    }

    private void update(String title, String value, Drawable drawable, Boolean isSpinnerType, Boolean hideSplitter) {
        titleTextView.setText(title);

        if (drawable == null) {
            iconImageView.setVisibility(GONE);
        } else {
            iconImageView.setVisibility(VISIBLE);
            iconImageView.setImageDrawable(drawable);
        }

        hideSplitter(hideSplitter);

        if (isSpinnerType) {
            rightArrowImageView.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
        } else {
            rightArrowImageView.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        }

        rightArrowImageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.app_gradient_start)));
        updateValue(value);
    }

    public void updateUI(Boolean isSpinnerType) {
        if (isSpinnerType) {
            titleTextView.setVisibility(GONE);
            spinner.setVisibility(VISIBLE);
            rightArrowImageView.setVisibility(GONE);
            valueTextView.setVisibility(GONE);
        } else {
            titleTextView.setVisibility(VISIBLE);
            spinner.setVisibility(INVISIBLE);
            rightArrowImageView.setVisibility(VISIBLE);
            updateValue(valueTextView.getText().toString());
        }
    }

    public void hideOrShowRightArrow(Boolean isVisible) {
        if (!isVisible) {
            rightArrowImageView.setVisibility(GONE);
        } else {
            rightArrowImageView.setVisibility(VISIBLE);
        }
    }

    public void hideSplitter(Boolean hideSplitter) {
        if (hideSplitter) {
            splitter.setVisibility(GONE);
        } else {
            splitter.setVisibility(VISIBLE);
        }
    }

    public void updateValue(String value) {
        if (!value.isEmpty()) {
            valueTextView.setVisibility(VISIBLE);
            valueTextView.setText(value);
        } else {
            valueTextView.setVisibility(GONE);
        }
    }

    public void updateAdapter(ArrayAdapter arrayAdapter, AdapterView.OnItemSelectedListener listener) {
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(listener);
    }

    public void openSpinner() {
        updateUI(true);
        spinner.performClick();
    }

}
