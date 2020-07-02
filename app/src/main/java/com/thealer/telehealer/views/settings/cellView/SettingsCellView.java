package com.thealer.telehealer.views.settings.cellView;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.thealer.telehealer.R;

/**
 * Created by rsekar on 11/20/18.
 */

public class SettingsCellView extends ConstraintLayout {

    private TextView titleTextView;
    private Switch settingSwitch;

    public SettingsCellView(Context context) {
        super(context);

        initMyView();
    }

    public SettingsCellView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initMyView();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsCellView);
            String title = a.getString(R.styleable.SettingsCellView_settings_title);
            Boolean isSwitchNeeded = a.getBoolean(R.styleable.SettingsCellView_isSwitchNeed, false);

            update(title, isSwitchNeeded);
        }

    }

    private void initMyView() {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_settings_view, this, true);

        titleTextView = view.findViewById(R.id.title);
        settingSwitch = view.findViewById(R.id.settings_switch);
        settingSwitch.setClickable(false);
    }

    private void update(String title, Boolean isSwitchNeeded) {
        titleTextView.setText(title);

        if (isSwitchNeeded) {
            settingSwitch.setVisibility(VISIBLE);
        } else {
            settingSwitch.setVisibility(GONE);
        }
    }

    public  void setFocusableTitle () { settingSwitch.setAlpha(0.5f);}

    public void updateSwitch(Boolean isSelected) {
        settingSwitch.setChecked(isSelected);
    }

    public void toggleSwitch() {
        settingSwitch.setChecked(!settingSwitch.isChecked());
    }

    public Boolean getSwitchStatus() {
        return settingSwitch.isChecked();
    }

}
