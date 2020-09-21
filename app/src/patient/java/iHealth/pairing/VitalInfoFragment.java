package iHealth.pairing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalFirmwareFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;

import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 12/3/18.
 */

public class VitalInfoFragment extends BaseFragment implements View.OnClickListener,VitalBatteryFetcher,
        VitalPairInterface,VitalFirmwareFetcher {

    private ImageView device_iv;
    private TextView device_title,device_description,hardware_version_tv,device_version_tv,device_info_url,support_url;
    private LinearLayout device_info;
    private Button upgrade_bt,forget_device_bt;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private VitalManagerInstance vitalManagerInstance;
    private ToolBarInterface toolBarInterface;

    private VitalDevice vitalDevice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_info, container, false);

        vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);

        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));
        onViewChangeInterface.hideOrShowClose(true);
        onViewChangeInterface.hideOrShowBackIv(true);

        vitalManagerInstance.updateBatteryView(View.GONE,0);
        vitalManagerInstance.getInstance().setListener(this);
        vitalManagerInstance.getInstance().setFirmwareFetcherListener(this);
        vitalManagerInstance.getInstance().setBatteryFetcherListener(this);
        fetchBattery();
        vitalManagerInstance.getInstance().getFirmWareInfo(vitalDevice.getType(),vitalDevice.getDeviceId());

        toolBarInterface.updateSubTitle("",View.GONE);
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

    private void initView(View baseView) {
        device_iv = baseView.findViewById(R.id.device_iv);
        device_title = baseView.findViewById(R.id.device_title);
        device_description = baseView.findViewById(R.id.device_description);
        hardware_version_tv = baseView.findViewById(R.id.hardware_version_tv);
        device_version_tv = baseView.findViewById(R.id.device_version_tv);
        device_info_url = baseView.findViewById(R.id.device_info_url);
        support_url = baseView.findViewById(R.id.support_url);
        device_info = baseView.findViewById(R.id.device_info);
        upgrade_bt = baseView.findViewById(R.id.upgrade_bt);
        forget_device_bt = baseView.findViewById(R.id.forget_device_bt);

        forget_device_bt.setOnClickListener(this);
        upgrade_bt.setOnClickListener(this);
        support_url.setOnClickListener(this);
        device_info_url.setOnClickListener(this);

        String supportUrl = VitalDeviceType.shared.getSupportUrl(vitalDevice.getType());
        support_url.setText(Utils.fromHtml("<u>"+supportUrl+"</u>"));

        String infoUrl = VitalDeviceType.shared.getUrl(vitalDevice.getType());
        device_info_url.setText(Utils.fromHtml("<u>"+infoUrl+"</u>"));

        device_iv.setImageResource(VitalDeviceType.shared.getImage(vitalDevice.getType()));
        device_title.setText(VitalDeviceType.shared.getTitle(vitalDevice.getType()));
        device_description.setText(VitalDeviceType.shared.getShortDescription(vitalDevice.getType()));
    }

    private void fetchBattery() {
        vitalManagerInstance.getInstance().fetchBattery(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_device_bt:
                vitalManagerInstance.getInstance().disconnect(vitalDevice.getType(),vitalDevice.getDeviceId());
                VitalPairedDevices.removePairDevice(vitalDevice,appPreference);
                getActivity().finish();
                break;
            case R.id.upgrade_bt:
                break;
            case R.id.support_url:
                String supportUrl = VitalDeviceType.shared.getSupportUrl(vitalDevice.getType());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(supportUrl));
                startActivity(browserIntent);
                break;
            case R.id.device_info_url:
                String infoUrl = VitalDeviceType.shared.getUrl(vitalDevice.getType());
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(infoUrl));
                startActivity(browserIntent2);
                break;
        }
    }

    //VitalBatteryFetcher methods
    @Override
    public void updateBatteryDetails(BatteryResult batteryResult) {
        if (batteryResult.getBattery() != -1) {
            vitalManagerInstance.updateBatteryView(View.VISIBLE,batteryResult.getBattery());
        } else {
            vitalManagerInstance.updateBatteryView(View.GONE,0);
        }
    }

    @Override
    public void notConnected(String deviceType, String deviceMac) {

    }

    //VitalPairInterface methods
    @Override
    public void didScanFinish() {
        //nothing to do
    }

    @Override
    public void didScanFailed(String error) {
        //nothing to do
    }

    @Override
    public void didDiscoverDevice(String type, String serailNumber) {
        //nothing to do
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        fetchBattery();
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {

    }

    @Override
    public void didFailConnectDevice(String type, String serailNumber, String errorMessage) {

    }

    //VitalFirmwareFetcher method
    @Override
    public void didFetchedLocalInfo(String hardwareVersion, String firmwareVersion) {
        device_info.setVisibility(View.VISIBLE);
        hardware_version_tv.setText(getString(R.string.hardware,": "+hardwareVersion));
        device_version_tv.setText(getString(R.string.firmware,": "+firmwareVersion));
    }
    @Override
    public void didFetchedServerInfo(String hardwareVersion,String firmwareVersion) {
        upgrade_bt.setVisibility(View.VISIBLE);
    }
}
