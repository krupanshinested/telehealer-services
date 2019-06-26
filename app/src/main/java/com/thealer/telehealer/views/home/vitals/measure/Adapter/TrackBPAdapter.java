package com.thealer.telehealer.views.home.vitals.measure.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.common.BaseAdapter;
import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;

import java.util.ArrayList;

public class TrackBPAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BPTrack> bpTracks;
    private ArrayList<BPTrack> selectedTracks;
    private boolean presentInsideCall = false;

    public TrackBPAdapter(Context context, ArrayList<BPTrack> bpTracks,ArrayList<BPTrack> selectedTracks,boolean presentInsideCall) {
        this.context = context;
        this.bpTracks = bpTracks;
        this.selectedTracks = selectedTracks;
        this.generateModel(new ArrayList<BaseAdapterObjectModel>(bpTracks));
        this.presentInsideCall = presentInsideCall;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case BaseAdapter.headerType:
                View titleView = LayoutInflater.from(context).inflate(R.layout.adapter_title, viewGroup, false);
                return new TitleHolder(titleView);
            case BaseAdapter.bodyType:
                View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_track_bp, viewGroup, false);
                return new DataHolder(dataView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        BaseAdapterModel baseAdapterModel = items.get(i);

        switch (baseAdapterModel.getType()) {
            case BaseAdapter.headerType:
                ((TitleHolder) viewHolder).titleTv.setText(baseAdapterModel.title);
                break;
            case BaseAdapter.bodyType:

                if (baseAdapterModel.actualValue instanceof BPTrack) {

                    BPTrack bpTrack = (BPTrack) baseAdapterModel.actualValue;
                    DataHolder dataHolder = ((DataHolder) viewHolder);
                    dataHolder.value_tv.setText(bpTrack.getSys() + "/" + bpTrack.getDia());
                    dataHolder.unit_tv.setText(SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.bp));
                    dataHolder.time_tv.setText(Utils.getStringFromDate(bpTrack.getDate(), "hh:mm a"));
                    dataHolder.check_box.setChecked(isSelected(bpTrack));

                    if (presentInsideCall) {
                        dataHolder.time_tv.setTextColor(context.getResources().getColor(R.color.colorBlack,null));
                        dataHolder.check_box.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorBlack,null)));
                    } else {
                        dataHolder.time_tv.setTextColor(context.getResources().getColor(R.color.app_gradient_start,null));
                        dataHolder.check_box.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.app_gradient_start,null)));
                    }

                    if (UserType.isUserPatient()) {
                        dataHolder.check_box.setVisibility(View.VISIBLE);
                    } else {
                        dataHolder.check_box.setVisibility(View.GONE);
                    }

                    dataHolder.main_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isSelected(bpTrack)) {
                                selectedTracks.remove(bpTrack);
                                dataHolder.check_box.setChecked(false);
                            } else {
                                selectedTracks.add(bpTrack);
                                dataHolder.check_box.setChecked(true);
                            }
                        }
                    });

                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    public void reload(ArrayList<BPTrack> bpTracks,ArrayList<BPTrack> selectedTracks) {
        this.bpTracks = bpTracks;
        this.selectedTracks = selectedTracks;
        this.generateModel(new ArrayList<BaseAdapterObjectModel>(bpTracks));
        this.notifyDataSetChanged();
    }

    private boolean isSelected(BPTrack bpTrack) {
        boolean isSelected = false;
        for (BPTrack track : selectedTracks) {
            if (track.getDataID().equals(bpTrack.getDataID())) {
                isSelected = true;
                break;
            }
        }

        return isSelected;
    }

    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView value_tv, unit_tv, time_tv;
        private CheckBox check_box;
        private ConstraintLayout main_container;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            value_tv = (TextView) itemView.findViewById(R.id.value_tv);
            unit_tv = (TextView) itemView.findViewById(R.id.unit_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            main_container = itemView.findViewById(R.id.main_container);

            check_box = itemView.findViewById(R.id.check_box);
        }
    }

}
