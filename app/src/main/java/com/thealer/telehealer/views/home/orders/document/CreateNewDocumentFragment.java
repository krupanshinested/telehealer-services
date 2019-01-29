package com.thealer.telehealer.views.home.orders.document;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCreateApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.documents.DocumentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.base.OrdersBaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        ordersCreateApiViewModel = ViewModelProviders.of(this).get(OrdersCreateApiViewModel.class);
        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);

        attachObserverInterface.attachObserver(ordersApiViewModel);

        ordersCreateApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, baseApiResponseModel.isSuccess());

                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.successfully_uploaded));

                    LocalBroadcastManager
                            .getInstance(getActivity())
                            .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                    .putExtras(bundle));

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
        setTitle("New Document");
        initView(view);
        return view;
    }

    private void initView(View view) {
        documentNameTil = (TextInputLayout) view.findViewById(R.id.document_name_til);
        documentNameEt = (EditText) view.findViewById(R.id.document_name_et);
        documentTv = (TextView) view.findViewById(R.id.document_tv);
        documentIv = (ImageView) view.findViewById(R.id.document_iv);
        addBtn = (Button) view.findViewById(R.id.add_btn);
        appbarLl = (LinearLayout) view.findViewById(R.id.appbar_ll);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

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

                    Utils.setImageWithGlide(getActivity(), documentIv, image_path, getActivity().getDrawable(R.drawable.ic_orders_documents), true);

                }

                addBtn.setText(getString(R.string.update));
            }
        }

        isDataObtained();


    }

    private void enableOrDisableAdd(boolean enable) {
        addBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                if (!isEditMode) {
                    uploadDocument();
                } else {
                    updateDocument();
                }
                break;
            case R.id.document_iv:
                if (!isEditMode) {
                    CameraUtil.with(getActivity()).showImageSelectionAlert();
                }
                break;
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    private void updateDocument() {
        ordersApiViewModel.updateDocument(documentId, documentNameEt.getText().toString(), true);
    }

    private void uploadDocument() {

        ordersCreateApiViewModel.uploadDocument(documentNameEt.getText().toString(), image_path, false);

        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestID.REQ_SHOW_SUCCESS_VIEW) {
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
        if (!documentNameEt.getText().toString().isEmpty() && image_path != null && !image_path.isEmpty()) {
            enableOrDisableAdd(true);
        } else {
            enableOrDisableAdd(false);
        }
    }
}
