package com.thealer.telehealer.views.home.orders.labs;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 23,November,2018
 */
public class IcdCodeListAdapter extends RecyclerView.Adapter<IcdCodeListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private Map<String, String> icdCodeList = new HashMap<>();
    private List<String> icd10_codes;
    private ShowSubFragmentInterface showSubFragmentInterface;

    public IcdCodeListAdapter(FragmentActivity activity, List<String> icd10_codes, Map<String, String> icdCodeList) {
        this.activity = activity;
        this.icdCodeList = icdCodeList;
        this.icd10_codes = icd10_codes;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
    }

    public IcdCodeListAdapter(FragmentActivity activity, List<String> selectedIcdCodeList, HashMap<String, IcdCodeApiResponseModel.ResultsBean> icdCodeDetailHashMap) {
        this.activity = activity;
        this.icd10_codes = selectedIcdCodeList;
        for (String icdCode : selectedIcdCodeList) {
            icdCodeList.put(icdCode, icdCodeDetailHashMap.get(icdCode).getDescription());
        }
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_lab_icd_code_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.icdCodeTv.setText(icd10_codes.get(i));
        viewHolder.icdDetailTv.setText(icdCodeList.get(icd10_codes.get(i)));
        viewHolder.itemCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectIcdCodeFragment selectIcdCodeFragment = new SelectIcdCodeFragment();
                showSubFragmentInterface.onShowFragment(selectIcdCodeFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return icd10_codes.size();
    }

    public void setData(List<String> selectedIcdCodeList, HashMap<String, IcdCodeApiResponseModel.ResultsBean> icdCodeDetailHashMap) {
        this.icd10_codes = selectedIcdCodeList;
        for (String icdCode : selectedIcdCodeList) {
            this.icdCodeList.put(icdCode, icdCodeDetailHashMap.get(icdCode).getDescription());
        }
        notifyDataSetChanged();
    }

    public void setMapData(HashMap<String, String> icdCodeMap) {
        this.icdCodeList = icdCodeMap;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView icdCodeTv;
        private TextView icdDetailTv;
        private ConstraintLayout itemCl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCl = (ConstraintLayout) itemView.findViewById(R.id.itemView);
            icdCodeTv = (TextView) itemView.findViewById(R.id.icd_code_tv);
            icdDetailTv = (TextView) itemView.findViewById(R.id.icd_detail_tv);
        }
    }
}
