package Flavor.iHealth.pairing.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.BaseAdapter;
import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDataSource;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;

import java.util.ArrayList;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/28/18.
 */

public class NewVitalSetUpAdapter extends BaseAdapter {

    private Context context;
    public VitalSetUpInterface vitalSetUpInterface;

    public NewVitalSetUpAdapter(Context context, ArrayList<VitalDataSource> dataSources,VitalSetUpInterface vitalSetUpInterface) {
        this.context = context;
        this.vitalSetUpInterface = vitalSetUpInterface;
        this.sortByAscending = true;
        this.generateModel(new ArrayList<BaseAdapterObjectModel>(dataSources));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case BaseAdapter.headerType:
                View titleView = LayoutInflater.from(context).inflate(R.layout.adapter_title, viewGroup, false);
                return new TitleHolder(titleView);
            case BaseAdapter.bodyType:
                View dataView = LayoutInflater.from(context).inflate(R.layout.adapter_new_vital_device_setup, viewGroup, false);
                return new NewVitalSetUpAdapter.DataHolder(dataView);
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        BaseAdapterModel baseAdapterModel = items.get(i);

        switch (baseAdapterModel.getType()) {
            case BaseAdapter.headerType:
                ((TitleHolder) viewHolder).titleTv.setText(context.getString(SupportedMeasurementType.getTitle(baseAdapterModel.title)));
                break;
            case BaseAdapter.bodyType:
                if (baseAdapterModel.actualValue instanceof VitalDataSource) {
                    DataHolder dataHolder = (DataHolder)viewHolder;
                    VitalDataSource vitalDataSource = (VitalDataSource) baseAdapterModel.actualValue;
                    dataHolder.titleTv.setText(VitalDeviceType.shared.getTitle(vitalDataSource.getDeviceType()));
                    dataHolder.subTitleTv.setText(VitalDeviceType.shared.getDescription(vitalDataSource.getDeviceType()));

                    if (VitalPairedDevices.getAllPairedDevices(appPreference).isAlreadyPairedType(vitalDataSource.getDeviceType())) {
                        dataHolder.tickMark.setVisibility(View.VISIBLE);
                    } else {
                        dataHolder.tickMark.setVisibility(View.GONE);
                    }

                    Glide.with(context)
                            .load(context.getDrawable(VitalDeviceType.shared.getImage(vitalDataSource.getDeviceType())))
                            .apply(new RequestOptions())
                            .thumbnail(0.5f)
                            .into(dataHolder.imageView);
                    dataHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (vitalSetUpInterface != null) {
                                vitalSetUpInterface.didSelect(vitalDataSource);
                            }
                        }
                    });
                }
            default:
                break;
        }
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



    public interface VitalSetUpInterface {
        public void didSelect(VitalDataSource dataSource);
    }
}
