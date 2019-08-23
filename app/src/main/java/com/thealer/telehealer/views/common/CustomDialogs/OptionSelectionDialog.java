package com.thealer.telehealer.views.common.CustomDialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.thealer.telehealer.R;

import java.util.List;

/**
 * Created by Aswin on 15,March,2019
 */
public class OptionSelectionDialog extends AlertDialog {

    private RecyclerView optionsRv;
    private CardView cancelCv;
    private List<String> optionList;
    private PickerListener pickerListener;
    private int selectedPosition;

    public OptionSelectionDialog(@NonNull Context context, List<String> optionList, int selectedPosition, PickerListener pickerListener) {
        super(context);
        this.optionList = optionList;
        this.pickerListener = pickerListener;
        this.selectedPosition = selectedPosition;
    }

    protected OptionSelectionDialog(@NonNull Context context, @NonNull List<String> optionList, @NonNull PickerListener pickerListener, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.optionList = optionList;
        this.pickerListener = pickerListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_options_select);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setGravity(Gravity.BOTTOM);

        initView();
    }

    private void initView() {
        optionsRv = (RecyclerView) findViewById(R.id.options_rv);
        cancelCv = (CardView) findViewById(R.id.cancel_cv);

        cancelCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        optionsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsRv.setAdapter(new OptionSelectionAdapter(getContext(), optionList, selectedPosition, pickerListener, this));

    }
}
