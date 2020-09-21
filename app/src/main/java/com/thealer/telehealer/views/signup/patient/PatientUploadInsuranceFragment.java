package com.thealer.telehealer.views.signup.patient;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by Aswin on 14,October,2018
 */
public class PatientUploadInsuranceFragment extends BaseFragment implements View.OnClickListener, DoCurrentTransactionInterface, CameraInterface {

    private ImageView insuranceFrontIv;
    private ImageView insuranceBackIv;
    private TextView titleTv;

    private String frontImgBitmapPath, backImgBitmapPath;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;

    private static boolean isFrontImage;

    private int currentScreenType = Constants.forRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_upload_insurance, container, false);

        if (getArguments() != null) {
            currentScreenType = getArguments().getInt(ArgumentKeys.SCREEN_TYPE,Constants.forRegistration);
        }

        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.hideOrShowNext(true);

        onViewChangeInterface.updateNextTitle(getString(R.string.next));
        if (currentScreenType == Constants.forProfileUpdate) {
            onViewChangeInterface.updateTitle(getString(R.string.insurance_details));
        }

        checkImagesAdded();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            frontImgBitmapPath = savedInstanceState.getString(getString(R.string.front_image_path));
            backImgBitmapPath = savedInstanceState.getString(getString(R.string.back_image_path));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (frontImgBitmapPath != null){
            outState.putString(getString(R.string.front_image_path), frontImgBitmapPath);
        }

        if (backImgBitmapPath != null){
            outState.putString(getString(R.string.back_image_path), backImgBitmapPath);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    private void initView(View view) {
        insuranceFrontIv = (ImageView) view.findViewById(R.id.insurance_front_iv);
        insuranceBackIv = (ImageView) view.findViewById(R.id.insurance_back_iv);
        titleTv = view.findViewById(R.id.textView5);
        insuranceFrontIv.setOnClickListener(this);
        insuranceBackIv.setOnClickListener(this);

        if (currentScreenType == Constants.forProfileUpdate) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setVisibility(View.VISIBLE);
        }

        setFrontImage();
        setBackImage();
    }

    private void setBackImage() {
        if (backImgBitmapPath != null && !backImgBitmapPath.isEmpty()){
            Bitmap backImgBitmap = getBitmpaFromPath(backImgBitmapPath);
            if (backImgBitmap != null){
                insuranceBackIv.setImageBitmap(backImgBitmap);
            }
        }
        checkImagesAdded();
    }

    private void setFrontImage() {
        if (frontImgBitmapPath != null && !frontImgBitmapPath.isEmpty()){
            Bitmap frontImgBitmap = getBitmpaFromPath(frontImgBitmapPath);
            if (frontImgBitmap != null){
                insuranceFrontIv.setImageBitmap(frontImgBitmap);
            }
        }
        checkImagesAdded();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.insurance_front_iv:
                CameraUtil.with(getActivity()).showImageSelectionAlert();
                isFrontImage = true;
                break;
            case R.id.insurance_back_iv:
                isFrontImage = false;
                CameraUtil.with(getActivity()).showImageSelectionAlert();
                break;
        }
    }

    private void checkImagesAdded() {
        if (frontImgBitmapPath != null && backImgBitmapPath != null){
            onViewChangeInterface.enableNext(true);
        } else {
            onViewChangeInterface.enableNext(false);
        }
    }

    @Override
    public void doCurrentTransaction() {
        switch (currentScreenType) {
            case Constants.forRegistration:
                CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
                createUserRequestModel.setInsurance_front_path(frontImgBitmapPath);
                createUserRequestModel.setInsurance_back_path(backImgBitmapPath);

                onActionCompleteInterface.onCompletionResult(null, true, null);
                break;
            case Constants.forProfileUpdate:
                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.CASH_SELECTED,false);
                bundle.putString(ArgumentKeys.INSURANCE_FRONT,frontImgBitmapPath);
                bundle.putString(ArgumentKeys.INSURANCE_BACK,backImgBitmapPath);
                onActionCompleteInterface.onCompletionResult(RequestID.INSURANCE_CHANGE_RESULT, true, bundle);
                break;
        }
    }

    @Override
    public void onImageReceived(String imagePath) {

        if (isFrontImage) {
            frontImgBitmapPath = imagePath;
            setFrontImage();
        }else {
            backImgBitmapPath = imagePath;
            setBackImage();
        }

    }
}
