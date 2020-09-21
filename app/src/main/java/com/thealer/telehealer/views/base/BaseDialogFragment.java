package com.thealer.telehealer.views.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Aswin on 19,November,2018
 */
public class BaseDialogFragment extends DialogFragment {

//    public void showOrHideSoftInputWindow(FragmentActivity activity, boolean showOrHide) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (showOrHide)
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        else
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        setCancelable(false);
    }
}
