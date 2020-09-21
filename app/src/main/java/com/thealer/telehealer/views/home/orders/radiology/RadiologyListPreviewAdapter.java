package com.thealer.telehealer.views.home.orders.radiology;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 12,December,2018
 */
class RadiologyListPreviewAdapter extends RecyclerView.Adapter<RadiologyListPreviewAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<RadiologyListModel> radiologyModelList;
    private List<String> sectionsList = new ArrayList<>();
    private HashMap<String, String> headersMap = new HashMap<>();
    private HashMap<String, String> itemsMap = new HashMap<>();
    private ShowSubFragmentInterface showSubFragmentInterface;

    public RadiologyListPreviewAdapter(FragmentActivity activity, List<RadiologyListModel> radiologyModelList) {
        this.activity = activity;
        this.radiologyModelList = radiologyModelList;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        generateModelList();
    }

    private void generateModelList() {
        for (int i = 0; i < radiologyModelList.size(); i++) {
            String section = radiologyModelList.get(i).getSection();

            if (!sectionsList.contains(section)) {
                sectionsList.add(section);
            }

            if (radiologyModelList.get(i).getHeader() != null && !radiologyModelList.get(i).getHeader().isEmpty()) {
                if (!headersMap.containsKey(section)) {
                    headersMap.put(section, "");
                }

                String headers = headersMap.get(section);
                if (headers == null || headers.isEmpty()) {
                    headers = radiologyModelList.get(i).getHeader();
                } else {
                    if (!headers.contains(radiologyModelList.get(i).getHeader())) {
                        headers = headers + "," + radiologyModelList.get(i).getHeader();
                    }
                }

                headersMap.put(section, headers);

            }

            if (!itemsMap.containsKey(section)) {
                itemsMap.put(section, "");
            }

            String items = itemsMap.get(section);
            if (items == null || items.isEmpty()) {
                items = radiologyModelList.get(i).getDisplayText();
            } else {
                items = items + "," + radiologyModelList.get(i).getDisplayText();
            }
            itemsMap.put(section, items);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_radiology_preview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.sectionsTv.setText(sectionsList.get(i));
        if (headersMap.get(sectionsList.get(i)) == null) {
            viewHolder.headersTv.setVisibility(View.GONE);
        }
        viewHolder.headersTv.setText(headersMap.get(sectionsList.get(i)));
        viewHolder.itemsTv.setText(itemsMap.get(sectionsList.get(i)));
        viewHolder.itemCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectRadiologyTestFragment selectRadiologyTestFragment = new SelectRadiologyTestFragment();
                showSubFragmentInterface.onShowFragment(selectRadiologyTestFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionsTv;
        private TextView headersTv;
        private TextView itemsTv;
        private ConstraintLayout itemCl;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCl = (ConstraintLayout) itemView.findViewById(R.id.itemView);
            sectionsTv = (TextView) itemView.findViewById(R.id.sections_tv);
            headersTv = (TextView) itemView.findViewById(R.id.headers_tv);
            itemsTv = (TextView) itemView.findViewById(R.id.items_tv);
        }
    }

    public void setRadiologyModelList(List<RadiologyListModel> radiologyModelList) {
        this.radiologyModelList = radiologyModelList;
        generateModelList();
    }
}
