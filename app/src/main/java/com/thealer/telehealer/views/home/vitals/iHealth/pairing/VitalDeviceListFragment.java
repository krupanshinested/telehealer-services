package com.thealer.telehealer.views.home.vitals.iHealth.pairing;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.iHealth.pairing.Adapters.VitalDeviceListAdapter;
import com.thealer.telehealer.views.settings.newDeviceSupport.MyDeviceListActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;

import flavor.GoogleFit.VitalDeviceListWithGoogleFitFragment;


/**
 * Created by rsekar on 11/28/18.
 */

public class VitalDeviceListFragment extends BaseFragment {

    public static VitalDeviceListFragment getInstance() {
        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
            return new VitalDeviceListWithGoogleFitFragment();
        } else {
            return new VitalDeviceListFragment();
        }
    }

    private RecyclerView recyclerView;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private ToolBarInterface toolBarInterface;
    private VitalManagerInstance vitalManagerInstance;

    @Nullable
    private String measurementType;

    private ArrayList<VitalDevice> unconnectedDevice = new ArrayList<>();
    private ArrayList<VitalDevice> connectedDevice = new ArrayList<>();
    private VitalDeviceListAdapter vitalDeviceListAdapter;
    private AssociationApiViewModel associationApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private ArrayList<DoctorGroupedAssociations> doctorGroupedAssociations;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        measurementType = getArguments().getString(ArgumentKeys.SELECTED_VITAL_TYPE);


        generateDataSource();

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(deviceStateChanger, new IntentFilter(getString(R.string.did_device_state_changed)));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_devices, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        toolBarInterface.updateTitle(getString(R.string.new_vital));
        onViewChangeInterface.hideOrShowClose(true);
        onViewChangeInterface.hideOrShowBackIv(false);
        toolBarInterface.updateSubTitle("", View.GONE);
        vitalManagerInstance.updateBatteryView(View.GONE, 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();
        vitalManagerInstance = (VitalManagerInstance) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        attachObserverInterface.attachObserver(associationApiViewModel);

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                doctorGroupedAssociations = new ArrayList(baseApiResponseModels);

                Log.d("TAG", "onChanged: " + doctorGroupedAssociations.size());
                vitalDeviceListAdapter.setProviderData(doctorGroupedAssociations.get(0).getDoctors());
            }
        });

