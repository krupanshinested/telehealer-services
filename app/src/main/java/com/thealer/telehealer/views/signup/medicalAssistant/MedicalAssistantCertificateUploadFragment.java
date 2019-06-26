package com.thealer.telehealer.views.signup.medicalAssistant;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by Aswin on 16,October,2018
 */
public class MedicalAssistantCertificateUploadFragment extends BaseFragment implements View.OnClickListener, DoCurrentTransactionInterface, CameraInterface {

    private CustomButton takePhotoBtn;
    private CustomButton addDocumentBtn;
    private String certificate_path;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_assistant_certificate_upload, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    private void initView(View view) {
        takePhotoBtn = (CustomButton) view.findViewById(R.id.take_photo_btn);
        addDocumentBtn = (CustomButton) view.findViewById(R.id.add_document_btn);

        takePhotoBtn.setOnClickListener(this);
        addDocumentBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo_btn:
                CameraUtil.showImageSelectionAlert(getActivity());
                break;
            case R.id.add_document_btn:
                CameraUtil.showImageSelectionAlert(getActivity());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.hideOrShowNext(false);
    }

    @Override
    public void doCurrentTransaction() {
    }

    @Override
    public void onImageReceived(String imagePath) {
        certificate_path = imagePath;

        if (certificate_path != null && !certificate_path.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.image_path), certificate_path);

            onActionCompleteInterface.onCompletionResult(null, true, bundle);
            certificate_path = null;
        }

    }
}
