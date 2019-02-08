package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by Aswin on 29,October,2018
 */
public class DoctorDriverLicenseFragment extends BaseFragment implements DoCurrentTransactionInterface, CameraInterface {

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private ImageView licenseIv;
    private TextView pageHintTv;
    private String doctorLicensePath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            doctorLicensePath = savedInstanceState.getString(Constants.LICENSE_IMAGE_PATH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_driver_license, container, false);

        onViewChangeInterface.enableNext(false);

        initView(view);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.LICENSE_IMAGE_PATH, doctorLicensePath);
    }

    @Override
    public void doCurrentTransaction() {
        CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
        createUserRequestModel.setDoctor_driving_license_path(doctorLicensePath);
        onActionCompleteInterface.onCompletionResult(null, true, null);
    }

    private void initView(View view) {
        licenseIv = (ImageView) view.findViewById(R.id.license_iv);
        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);

        licenseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.showImageSelectionAlert(getActivity());
            }
        });

        if (isDeviceXLarge() && isModeLandscape()){
            pageHintTv.setVisibility(View.INVISIBLE);
        }

        setLicenseImage();

    }

    private void setLicenseImage() {
        if (doctorLicensePath != null && !doctorLicensePath.isEmpty()) {
            licenseIv.setImageBitmap(getBitmpaFromPath(doctorLicensePath));
            onViewChangeInterface.enableNext(true);
        }
    }

    @Override
    public void onImageReceived(String imagePath) {
        doctorLicensePath = imagePath;
        setLicenseImage();
    }
}
