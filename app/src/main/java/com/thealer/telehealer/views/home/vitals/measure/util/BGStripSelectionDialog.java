package com.thealer.telehealer.views.home.vitals.measure.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BGStripSelectionInterface;

public class BGStripSelectionDialog extends DialogFragment implements View.OnClickListener {

    private android.widget.TextView message_tv;
    private ConstraintLayout coded_lay,non_coded_lay;
    private CheckBox non_coded_cb,coded_cb;
    private CustomButton continue_btn;

    @Nullable
    public BGStripSelectionInterface bgStripSelectionInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bg_strip_selector, null, true);
        initView(view);
        builder.setView(view);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }

    private void initView(View view) {
        message_tv = (TextView) view.findViewById(R.id.message_tv);
        coded_lay = view.findViewById(R.id.coded_lay);
        non_coded_lay =  view.findViewById(R.id.non_coded_lay);
        non_coded_cb = view.findViewById(R.id.non_coded_cb);
        coded_cb =  view.findViewById(R.id.coded_cb);
        continue_btn =  view.findViewById(R.id.continue_btn);

        coded_lay.setOnClickListener(this);
        non_coded_lay.setOnClickListener(this);
        continue_btn.setOnClickListener(this);

        coded_cb.setClickable(false);
        non_coded_cb.setClickable(false);

        selectedCode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coded_lay:
                selectedCode();
                break;
            case R.id.non_coded_lay:
                selectedNonCode();
                break;
            case R.id.continue_btn:
                if (bgStripSelectionInterface != null)
                    bgStripSelectionInterface.didSelectStrip(coded_cb.isChecked());
                dismiss();
                break;
        }
    }

    private void selectedCode() {
        coded_cb.setChecked(true);
        non_coded_cb.setChecked(false);

        message_tv.setText(getResources().getText(R.string.god_strip_message));
    }

    private void selectedNonCode() {
        coded_cb.setChecked(false);
        non_coded_cb.setChecked(true);

        message_tv.setText(getResources().getText(R.string.gdh_strip_message));
    }
}
