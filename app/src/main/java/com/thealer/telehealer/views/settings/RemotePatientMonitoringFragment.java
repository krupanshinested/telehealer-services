package com.thealer.telehealer.views.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalErrorThreshold;
import com.thealer.telehealer.views.settings.Adapters.VitalThresholdAdapter;
import com.thealer.telehealer.apilayer.models.vitals.VitalThresholdModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.settings.cellView.SettingsCellView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RemotePatientMonitoringFragment extends BaseFragment {

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView editTv;
    private Button saveBtn;
    private VitalsApiViewModel vitalsApiViewModel;
    private VitalThresholdModel.Result result;
    private VitalThresholdAdapter vitalThresholdAdapter;
    private RecyclerView vitalsThresholdRv;
    private SettingsCellView notificationCellView,rpmCellView;
    List<VitalThresholdModel.VitalsThreshold> vitalThresholdList = new ArrayList<>();
    private  boolean isEditable = false;
    public  static  List<VitalErrorThreshold> errorPos=new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();

        vitalsApiViewModel = new ViewModelProvider(this).get(VitalsApiViewModel.class);
        attachObserverInterface.attachObserver(vitalsApiViewModel);

        vitalsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof VitalThresholdModel) {
                        VitalThresholdModel vitalThresholdModel = (VitalThresholdModel) baseApiResponseModel;
                        result = vitalThresholdModel.getResult();
                        if (result != null) {
                            setUpData();
                        }
                    }else{
                        saveBtn.setVisibility(View.GONE);
                        editTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        vitalsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                String json = errorModel.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    if (jsonObject.has("display_button") && !errorModel.isDisplayButton()) {
                        String errMsg = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                        Utils.showAlertDialog(getActivity(), getString(R.string.app_name), errMsg,
                                getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, null);
                    }else if (!jsonObject.has("is_cc_captured") && !jsonObject.has("is_default_card_valid")) {
                        String message = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                        Utils.showAlertDialog(getActivity(), getString(R.string.app_name), message,
                                getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, null);
                        editTv.setVisibility(View.GONE);
                    }else{
                        Utils.showAlertDialog(getContext(), getString(R.string.app_name),
                                errorModel.getMessage() != null && !errorModel.getMessage().isEmpty() ? errorModel.getMessage() : getString(R.string.failed_to_connect),
                                null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setUpData() {
        initAdapter();
        if(result!=null && result.vitals_thresholds!=null) {
            vitalThresholdList = result.vitals_thresholds;
            vitalThresholdAdapter.UpdateItem(vitalThresholdList,isEditable);
            notificationCellView.updateSwitch(result.is_notify_on_capture!=null?result.is_notify_on_capture:false);
            rpmCellView.updateSwitch(result.is_rpm_enabled!=null?result.is_rpm_enabled:false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_patient_monitoring, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        editTv = (TextView) view.findViewById(R.id.next_tv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        vitalsThresholdRv =  view.findViewById(R.id.vitals_threshold_rv);
        rpmCellView = view.findViewById(R.id.remote_patient_monitoring_cell_view);
        notificationCellView =view.findViewById(R.id.notification_cell_view);
        saveBtn = (Button) view.findViewById(R.id.save_btn);
        saveBtn.setVisibility(View.GONE);
        editTv.setText(getString(R.string.edit));
        editTv.setVisibility(View.VISIBLE);
        toolbarTitle.setText(R.string.vital_chart);
        vitalsApiViewModel.getVitalThreshold(true);
        rpmCellView.updateTextviewPadding(20,20,25,20);
        notificationCellView.updateTextviewPadding(20,20,20,20);
        notificationCellView.setRightDrawableIcon(R.drawable.ic_baseline_info_24);
        rpmCellView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditable) {
                    rpmCellView.toggleSwitch();
                    result.is_rpm_enabled=rpmCellView.getSwitchStatus();
                }
            }
        });
        notificationCellView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditable) {
                    notificationCellView.toggleSwitch();
                    result.is_notify_on_capture = notificationCellView.getSwitchStatus();
                } else{
                    Utils.showAlertDialog(getActivity(), getString(R.string.notifications_alert), getString(R.string.str_notification_threshold_msg),
                            getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null);
                }
            }
        });


        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditable = true;
                setUpData();
                saveBtn.setVisibility(View.VISIBLE);
                editTv.setVisibility(View.GONE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActivity());
                if(errorPos.size() ==0) {
                    isEditable = false;
                    setUpData();
                    result.vitals_thresholds = vitalThresholdList;
                    vitalsApiViewModel.updateVitalThreshold(result);
                }else{
                    showToast(getString(R.string.please_fill_up_details));
                }
            }
        });


        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        initAdapter();

    }

    private void initAdapter() {
        if (vitalThresholdAdapter == null) {
            vitalThresholdAdapter = new VitalThresholdAdapter(getActivity(), vitalThresholdList, new OnListItemSelectInterface() {
                @Override
                public void onListItemSelected(int position, Bundle bundle) {
                    boolean isRangeVisible=vitalThresholdList.get(position).isRangeVisible();
                    vitalThresholdList.get(position).setRangeVisible(!isRangeVisible);
                    vitalThresholdAdapter.notifyDataSetChanged();
                }
            });

            vitalsThresholdRv.setAdapter(vitalThresholdAdapter);
            vitalsThresholdRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }



}