//        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
//            @Override
//            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
//                doctorPatientListCrv.getSwipeLayout().setRefreshing(false);
//                if (baseApiResponseModel != null) {
//
//                    if (baseApiResponseModel instanceof AssociationApiResponseModel) {
//                        associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
//                    }
//
//
//                    didReceivedResult();
//                }
//            }
//        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(deviceStateChanger);
        }
    }

    private void initView(View baseView) {
        recyclerView = baseView.findViewById(R.id.device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        VitalDeviceListAdapter vitalDeviceListAdapter = new VitalDeviceListAdapter(getActivity(), connectedDevice, unconnectedDevice, measurementType);
        this.vitalDeviceListAdapter = vitalDeviceListAdapter;
        recyclerView.setAdapter(vitalDeviceListAdapter);
        associationApiViewModel.getDoctorGroupedAssociations(true);

        vitalDeviceListAdapter.clickListener = new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                VitalDeviceListAdapter.DataSource dataSource = ((VitalDeviceListAdapter) recyclerView.getAdapter()).getSources().get(position);

                Bundle bundle = null;
                if (measurementType != null) {
                    bundle = new Bundle();
                    bundle.putString(ArgumentKeys.SELECTED_VITAL_TYPE, measurementType);
                }

                switch (dataSource.getDataSubType()) {
                    case VitalDeviceListAdapter.set_up_device:
                        onActionCompleteInterface.onCompletionResult(RequestID.SET_UP_DEVICE, true, bundle);
                        break;
                    case VitalDeviceListAdapter.set_up_telihealth_device:
                        startActivity(new Intent(getActivity(), MyDeviceListActivity.class));
                        break;
                    case VitalDeviceListAdapter.manual_entry_type:

                        if (measurementType != null) {
                            onActionCompleteInterface.onCompletionResult(RequestID.TRIGGER_MANUAL_ENTRY, true, bundle);
                        } else {
                            ArrayList<String> titles = new ArrayList<>();
                            for (String type : SupportedMeasurementType.getItems()) {
                                titles.add(getString(SupportedMeasurementType.getTitle(type)));
                            }
                            ItemPickerDialog pickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_the_type), titles, new PickerListener() {
                                @Override
                                public void didSelectedItem(int position) {
                                    Bundle finalBundle = new Bundle();
                                    finalBundle.putString(ArgumentKeys.SELECTED_VITAL_TYPE, SupportedMeasurementType.getItems().get(position));
                                    onActionCompleteInterface.onCompletionResult(RequestID.TRIGGER_MANUAL_ENTRY, true, finalBundle);
                                }

                                @Override
                                public void didCancelled() {
                                    //nothing
                                }
                            });

                            pickerDialog.show();
                        }

                        break;
                    case VitalDeviceListAdapter.device_type:

                        if (bundle == null) {
                            bundle = new Bundle();
                        }

                        bundle.putString(ArgumentKeys.DEVICE_TYPE, dataSource.getDevice().getType());
                        bundle.putString(ArgumentKeys.DEVICE_MAC, dataSource.getDevice().getDeviceId());
                        bundle.putSerializable(ArgumentKeys.VITAL_DEVICE, dataSource.getDevice());
                        bundle.putBoolean(ArgumentKeys.NEED_TO_TRIGGER_VITAL_AUTOMATICALLY, false);

                        if (dataSource.getDevice().getConnected()) {
                            onActionCompleteInterface.onCompletionResult(RequestID.OPEN_CONNECTED_DEVICE, true, bundle);
                        } else {
                            onActionCompleteInterface.onCompletionResult(RequestID.OPEN_NOT_CONNECTED_DEVICE, true, bundle);
                        }
                        break;
                    case VitalDeviceListAdapter.google_fit_sources:
                        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
                            openGoogleFitSourceActivity();
                        }
                        break;
                    case VitalDeviceListAdapter.none:
                        break;
                }
            }
        };
    }

    public void openGoogleFitSourceActivity() {

    }

    private void generateDataSource() {
        connectedDevice.clear();
        unconnectedDevice.clear();

        VitalPairedDevices vitalPairedDevices = VitalPairedDevices.getAllPairedDevices(appPreference);

        for (VitalDevice device : vitalPairedDevices.getDevices()) {

            String deviceType = VitalDeviceType.shared.getMeasurementType(device.getType());

            if (!TextUtils.isEmpty(measurementType)) {

                if (measurementType.equals(SupportedMeasurementType.heartRate)) {
                    if (deviceType.equals(SupportedMeasurementType.bp) || deviceType.equals(SupportedMeasurementType.pulseOximeter)) {

                        if (vitalManagerInstance.getInstance().isConnected(device.getType(), device.getDeviceId())) {
                            device.setConnected(true);
                            connectedDevice.add(device);
                        } else {
                            device.setConnected(false);
                            unconnectedDevice.add(device);
                        }

                    }
                } else if (deviceType.equals(measurementType)) {

                    if (vitalManagerInstance.getInstance().isConnected(device.getType(), device.getDeviceId())) {
                        device.setConnected(true);
                        connectedDevice.add(device);
                    } else {
                        device.setConnected(false);
                        unconnectedDevice.add(device);
                    }

                }

            } else if (TextUtils.isEmpty(measurementType)) {
                if (vitalManagerInstance.getInstance().isConnected(device.getType(), device.getDeviceId())) {
                    device.setConnected(true);
                    connectedDevice.add(device);
                } else {
                    device.setConnected(false);
                    unconnectedDevice.add(device);
                }
            }
        }

    }

    private BroadcastReceiver deviceStateChanger = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("VitalDeviceList", "device connection changed refresh");
            generateDataSource();
            if (vitalDeviceListAdapter != null)
                vitalDeviceListAdapter.notifyDataSetChanged();
        }
    };
}
