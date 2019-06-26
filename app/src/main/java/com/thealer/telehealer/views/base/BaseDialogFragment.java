package com.thealer.telehealer.views.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Aswin on 19,November,2018
 */
public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        setCancelable(false);
    }
}
