package com.thealer.telehealer.views.base;

import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 30,October,2018
 */
public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public ContextThemeWrapper contextThemeWrapper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    public void setBottomSheetHeight(View view) {
        Point point = new Point();
        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        display.getSize(point);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (point.y * 0.5));
        view.setLayoutParams(layoutParams);
    }

    public void setBottomSheetHeight(View view, double percentage) {
        double value = percentage / 100;
        Point point = new Point();
        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        display.getSize(point);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (point.y * value));
        view.setLayoutParams(layoutParams);
    }
}
