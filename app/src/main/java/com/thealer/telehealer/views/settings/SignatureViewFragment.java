package com.thealer.telehealer.views.settings;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.signature.SignatureApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

/**
 * Created by Aswin on 18,January,2019
 */
public class SignatureViewFragment extends BaseFragment {

    private ImageView signatureIv;
    private Button editBtn;

    private OnCloseActionInterface onCloseActionInterface;
    private SignatureApiViewModel signatureApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        signatureApiViewModel = ViewModelProviders.of(this).get(SignatureApiViewModel.class);
        attachObserverInterface.attachObserver(signatureApiViewModel);
        signatureApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel.isSuccess()) {

                                WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                                whoAmIApiResponseModel.getUser_detail().setSignature(null);
                                UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);

                                onCloseActionInterface.onClose(false);
                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signature_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        signatureIv = (ImageView) view.findViewById(R.id.signature_iv);
        editBtn = (Button) view.findViewById(R.id.edit_btn);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View alertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_signature_alert, null);
                builder.setView(alertView);

                AlertDialog alertDialog = builder.create();

                TextView disableSignatureTv;
                TextView newSignatureTv;
                CardView cancelCv;

                disableSignatureTv = (TextView) alertView.findViewById(R.id.disable_signature_tv);
                newSignatureTv = (TextView) alertView.findViewById(R.id.new_signature_tv);
                cancelCv = (CardView) alertView.findViewById(R.id.cancel_cv);

                disableSignatureTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        signatureApiViewModel.deleteSignature(true);
                    }
                });

                newSignatureTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        showNewSignature();
                    }
                });

                cancelCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                }
                alertDialog.show();

            }
        });

        if (UserDetailPreferenceManager.getWhoAmIResponse().getUser_detail().getSignature() == null ||
                UserDetailPreferenceManager.getWhoAmIResponse().getUser_detail().getSignature().isEmpty()) {
            showNewSignature();
        } else {
            setSignature();
        }

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        toolbarTitle.setText(getString(R.string.preview));
        nextTv.setVisibility(View.GONE);
    }

    private void showNewSignature() {
        startActivityForResult(new Intent(getActivity(), SignatureActivity.class), RequestID.REQ_SIGNATURE);
    }

    private void setSignature() {
        Utils.setImageWithGlide(getActivity().getApplicationContext(), signatureIv, UserDetailPreferenceManager.getWhoAmIResponse().getUser_detail().getSignature(), null, true, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_SIGNATURE) {
            if (resultCode == Activity.RESULT_OK) {
                setSignature();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                onCloseActionInterface.onClose(false);
            }
        }
    }
}
