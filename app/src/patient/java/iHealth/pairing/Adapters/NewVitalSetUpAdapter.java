package iHealth.pairing.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;

import java.util.ArrayList;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/28/18.
 */

public class NewVitalSetUpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> devices;
    public ClickListener clickListener;

    public NewVitalSetUpAdapter(Context context, ArrayList<String> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_new_vital_device_setup, viewGroup, false);
        return new NewVitalSetUpAdapter.DataHolder(dataView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        DataHolder dataHolder = (DataHolder)viewHolder;

        dataHolder.titleTv.setText(VitalDeviceType.shared.getTitle(devices.get(i)));
        dataHolder.subTitleTv.setText(VitalDeviceType.shared.getDescription(devices.get(i)));

        if (VitalPairedDevices.getAllPairedDevices(appPreference).isAlreadyPairedType(devices.get(i))) {
            dataHolder.tickMark.setVisibility(View.VISIBLE);
        } else {
            dataHolder.tickMark.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(context.getDrawable(VitalDeviceType.shared.getImage(devices.get(i))))
                .apply(new RequestOptions())
                .thumbnail(0.5f)
                .into(dataHolder.imageView);
        dataHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(dataHolder.mainContainer,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    private class DataHolder extends RecyclerView.ViewHolder {

        private TextView titleTv, subTitleTv;
        private ImageView imageView,tickMark;
        private ConstraintLayout mainContainer;

        private DataHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            subTitleTv = (TextView) itemView.findViewById(R.id.subtitle_tv);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            tickMark = itemView.findViewById(R.id.tickMark);
            mainContainer = (ConstraintLayout) itemView.findViewById(R.id.main_container);
        }
    }

}
