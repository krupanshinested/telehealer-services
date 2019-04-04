package com.thealer.telehealer.views.settings.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.Payments.VitalVisit;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rsekar on 1/23/19.
 */

public class PaymentDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ArrayList<VitalVisit> vitalVisits = new ArrayList<>();
    private ArrayList<RecentsApiResponseModel.ResultBean> calls = new ArrayList<>();
    private Boolean isCalls = true;

    private HashMap<String, CommonUserApiResponseModel> userDetailHashMap;

    public PaymentDetailAdapter(Context context) {
        this.context = context;
    }

    public void showCalls(ArrayList<RecentsApiResponseModel.ResultBean> calls) {
        this.calls = calls;
        isCalls = true;
        notifyDataSetChanged();
    }

    public void showVitals(ArrayList<VitalVisit> vitalVisits) {
        this.vitalVisits = vitalVisits;
        isCalls = false;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_call_log, viewGroup, false);
        return new PaymentDetailAdapter.DataHolder(dataView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PaymentDetailAdapter.DataHolder dataHolder = (PaymentDetailAdapter.DataHolder) viewHolder;

        if (isCalls) {
            RecentsApiResponseModel.ResultBean call = calls.get(i);

            dataHolder.title_tv.setText(call.getPatient().getFirst_name() + " " + call.getPatient().getLast_name());

            if (call.getStart_time() != null) {
                dataHolder.time_tv.setText(Utils.getDayMonthTime(call.getStart_time()));
            }

            dataHolder.duration_tv.setText(call.getFormattedDuration());

            dataHolder.duration_tv.setVisibility(View.VISIBLE);
            dataHolder.duration_iv.setVisibility(View.VISIBLE);

        } else {
            VitalVisit vitalVisit = vitalVisits.get(i);

            CommonUserApiResponseModel userDetail = userDetailHashMap.get(vitalVisit.getUser_guid());
            if (userDetail != null) {
                dataHolder.title_tv.setText(userDetail.getDisplayName());
            } else {
                dataHolder.title_tv.setText("");
            }
            dataHolder.time_tv.setText(vitalVisit.getDate());

            dataHolder.duration_tv.setVisibility(View.INVISIBLE);
            dataHolder.duration_iv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (isCalls) {
            return calls.size();
        } else {
            return vitalVisits.size();
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView title_tv, time_tv, duration_tv;
        private ConstraintLayout mainContainer;
        private ImageView duration_iv;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = (TextView) itemView.findViewById(R.id.title_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            duration_tv = (TextView) itemView.findViewById(R.id.duration_tv);
            mainContainer = (ConstraintLayout) itemView.findViewById(R.id.main_container);
            duration_iv = itemView.findViewById(R.id.duration_iv);
        }
    }

    public void setUserDetailHashMap(HashMap<String, CommonUserApiResponseModel> userDetailHashMap) {
        this.userDetailHashMap = userDetailHashMap;
        notifyDataSetChanged();
    }

}
