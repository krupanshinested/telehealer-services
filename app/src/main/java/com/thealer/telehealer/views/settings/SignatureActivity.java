package com.thealer.telehealer.views.settings;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.signature.SignatureApiResponseModel;
import com.thealer.telehealer.apilayer.models.signature.SignatureApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 18,January,2019
 */
public class SignatureActivity extends BaseActivity implements View.OnClickListener {
    private TextView cancelTv;
    private TextView eraseTv;
    private SignaturePad signaturepad;
    private TextView infoTv;
    private Button saveBtn;

    private SignatureApiViewModel signatureApiViewModel;
    private FrameLayout frameBack;
    private FrameLayout frameFront;
    private CustomButton proceedBtn;
    private TextView cancel2Tv;
    private TextView cancelInfoTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        if (PermissionChecker.with(this).checkPermission(PermissionConstants.PERMISSION_STORAGE)) {
            initViewModels();
            initView();
        }
    }

    private void initViewModels() {
        signatureApiViewModel = ViewModelProviders.of(this).get(SignatureApiViewModel.class);
        attachObserver(signatureApiViewModel);
        signatureApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel.isSuccess()) {
                                SignatureApiResponseModel signatureApiResponseModel = (SignatureApiResponseModel) baseApiResponseModel;
                                WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                                whoAmIApiResponseModel.getUser_detail().setSignature(signatureApiResponseModel.getPath());
                                UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        }
                    }
                });
    }

    private void initView() {
        cancelTv = (TextView) findViewById(R.id.cancel_tv);
        eraseTv = (TextView) findViewById(R.id.erase_tv);
        signaturepad = (SignaturePad) findViewById(R.id.signaturepad);
        infoTv = (TextView) findViewById(R.id.info_tv);
        saveBtn = (Button) findViewById(R.id.save_btn);
        frameBack = (FrameLayout) findViewById(R.id.frame_back);
        frameFront = (FrameLayout) findViewById(R.id.frame_front);
        proceedBtn = (CustomButton) findViewById(R.id.proceed_btn);
        cancel2Tv = (TextView) findViewById(R.id.cancel2_tv);
        cancelInfoTv = (TextView) findViewById(R.id.cancel_info_tv);

        cancelTv.setOnClickListener(this);
        eraseTv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        proceedBtn.setOnClickListener(this);
        cancel2Tv.setOnClickListener(this);

        if (getIntent() != null) {
            boolean showProposer = getIntent().getBooleanExtra(ArgumentKeys.SHOW_SIGNATURE_PROPOSER, false);
            isShowSignatureProposer(showProposer);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.erase_tv:
                signaturepad.clear();
                break;
            case R.id.save_btn:
                signatureApiViewModel.uploadSignature(true, signaturepad.getSignatureBitmap());
                break;
            case R.id.proceed_btn:
                isShowSignatureProposer(false);
                break;
            case R.id.cancel_tv:
            case R.id.cancel2_tv:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
    }

    private void isShowSignatureProposer(boolean show) {
        if (show) {
            frameFront.setVisibility(View.VISIBLE);
            frameBack.setAlpha(0.2f);

            String selectedItem = getIntent().getStringExtra(Constants.SELECTED_ITEM);
            cancelInfoTv.setText(String.format(getString(R.string.signature_cancel_info), selectedItem.toLowerCase()));

        } else {
            frameFront.setVisibility(View.GONE);
            frameBack.setAlpha(1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionConstants.PERMISSION_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {
                initViewModels();
                initView();
            } else {
                finish();
            }
        }
    }
}
