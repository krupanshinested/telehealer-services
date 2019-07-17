package com.thealer.telehealer.common;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
        return Utils.showDatePickerDialog(getActivity(), null, type, null);

    }
}
