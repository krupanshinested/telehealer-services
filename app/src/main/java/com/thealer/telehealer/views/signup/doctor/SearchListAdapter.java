package com.thealer.telehealer.views.signup.doctor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 25,October,2018
 */
class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private List<String> doctorsNameList = new ArrayList<>();
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnListItemSelectInterface onListItemSelectInterface;

    public SearchListAdapter(FragmentActivity fragmentActivity, List<String> doctorsNameList, OnListItemSelectInterface onListItemSelectInterface) {
        this.fragmentActivity = fragmentActivity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;
        this.onListItemSelectInterface = onListItemSelectInterface;
        this.doctorsNameList = doctorsNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_search_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (i == getItemCount() - 1) {
            viewHolder.parent.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            viewHolder.parent.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        viewHolder.nameTv.setText(doctorsNameList.get(i));
        viewHolder.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SEARCH_KEY, doctorsNameList.get(i));
                onListItemSelectInterface.onListItemSelected(i, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsNameList.size();
    }

    public void setData(List<String> data) {
        doctorsNameList = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_tv);
            parent = (ConstraintLayout) itemView.findViewById(R.id.parent);
        }
    }
}
