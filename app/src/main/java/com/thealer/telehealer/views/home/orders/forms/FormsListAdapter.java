package com.thealer.telehealer.views.home.orders.forms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 28,November,2018
 */
public class FormsListAdapter extends RecyclerView.Adapter<FormsListAdapter.ViewHolder> {

    private Context context;
    private OnListItemSelectInterface onListItemSelectInterface;
    private ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModelArrayList;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private List<String> selectedFormIds;

    public FormsListAdapter(FragmentActivity activity, OnListItemSelectInterface onListItemSelectInterface,
                            ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModelArrayList, List<String> selectedFormIds) {
        this.context = activity;
        this.onListItemSelectInterface = onListItemSelectInterface;
        this.formsApiResponseModelArrayList = formsApiResponseModelArrayList;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        this.selectedFormIds = selectedFormIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_forms_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.infoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditableFormFragment editableFormFragment = new EditableFormFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.FORM_DETAIL, formsApiResponseModelArrayList.get(i));
                bundle.putBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, true);
                editableFormFragment.setArguments(bundle);

                showSubFragmentInterface.onShowFragment(editableFormFragment);
            }
        });

        viewHolder.listCb.setText(formsApiResponseModelArrayList.get(i).getName());

        viewHolder.listCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemSelectInterface.onListItemSelected(formsApiResponseModelArrayList.get(i).getForm_id(), null);
            }
        });

        if (selectedFormIds.contains(String.valueOf(formsApiResponseModelArrayList.get(i).getForm_id()))) {
            viewHolder.listCb.setChecked(true);
        } else {
            viewHolder.listCb.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return formsApiResponseModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox listCb;
        private ImageView infoIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            listCb = (CheckBox) itemView.findViewById(R.id.list_cb);
            infoIv = (ImageView) itemView.findViewById(R.id.info_iv);

        }
    }

    public void setFormsApiResponseModelArrayList(ArrayList<OrdersUserFormsApiResponseModel> formsApiResponseModelArrayList) {
        this.formsApiResponseModelArrayList = formsApiResponseModelArrayList;
        notifyDataSetChanged();
    }
}
