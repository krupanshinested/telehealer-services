package com.thealer.telehealer.views.signup.medicalAssistant;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.SignupBaseFragment;

/**
 * Created by Aswin on 16,October,2018
 */
public class MedicalAssistantCertificatePreviewFragment extends SignupBaseFragment implements DoCurrentTransactionInterface {
    private Bitmap bitmap;
    private ImageView certificatePreviewIv;
    private String certificatePath;
    private OnViewChangeInterface onViewChangeInterface;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_assistant_certificate_preview, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    private void initView(View view) {
        certificatePreviewIv = (ImageView) view.findViewById(R.id.certificate_preview_iv);

        if (getArguments() != null) {
            certificatePath = getArguments().getString(getString(R.string.image_path));
        }

        if (certificatePath != null) {
            bitmap = getBitmpaFromPath(certificatePath);
            if (bitmap != null)
                certificatePreviewIv.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(true);
    }

    @Override
    public void doCurrentTransaction() {
        CreateUserRequestModel createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);
        createUserRequestModel.setCertification_path(certificatePath);
        postMADetail(createUserRequestModel);

    }
}
