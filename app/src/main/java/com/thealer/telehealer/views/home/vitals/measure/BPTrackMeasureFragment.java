package com.thealer.telehealer.views.home.vitals.measure;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalEvents;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.measure.Adapter.TrackBPAdapter;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BPTrackMeasureFragment extends BaseFragment implements VitalPairInterface,
        View.OnClickListener, BPMeasureInterface, VitalBatteryFetcher, CallVitalEvents {

    @Nullable
    private OnActionCompleteInterface onActionCompleteInterface;
    @Nullable
    private OnViewChangeInterface onViewChangeInterface;
    @Nullable
    private VitalManagerInstance vitalManagerInstance;
    @Nullable
    private ToolBarInterface toolBarInterface;

    @Nullable
    private Action action;

    private VitalDevice vitalDevice;

    private RecyclerView recyclerView;
    private ImageView otherOptionView;
    private TextView message_tv,title_tv;
    private CustomButton sync_bt;
    private ConstraintLayout main_container;

    private VitalsApiViewModel vitalsApiViewModel;

    private Boolean isNeedToTrigger = false;

    private String finalBPValue = "",finalHeartRateValue = "";

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    private boolean isModelLoadInitially,isDataFetched;

    private ArrayList<BPTrack> selectedList = new ArrayList<>();

    @Nullable
    private TrackBPAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);

        if (getArguments() != null) {
            vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);
            isNeedToTrigger = getArguments().getBoolean(ArgumentKeys.NEED_TO_TRIGGER_VITAL_AUTOMATICALLY);
        }

        if (savedInstanceState != null) {
            isModelLoadInitially = savedInstanceState.getBoolean(ArgumentKeys.IS_MODEL_LOAD_INITIALLY);
            isDataFetched = savedInstanceState.getBoolean(ArgumentKeys.IS_DATA_FETCHED);
        } else {
            isModelLoadInitially = false;
            isDataFetched = false;
        }

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(vitalsApiViewModel);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bp_track, container, false);

        initView(view);
        updateSyncButtonTitle();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstnace) {
        super.onSaveInstanceState(saveInstnace);

        saveInstnace.putBoolean(ArgumentKeys.IS_MODEL_LOAD_INITIALLY,isModelLoadInitially);
        saveInstnace.putBoolean(ArgumentKeys.IS_DATA_FETCHED,isDataFetched);
    }

    @Override
    public void onResume() {
        super.onResume();

        assignVitalListener();

        if (toolBarInterface != null)
            toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));

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
            otherOptionView.setOnClickListener(this);

            otherOptionView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            otherOptionView.setImageResource(R.drawable.info);
        }

        connectDeviceIfNeedeed();

        if (vitalManagerInstance != null) {
            vitalManagerInstance.updateBatteryView(View.GONE, 0);
            vitalManagerInstance.getInstance().setBatteryFetcherListener(this);
        }
        fetchBattery();
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

    private void initView(View baseView) {
        message_tv = baseView.findViewById(R.id.message_tv);
        main_container = baseView.findViewById(R.id.main_container);
        title_tv = baseView.findViewById(R.id.title_tv);
        recyclerView = baseView.findViewById(R.id.recyclerView);

        sync_bt = baseView.findViewById(R.id.sync_bt);
        sync_bt.setOnClickListener(this);

        if (isNeedToTrigger && !isDataFetched) {
            startMeasure();
        }

        if (isPresentedInsideCallActivity()) {
            title_tv.setVisibility(View.VISIBLE);
            title_tv.setText(VitalDeviceType.shared.getTitle(vitalDevice.getType()));
            main_container.setBackgroundColor(getResources().getColor(R.color.colorWhiteWithLessAlpha));
        }


        if (action != null) {
            action.doItNow();
            action = null;
        }

    }

    private void updateSyncButtonTitle() {
        if (isDataFetched) {
            sync_bt.setText(getResources().getString(R.string.UPLOAD));
        } else {
            sync_bt.setText(getResources().getString(R.string.SYNC));
        }
    }

    private void startMeasure() {
        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().startMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    private Boolean isPresentedInsideCallActivity() {
        return getActivity() instanceof CallActivity;
    }


    private void connectDeviceIfNeedeed() {
        if (vitalManagerInstance != null && !vitalManagerInstance.getInstance().isConnected(vitalDevice.getType(),vitalDevice.getDeviceId())) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }
    }

    private void fetchBattery() {
        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().fetchBattery(vitalDevice.getType(),vitalDevice.getDeviceId());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.other_option:
                if (onActionCompleteInterface != null)
                    onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_INFO,true,getArguments());
                break;
            case R.id.sync_bt:
                if (!isDataFetched) {
                    startMeasure();
                } else {

                    selectedList.clear();

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    if (vitalManagerInstance != null) {
                        for (BPTrack track : selectedList) {
                            vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.bp, track.getSys()+"/"+track.getDia(), vitalsApiViewModel);
                            vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.heartRate, track.getHeartRate()+"", vitalsApiViewModel);
                        }
                    }

                    if (isPresentedInsideCallActivity()) {
                        if (vitalManagerInstance != null) {
                            vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());

                            if (callVitalPagerInterFace != null) {
                                callVitalPagerInterFace.closeVitalController();
                            }
                        }
                    } else {
                        getActivity().finish();
                    }

                }
                break;
        }
    }

    //BPMeasureInterface methods
    @Override
    public void updateBPMessage(String deviceType,String message) {

    }

    @Override
    public void didStartBPMesure(String deviceType) {

    }

    @Override
    public void didUpdateBPMesure(String deviceType,ArrayList<Double> value) {

    }

    @Override
    public void didUpdateBPM(String deviceType,ArrayList<Double> value) {

    }

    @Override
    public void didFinishBPMesure(String deviceType,Double systolicValue, Double diastolicValue, Double heartRate) {

    }

    @Override
    public void didFailBPMesure(String deviceType,String error) {
        message_tv.setText(error);
    }

    @Override
    public void didFinishBpMeasure(Object object) {

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.updateState(Constants.idle);

        isModelLoadInitially = true;

        ArrayList<BPTrack> tracks = (ArrayList<BPTrack>)object;
        if (tracks != null) {
            if (tracks.size() > 0) {
                isDataFetched = true;
            } else {
                isDataFetched = false;
            }

            TrackBPAdapter adapter = new TrackBPAdapter(getActivity(),tracks,selectedList);
            this.adapter = adapter;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }

        updateSyncButtonTitle();
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
        message_tv.setText(getString(R.string.connecting));
    }

    //VitalPairInterface methods
    @Override
    public void didDiscoverDevice(String type, String serailNumber) {
        //nothing to do
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        Log.e("bp measure", "state changed "+serailNumber);
        fetchBattery();
        startMeasure();
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {
        if (type.equals(vitalDevice.getType()) && serailNumber.equals(vitalDevice.getDeviceId())) {
            if (!isPresentedInsideCallActivity()) {
                showAlertDialog(getActivity(), getString(R.string.error), message_tv.getText().toString(), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }, null);
            }
        }
    }

    @Override
    public void didFailConnectDevice(String type, String serailNumber, String errorMessage) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        }

        showAlertDialog(getActivity(), getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, null);
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

    @Override
    public void notConnected(String deviceType, String deviceMac) {
        connectDeviceIfNeedeed();
    }


    //Call Events methods
    @Override
    public void didReceiveData(String data) {
        Log.d("BPMeasureFragment","received data");

        if (message_tv == null) {
            action = new Action() {
                @Override
                public void doItNow() {
                    processSignalMessagesForBP(data);
                }
            };
        } else {
            processSignalMessagesForBP(data);
        }
    }
    @Override
    public void assignVitalListener() {
        if (vitalManagerInstance != null) {
            vitalManagerInstance.getInstance().setListener(this);
            vitalManagerInstance.getInstance().setBPListener(this);
        }
    }

    @Override
    public String getVitalDeviceType() {
        return vitalDevice.getType();
    }

    private void processSignalMessagesForBP(String data) {
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();

        try {
            HashMap<String, Object> map = Utils.deserialize(data, type);

            switch ((String) map.get(VitalsConstant.VitalCallMapKeys.status)) {
                case VitalsConstant.VitalCallMapKeys.startedToMeasure:
                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didFailBPMesure(vitalDevice.getType(),errorMessage);

                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:

                    ArrayList<HashMap<String,String>> items =  ( ArrayList<HashMap<String,String>>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    ArrayList<BPTrack> tracks = new ArrayList<>();

                    for (HashMap<String,String> item : items) {
                        tracks.add(new BPTrack(item));
                    }

                    didFinishBpMeasure(tracks);

                    break;
            }

            openInDetail();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openInDetail() {
        didStartBPMesure(vitalDevice.getType());

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.didInitiateMeasure(vitalDevice.getType());

    }

    @Override
    public void assignVitalDevice(VitalDevice vitalDevice) {
        this.vitalDevice = vitalDevice;
    }
}
