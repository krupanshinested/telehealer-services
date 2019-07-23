package com.thealer.telehealer.views.home.vitals.iHealth.pairing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.SupportInformation;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by rsekar on 11/29/18.
 */

public class VitalConnectingFragment extends BaseFragment implements VitalPairInterface, View.OnClickListener {

    private TextView statusTv, chargeTv, reconnectingTv;
    private ImageView deviceImage;
    private CustomButton startButton, statusView;
    private ImageView otherOptionView;
    private LinearLayout suggestionsLay;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private VitalManagerInstance vitalManagerInstance;
    private ToolBarInterface toolBarInterface;

    private VitalDevice vitalDevice;
    private int currentState = connecting;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);

        if (saveInstance != null) {
            currentState = saveInstance.getInt(ArgumentKeys.CURRENT_VITAL_STATE, connecting);
        } else {
            currentState = connecting;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_connecting, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateView(currentState);
        vitalManagerInstance.updateBatteryView(View.GONE, 0);

        vitalManagerInstance.getInstance().setListener(this);

        toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));

        onViewChangeInterface.hideOrShowClose(false);
        onViewChangeInterface.hideOrShowBackIv(true);

        otherOptionView = toolBarInterface.getExtraOption();
        if (otherOptionView != null) {
            otherOptionView.setVisibility(View.VISIBLE);

            otherOptionView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            otherOptionView.setClickable(true);
            otherOptionView.setOnClickListener(this);
            otherOptionView.setImageResource(R.drawable.info);
        }

        SupportInformation supportInformation = VitalDeviceType.getConnectInfo(vitalDevice.getType());

        if (supportInformation != null && !getArguments().getBoolean("isDisplaySupportDialog") && !vitalManagerInstance.getInstance().isConnected(vitalDevice.getType(), vitalDevice.getDeviceId())) {
            getArguments().putBoolean("isDisplaySupportDialog", true);

            Intent contentIntent = new Intent(getActivity(), ContentActivity.class);
            contentIntent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok));
            contentIntent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);

            if (supportInformation.getTitleId() != 0) {
                contentIntent.putExtra(ArgumentKeys.TITLE, getString(supportInformation.getTitleId()));
            } else {
                contentIntent.putExtra(ArgumentKeys.TITLE, "");
            }

            contentIntent.putExtra(ArgumentKeys.DESCRIPTION, getString(supportInformation.getDescriptionId()));
            contentIntent.putExtra(ArgumentKeys.RESOURCE_ICON, supportInformation.getIconId());
            contentIntent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);
            contentIntent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false);
            contentIntent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, true);

            startActivity(contentIntent);

        } else {
            connectVital();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        vitalManagerInstance = (VitalManagerInstance) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        vitalManagerInstance.getInstance().reset(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);

        savedInstance.putInt(ArgumentKeys.CURRENT_VITAL_STATE, currentState);
    }

    private void initView(View baseView) {
        statusTv = baseView.findViewById(R.id.status_tv);
        deviceImage = baseView.findViewById(R.id.device_iv);
        startButton = baseView.findViewById(R.id.start_measure_bt);
        statusView = baseView.findViewById(R.id.status_view);
        suggestionsLay = baseView.findViewById(R.id.suggestions_lay);
        chargeTv = baseView.findViewById(R.id.charge_tv);
        reconnectingTv = baseView.findViewById(R.id.reconnecting_tv);

        deviceImage.setImageResource(VitalDeviceType.shared.getImage(vitalDevice.getType()));

        String deviceName = getString(VitalDeviceType.shared.getTitle(vitalDevice.getType()));
        chargeTv.setText(getString(R.string.charge_text, deviceName));
        reconnectingTv.setText(getString(R.string.reconnect_text, deviceName));

        startButton.setOnClickListener(this);
    }

    private void updateView(int state) {
        switch (state) {
            case connected:
                startButton.setVisibility(View.VISIBLE);

                if (vitalDevice.getType().equals(VitalsConstant.TYPE_550BT)) {
                    startButton.setText(getResources().getString(R.string.SYNC));
                } else {
                    startButton.setText(getResources().getString(R.string.START));
                }

                toolBarInterface.updateSubTitle(getString(R.string.connected), View.VISIBLE);
                suggestionsLay.setVisibility(View.GONE);
                break;
            case connecting:
                startButton.setVisibility(View.GONE);
                toolBarInterface.updateSubTitle(getString(R.string.connecting), View.VISIBLE);
                suggestionsLay.setVisibility(View.VISIBLE);
                break;
            case failed:
                startButton.setVisibility(View.VISIBLE);
                startButton.setText(getResources().getString(R.string.retry));
                toolBarInterface.updateSubTitle(getString(R.string.failed_to_connect), View.VISIBLE);
                suggestionsLay.setVisibility(View.VISIBLE);
                break;
            case notConnected:
                startButton.setText(getResources().getString(R.string.connect));
                toolBarInterface.updateSubTitle(getString(R.string.not_connected), View.VISIBLE);
                suggestionsLay.setVisibility(View.VISIBLE);
                break;
        }
        if (state != connected) {
            Utils.greyoutProfile(deviceImage);
        } else {
            Utils.removeGreyoutProfile(deviceImage);
        }
    }

    private void connectVital() {
        currentState = connecting;
        updateView(currentState);
        vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(), vitalDevice.getDeviceId());
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

    }

    @Override
    public void didDiscoverDevice(String type, String serailNumber) {
        //nothing to do
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    currentState = connected;
                    updateView(currentState);
                }
            });
        }
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {
        if (vitalDevice.getDeviceId().equals(serailNumber) && vitalDevice.getType().equals(type)) {
            currentState = notConnected;
            updateView(currentState);
        }
    }

    @Override
    public void didFailConnectDevice(String type, String serailNumber, String errorMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Utils.showAlertDialog(getActivity(), getString(R.string.error),
                            errorMessage,
                            getString(R.string.retry),
                            getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    connectVital();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    currentState = failed;
                                    updateView(currentState);
                                }
                            });
                }
            });
        }
    }

    private Bundle getResultBundle() {
        Bundle bundle;
        if (getArguments() != null) {
            bundle = getArguments();
        } else {
            bundle = new Bundle();
        }
        bundle.putSerializable(ArgumentKeys.VITAL_DEVICE, vitalDevice);
        bundle.putBoolean(ArgumentKeys.NEED_TO_TRIGGER_VITAL_AUTOMATICALLY, true);
        return bundle;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_measure_bt:
                switch (currentState) {
                    case connected:
                        onActionCompleteInterface.onCompletionResult(RequestID.OPEN_CONNECTED_DEVICE, true, getResultBundle());
                        break;
                    case failed:
                        connectVital();
                        break;
                    case notConnected:
                        connectVital();
                        break;
                }
                break;
            case R.id.other_option:
                onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_INFO, true, getResultBundle());
                break;
        }
    }
}
