package com.thealer.telehealer.views.call.Adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.call.Interfaces.CallVitalEvents;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.call.Interfaces.LiveVitalCallBack;
import com.thealer.telehealer.views.common.imagePreview.ImagePreviewDialogFragment;
import com.thealer.telehealer.views.common.imagePreview.ImagePreviewViewModel;
import com.thealer.telehealer.views.home.vitals.measure.BPMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.PulseMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.ThermoMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.WeightMeasureFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 1/24/19.
 */

public class VitalCallAdapter extends FragmentStatePagerAdapter implements CallVitalPagerInterFace {

    private SparseArray<CallVitalEvents> deviceScreens = new SparseArray<CallVitalEvents>();
    private HashMap<String,Integer> deviceScreenMap = new HashMap<>();

    private LiveVitalCallBack liveVitalCallBack;

    public VitalCallAdapter(FragmentManager manager,ArrayList<VitalDevice> devices,LiveVitalCallBack liveVitalCallBack) {
        super(manager);
        assignFragments(devices);
        this.liveVitalCallBack = liveVitalCallBack;
    }

    @Override
    public Fragment getItem(int i) {
        Log.d("VitalCallAdapter","getItem "+i);
        if (deviceScreens.get(i) != null) {
            return (Fragment) deviceScreens.get(i);
        } else {
            return new Fragment();
        }

    }

    @Override
    public int getItemPosition(Object object) {
        Log.d("VitalCallAdapter","getItemPosition");
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
        //return object.equals(this.removeFragment) ? FragmentStatePagerAdapter.POSITION_NONE : this.fragments.indexOf(object);
    }

    @Override
    public int getCount() {
        liveVitalCallBack.didChangedNumberOfScreens(deviceScreens.size());
        return deviceScreens.size();
    }

    @Override
    public void didInitiateMeasure(String type) {
        Log.d("VitalCallAdapter","didInitiateMeasure");
        liveVitalCallBack.didInitiateMeasure(type);
    }

    @Override
    public void closeVitalController() {
        liveVitalCallBack.closeVitalController();
    }

    @Override
    public void updateState(int state) {
        liveVitalCallBack.didChangeStreamingState(state);
    }

    public void didReceivedVitalData(@NonNull String type,@NonNull String message) {
        Log.d("VitalCallAdapter","didReceiveVitalData "+type);
        String deviceType = VitalDeviceType.shared.getVitalType(type.replaceAll("_"," "));
        Integer devicePosition = deviceScreenMap.get(deviceType);
        if (devicePosition != null) {
            CallVitalEvents callVitalEvents = deviceScreens.get(devicePosition);
            if (callVitalEvents != null) {
                callVitalEvents.didReceiveData(message);
            } else {
                openVitalMeasurementForNonPatient(deviceType,message);
            }
        } else {
            openVitalMeasurementForNonPatient(deviceType,message);
        }
    }

    public void didScrolled(int currentPosition) {
        if (deviceScreens.get(currentPosition) != null) {
            deviceScreens.get(currentPosition).assignVitalListener();
        }
    }

    private void openVitalMeasurementForNonPatient(@NonNull String type,@NonNull String message) {
        if (!UserType.isUserPatient()) {

            ArrayList<VitalDevice> devices = new ArrayList<>();
            devices.add(new VitalDevice(type));
            assignFragments(devices);

            CallVitalEvents callVitalEvents = deviceScreens.get(0);
            if (callVitalEvents != null) {
                callVitalEvents.didReceiveData(message);
            }

            notifyDataSetChanged();
        }
    }

    private void assignFragments(ArrayList<VitalDevice> devices) {
        deviceScreens.clear();
        deviceScreenMap.clear();

        for (int i = 0;i< devices.size();i++) {
            VitalDevice device = devices.get(i);
            CallVitalEvents callVitalEvents = getVitalScreen(device);
            deviceScreens.append(i,callVitalEvents);
            deviceScreenMap.put(device.getType(),i);
        }
    }

    @Nullable
    private CallVitalEvents getVitalScreen(VitalDevice vitalDevice) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ArgumentKeys.VITAL_DEVICE,vitalDevice);

        @Nullable
        CallVitalEvents callVitalEvents;

        switch (VitalDeviceType.shared.getMeasurementType(vitalDevice.getType())) {
            case SupportedMeasurementType.bp:
                BPMeasureFragment bpMeasureFragment = new BPMeasureFragment();
                bpMeasureFragment.setArguments(bundle);
                bpMeasureFragment.callVitalPagerInterFace = this;

                callVitalEvents = bpMeasureFragment;
                break;
            case SupportedMeasurementType.pulseOximeter:
                PulseMeasureFragment pulseMeasureFragment = new PulseMeasureFragment();
                pulseMeasureFragment.setArguments(bundle);
                pulseMeasureFragment.callVitalPagerInterFace = this;

                callVitalEvents = pulseMeasureFragment;
                break;
            case SupportedMeasurementType.weight:
                WeightMeasureFragment weightMeasureFragment = new WeightMeasureFragment();
                weightMeasureFragment.setArguments(bundle);
                weightMeasureFragment.callVitalPagerInterFace = this;

                callVitalEvents = weightMeasureFragment;
                break;
            case SupportedMeasurementType.temperature:
                ThermoMeasureFragment thermoMeasureFragment = new ThermoMeasureFragment();
                thermoMeasureFragment.setArguments(bundle);
                thermoMeasureFragment.callVitalPagerInterFace = this;

                callVitalEvents = thermoMeasureFragment;
                break;
            default:
                callVitalEvents = null;
        }

        if (callVitalEvents != null) {
            callVitalEvents.assignVitalDevice(vitalDevice);
        }

        return callVitalEvents;
    }

}
