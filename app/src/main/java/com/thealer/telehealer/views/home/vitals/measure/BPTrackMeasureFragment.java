package com.thealer.telehealer.views.home.vitals.measure;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsCreateApiModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.Array.ArrayListFilter;
import com.thealer.telehealer.common.Util.Array.ArrayListMap;
import com.thealer.telehealer.common.Util.Array.ArrayListUtil;
import com.thealer.telehealer.common.Util.Vital.BulkVitalUtil;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.home.vitals.measure.Adapter.TrackBPAdapter;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BPTrackMeasureFragment extends VitalMeasureBaseFragment implements
        View.OnClickListener, BPMeasureInterface {

    private CustomRecyclerView recyclerView;
    private TextView message_tv, title_tv;
    private Button select_all_bt;
    private CustomButton sync_bt;
    private ConstraintLayout main_container;
    private String finalBPValue = "", finalHeartRateValue = "";
    private TextView hint_tv;

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    private boolean isModelLoadInitially, isDataFetched;
    private ArrayList<BPTrack> selectedList = new ArrayList<>();
    private ArrayList<BPTrack> tracks = new ArrayList<>();
    private boolean isSelectAllPressed = false;

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

        if (isPresentedInsideCallActivity()) {
            ArrayList<BPTrack> unselectedItems = new ArrayList<>();
            final ArrayList<String> selectedIds;

            ArrayListUtil<BPTrack,String> mapUtil = new ArrayListUtil<>();
            selectedIds = mapUtil.mapList(selectedList, new ArrayListMap<BPTrack, String>() {
                @Override
                public String transform(BPTrack model) {
                    return model.getDataID();
                }
            });

            ArrayListUtil<BPTrack,BPTrack> util = new ArrayListUtil<>();
            unselectedItems = util.filterList(tracks, new ArrayListFilter<BPTrack>() {
                @Override
                public Boolean needToAddInFilter(BPTrack model) {
                    return !selectedIds.contains(model.getDataID());
                }
            });

            tracks.clear();
            tracks.addAll(unselectedItems);

            if (vitalManagerInstance != null) {
                vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(), vitalDevice.getDeviceId());
            }

            selectedList.clear();

            if (adapter != null) {
                adapter.reload(tracks, selectedList);
                Log.d("BPTrack", "adapter.reload");
            } else {
                recyclerView.getRecyclerView().getAdapter().notifyDataSetChanged();
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
        select_all_bt = baseView.findViewById(R.id.select_all_bt);
        select_all_bt.setOnClickListener(this);

        hint_tv = baseView.findViewById(R.id.hint_tv);
        hint_tv.setText(getString(R.string.sync_track_message));

        sync_bt = baseView.findViewById(R.id.sync_bt);
        sync_bt.setOnClickListener(this);

        if (isNeedToTrigger && !isDataFetched) {
            startMeasure();
        }

        if (UserType.isUserPatient() || !isPresentedInsideCallActivity()) {
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
        view.setClickable(false);
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


                        String userGuid = null;
                        if ((BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_DOCTOR)) && getArguments().getSerializable(Constants.USER_DETAIL) != null) {
                            CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                            userGuid = commonUserApiResponseModel.getUser_guid();
                        }

                        if (selectedList.size() == 1) {
                            Log.d("BPTrack","uploading one");
                            BPTrack track = selectedList.get(0);
                            sendVitals(SupportedMeasurementType.bp, track.getSys() + "/" + track.getDia(), SupportedMeasurementType.heartRate, track.getHeartRate() + "",track.postDateInString());
                        } else if (selectedList.size() > 1) {
                            Log.d("BPTrack","uploading many");
                            BPTrack track = selectedList.get(0);
                            sendVitals(SupportedMeasurementType.bp, track.getSys() + "/" + track.getDia(), SupportedMeasurementType.heartRate, track.getHeartRate() + "",track.postDateInString());
                            Log.d("BPTrack","uploading first");

                            ArrayList<VitalsApiResponseModel> vitalApiRequestModels = new ArrayList<>();
                            ArrayList<BPTrack> tracks = new ArrayList<>(selectedList.subList(1,selectedList.size()));
                            String bundleId = null;
                            for (BPTrack track1 : tracks) {
                                String date = Utils.getStringFromDate(track.getDate(),"yyyy-MM-dd HH:mm:ss.SSS");
                                VitalsApiResponseModel bpRequest = new VitalsApiResponseModel(SupportedMeasurementType.bp, track1.getSys() + "/" + track1.getDia(),null, VitalsConstant.VITAL_MODE_DEVICE,date, bundleId);
                                VitalsApiResponseModel hrRequest = new VitalsApiResponseModel(SupportedMeasurementType.heartRate, track1.getHeartRate() + "", null,VitalsConstant.VITAL_MODE_DEVICE, date, bundleId);
                                vitalApiRequestModels.add(bpRequest);
                                vitalApiRequestModels.add(hrRequest);
                            }
                            Log.d("BPTrack","uploading rest");
                            String finalUserGuid = userGuid;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    BulkVitalUtil.getInstance().uploadAllVitals(1,vitalApiRequestModels, finalUserGuid,null,null);
                                }
                            });

                        }
                    }

                }
                break;
            case R.id.select_all_bt:
                if (isSelectAllPressed) {
                    isSelectAllPressed = false;
                    select_all_bt.setText(getString(R.string.select_all));
                    selectedList.clear();
                } else {
                    isSelectAllPressed = true;
                    select_all_bt.setText(getString(R.string.un_select_all));
                    selectedList.clear();
                    selectedList.addAll(tracks);
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                break;
        }

        view.setClickable(true);
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

            if (tracks.size() > 0) {
                hint_tv.setText(getString(R.string.select_readings));
                select_all_bt.setVisibility(View.VISIBLE);
            } else {
                hint_tv.setText("");
                select_all_bt.setVisibility(View.GONE);
            }

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
