package com.thealer.telehealer.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * Created by Aswin on 06,November,2018
 */
public class DatePickerDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        int type = 0;

        if (getArguments() != null) {
            type = getArguments().getInt(Constants.DATE_PICKER_TYPE);
        }
        return Utils.showDatePickerDialog(getActivity(), type);

    }
}
