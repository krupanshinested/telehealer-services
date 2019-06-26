package com.thealer.telehealer.views.common.CustomDialogs;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.ArrayList;


/**
 * Created by rsekar on 12/11/18.
 */

public class ItemPickerDialog extends AlertDialog implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Button cancel, done;
    private TextView title_tv;

    private PickerListener pickerListener;
    private ArrayList<String> items;
    private ItemPickerAdapter itemPickerAdapter;
    private String title;
    private int defaultSelectedPosition = 0;

    public ItemPickerDialog(@NonNull Context context, String title, ArrayList<String> items, PickerListener pickerListener) {
        super(context);

        this.items = items;
        this.pickerListener = pickerListener;
        this.title = title;
    }

    public ItemPickerDialog(@NonNull Context context, String title, ArrayList<String> items, PickerListener pickerListener, int defaultSelectedPosition) {
        this(context, title, items, pickerListener);

        this.defaultSelectedPosition = defaultSelectedPosition;
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_item_picker);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        recyclerView = findViewById(R.id.recyclerView);
        cancel = findViewById(R.id.cancel);
        done = findViewById(R.id.done);
        title_tv = findViewById(R.id.title_tv);

        cancel.setOnClickListener(this);
        done.setOnClickListener(this);

        title_tv.setText(title);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemPickerAdapter itemPickerAdapter = new ItemPickerAdapter(getContext(), items);
        this.itemPickerAdapter = itemPickerAdapter;
        this.itemPickerAdapter.selectedPosition = defaultSelectedPosition;
        recyclerView.setAdapter(itemPickerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                pickerListener.didCancelled();
                this.dismiss();
                break;
            case R.id.done:
                pickerListener.didSelectedItem(itemPickerAdapter.selectedPosition);
                this.dismiss();
                break;
        }
    }
}
