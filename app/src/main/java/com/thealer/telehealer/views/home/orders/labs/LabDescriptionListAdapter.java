package com.thealer.telehealer.views.home.orders.labs;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.LabsBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 03,December,2018
 */
public class LabDescriptionListAdapter extends RecyclerView.Adapter<LabDescriptionListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<LabsBean> labsBeanList = new ArrayList<>();
    private HashMap<String, IcdCodeApiResponseModel.ResultsBean> icdCodeDetailHashMap;
    private LabTestDataViewModel labTestDataViewModel;
    private ShowSubFragmentInterface showSubFragmentInterface;

    public LabDescriptionListAdapter(FragmentActivity activity,
                                     List<LabsBean> labsBeanList,
                                     HashMap<String, IcdCodeApiResponseModel.ResultsBean> icdCodeDetailHashMap) {
        this.activity = activity;
        this.labsBeanList = labsBeanList;
        this.icdCodeDetailHashMap = icdCodeDetailHashMap;
        labTestDataViewModel = new ViewModelProvider(activity).get(LabTestDataViewModel.class);
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_description_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.removeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labsBeanList.remove(i);
                labTestDataViewModel.getLabsBeanLiveData().setValue(labsBeanList);
                notifyDataSetChanged();
            }
        });

        viewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                labTestDataViewModel.setTestTitle(null);

                LabTestPreviewFragment labTestPreviewFragment = new LabTestPreviewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.VIEW_TITLE, activity.getString(R.string.lab_description));
                bundle.putBoolean(ArgumentKeys.IS_EDIT_MODE, true);
                bundle.putInt(ArgumentKeys.LAB_TEST_SELECTED_POSITION, i);

                labTestPreviewFragment.setArguments(bundle);

                showSubFragmentInterface.onShowFragment(labTestPreviewFragment);
            }
        });

        viewHolder.testTitleTv.setText(labsBeanList.get(i).getTest_description());

        for (String icdCode : labsBeanList.get(i).getICD10_codes()) {

            View view = LayoutInflater.from(activity).inflate(R.layout.adapter_lab_icd_code_list, null);

            TextView icdCodeTv;
            TextView icdDetailTv;

            icdCodeTv = (TextView) view.findViewById(R.id.icd_code_tv);
            icdDetailTv = (TextView) view.findViewById(R.id.icd_detail_tv);

            icdCodeTv.setText(icdCode);
            icdDetailTv.setText(icdCodeDetailHashMap.get(icdCode).getDescription());

            viewHolder.viewHolder.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        return labsBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView removeIv;
        private TextView testTitleTv;
        private LinearLayout viewHolder;
        private ConstraintLayout listItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            removeIv = (ImageView) itemView.findViewById(R.id.remove_iv);
            testTitleTv = (TextView) itemView.findViewById(R.id.test_title_tv);
            listItem = (ConstraintLayout) itemView.findViewById(R.id.list_item);
            viewHolder = (LinearLayout) itemView.findViewById(R.id.view_holder);
        }
    }
}
