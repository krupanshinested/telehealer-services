package com.thealer.telehealer.views.home.orders.labs;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class SelectLabTestAdapter extends RecyclerView.Adapter<SelectLabTestAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<String> testList;
    private String selectedTitle;
    private OnListItemSelectInterface onListItemSelectInterface;

    public SelectLabTestAdapter(FragmentActivity activity, List<String> testList, String selectedPosition, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.testList = testList;
        this.selectedTitle = selectedPosition;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_select_lab_test, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (testList.get(i).equals(selectedTitle)) {
            viewHolder.statusIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.statusIv.setVisibility(View.GONE);
        }

        viewHolder.testListTv.setText(testList.get(i));
        viewHolder.testListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTitle = testList.get(i);
                notifyDataSetChanged();
                onListItemSelectInterface.onListItemSelected(i, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView testListTv;
        private ImageView statusIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            testListTv = (TextView) itemView.findViewById(R.id.test_list_tv);
            statusIv = (ImageView) itemView.findViewById(R.id.status_iv);
        }
    }

    public void setTestList(List<String> testList) {
        this.testList = testList;
        notifyDataSetChanged();
    }


}
