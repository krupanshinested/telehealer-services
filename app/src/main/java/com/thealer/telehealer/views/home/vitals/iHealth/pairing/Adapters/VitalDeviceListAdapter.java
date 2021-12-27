package com.thealer.telehealer.views.home.vitals.iHealth.pairing.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;

import java.util.ArrayList;

/**
 * Created by rsekar on 11/28/18.
 */

public class VitalDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int sectionHeader = 1;
    public static final int data = 2;
    public static final int data_without_subitem = 3;
    public static final int empty_space = 4;

    public static final int none = 0;
    public static final int manual_entry_type = 1;
    public static final int device_type = 2;
    public static final int set_up_device = 3;
    public static final int set_up_telihealth_device = 5;
    public static final int google_fit_sources = 4;

    private Context context;
    private ArrayList<VitalDevice> connectedDevices;
    private ArrayList<VitalDevice> unconnectedDevices;
    public ClickListener clickListener;

    private ArrayList<DataSource> sources = new ArrayList<>();

    public VitalDeviceListAdapter(Context context,ArrayList<VitalDevice> connectedDevices,ArrayList<VitalDevice> unconnectedDevices,@Nullable String measurementType) {
        this.context = context;
        this.connectedDevices = connectedDevices;
        this.unconnectedDevices = unconnectedDevices;

        if (connectedDevices.size() > 0) {
            sources.add(new DataSource(null,sectionHeader,context.getString(R.string.connected_devices),none));

            for (int i = 0;i<connectedDevices.size();i++) {
                VitalDevice device = connectedDevices.get(i);
                sources.add(new DataSource(device,data,"",device_type));
            }
            
        }

        if (unconnectedDevices.size() > 0) {
            sources.add(new DataSource(null,sectionHeader,context.getString(R.string.unconnected_devices),none));

            for (int i = 0;i<unconnectedDevices.size();i++) {
                VitalDevice device = unconnectedDevices.get(i);
                sources.add(new DataSource(device,data,"",device_type));
            }

        }

        if (measurementType != null && measurementType.equals(SupportedMeasurementType.height)) {

        } else if (TeleHealerApplication.appConfig.getVitalPemFileName() != null) {
            sources.add(new DataSource(null, data_without_subitem, context.getString(R.string.setup_new_devices), set_up_device));
        }

        sources.add(new DataSource(null,empty_space,"",none));
        sources.add(new DataSource(null,data_without_subitem,context.getString(R.string.manual_input),manual_entry_type));
        sources.add(new DataSource(null,empty_space,"",none));
        sources.add(new DataSource(null,data_without_subitem,context.getString(R.string.str_new_device_setup),set_up_telihealth_device));

        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
            sources.add(new DataSource(null,empty_space,"",none));
            sources.add(new DataSource(null,data_without_subitem,context.getString(R.string.manage_sources),google_fit_sources));
            sources.add(new DataSource(null,sectionHeader,context.getString(R.string.manage_sources_sub),none));
        }

    }

    public ArrayList<DataSource> getSources() {
        return sources;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case data:
                View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_vital_devices_data, viewGroup, false);
                return new VitalDeviceListAdapter.DataHolder(dataView);
            case sectionHeader:
                View sectionView = LayoutInflater.from(context).inflate(R.layout.adapter_vital_devices_section, viewGroup, false);
                return new VitalDeviceListAdapter.SectionHolder(sectionView);
            case data_without_subitem:
                View dataWithoutList = LayoutInflater.from(context).inflate(R.layout.adapter_vital_devices_data_1, viewGroup, false);
                return new VitalDeviceListAdapter.DataWithoutListHolder(dataWithoutList);
            case empty_space:
                View spaceView = LayoutInflater.from(context).inflate(R.layout.adapter_space, viewGroup, false);
                return new VitalDeviceListAdapter.SectionHolder(spaceView);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        DataSource dataSource = sources.get(position);

        return dataSource.getDataType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        DataSource dataSource = sources.get(position);
        View baseView = null;
        switch (dataSource.getDataType()) {
            case data:
                DataHolder dataHolder = ((DataHolder) viewHolder);
                dataHolder.titleTv.setText(VitalDeviceType.shared.getTitle(dataSource.device.getType()));
                dataHolder.subTitleTv.setText(dataSource.device.getDeviceId());
                dataHolder.subTitleTv.setVisibility(View.VISIBLE);
                dataHolder.icon.setImageResource(VitalDeviceType.shared.getImage(dataSource.device.getType()));
                baseView = dataHolder.mainContainer;
                break;
            case sectionHeader:
                SectionHolder sectionHolder = ((SectionHolder) viewHolder);
                sectionHolder.titleTv.setText(dataSource.getTitle());
                baseView = sectionHolder.mainContainer;
                break;
            case data_without_subitem:
                DataWithoutListHolder dataHolder1 = ((DataWithoutListHolder) viewHolder);
                dataHolder1.titleTv.setText(dataSource.title);
                if (dataSource.dataSubType == set_up_device) {
                    dataHolder1.top_view.setVisibility(View.GONE);
                } else {
                    dataHolder1.top_view.setVisibility(View.VISIBLE);
                }
                baseView = dataHolder1.mainContainer;
                break;
        }

        if (baseView != null) {
            View finalBaseView = baseView;
            baseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onClick(finalBaseView,position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return sources.size();
    }


    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView titleTv,subTitleTv;
        private ConstraintLayout mainContainer;
        private ImageView icon;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            subTitleTv = (TextView) itemView.findViewById(R.id.subtitle_tv);
            mainContainer = itemView.findViewById(R.id.main_container);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    private class SectionHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private ConstraintLayout mainContainer;

        private SectionHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            mainContainer = itemView.findViewById(R.id.main_container);

        }
    }

    private class DataWithoutListHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private View top_view;
        private ConstraintLayout mainContainer;

        private DataWithoutListHolder(@NonNull View itemView) {
            super(itemView);
            top_view = itemView.findViewById(R.id.top_view);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            mainContainer = itemView.findViewById(R.id.main_container);
        }
    }

    public class DataSource {
        @Nullable
        VitalDevice device;
        int dataType;
        String title;
        int dataSubType;

        DataSource(@Nullable VitalDevice device, int dataType, String title,int dataSubType) {
            this.device = device;
            this.dataType = dataType;
            this.title = title;
            this.dataSubType = dataSubType;
        }

        @Nullable
        public VitalDevice getDevice() {
            return device;
        }

        public int getDataType() {
            return dataType;
        }

        public String getTitle() {
            return title;
        }

        public int getDataSubType() {
            return dataSubType;
        }
    }
}
