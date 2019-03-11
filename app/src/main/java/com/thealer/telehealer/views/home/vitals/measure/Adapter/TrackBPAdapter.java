package com.thealer.telehealer.views.home.vitals.measure.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.BaseAdapter;
import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static com.thealer.telehealer.TeleHealerApplication.appPreference;

    public class TrackBPAdapter  extends BaseAdapter {

    private Context context;
    private ArrayList<BPTrack> bpTracks;
    private ArrayList<BPTrack> selectedTracks;

    public TrackBPAdapter(Context context, ArrayList<BPTrack> bpTracks,ArrayList<BPTrack> selectedTracks) {
        this.context = context;
        this.bpTracks = bpTracks;
        this.selectedTracks = selectedTracks;
        this.generateModel(new ArrayList<BaseAdapterObjectModel>(bpTracks));
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
                    dataHolder.value_tv.setText(bpTrack.getSys()+"/"+bpTrack.getDia());
                    dataHolder.unit_tv.setText(SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.bp));
                    dataHolder.time_tv.setText(Utils.getStringFromDate(bpTrack.getDate(),"hh:mm a"));
                    dataHolder.check_box.setChecked(isSelected(bpTrack));

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

        private TextView value_tv, unit_tv,time_tv;
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
