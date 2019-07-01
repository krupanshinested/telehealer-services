package com.thealer.telehealer.views.home.vitals.measure;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalEvents;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.VitalsSendBaseFragment;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

public class VitalMeasureBaseFragment extends VitalsSendBaseFragment implements VitalPairInterface,
       VitalBatteryFetcher, CallVitalEvents  {

    @Nullable
    protected OnActionCompleteInterface onActionCompleteInterface;
    @Nullable
    protected OnViewChangeInterface onViewChangeInterface;
    @Nullable
    protected VitalManagerInstance vitalManagerInstance;
    @Nullable
    protected ToolBarInterface toolBarInterface;

    @Nullable
    protected Action action;

    protected VitalDevice vitalDevice;

    protected int currentState;
    protected Boolean isNeedToTrigger = false;
    protected Boolean isOpeningDirectlyFromPairing = false;
    protected ImageView otherOptionView;

    private Boolean startDeviceAfterSuccessfullConnect = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);
            isNeedToTrigger = getArguments().getBoolean(ArgumentKeys.NEED_TO_TRIGGER_VITAL_AUTOMATICALLY);
            isOpeningDirectlyFromPairing = getArguments().getBoolean(ArgumentKeys.IS_OPENING_DIRECTLY_FROM_PAIRING);
        }

        if (savedInstanceState != null) {
            currentState = savedInstanceState.getInt(ArgumentKeys.CURRENT_VITAL_STATE, MeasureState.notStarted);
        } else {
            currentState = MeasureState.notStarted;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstnace) {
        super.onSaveInstanceState(saveInstnace);

        saveInstnace.putInt(ArgumentKeys.CURRENT_VITAL_STATE,currentState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("BPMeasureFragment","onResume");

        if (!isPresentedInsideCallActivity() || needToAssignIHealthListener) {
            assignVitalListener();
        }

        if (toolBarInterface != null) {
            toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));
        }

        if (onViewChangeInterface != null) {
            onViewChangeInterface.hideOrShowClose(false);
            onViewChangeInterface.hideOrShowBackIv(true);
        }

        if (toolBarInterface != null) {
            otherOptionView = toolBarInterface.getExtraOption();
            toolBarInterface.updateSubTitle("",View.GONE);
        }

        if (otherOptionView != null) {
            otherOptionView.setVisibility(View.VISIBLE);

            otherOptionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionCompleteInterface != null)
                        onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_INFO,true,getArguments());
                }
            });

            otherOptionView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            otherOptionView.setImageResource(R.drawable.info);
        }

        if (isNeedToTrigger) {
            connectDeviceIfNeedeed();
        }

        if (vitalManagerInstance != null) {
            vitalManagerInstance.updateBatteryView(View.GONE, 0);
            vitalManagerInstance.getInstance().setBatteryFetcherListener(this);
        }
        fetchBattery();
    }

    @Override
    public void didFinishPost() {
        Log.d("VitalMeasureBase","didFinishPost");
        if (isPresentedInsideCallActivity()) {

        } else {
            Intent data = new Intent();
            data.putExtra(ArgumentKeys.MEASUREMENT_TYPE,VitalDeviceType.shared.getMeasurementType(vitalDevice.getType()));
            getActivity().setResult(Activity.RESULT_OK,data);
            getActivity().finish();
        }
    }

    protected void connectDeviceIfNeedeed() {
        if (vitalManagerInstance != null && !vitalManagerInstance.getInstance().isConnected(vitalDevice.getType(),vitalDevice.getDeviceId())) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }
    }

    protected void fetchBattery() {
        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().fetchBattery(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    protected void startMeasure() {
        if (vitalManagerInstance != null) {
            if (vitalManagerInstance.getInstance().startMeasure(vitalDevice.getType(), vitalDevice.getDeviceId())) {
                setCurrentState(MeasureState.started);
                startDeviceAfterSuccessfullConnect = false;
            } else {
                startDeviceAfterSuccessfullConnect = true;
            }
        }
    }

    protected void setCurrentState(int state) {
        currentState = state;
        updateView(currentState);
    }

    protected void updateView(int currentState) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof OnViewChangeInterface) {
            onViewChangeInterface = (OnViewChangeInterface) getActivity();
        }


        if (getActivity() instanceof OnActionCompleteInterface) {
            onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        }

        if (getActivity() instanceof ToolBarInterface) {
            toolBarInterface = (ToolBarInterface) getActivity();
        }

        if (getActivity() instanceof VitalManagerInstance) {
            vitalManagerInstance = (VitalManagerInstance) getActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().reset(this);
    }


    //VitalBatteryFetcher methods
    @Override
    public void updateBatteryDetails(BatteryResult batteryResult) {
        if (vitalManagerInstance != null) {
            if (batteryResult.getBattery() != -1) {
                vitalManagerInstance.updateBatteryView(View.VISIBLE, batteryResult.getBattery());
            } else {
                vitalManagerInstance.updateBatteryView(View.GONE, 0);
            }
        }
    }

    //VitalPairInterface methods
    @Override
    public void notConnected(String deviceType, String deviceMac) {
        connectDeviceIfNeedeed();
    }

    @Override
    public void didScanFinish() {
        //nothing to do
    }

    @Override
    public void didScanFailed(String error) {
        //nothing to do
    }

    @Override
    public void startedToConnect(String type, String serailNumber) {
        //nothing to do
    }

    @Override
    public void didDiscoverDevice(String type, String serailNumber) {

    }

    @Override
    public void didConnected(String type, String serailNumber) {
        fetchBattery();
        if (vitalDevice != null && vitalDevice.getDeviceId().equals(serailNumber) && startDeviceAfterSuccessfullConnect) {
            startDeviceAfterSuccessfullConnect = false;
            startMeasure();
        }
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {
        if (type.equals(vitalDevice.getType()) && serailNumber.equals(vitalDevice.getDeviceId())) {
            if (!isPresentedInsideCallActivity()) {
                if (currentState == MeasureState.failed) {
                    Utils.showAlertDialog(getActivity(), getString(R.string.error),getResources().getString(R.string.device_disconnected_message), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getActivity() != null) {
                                if (isOpeningDirectlyFromPairing) {
                                    getActivity().finish();
                                } else {
                                    getActivity().onBackPressed();
                                }
                            }
                        }
                    }, null);
                } else {
                    if (getActivity() != null) {
                        if (isOpeningDirectlyFromPairing) {
                            getActivity().finish();
                        } else {
                            getActivity().onBackPressed();
                        }
                    }
                }
            } else {
                setCurrentState(MeasureState.notStarted);
            }
        }
    }

    @Override
    public void didFailConnectDevice(String type, String serailNumber, String errorMessage) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        }

        Utils.showAlertDialog(getActivity(), getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, null);
    }

    //CallVitalEvents methods
    @Override
    public void didReceiveData(String data) {

    }

    @Override
    public void assignVitalListener() {

    }

    @Override
    public void assignVitalDevice(VitalDevice vitalDevice) {
        this.vitalDevice = vitalDevice;
    }

    @Override
    public String getVitalDeviceType() {
        return vitalDevice.getType();
    }
}
