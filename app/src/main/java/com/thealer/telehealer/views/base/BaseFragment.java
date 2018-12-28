package com.thealer.telehealer.views.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 10,October,2018
 */
public class BaseFragment extends Fragment {

    public Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onStop() {
        closeDailogs();
        super.onStop();
    }

    private void closeDailogs() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public boolean isDeviceXLarge() {
        return getActivity().getResources().getBoolean(R.bool.isXlarge);
    }

    public boolean isModeLandscape() {
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isSplitModeNeeded() {
        return isDeviceXLarge() && isModeLandscape();
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public Bitmap getBitmpaFromPath(String profileImgPath) {

        return BitmapFactory.decodeFile(profileImgPath);
    }

    public void showOrHideSoftInputWindow(boolean showOrHide) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (showOrHide) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public void showAlertDialog(Context context, String title, String message,
                                @Nullable String positiveTitle,
                                @Nullable String negativeTitle,
                                @Nullable DialogInterface.OnClickListener positiveListener,
                                @Nullable DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        if (positiveTitle != null) {
            alertDialog.setPositiveButton(positiveTitle, positiveListener);
        }

        if (negativeTitle != null) {
            alertDialog.setNegativeButton(negativeTitle, negativeListener);
        }
        alertDialog.create();
        alertDialog.show();
    }

    public void showSuccessView(Fragment fragment, int requestId) {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        if (fragment != null) {
            successViewDialogFragment.setTargetFragment(fragment, requestId);
        }
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
    }

    public void sendSuccessViewBroadCast(Context context, boolean status, String title, String description) {

        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, status);
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, title);
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, description);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void showSnack(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.show();
    }

}
