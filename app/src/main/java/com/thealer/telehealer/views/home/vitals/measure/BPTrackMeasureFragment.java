package com.thealer.telehealer.views.home.vitals.measure;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsCreateApiModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.CustomRecyclerView;
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
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalEvents;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.VitalsSendBaseFragment;
import com.thealer.telehealer.views.home.vitals.measure.Adapter.TrackBPAdapter;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BPTrackMeasureFragment extends VitalMeasureBaseFragment implements
        View.OnClickListener, BPMeasureInterface {

    private CustomRecyclerView recyclerView;
    private TextView message_tv, title_tv;
    private CustomButton sync_bt;
    private ConstraintLayout main_container;
    private String finalBPValue = "", finalHeartRateValue = "";

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    private boolean isModelLoadInitially, isDataFetched;
    private ArrayList<BPTrack> selectedList = new ArrayList<>();
    private ArrayList<BPTrack> tracks = new ArrayList<>();

    @Nullable
    private TrackBPAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            isModelLoadInitially = savedInstanceState.getBoolean(ArgumentKeys.IS_MODEL_LOAD_INITIALLY);
            isDataFetched = savedInstanceState.getBoolean(ArgumentKeys.IS_DATA_FETCHED);
        } else {
            isModelLoadInitially = false;
            isDataFetched = false;
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

        saveInstnace.putBoolean(ArgumentKeys.IS_MODEL_LOAD_INITIALLY, isModelLoadInitially);
        saveInstnace.putBoolean(ArgumentKeys.IS_DATA_FETCHED, isDataFetched);
    }

    @Override
    public void didFinishPost() {
        Log.d("BPTrack","didFinishPost");
        ArrayList<BPTrack> unselectedItems = new ArrayList<>();
        ArrayList<String> selectedIds = new ArrayList<>();

        for (BPTrack bpTrack : selectedList) {
            selectedIds.add(bpTrack.getDataID());
        }

        for (BPTrack bpTrack : tracks) {
            if (!selectedIds.contains(bpTrack.getDataID())) {
                unselectedItems.add(bpTrack);
            }
        }

        tracks.clear();
        tracks.addAll(unselectedItems);

        if (isPresentedInsideCallActivity()) {
            if (vitalManagerInstance != null) {
                vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(), vitalDevice.getDeviceId());
            }

            selectedList.clear();

            if (adapter != null) {
                adapter.reload(tracks, selectedList);
                Log.d("BPTrack", "adapter.reload");
            } else {
                Log.d("BPTrack", "adapter null");
            }

            if (tracks.size() == 0) {
                isDataFetched = false;
                updateSyncButtonTitle();
                setCurrentState(MeasureState.notStarted);
            }

            if (callVitalPagerInterFace != null) {
                callVitalPagerInterFace.closeVitalController();
            }

        } else {
            Intent data = new Intent();
            data.putExtra(ArgumentKeys.MEASUREMENT_TYPE,VitalDeviceType.shared.getMeasurementType(vitalDevice.getType()));
            getActivity().setResult(Activity.RESULT_OK,data);
            getActivity().finish();
        }
    }

    private void initView(View baseView) {
        message_tv = baseView.findViewById(R.id.message_tv);
        main_container = baseView.findViewById(R.id.main_container);
        title_tv = baseView.findViewById(R.id.title_tv);
        recyclerView = baseView.findViewById(R.id.rv);

        sync_bt = baseView.findViewById(R.id.sync_bt);
        sync_bt.setOnClickListener(this);

        if (isNeedToTrigger && !isDataFetched) {
            startMeasure();
        }

        if (UserType.isUserPatient()) {
            sync_bt.setVisibility(View.VISIBLE);
        } else {
            sync_bt.setVisibility(View.GONE);
        }

        if (isPresentedInsideCallActivity()) {
            title_tv.setVisibility(View.VISIBLE);
            String title = getString(VitalDeviceType.shared.getTitle(vitalDevice.getType()));
            if (!TextUtils.isEmpty(vitalDevice.getDeviceId())) {
                title += " ("+vitalDevice.getDeviceId()+")";
            }
            title_tv.setText(title);
            main_container.setBackgroundColor(getResources().getColor(R.color.colorWhiteWithLessAlpha));
        }


        if (action != null) {
            action.doItNow();
            action = null;
        }

        recyclerView.setScrollable(false);
    }

    private void updateSyncButtonTitle() {
        if (isDataFetched) {
            sync_bt.setText(getResources().getString(R.string.UPLOAD));
        } else {
            sync_bt.setText(getResources().getString(R.string.SYNC));
        }
    }

    @Override
    public boolean needToAddViewModelObserver() {
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_bt:
                if (!isDataFetched) {
                    startMeasure();
                } else {

                    if (vitalManagerInstance != null) {

                        Collections.sort(selectedList, new Comparator<BPTrack>() {
                            public int compare(BPTrack obj1, BPTrack obj2) {
                                return obj2.getDate().compareTo(obj1.getDate());
                            }
                        });


                        for (int i = 0; i < selectedList.size(); i++) {

                            BPTrack track = selectedList.get(i);

                            if (i == 0) {
                                sendVitals(SupportedMeasurementType.bp, track.getSys() + "/" + track.getDia(), SupportedMeasurementType.heartRate, track.getHeartRate() + "");
                            } else {
                                VitalsCreateApiModel vitalsCreateApiModel = new VitalsCreateApiModel(TeleHealerApplication.application);

                                CreateVitalApiRequestModel bpRequest = new CreateVitalApiRequestModel(SupportedMeasurementType.bp, track.getSys() + "/" + track.getDia(), VitalsConstant.VITAL_MODE_DEVICE, null, null);
                                vitalsCreateApiModel.createVital(bpRequest, null);

                                CreateVitalApiRequestModel hrRequest = new CreateVitalApiRequestModel(SupportedMeasurementType.heartRate, track.getHeartRate() + "", VitalsConstant.VITAL_MODE_DEVICE, null, null);
                                vitalsCreateApiModel.createVital(hrRequest, null);
                            }
                        }
                    }

                }
                break;
        }
    }


    //BPMeasureInterface methods
    @Override
    public void updateBPMessage(String deviceType, String message) {

    }

    @Override
    public void didStartBPMesure(String deviceType) {

    }

    @Override
    public void didUpdateBPMesure(String deviceType, ArrayList<Double> value) {

    }

    @Override
    public void didUpdateBPM(String deviceType, ArrayList<Double> value) {

    }

    @Override
    public void didFinishBPMesure(String deviceType, Double systolicValue, Double diastolicValue, Double heartRate) {

    }

    @Override
    public void didFailBPMesure(String deviceType, String error) {
        message_tv.setText(error);
    }

    @Override
    public void didFinishBpMeasure(Object object) {

        Log.d("BPTrackMeasureFragment", "didFinishBpMeasure");

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.updateState(Constants.idle);

        isModelLoadInitially = true;

        ArrayList<BPTrack> tracks = (ArrayList<BPTrack>) object;
        if (tracks != null) {
            ArrayList<BPTrack> modifedTracks = new ArrayList<>();
            if (tracks.size() > 0) {
                this.tracks = tracks;
                modifedTracks = tracks;
            } else {
                modifedTracks = this.tracks;
            }

            isDataFetched = modifedTracks.size() > 0;

            this.adapter = new TrackBPAdapter(getActivity(), modifedTracks, selectedList,isPresentedInsideCallActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.getRecyclerView().setAdapter(adapter);
            recyclerView.setEmptyState(EmptyViewConstants.EMPTY_BP_TRACK_VALUE);
            recyclerView.updateView();

            Log.d("BPTrackMeasureFragment", "tracks not null");
        } else {
            Log.d("BPTrackMeasureFragment", "tracks null");
        }

        updateSyncButtonTitle();
    }

    @Override
    public void startedToConnect(String type, String serailNumber) {
        message_tv.setText(getString(R.string.connecting));
    }

    //VitalPairInterface methods
    @Override
    public void didConnected(String type, String serailNumber) {
        Log.e("bptrack measure", "state changed " + serailNumber);
        fetchBattery();
        startMeasure();
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {
        /*if (type.equals(vitalDevice.getType()) && serailNumber.equals(vitalDevice.getDeviceId())) {
            if (!isPresentedInsideCallActivity()) {
                Utils.showAlertDialog(getActivity(), getString(R.string.error), getResources().getString(R.string.device_disconnected_message), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
            }
        }*/
    }

    //Call Events methods
    @Override
    public void didReceiveData(String data) {
        Log.d("BPMeasureFragment", "received data");

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
            Log.d("BPTrackMeasureFragment","assignVitalListener");
            vitalManagerInstance.getInstance().setListener(this);
            vitalManagerInstance.getInstance().setBPListener(this);
            needToAssignIHealthListener = false;
        } else {
            needToAssignIHealthListener = true;
        }
    }

    private void processSignalMessagesForBP(String data) {
        Log.d("BPTrackMeasureFragment","processSignalMessagesForBP");
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();

        try {
            HashMap<String, Object> map = Utils.deserialize(data, type);
            Log.d("BPTrackMeasureFragment","processSignalMessagesForBP "+map.toString());

            switch ((String) map.get(VitalsConstant.VitalCallMapKeys.status)) {
                case VitalsConstant.VitalCallMapKeys.startedToMeasure:
                    if (currentState != MeasureState.started) {
                        setCurrentState(MeasureState.started);
                    }
                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didFailBPMesure(vitalDevice.getType(), errorMessage);

                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:

                    ArrayList<LinkedTreeMap<String, String>> items = (ArrayList<LinkedTreeMap<String, String>>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    ArrayList<BPTrack> tracks = new ArrayList<>();

                    for (LinkedTreeMap<String, String> item : items) {
                        tracks.add(new BPTrack(item));
                    }

                    didFinishBpMeasure(tracks);

                    break;
            }

            openInDetail();

        } catch (Exception e) {
            Log.d("BPTrackMeasureFragment","processSignalMessagesForBP "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void openInDetail() {
        didStartBPMesure(vitalDevice.getType());

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.didInitiateMeasure(vitalDevice.getType());

    }
}
