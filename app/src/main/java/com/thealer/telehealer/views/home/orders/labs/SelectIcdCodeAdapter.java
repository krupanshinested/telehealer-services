package com.thealer.telehealer.views.home.orders.labs;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class SelectIcdCodeAdapter extends RecyclerView.Adapter<SelectIcdCodeAdapter.ViewHolder> {

    private FragmentActivity fragmentActivity;
    private List<IcdCodeApiResponseModel.ResultsBean> resultsBeanList;
    private IcdCodesDataViewModel icdCodesDataViewModel;
    private HashMap<String, IcdCodeApiResponseModel.ResultsBean> selectedListItems = new HashMap<>();
    private List<String> selectedIcdCodeList = new ArrayList<>();

    public SelectIcdCodeAdapter(FragmentActivity fragmentActivity, List<IcdCodeApiResponseModel.ResultsBean> resultsBeanList) {
        this.fragmentActivity = fragmentActivity;
        this.resultsBeanList = resultsBeanList;
        icdCodesDataViewModel = ViewModelProviders.of(fragmentActivity).get(IcdCodesDataViewModel.class);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        icdCodesDataViewModel.getSelectedIcdCodeList().observe(fragmentActivity, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {
                if (list != null) {
                    selectedIcdCodeList = list;
                    notifyDataSetChanged();
                }
            }
        });

        icdCodesDataViewModel.getIcdCodeDetailHashMap().observe(fragmentActivity, new Observer<HashMap<String, IcdCodeApiResponseModel.ResultsBean>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, IcdCodeApiResponseModel.ResultsBean> resultsBeanHashMap) {
                if (resultsBeanHashMap != null) {
                    selectedListItems = resultsBeanHashMap;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_select_icd_code, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (selectedIcdCodeList.contains(resultsBeanList.get(i).getCode())) {
            viewHolder.icdCodeCb.setChecked(true);
        } else {
            viewHolder.icdCodeCb.setChecked(false);
        }

        viewHolder.icdCodeCb.setText(resultsBeanList.get(i).getCode());
        viewHolder.icdDetailTv.setText(resultsBeanList.get(i).getDescription());

        viewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.icdCodeCb.isChecked()) {
                    viewHolder.icdCodeCb.setChecked(true);

                    selectedIcdCodeList.add(resultsBeanList.get(i).getCode());
                    selectedListItems.put(resultsBeanList.get(i).getCode(), resultsBeanList.get(i));
                } else {
                    viewHolder.icdCodeCb.setChecked(false);

                    selectedIcdCodeList.remove(resultsBeanList.get(i).getCode());
                }

                icdCodesDataViewModel.getIcdCodeDetailHashMap().setValue(selectedListItems);
                icdCodesDataViewModel.getSelectedIcdCodeList().setValue(selectedIcdCodeList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultsBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout listItem;
        private CheckBox icdCodeCb;
        private TextView icdDetailTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            listItem = (ConstraintLayout) itemView.findViewById(R.id.list_item);
            icdCodeCb = (CheckBox) itemView.findViewById(R.id.icd_code_cb);
            icdDetailTv = (TextView) itemView.findViewById(R.id.icd_detail_tv);
        }
    }

    public void setIcdCodeApiResponseModel(List<IcdCodeApiResponseModel.ResultsBean> resultsBeanList, int searchKey) {
        if (searchKey == 1) {
            this.resultsBeanList = resultsBeanList;
        } else {
            this.resultsBeanList.addAll(resultsBeanList);
        }
        notifyDataSetChanged();
    }

}
