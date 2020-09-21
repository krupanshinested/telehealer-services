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
public class DoctorCertificateFragment extends BaseFragment implements DoCurrentTransactionInterface, CameraInterface {
    private ImageView certificateIv;
    private TextView pageHintTv;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private String doctorCertificatePath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            doctorCertificatePath = savedInstanceState.getString(Constants.PICTURE_PATH);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.PICTURE_PATH, doctorCertificatePath);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_certificate, container, false);

        onViewChangeInterface.enableNext(false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        certificateIv = (ImageView) view.findViewById(R.id.certificate_iv);
        pageHintTv = (TextView) view.findViewById(R.id.page_hint_tv);

        certificateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.with(getActivity()).showImageSelectionAlert();
            }
        });

        setCertificateImage();

        if (isDeviceXLarge() && isModeLandscape()) {
            pageHintTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void doCurrentTransaction() {
        CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
        createUserRequestModel.setDoctor_certificate_path(doctorCertificatePath);
        onActionCompleteInterface.onCompletionResult(null, true, null);
    }


    private void setCertificateImage() {
        if (doctorCertificatePath != null && !doctorCertificatePath.isEmpty()) {
            onViewChangeInterface.enableNext(true);
            certificateIv.setImageBitmap(getBitmpaFromPath(doctorCertificatePath));
        }
    }

    @Override
    public void onImageReceived(String imagePath) {
        doctorCertificatePath = imagePath;
        setCertificateImage();
    }
}
