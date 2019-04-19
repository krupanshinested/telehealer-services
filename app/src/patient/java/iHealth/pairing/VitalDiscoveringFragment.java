package iHealth.pairing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.SupportInformation;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.common.Constants.LOCATION_SETTINGS_REQUEST;

/**
 * Created by rsekar on 12/5/18.
 */

public class VitalDiscoveringFragment extends BaseFragment implements VitalPairInterface, View.OnClickListener {

    private TextView statusTv;
    private ImageView deviceImage;
    private CustomButton retryButton, statusView;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private VitalManagerInstance vitalManagerInstance;
    private ToolBarInterface toolBarInterface;

    private Boolean isDeviceFound = false;

    private String deviceType;
    @Nullable
    private String deviceMac = null;

    private int currentState = connecting;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        if (saveInstance != null) {
            currentState = saveInstance.getInt(ArgumentKeys.CURRENT_VITAL_STATE);
        } else {
            currentState = connecting;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_discovering, container, false);

        deviceType = getArguments().getString(ArgumentKeys.DEVICE_TYPE);

        String deviceMac = getArguments().getString(ArgumentKeys.DEVICE_MAC);
        if (deviceMac != null && !deviceMac.isEmpty()) {
            this.deviceMac = deviceMac;
        }

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        vitalManagerInstance.getInstance().setListener(this);
        vitalManagerInstance.updateBatteryView(View.GONE, 0);
        updateView(currentState);

        toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(deviceType)));
        onViewChangeInterface.hideOrShowClose(true);
        onViewChangeInterface.hideOrShowBackIv(true);

        SupportInformation supportInformation = VitalDeviceType.getMeasureInfo(deviceType);

        if (supportInformation != null && !getArguments().getBoolean("isDisplaySupportDialog")) {
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
            if (VitalDeviceType.shared.getMeasurementType(deviceType).equals(SupportedMeasurementType.temperature)) {
                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (deviceMac != null) {
                        connectVital(deviceType, deviceMac);
                    } else {
                        scanVital(deviceType);
                    }
                } else {
                    displayLocationSettingsRequest(getActivity());
                }
            } else {
                if (deviceMac != null) {
                    connectVital(deviceType, deviceMac);
                } else {
                    scanVital(deviceType);
                }
            }
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

        savedInstance.putString(ArgumentKeys.DEVICE_TYPE, deviceType);
        savedInstance.putString(ArgumentKeys.DEVICE_MAC, deviceMac);
        savedInstance.putInt(ArgumentKeys.CURRENT_VITAL_STATE, currentState);
    }

    private void initView(View baseView) {
        statusTv = baseView.findViewById(R.id.status_tv);
        deviceImage = baseView.findViewById(R.id.device_iv);
        retryButton = baseView.findViewById(R.id.retry_button);
        statusView = baseView.findViewById(R.id.status_view);

        statusTv.setText(getString(R.string.discovering));
        deviceImage.setImageResource(VitalDeviceType.shared.getImage(deviceType));

        retryButton.setOnClickListener(this);

        Glide.with(getActivity())
                .load(R.raw.connecting)
                .into((ImageView) baseView.findViewById(R.id.connecting_view));
    }

    private void connectVital(String type, String serailNumber) {
        currentState = connecting;
        updateView(currentState);
        vitalManagerInstance.getInstance().connectDevice(type, serailNumber);
    }

    private void scanVital(String type) {
        isDeviceFound = false;
        currentState = discovering;
        updateView(currentState);
        vitalManagerInstance.getInstance().startScan(type);
    }

    private void updateView(int state) {
        switch (state) {
            case discovering:
                toolBarInterface.updateSubTitle(getString(R.string.discovering), View.VISIBLE);
                retryButton.setVisibility(View.GONE);
                break;
            case connecting:
                toolBarInterface.updateSubTitle(getString(R.string.connecting), View.VISIBLE);
                retryButton.setVisibility(View.GONE);
                break;
            case connected:
                toolBarInterface.updateSubTitle(getString(R.string.connected), View.VISIBLE);
                break;
            case failed:
                toolBarInterface.updateSubTitle(getString(R.string.failed_to_connect), View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                break;
            case scanned:
                toolBarInterface.updateSubTitle(getString(R.string.scan_completed), View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                break;
            case scanningFailed:
                toolBarInterface.updateSubTitle(getString(R.string.scanning_failed), View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void didScanFinish() {
        if (!isDeviceFound) {
            Utils.showAlertDialog(getActivity(), getString(R.string.device_not_found),
                    getString(R.string.device_not_found_description),
                    getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            scanVital(deviceType);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateView(scanned);
                        }
                    });
        }
    }

    @Override
    public void didScanFailed(String error) {
        currentState = scanningFailed;
        updateView(currentState);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Utils.showAlertDialog(getActivity(), getString(R.string.error),
                        error,
                        getString(R.string.retry),
                        getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                scanVital(deviceType);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentState = scanningFailed;
                                updateView(failed);
                            }
                        });
            }
        });
    }

    @Override
    public void startedToConnect(String type, String serailNumber) {

    }

    @Override
    public void didDiscoverDevice(String type, String serailNumber) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                VitalPairedDevices vitalPairedDevices = VitalPairedDevices.getAllPairedDevices(appPreference);

                if (!vitalPairedDevices.isAlreadyPaired(type, serailNumber) && type.equals(deviceType)) {

                    isDeviceFound = true;

                    Utils.showAlertDialog(getActivity(), getString(R.string.vital_discover_title),
                            serailNumber,
                            getString(R.string.connect), null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    connectVital(type, serailNumber);
                                }
                            }, null);
                }

            }

        });
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                appPreference.setBoolean(PreferenceConstants.IS_VITAL_DEVICE_CONNECTED, true);

                deviceMac = serailNumber;
                deviceType = type;

                currentState = connected;
                updateView(currentState);

                VitalDevice vitalDevice = new VitalDevice(serailNumber, type, false, "");
                VitalPairedDevices.addPairDevice(vitalDevice, appPreference);

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.device_connected_successfully));

                SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                successViewDialogFragment.setArguments(bundle);
                successViewDialogFragment.setTargetFragment(VitalDiscoveringFragment.this, RequestID.REQ_SHOW_SUCCESS_VIEW);
                successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

            }
        });
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {

    }

    @Override
    public void didFailConnectDevice(String type, String serailNumber, String errorMessage) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Utils.showAlertDialog(getActivity(), getString(R.string.error),
                        errorMessage,
                        getString(R.string.retry),
                        getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                connectVital(type, serailNumber);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentState = failed;
                                updateView(failed);
                            }
                        });
            }
        });
    }

    private Bundle getResultBundle() {
        Bundle bundle;
        if (getArguments() != null) {
            bundle = getArguments();
        } else {
            bundle = new Bundle();
        }
        bundle.putSerializable(ArgumentKeys.IS_OPENING_DIRECTLY_FROM_PAIRING, true);
        bundle.putString(ArgumentKeys.DEVICE_MAC, deviceMac);
        bundle.putString(ArgumentKeys.DEVICE_TYPE, deviceType);
        bundle.putSerializable(ArgumentKeys.VITAL_DEVICE, getSelectedDevice());
        return bundle;
    }

    private VitalDevice getSelectedDevice() {
        return VitalPairedDevices.getAllPairedDevices(appPreference).getVitalDevice(deviceType, deviceMac);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retry_button:
                if (currentState == scanningFailed) {
                    scanVital(deviceType);
                } else {
                    connectVital(deviceType, deviceMac);
                }
                break;
        }
    }

    private void displayLocationSettingsRequest(Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                Log.d("VitalManager", "LocationSettingsResponse");
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                    if (response != null) {
                        if (deviceMac != null) {
                            connectVital(deviceType, deviceMac);
                        } else {
                            scanVital(deviceType);
                        }

                    }
                } catch (ApiException ex) {

                    Log.d("VitalManager", ex.getLocalizedMessage());

                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(activity,
                                                LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    onActionCompleteInterface.onCompletionResult(RequestID.OPEN_INITIAL_FRAGMENT,true,null);
                    onActionCompleteInterface.onCompletionResult(RequestID.OPEN_CONNECTED_DEVICE, true, getResultBundle());
                    toolBarInterface.updateSubTitle("",View.GONE);
                    break;
                default:
                    break;
            }
        }
    }
}
