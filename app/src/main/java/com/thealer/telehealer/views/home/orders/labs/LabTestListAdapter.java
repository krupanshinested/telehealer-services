package com.thealer.telehealer.views.home.orders.labs;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.lab.LabsBean;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 23,November,2018
 */
public class LabTestListAdapter extends RecyclerView.Adapter<LabTestListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<LabsBean> labsBeanList;
    private Map<String, String> icdCodeList = new HashMap<>();

    public LabTestListAdapter(FragmentActivity activity, List<LabsBean> labs,
                              Map<String, String> icdCodeList) {
        this.activity = activity;
        this.labsBeanList = labs;
        this.icdCodeList = icdCodeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_lab_test_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.titleOcv.setLabelTv(activity.getString(R.string.test) + " " + (i + 1));
        viewHolder.titleOcv.setTitleTv(labsBeanList.get(i).getTest_description());
        viewHolder.statSwitch.setChecked(labsBeanList.get(i).isStat());

        viewHolder.icdRv.setLayoutManager(new LinearLayoutManager(activity));
        IcdCodeListAdapter icdCodeListAdapter = new IcdCodeListAdapter(activity, labsBeanList.get(i).getICD10_codes(), icdCodeList);
        viewHolder.icdRv.setAdapter(icdCodeListAdapter);
    }

    @Override
    public int getItemCount() {
        return labsBeanList.size();
    }

    public void setIcdCodeList(Map<String, String> icdCodeList) {
        this.icdCodeList = icdCodeList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private OrdersCustomView titleOcv;
        private RecyclerView icdRv;
        private Switch statSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleOcv = (OrdersCustomView) itemView.findViewById(R.id.title_ocv);
            icdRv = (RecyclerView) itemView.findViewById(R.id.icd_rv);
            statSwitch = (Switch) itemView.findViewById(R.id.stat_switch);
        }
    }
}
