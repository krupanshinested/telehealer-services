package com.thealer.telehealer.views.home.orders.document;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.onboarding.OnBoardingViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 29,November,2018
 */

public class CreateNewDocumentFragment extends OrdersBaseFragment implements View.OnClickListener, CameraInterface {
    private TextInputLayout documentNameTil;
    private EditText documentNameEt;
    private TextView documentTv;
    private ImageView documentIv;
    private Button addBtn;

    private String image_path;
    private OrdersCreateApiViewModel ordersCreateApiViewModel;
    private OrdersApiViewModel ordersApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;
    private boolean isEditMode;
    private int documentId;
    private LinearLayout appbarLl;
    private ImageView backIv;
    private TextView toolbarTitle;
    private ConstraintLayout viewPagerCl;
    private ViewPager viewPager;
    private LinearLayout pagerIndicator;
    private OnBoardingViewPagerAdapter onBoardingViewPagerAdapter;
    private ImageView[] indicators;
    private boolean isShareIntent = false;
    private List<String> imagePathList = new ArrayList<>();
    private int next = 1;
    private ConstraintLayout parent;
    private String patientGuid = null, doctorGuid = null;
    private OrdersCustomView visitOcv;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        ordersCreateApiViewModel = new ViewModelProvider(this).get(OrdersCreateApiViewModel.class);

        ordersApiViewModel = new ViewModelProvider(this).get(OrdersApiViewModel.class);

        attachObserverInterface.attachObserver(ordersApiViewModel);

        ordersCreateApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (isShareIntent) {
                        next = next + 1;
                        imagePathList.remove(0);

                        if (imagePathList.size() == 0) {
                            Constants.sharedPath = null;
                            sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), getString(R.string.successfully_uploaded));
                        } else {
                            uploadMultipleDocument();
                        }
                    } else {
                        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.success), getString(R.string.successfully_uploaded));
                    }
                }
            }
        });

        ordersCreateApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, errorModel.isSuccess());
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.upload_document_failure));

                    LocalBroadcastManager
                            .getInstance(getActivity())
                            .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                    .putExtras(bundle));

                }
            }
        });

        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                        }
                        onCloseActionInterface.onClose(false);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_create_new, container, false);
        setTitle(getString(R.string.new_document));
        initView(view);
        return view;
    }

    private void initView(View view) {
        parent = (ConstraintLayout) view.findViewById(R.id.parent);
        documentNameTil = (TextInputLayout) view.findViewById(R.id.document_name_til);
        documentNameEt = (EditText) view.findViewById(R.id.document_name_et);
        documentTv = (TextView) view.findViewById(R.id.document_tv);
        documentIv = (ImageView) view.findViewById(R.id.document_iv);
        addBtn = (Button) view.findViewById(R.id.add_btn);
        appbarLl = (LinearLayout) view.findViewById(R.id.appbar_ll);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        viewPagerCl = (ConstraintLayout) view.findViewById(R.id.viewPager_cl);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        pagerIndicator = (LinearLayout) view.findViewById(R.id.pager_indicator);
        visitOcv = (OrdersCustomView) view.findViewById(R.id.visit_ocv);

        addBtn.setOnClickListener(this);
        documentIv.setOnClickListener(this);
        backIv.setOnClickListener(this);

        documentNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isDataObtained();
            }
        });

        if (getArguments() != null) {

            isEditMode = getArguments().getBoolean(ArgumentKeys.IS_EDIT_MODE);

            if (isEditMode) {

                appbarLl.setVisibility(View.VISIBLE);

                toolbarTitle.setText(getString(R.string.edit_document));

                DocumentsApiResponseModel.ResultBean resultBean = (DocumentsApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.DOCUMENT_MODEL);

                if (resultBean != null) {

                    documentId = resultBean.getUser_file_id();

                    documentNameEt.setText(resultBean.getName());

                    image_path = resultBean.getPath();

                    Utils.setImageWithGlide(getActivity().getApplicationContext(), documentIv, image_path, getActivity().getDrawable(R.drawable.ic_orders_documents), true, true);

                }

                addBtn.setText(getString(R.string.update));
            }

            isShareIntent = getArguments().getBoolean(ArgumentKeys.IS_SHARED_INTENT);

            if (isShareIntent) {

                showShareData();
            }

            CommonUserApiResponseModel patientDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (patientDetail != null) {
                patientGuid = patientDetail.getUser_guid();
                visitOcv.setVisibility(View.VISIBLE);
                setVisitsView(visitOcv, patientGuid, doctorGuid);
                getPatientsRecentsList(patientDetail.getUser_guid(), doctorGuid);
            }

            CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorDetail != null) {
                doctorGuid = doctorDetail.getUser_guid();
            }


        }

        isDataObtained();
    }

    private void showShareData() {
        if (PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_STORAGE)) {

            List<Bitmap> bitmapList = new ArrayList<>();
            documentIv.setVisibility(View.GONE);
            viewPagerCl.setVisibility(View.VISIBLE);

            imagePathList = Constants.sharedPath;

            int size = Constants.sharedPath.size();

            if (size > 10) {
                Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.max_10_images),
                        getString(R.string.ok), null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, null);
                size = 10;
            }

            for (int i = 0; i < size; i++) {
                bitmapList.add(BitmapFactory.decodeFile(imagePathList.get(i)));
            }

            if (bitmapList.size() > 0) {
                pagerIndicator.setVisibility(View.VISIBLE);
            } else {
                pagerIndicator.setVisibility(View.GONE);
            }

            setUpViewPager(bitmapList);
        }
    }

    private void setUpViewPager(List<Bitmap> imagePathList) {
        onBoardingViewPagerAdapter = new OnBoardingViewPagerAdapter(getActivity(), imagePathList);
        viewPager.setAdapter(onBoardingViewPagerAdapter);
        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                for (int j = 0; j < onBoardingViewPagerAdapter.getCount(); j++) {
                    indicators[j].setImageDrawable(getResources().getDrawable(R.drawable.circular_unselected_indicator));
                }
                indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (imagePathList.size() > 0)
            createIndicator();

    }

    private void createIndicator() {
        int count = onBoardingViewPagerAdapter.getCount();
        indicators = new ImageView[count];

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.circular_unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            pagerIndicator.addView(indicators[i], params);
        }

        indicators[0].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));

    }


    private void enableOrDisableAdd(boolean enable) {
        addBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Utils.hideKeyboard(getActivity());
                if (!isShareIntent) {
                    if (!isEditMode) {
                        uploadDocument();
                    } else {
                        updateDocument();
                    }
                } else {
                    showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, null);
                    uploadMultipleDocument();
                }
                break;
            case R.id.document_iv:
                if (!isEditMode) {
                    CameraUtil.showImageSelectionAlert(getActivity());
                }
                break;
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    private void uploadMultipleDocument() {
        ordersCreateApiViewModel.uploadDocument(null, null, documentNameEt.getText().toString().concat("_" + next), imagePathList.get(0), null, false);
    }

    private void updateDocument() {
        ordersApiViewModel.updateDocument(documentId, documentNameEt.getText().toString(), true);
    }

    private void uploadDocument() {

        ordersCreateApiViewModel.uploadDocument(patientGuid, doctorGuid, documentNameEt.getText().toString(), image_path, getVistOrderId() , false);
        showSuccessView(this, RequestID.REQ_SHOW_SUCCESS_VIEW, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionConstants.PERMISSION_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {
                showShareData();
            } else {
                onCloseActionInterface.onClose(true);
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW) {
                if (!((BaseActivity) getActivity()).isPreviousActivityAvailable()) {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().setResult(Activity.RESULT_OK);
                }
                getActivity().finish();
            }
        }
    }

    @Override
    public void onImageReceived(String imagePath) {
        this.image_path = imagePath;
        documentIv.setImageBitmap(getBitmpaFromPath(imagePath));
        isDataObtained();
    }

    private void isDataObtained() {
        if (!documentNameEt.getText().toString().isEmpty() &&
                ((image_path != null && !image_path.isEmpty()) ||
                        (imagePathList != null && imagePathList.size() > 0) ||
                        (Constants.sharedPath != null && Constants.sharedPath.size() > 0))) {
            enableOrDisableAdd(true);
        } else {
            enableOrDisableAdd(false);
        }
    }
}
