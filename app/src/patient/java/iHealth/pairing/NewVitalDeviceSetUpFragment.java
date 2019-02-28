package iHealth.pairing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import iHealth.pairing.Adapters.NewVitalSetUpAdapter;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;

/**
 * Created by rsekar on 11/28/18.
 */

public class NewVitalDeviceSetUpFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private TextView learMoreTv;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private ToolBarInterface toolBarInterface;
    private VitalManagerInstance vitalManagerInstance;

    @Nullable
    private String measurementType;
    private ArrayList<String> deviceSources = new ArrayList<>();

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        if (getArguments() != null) {
            String device = getArguments().getString(ArgumentKeys.SELECTED_VITAL_TYPE);
            if (device != null && !device.isEmpty()) {
                measurementType = device;
            }
        }
        deviceSources = new ArrayList<>();

        if (measurementType != null && !measurementType.isEmpty()) {
            for(int i=0;i<VitalDeviceType.types.size();i++) {
                String type = VitalDeviceType.types.get(i);

                String deviceType = VitalDeviceType.shared.getMeasurementType(type);
                if (measurementType.equals(SupportedMeasurementType.heartRate)) {
                    if (deviceType.equals(SupportedMeasurementType.bp) || deviceType.equals(SupportedMeasurementType.pulseOximeter)) {
                        deviceSources.add(type);
                    }
                } else if (deviceType.equals(measurementType)) {
                    deviceSources.add(type);
                }

            }
        } else {
            deviceSources = VitalDeviceType.types;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_vital_device_setup, container, false);

        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();
        vitalManagerInstance = (VitalManagerInstance) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        toolBarInterface.updateTitle(getString(R.string.setup_new_devices));
        onViewChangeInterface.hideOrShowClose(true);
        onViewChangeInterface.hideOrShowBackIv(true);

        toolBarInterface.updateSubTitle("",View.GONE);
        vitalManagerInstance.updateBatteryView(View.GONE,0);
    }

    private void initView(View baseView) {

        recyclerView = baseView.findViewById(R.id.device_list);
        learMoreTv = baseView.findViewById(R.id.lean_more_tv);

        String clickHere = "<font color='#3B9EFF'>"+getString(R.string.click_here)+"</font>";
        learMoreTv.setText(Utils.fromHtml(getString(R.string.device_learn_more)+" "+clickHere));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        NewVitalSetUpAdapter vitalDeviceListAdapter = new NewVitalSetUpAdapter(getActivity(), deviceSources);
        recyclerView.setAdapter(vitalDeviceListAdapter);

        vitalDeviceListAdapter.clickListener = new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EventRecorder.recordVitals("SELECT_PAIR_DEVICE", deviceSources.get(position));
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.DEVICE_TYPE,deviceSources.get(position));
                onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_SETUP,true,bundle);
            }
        };

        learMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://telehealer.com/vitals-devices/"));
                startActivity(browserIntent);
            }
        });

    }


}
