package com.thealer.telehealer.views.settings.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.commonResponseModel.VitalBean;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 26,July,2021
 **/
public class AboutHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FragmentActivity activity;
    List historyList = new ArrayList<>();
    private static final int VIEW_TYPE_VITAL = 1;
    private static final int VIEW_TYPE_HISTORY = 2;
    private static final int VIEW_TYPE_MEDICATION = 3;


    public AboutHistoryAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        if (viewType == VIEW_TYPE_VITAL) {
            view = layoutInflater.inflate(R.layout.vital_raw_item, viewGroup, false);
            return new VitalHolder(view);
        } else if(viewType == VIEW_TYPE_HISTORY){
            view = layoutInflater.inflate(R.layout.history_item, viewGroup, false);
            return new HistoryHolder(view);
        }else{
            view = layoutInflater.inflate(R.layout.medication_item, viewGroup, false);
            return new MedicationHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_VITAL: {
                VitalBean vitalInfo = (VitalBean) historyList.get(position);
                VitalHolder vitalHolder = ((VitalHolder) holder);
                if (vitalInfo.getType() != null && !vitalInfo.getType().isEmpty())
                    vitalHolder.tvTitle.setText(vitalInfo.getType());
                else
                    vitalHolder.tvTitle.setVisibility(View.GONE);

                if (vitalInfo.getLast_value() != null && !vitalInfo.getLast_value().isEmpty())
                    vitalHolder.tvVital.setText(vitalInfo.getLast_value());
                else
                    vitalHolder.tvVital.setVisibility(View.GONE);

                Date date;
                if (vitalInfo.getLast_date() != null && !vitalInfo.getLast_date().isEmpty()) {
                    date = Utils.getDateFromPossibleFormat(vitalInfo.getLast_date());
                    String currentDate = activity.getString(R.string.recorded_on, Utils.getStringFromDate(date, Utils.defaultDateFormat));
                    vitalHolder.tvDesc.setText(currentDate);
                } else
                    vitalHolder.tvDesc.setVisibility(View.GONE);

                vitalHolder.tvVital.setText(vitalInfo.getLast_value());
            }
            break;
            case VIEW_TYPE_HISTORY: {
                HistoryBean historyInfo =(HistoryBean) historyList.get(position);
                HistoryHolder historyHolder =((HistoryHolder)holder);
                if (historyInfo.isIsYes())
                    historyHolder.tvHistory.setText(activity.getString(R.string.yes));
                else
                    historyHolder.tvHistory.setText(activity.getString(R.string.no));
                historyHolder.tvTitle.setText(historyInfo.getQuestion());

            }
            break;
            default: {
                // Medication View Will shown
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        Object adapterItemObject = historyList.get(position);
        if (adapterItemObject instanceof VitalBean) {
            return VIEW_TYPE_VITAL;
        } else if (adapterItemObject instanceof HistoryBean) {
            return VIEW_TYPE_HISTORY;
        } else {
            return VIEW_TYPE_MEDICATION;
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

<<<<<<< HEAD
=======
    public void setDataAdapter(List historyList) {
        this.historyList=historyList;
        notifyDataSetChanged();
    }

>>>>>>> 6d995c019ccbe57d17af5d6a4255538d1a426d88
    private class VitalHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvVital, tvDesc;

        public VitalHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvVital = (TextView) itemView.findViewById(R.id.tv_vital);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }
    private class HistoryHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvHistory;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvHistory = (TextView) itemView.findViewById(R.id.tv_history);
        }
    }
    private class MedicationHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvMedication;

        public MedicationHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvMedication = (TextView) itemView.findViewById(R.id.tv_medication);
        }
    }

}
