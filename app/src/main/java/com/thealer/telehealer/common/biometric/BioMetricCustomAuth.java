package com.thealer.telehealer.common.biometric;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

/**
 * Created by Aswin on 31,October,2018
 */
public class BioMetricCustomAuth extends BaseBottomSheetDialogFragment {
    private TextView titleTv;
    private TextView subtitleTv;
    private TextView descriptionTv;
    private ImageView biometricIv;
    private TextView biometricStatusTv;
    private Button cancelTv;
    private BiometricInterface biometricInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        biometricInterface = (BiometricInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_biometric_auth, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        subtitleTv = (TextView) view.findViewById(R.id.subtitle_tv);
        descriptionTv = (TextView) view.findViewById(R.id.description_tv);
        biometricIv = (ImageView) view.findViewById(R.id.biometric_iv);
        biometricStatusTv = (TextView) view.findViewById(R.id.biometric_status_tv);
        cancelTv = (Button) view.findViewById(R.id.cancel_tv);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricInterface.onBioMetricActionComplete(null, Constants.BIOMETRIC_CANCEL);
                getDialog().dismiss();
            }
        });

        BiometricFingerPrint.initializeFingerPrint(getActivity(), new BiometricInterface() {
            @Override
            public void onBioMetricActionComplete(String status, int code) {
                setStatus(status);
                switch (code) {
                    case Constants.BIOMETRIC_ERROR:
                        break;
                    case Constants.BIOMETRIC_FAILED:
                        break;
                    case Constants.BIOMETRIC_SUCCESS:
                        biometricInterface.onBioMetricActionComplete(status, code);
                        getDialog().dismiss();
                        break;
                }
            }
        });
    }


    public void setStatus(String status) {
        biometricStatusTv.setText(status);
    }


}
